# 
#   The MIT License
#  
#   Copyright 2006-2010 Karl W. Pfalzer.
#   Copyright 2012-     George P. Burdell
#  
#   Permission is hereby granted, free of charge, to any person obtaining a copy
#   of this software and associated documentation files (the "Software"), to deal
#   in the Software without restriction, including without limitation the rights
#   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
#   copies of the Software, and to permit persons to whom the Software is
#   furnished to do so, subject to the following conditions:
#  
#   The above copyright notice and this permission notice shall be included in
#   all copies or substantial portions of the Software.
#  
#   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
#   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
#   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
#   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
#   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
#   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
#   THE SOFTWARE.
#
require 'jinit'
require 'util'
require 'message'

class Parse
  MAX_ITER = 1000

  attr_reader :unresolved, :nodefn
  
  def initialize(opts)
    @opts = opts
    Message.set_message_level(@opts.msg_lvl) if @opts.msg_lvl
    if @opts.opt_e
      File.delete @opts.opt_e if File.exist?(@opts.opt_e)
      Java::apfe.vlogpp2.VppMain.setDumpVpp(@opts.opt_e)
    end
    Java::apfe.vlogpp2.MacroDefns.setRedefinedCheck(@opts.redefn_lvl) if @opts.redefn_lvl
    Java::apfe.vlogpp2.MacroDefns.setSingleCompilationUnits(0 != @opts.sfcu) if @opts.sfcu
    #todo
    @sysvlog = SysVlog.new(@opts.sv, @opts.define, @opts.incdir)
    @vhdl = Vhdl.new(@opts.vhdl)
    @slf = Slf.new(@opts.slf)
    @top = opts.top_mod
    @linker = nil
    @unresolved = []
    @state = :start #state-machine
    @changed_state = false  #@see link for context
    @v = @opts.v
    #Process seekers in this order
    #Pair of seeker and parser
    @seekers = []
    @seekers << [@opts.slf_seeker, @slf]
    @seekers << [@opts.sv_seeker, @sysvlog]
    @seekers << [@opts.vhdl_seeker, @vhdl]
  end

  def has_unresolved?
    !@unresolved.empty?
  end

  def get_define_only_files(srcs)
    @sysvlog.get_define_only_files(srcs)
  end

  public
  def get_status(msg=true)
    svr,stat = has_unresolved? ? ['E',false] : ['I',true]
    if msg
      Message.message(svr, 'LINK-2', stat.to_s)
      Message.message(svr, 'LINK-1', @unresolved.join(' ').to_s) unless stat
    end
    stat
  end

  #Return list of used source types, in order of processed.
  public
  def get_used_srcs(type=:sv)
    used = @linker.get_used_srcs(@top)
    bases = {:slf=>@slf,:sv=>@sysvlog,:vhdl=>@vhdl}
    base = bases[type]
    in_order = base.processed_srcs & used
  end

  public
  def get_included_files
    @sysvlog.nil? ? [] : @sysvlog.get_included_files
  end

  #Return list of used srcs by type
  public
  def get_used_srcs_by_type
    r = Hash.new
    [:slf,:sv,:vhdl].each do |type|
      r[type] = get_used_srcs(type)
    end
    r
  end

  public
  def link
    #list of Base to collect from
    bases = [@slf,@sysvlog,@vhdl]
    @linker = Linker.new(bases, @opts.nodefn) #1st pass
    unresolved = []
    while true
      unless @changed_state
        iter,unresolved = @linker.link(@top)
        unresolved.each do |n|
          Util.debug "1: unresolved: pass #{iter}: #{n}"
        end if Util::DEBUG
        if unresolved.empty?
          @unresolved = []
          break
        end
        Message::fatal("Link iteration > #{MAX_ITER}") if MAX_ITER < iter
        break if unresolved==@unresolved and @state==:finish #no change
        # Now, for the more complex...
      end
      Util.debug("3: changed=#{@changed_state}: state=#{@state}")
      @changed_state = false
      case @state
      when :start
        @state = @v.empty? ? :try_seekers : :try_v
        @changed_state = true
      when :try_v
        if @v.empty?
          @state = :try_seekers
          @changed_state = true
        else
          added = @sysvlog.parse_incr(@v.delete_at(0).split)  #pop -v file
          @linker.add(added)
        end
      when :try_seekers
        seeker,parser = @seekers.delete_at(0)  #pop
        if seeker
          @state = :seek0
        elsif @seekers.empty?
          @state = :finish
        else
          #same state
        end
        @changed_state = true
      when :seek0
        fn = nil
        save_unresolved = unresolved.dup
        while !unresolved.empty?
          m = unresolved.delete_at(0)
          fn = seeker.get_next(m)
          break if fn
        end
        if fn
          added = parser.parse_incr(fn.split)
          @linker.add(added)
        else
          @state = :try_seekers
          @changed_state = true
          unresolved = save_unresolved
        end
      when :finish
      end
      @unresolved = unresolved unless @changed_state
    end
    @unresolved = unresolved.sort
    @nodefn = @linker.nodefn  #hash of +nodefn+ not defined
    used_nodefn = @unresolved & @nodefn.keys  #intersection
    used_nodefn.each {|k| @nodefn[k] = true}
    @unresolved -= used_nodefn
  end

  public
  def get_trackers
    [@slf.jtracker, @sysvlog.jtracker, @vhdl.jtracker]
  end

  private
  class Linker
    attr_reader :mod_by_name, :nodefn

    def initialize(bases, nodefn)
      @mod_by_name = Hash.new #Module by name
      @cnt = 0
      @unresolved = []
      @nodefn = Hash.new
      nodefn.each { |mod| @nodefn[mod] = false} #Hash of nodefn reference
      init(bases)
    end

    #Return [iter,unresolved]
    def link(top)
      @unresolved = get_unresolved(top)
      @cnt +=1
      [@cnt,@unresolved]
    end

    private
    def init(bases)
      bases.each do |parser_base|
        jmods_by_name = parser_base.jmods_by_name
        #jmods_by_name is Hash[String]=>Parser::Module
        add(jmods_by_name)
      end
    end

    public
    def add(pairs)
      pairs.each do |name,jmodule|
        Util.debug "2: mod=#{name}"
        if @mod_by_name.key?(name)
          redefined_error(name, @mod_by_name[name], jmodule)
        else
          if @nodefn.has_key?(name)
            loc = Util.jlocation_to_a(jmodule.get_decl_loc)
            opts = [loc,name,name].flatten
            Message::message('W', 'DEFN-5', opts)
            #remove, since we have defn
            @nodefn.delete(name)
          end
          mod = Module.new(jmodule)
          @mod_by_name[name] = mod
        end
      end if pairs
    end

    private
    #Get all unresolved refs (list of name)
    def get_unresolved(nm,did=nil)
      did = Hash.new unless did  #keep track of mods already done
      did[nm] = true
      mod = @mod_by_name[nm]
      if mod
        unresolved = []
        mod.get_ref_names.each do |rnm|
          unresolved += get_unresolved(rnm,did) unless did.key?(rnm)
        end unless mod.is_leaf?
      else
        unresolved = [nm]
      end
      unresolved.uniq
    end

    public
    def get_used_srcs(nm,did=nil)
      did = Hash.new unless did  #keep track of mods already done
      did[nm] = true
      mod = @mod_by_name[nm]
      if mod
        used = [mod.get_file_name]
        mod.get_ref_names.each do |rnm|
          used += get_used_srcs(rnm,did) unless did.key?(rnm)
        end unless mod.is_leaf?
      else
        used = []
      end
      used.uniq
    end

    private
    def redefined_error(name, curr, jnew_one)
      curr_loc = curr.jmodule.get_decl_loc.to_string
      jnew_loc = Util.jlocation_to_a(jnew_one.get_decl_loc)
      opts = [jnew_loc,name,curr_loc].flatten
      Message::message('E', 'DEFN-1', opts)
    end

    #Convenience wrapper around parser.Module.
    class Module
      attr_reader :jmodule

      def initialize(jmodule)
        @jmodule = jmodule
        @refs = nil #Hash of references (Module)
        #NOTE: detailed instance stuff kept on java side
        init
      end

      def get_file_name
        if @jmodule
          jloc = Util.jlocation_to_a(@jmodule.get_decl_loc)
          fn = jloc[0]
        else
          fn = nil
        end
        fn
      end

      def is_leaf?
        @refs == nil
      end

      def get_name
        @jmodule.get_name
      end

      def get_ref_names
        is_leaf? ? [] : @refs.keys
      end

      def is_resolved?
        @jmodule != nil
      end

      #Return names of unresolved references.
      #Note: does descend into children!
      def get_unresolved
        unresolved = []
        @refs.each do |nm,mod|
          unresolved << nm unless mod
        end unless is_leaf?
        return unresolved
      end

      private
      def init
        insts = @jmodule.get_instances
        insts.each do |jminst|
          refname = jminst.get_ref_name
          @refs = Hash.new unless @refs
          @refs[refname] = nil unless @refs.key?(refname)
        end if insts
      end
    end
  end

	private
	class Base
    attr_reader :jmods_by_name, :processed_srcs, :jtracker

    public
    def parse_incr(srcs)
      add_srcs(srcs)
      before = get_keys
      @jparser.parse(srcs.to_java(:string))  #explicit call of overload for incr
      r = []
      added = get_keys - before
      added.each {|k| r << [k,@jmods_by_name[k]]}
      r  #return
    end

		protected
    def init(srcs)
      @processed_srcs = []  #keep track of sources read (in order) for all
      set_mods_by_name
      add_srcs(srcs)
 		end

		protected
		def parse(srcs)
			nil  #derived class needs to implement
		end

		private
		def set_mods_by_name
      @jmods_by_name = @jtracker.get_modules_by_name
		end

    private
    def get_keys
      Util.keys_of_map(@jmods_by_name)
    end

    protected
    def add_srcs(srcs)
      @processed_srcs += srcs
      @processed_srcs.uniq!
    end
	end

  private
  class SysVlog < Base
		java_import 'parser.apfe.sv2009.SvMain'
		java_import 'apfe.vlogpp2.Helper'

		def initialize(sv,defs,incdirs)
			parse(sv,defs,incdirs)
      init(sv)  #finish constructor aft
		end

	  def parse(srcs,defs=[],incdirs=[])
	    args = []
      #TODO: +define+ are lumped from deep inside .f all the way to top.
      #This will break any redefn, as in +define+FOO=1, ..., +define+FOO=2,
      #since last will overwrite any previous, in the very beginning!
      args += Parse::prefix('-D',defs) unless defs.empty?
      args += Parse::prefix('-I',incdirs) unless incdirs.empty?
      args += srcs
      @jparser = SvMain.getParser
      SvMain.main(args)
      @jtracker = @jparser.getTracker
	  end

    def get_define_only_files(used)
      def_only_files = Helper.getTheOne.getMacroDefns.getDefineOnlyFiles(used)
      #reconcile again processed_srcs to get order correct
      rval = []
      @processed_srcs.each do |f|
        rval << f if Util::has_same_file(def_only_files, f)
      end
      rval
    end

    def get_included_files
      included = []
      files = Helper.getTheOne.getIncludeDirs.getIncludedFiles
      files.each {|k| included << k}
      included
    end
	end

  private
  class Vhdl < Base
		java_import 'parser.vhdl.Parser'
		java_import 'parser.vhdl.VhdlParser'

		def initialize(srcs)
			parse(srcs)
      init(srcs)  #finish constructor aft
		end

	  def parse(srcs)
	    @jparser = Parser.new
      @jparser.parse(srcs.to_java(:string))
      @jtracker = VhdlParser.stTracker
	  end
	end

  private
  class Slf < Base
		java_import 'parser.slf.Parser'
		java_import 'parser.slf.SlfParser'

		def initialize(srcs)
			parse(srcs)
      init(srcs)  #finish constructor aft
		end

	  def parse(srcs)
	    @jparser = Parser.new
      @jparser.parse(srcs.to_java(:string))
      @jtracker = SlfParser.stTracker
	  end
	end

  private
  def self.prefix(pfx, args)
    args.collect {|e| [pfx,e]}.flatten
  end
end
