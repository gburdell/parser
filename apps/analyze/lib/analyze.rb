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
require 'message'
require 'iter'
require 'parse'
require 'util'

def errmsg_and_exit
  Message.message('E', 'ERR-2', 'Exit due to previous error(s)')
  exit 1
end

class Analyze
  VERSION = "r2.0.20"

  def initialize(argv, cmd = "analyze")
    @argv = argv
    @cmd = cmd
    @opts, @parser = nil, nil
  end

  def process
    if (2 > @argv.length)
      usage()
      exit 1
    end
    @opts = block_given? ? yield(@argv) : Opts.new(@argv)
    errmsg_and_exit if Util.has_error?
    @parser = Parse.new(@opts)
    @parser.link
    errmsg_and_exit if Util.has_error? && @opts.exit_on_err
    stat = @parser.get_status
    errmsg_and_exit if !stat && @opts.exit_on_err
    #
    #Generate output files
    Dump.tcl(@opts, @parser) if @opts.tcl
  end

  def usage
    indent = " "*(8+@cmd.length)
    usage = <<L1

  Usage: #{@cmd} -m <topModule> [option] <file>...

  Version #{VERSION}.

  Analyze one or more SystemVerilog, VHDL or Liberty <file> 
  and quick link <topModule>.

  Options:
    -h|--help           This message.
    -f <file>           Read options from <file>.
    +define+<def>       Define <macro> values for (`<macro>) Verilog-style
                          pre-processing.
                          <def> is either <macro> or <macro>=<value>.
                          <def> may be repeated, as in +define+<def1>+<def2>+...
    +incdir+<dir>       Specify directory <dir> as search path for
                          Verilog `include file(s).
                          <dir> may be repeated, as in +incdir+<dir1>+<dir2>+...
    -v <file>           Specify Verilog library <file>.
    -y <dir>            Specify directory <dir> for finding unresolved 
                          references.
                          For any missing <module>, directory <dir> will be
                          searched for a file name <module><sfx>, where <sfx>
                          is specified by +libext+<sfx>
                          option.  The matching file will then be analyzed using
                          SystemVerilog parser.
    +libext+<sfx>       Specify <sfx> for <module><sfx> matching as
                          described for -y option.
                          <sfx> may be repeated, as in +libext+<sfx1>+<sfx2>+...
    +slf+<file>         Specify Synopsys Libery (aka. ".lib") <file>.
                          Such <file> are analyzed for leaf cell definitions.
                          <file> may be repeated, as in +slf+<file1>+<file2>+...
    +vhdl+<file>        Specify a VHDL source file to analyze.
                          <file> may be repeated, 
                          as in +vhdl+<file1>+<file2>+...
    --slf <file>...     Specify Synopsys Libery (aka. ".lib") <file>s.
                          Such <file> are analyzed for leaf cell definitions.
    --vhdl <file>...    Specify VHDL source <file>s to analyze.
    -- <file>...        Subsequent <file>... read as SystemVerilog.
    --flatf <file>      Generate flattened .f file into <file> immediately
                          after option processing.
    --yvhdl <dir>       Specify directory <dir> for finding unresolved 
                          references.
                          For any missing <module>, directory <dir> will be
                          searched for a file name <module><sfx>, where <sfx>
                          is specified by +libextvhdl+<sfx>
                          option.  The matching file will then be analyzed using
                          Vhdl parser.
    +libextvhdl+<sfx>   Specify <sfx> for <module><sfx> matching as
                          described for --yvhdl option.
                          <sfx> may be repeated,
                          as in +libextvhdl+<sfx1>+<sfx2>+...
    --yslf <dir>        Specify directory <dir> for finding unresolved 
                          references.
                          For any missing <module>, directory <dir> will be
                          searched for a file name <module><sfx>, where <sfx>
                          is specified by +libextslf+<sfx>
                          option.  The matching file will then be analyzed using
                          Synopsys Liberty file (slf) parser.
    +libextslf+<sfx>    Specify <sfx> for <module><sfx> matching as
                          described for --yslf option.
                          <sfx> may be repeated,
                          as in +libextslf+<sfx1>+<sfx2>+...
    +nodefn+<mod>       Specify <mod> module name as defined elsewhere.  Such
                          <mod> are excluded from link operation: i.e., any
                          reference to <mod> will be marked as resolved.
                          <mod> may be repeated as in +nodefn+<mod1>+<mod2>+...
                          Common use would be for DesignWare parts: e.g.,
                            +nodefn+DW02_mult.
    --tcl <file>        Dump analyze details to <file> in Tcl format.
    -E <file>           Dump intermediate, pre-processor output to <file>.
    --exit_on_err       Exit if any parser errors (no output files generated).
    --excl_emit <spec>  Specify file suffixes to exclude from parsing.
                          However, any matched files will be emitted
                          into the specified <file> of "--tcl <file>".  The
                          <spec> is type:sfx, where type is sv|vhdl and sfx is 
                          a filename suffix (e.g.: .dc.vp).  Multiple sfx can be
                          specified, separated by :, as in: vhd:.sfx1:.sfx2
    --msg_lvl 0..3      Set message level.
                          0 is less verbose, 3 is most verbose. (Default: 1).
    --redefn_lvl 1..2   Set macro re-definition check level.
                          1 checks for different value, 2 checks for different
                          location (Default: 1.  2 is more conservative).
    --sfcu 0..1         Set single file compilation unit granularity for
                          determination of macro undef scope.
                          Default: 0, which enables all files as compilation
                          unit (which is less strict, but more common model).
L1
    STDERR << "#{usage}"
  end
end

class Opts
  def initialize(args)
    @more_opts = {} #hash for use by subclasses
    init(args)
    validate
  end

  attr_accessor :define

  attr_reader :top_mod, :incdir, :v, :slf, :vhdl, :sv,
              :sv_seeker, :slf_seeker, :vhdl_seeker, :nodefn, :tcl, :opt_e,
              :exit_on_err, :more_opts, :excl_emit, :msg_lvl, :redefn_lvl,
              :sfcu

  private
  def init(args)
    #Initialize instance vars in order of usage.
    @exit_on_err = false
    @top_mod = nil
    @define = []
    @incdir = []
    @v = []
    @sv_seeker = Util::FileSeeker.new([], ['.v']); #+libext+.v default
    @slf = []
    @slf_seeker = Util::FileSeeker.new
    @vhdl = []
    @vhdl_seeker = Util::FileSeeker.new
    @sv = []
    @excl_emit = {:sv => {}, :vhdl => {}}
    @excl_emit.each_key { |k| @excl_emit[k] = {:sfxs => [], :files => []} }
    #Process options
    @arg_i = OptIter.new(args)
    @file_as = :sysvlog #:vhdl or :slf
    @flatf = nil
    @nodefn = []
    @tcl = nil
    @opt_e = nil
    @msg_lvl = nil
    @redefn_lvl = nil
    @sfcu = nil
    process_args
    #Get rid of duplicates
    @define.uniq!
    @incdir.uniq!
    @v.uniq!
    @slf.uniq!
    @vhdl.uniq!
    @sv.uniq!
    #Get rid of any overlaps
    @v = @v - @sv
    #
    skim_excl_emit
    dump_outf(@flatf)
  end

  #Delimiter used to stylize location of +define+ in .f files
  public
  DEFINE_LOC_DELIM = Java::apfe.vlogpp2.MacroDefns.stDefineDelimitLoc

  private
  def dump_outf(ofn)
    return unless ofn
    Message.info(1, 'FILE-4', ofn)
    ofid = File.new(ofn, 'w')
    ofid << "// Created #{Time::now}\n//\n"
    ofid << "-m #{@top_mod}\n"
    @define.each do |d|
      d = d.split(DEFINE_LOC_DELIM)
      ofid << "+define+#{d.last}"
      ofid << " // #{d[0]}" if 1 < d.length
      ofid << "\n"
    end
    @incdir.each { |i| ofid << "+incdir+#{i}\n" }
    @v.each { |i| ofid << "-v #{i}\n" }
    @slf_seeker.dump_outf(ofid)
    @sv.each { |i| ofid << "#{i}\n" }
    @vhdl.each { |i| ofid << "+vhdl+#{i}\n" }
    @vhdl_seeker.dump_outf(ofid, 'vhdl')
    @slf.each { |i| ofid << "+slf+#{i}\n" }
    @slf_seeker.dump_outf(ofid, 'slf')
    ofid.close
  end

  private
  def get_dir
    @arg_i.get_dir
  end

  #Process args
  private
  def process_args
    while (@arg_i.has_more?)
      @ai = @arg_i.next
      case @ai
        when /^(\-h|\-\-help)$/
          usage
          exit 1
        when '-E'
          expect_arg do |v|
            unless @opt_e
              @opt_e = v
            else
              error('ARG-5', @ai)
            end
          end
        when '--msg_lvl'
          expect_arg do |v|
            unless @msg_lvl
              if ('0'..'3').member?(v)
                @msg_lvl = v.to_i
              else
                error('ARG-6', [v, @ai, '0..3'])
              end
            else
              error('ARG-5', @ai)
            end
          end
        when '--redefn_lvl'
          expect_arg do |v|
            unless @redefn_lvl
              if ('1'..'2').member?(v)
                @redefn_lvl = v.to_i
              else
                error('ARG-6', [v, @ai, '1..2'])
              end
            else
              error('ARG-5', @ai)
            end
          end
        when '--sfcu'
          expect_arg do |v|
            unless @sfcu
              if ('0'..'1').member?(v)
                @sfcu = v.to_i
              else
                error('ARG-6', [v, @ai, '0..1'])
              end
            else
              error('ARG-5', @ai)
            end
          end
        when '-m'
          expect_arg do |v|
            unless @top_mod
              @top_mod = v
            else
              error('ARG-5', @ai)
            end
          end
        when '-f'
          expect_arg_as_file do |dotf|
            @arg_i.switch_to_dotf(dotf)
          end
        when '-v'
          expect_arg_as_file { |f| @v << f }
        when '-y'
          expect_arg_as_dir { |yd| @sv_seeker.add_path(yd); }
        when '--yvhdl'
          expect_arg_as_dir { |yd| @vhdl_seeker.add_path(yd); }
        when '--yslf'
          expect_arg_as_dir { |yd| @slf_seeker.add_path(yd); }
        when '--vhdl'
          @file_as = :vhdl
        when '--slf'
          @file_as = :slf
        when '--'
          @file_as = :sysvlog
        when '--flatf'
          expect_arg do |v|
            unless @flatf
              @flatf = v if as_writable_file(v, @ai)
            else
              error('ARG-5', @ai)
            end
          end
        when '--exit_on_err'
          unless @exit_on_err
            @exit_on_err = true
          else
            error('ARG-5', @ai)
          end
        when '--tcl'
          expect_arg do |v|
            unless @tcl
              @tcl = v if as_writable_file(v, @ai)
            else
              error('ARG-5', @ai)
            end
          end
        when '--excl_emit'
          expect_arg do |spec|
            lang = spec.split(':')[0]
            sfxs = spec.sub(/^[^:]+:/, '').split(':')
            case lang
              when 'sv', 'vhdl'
                ix = lang.to_sym
                vals = @excl_emit[ix][:sfxs] + sfxs
                @excl_emit[ix][:sfxs] = vals.flatten.uniq
              else
                error('SPEC-1', [lang, 'lang', @ai, 'sv|vhdl'])
            end
          end
        when /^\+nodefn\+/
          plusarg('+nodefn+', @nodefn) { |m| m } #always append
          @nodefn.uniq!
        when /^\+libext\+/
          plusarg('+libext+') { |sfx| @sv_seeker.add_sfx(sfx) }
        when /^\+libextvhdl\+/
          plusarg('+libextvhdl+') { |sfx| @vhdl_seeker.add_sfx(sfx) }
        when /^\+libextslf\+/
          plusarg('+libextslf+') { |sfx| @slf_seeker.add_sfx(sfx) }
        when /^\+define\+/
          plusarg('+define+', @define) do |m|
            where = @arg_i.where
            m = "#{where.join(':')}#{DEFINE_LOC_DELIM}#{m}" if where
            m #block return value
          end
        when /^\+incdir\+/
          plusarg('+incdir+', @incdir) { |d| as_abs_dirname(d, '+incdir+') }
        when /^\+slf\+/
          plusarg('+slf+', @slf) { |f| as_abs_filename(f, '+slf+') }
        when /^\+vhdl\+/
          plusarg('+vhdl+', @vhdl) { |f| as_abs_filename(f, '+vhdl+') }
        else
          option_else
      end if @ai #qualify w/ valid @ai, since nil could be returned from next
    end
  end

  #Move any files matching excl suffix from filelists for later emit.
  private
  def skim_excl_emit
    {:sv => @sv, :vhdl => @vhdl}.each do |key, vals|
      vals.each do |fn|
        @excl_emit[key][:sfxs].each do |sfx|
          if File.fnmatch("*#{sfx}", fn)
            @excl_emit[key][:files] << fn
            break #dont check more suffix
          end
        end
      end
      unless @excl_emit[key][:files].empty?
        @excl_emit[key][:files].uniq!
        to_rm = @excl_emit[key][:files]
        case key
          when :sv
            @sv = @sv - to_rm
          else
            @vhdl = @vhdl - to_rm
        end
      end
    end
  end

  protected
  def option_else
    case @ai
      when /^(\-|\+)/ #invalid opts
        error('ARG-4', @ai) #TODO: add fname+lnum
        break
      else
        v = as_abs_filename(@ai)
        case @file_as
          when :sysvlog
            @sv << v
          when :vhdl
            @vhdl << v
          else
            @slf << v
        end if v
    end
  end

  #parameterized severity message w/ line number, if apropos
  private
  def smessage(svr, code, args)
    loc = @arg_i.where
    if loc
      code += '.1' #code suffix prefix's w/ location
      args = args.insert(0, loc).flatten
    end
    Message.smessage(svr, code, args)
  end

  private
  def error(code, args)
    pos = @arg_i.where
    if pos
      code += '.1'
      unless args.is_a? Array
        args = args.to_s unless args.is_a? String
        args = [args]
      end
      args = args.insert(0, pos).flatten
    end
    Message.error(code, args)
  end

  private
  def as_abs_filename(fname, of_opt=nil, svr=:warn)
    dir = get_dir
    efname = File::expand_path(fname, dir)
    if File::file?(efname) && File::readable?(efname)
      return efname
    elsif of_opt
      smessage(svr, 'FILE-5', [of_opt, fname, 'read', 'file'])
    else
      smessage(svr, 'FILE-2', [fname, 'read'])
#STDERR.puts "DBG: dir=#{dir} : efname=#{efname}\n#{caller.join("\n")}"
    end
    return nil
  end

  private
  def as_writable_file(fname, of_opt=nil)
    efname = File::expand_path(fname)
    bad = File::file?(efname) && !File::writable?(efname)
    bad = !File::writable?(File::dirname(efname)) unless bad
    if of_opt
      error('FILE-5', [of_opt, fname, 'write', 'file'])
    else
      error('FILE-2', [fname, 'write'])
    end if bad
    return bad ? nil : fname
  end

  private
  def as_abs_dirname(d, of_opt, svr=:warn)
    dir = get_dir
    edir = File::expand_path(d, dir)
    return edir if File.directory?(edir) && File.readable?(edir)
    smessage(svr, 'FILE-5', [of_opt, d, 'read', 'directory'])
    return nil
  end

  #verify required options
  protected
  def validate
    Message.error('ARG-1', '-m') unless @top_mod
    Message.error('REQD-1') unless 0 < (@v+@slf+@vhdl+@sv).length
  end

  #Yield next/expected arg or nil
  protected
  def expect_arg
    v = @arg_i.next
    Message.error('ARG-1', @ai) unless v
    yield v
  end

  #Yield iff next arg is readable
  protected
  def expect_arg_as_file
    expect_arg do |f|
      f = as_abs_filename(f, @ai)
      yield f if f
    end
  end

  #Yield iff next arg is readable dir
  protected
  def expect_arg_as_dir
    expect_arg do |d|
      f = as_abs_dirname(d, @ai)
      yield f if f
    end
  end

  #Add to list.
  #An optional block can predicate whether value
  #is accepted or not.
  protected
  def plusarg(opt, list=nil)
    @ai.sub(opt, '').split('+').each do |v|
      if v && v != ""
        if block_given?
          v = yield(v)
          list << v if v if list
        else
          list << v
        end
      end
    end
  end

  private
  #Return args based on cmdline or dotf files
  class OptIter < Iter
    def initialize(args)
      @iter = ArrayIter.new(args)
			@opts_stk = []
			@dotf = nil
    end

    def has_more?
      v = @iter.has_more?
      v = pop unless v
      return v
    end

    def next
      @iter.next
    end

    def switch_to_dotf(fname) #push
			@opts_stk.push(@iter)
      pdir = @dotf ? @dotf.dir : nil
      @dotf = Dotf.new(fname, pdir)
      @iter = @dotf
    end

    #Return [file,lnum] if dotf, else nil
    def where
      return nil unless @dotf
      return [@dotf.fname, @dotf.lnum]
    end

    #Get dir of current context
    def get_dir
      return @dotf.dir if @dotf
      return File::expand_path('.')
    end

    private
    #Pop from dotf and return has_more?
    def pop
			@iter = @opts_stk.pop
			@dotf = nil
      return @iter ? @iter.has_more? : nil
    end
  end

  private
  class Dotf < Iter
    attr_reader :fname, :lnum, :dir

    def initialize(fname, pdir=nil)
      @fname = fname
      @lnum = 0
      @dir = File::expand_path(File::dirname(fname), pdir)
      @fname = File::expand_path(@fname, pdir) if pdir
      read(@fname)
      @irow = 0
      @line = nil
      next_line if has_more?
    end

    def has_more?
      @irow < @nrows
    end

		def next
			r = get_next
#STDERR.puts "DBG3: #{@fname}: #{r}"
			return r
		end

		private
    def get_next
      return nil unless has_more?
      return @line.next if @line.has_more?
      @irow += 1
      return nil unless has_more?
      next_line
      @line.next
    end

    private
    def next_line
      @lnum = @eles[@irow][0]
      @line = ArrayIter.new(@eles[@irow][1])
    end

		private
		def get_env(ix,fname,lnum)
			return ENV[ix] if ENV.key?(ix)
			Message.error('ENV-1', [fname, lnum, ix])
			errmsg_and_exit
		end

    private
    def read(fname)
      #Read entire file into array of pair: [lnum [toks ...]]
      @eles = []
      lnum = 0
      Message.info(1, 'FILE-3', fname)
      IO.foreach(fname) do |line|
        lnum += 1
        line = line.strip.sub(/\/\/.*$/, "") #only line comments handled
        #allow ${VAR} substitutions
        line = line.gsub(/(\$\{)([^\}]+)(\})/) { get_env($2,fname,lnum) }
        #allow $VAR substitutions
				line = line.gsub(/\$([^\/]+)(\/|$)/) { get_env($1,fname,lnum)+$2}
        unless line.empty?
          leles = line.split(/\s+/)
          @eles << [lnum, leles] unless leles.empty?
        end
      end
#STDERR.puts "DBG2: fname=#{fname} : eles=#{@eles}"
      @nrows = @eles.length
    end
  end
end

class Dump
  HEADER =<<L1
@! Created by analyze #{Analyze::VERSION} on #{Time.now}
@!
L1

  public
  def self.tcl(opts, parser)
    dmp = Tcl.new(opts, parser)
    dmp.dump
  end

  protected
  class Base
    def initialize(line_cmnt, fnm, opts, parser)
      @line_cmnt, @fnm, @opts, @parser = line_cmnt, fnm, opts, parser
      @ofid = nil
    end

    public
    #Get define only files as they cover macros used in sv_files and included files.
    def get_define_only_files(sv_files, included_files)
      #Get all used
      all_used = sv_files + included_files
      @parser.get_define_only_files(all_used)
    end

    private
    #Handle case where a file containing `define is read as a source and
    #also included.  Due file ordering (and bad code style), and file B
    #could use a `foo defined in file A (read as source file).  Later,
    #file C could `include A.  Without this fix, only the included version
    #would be accounted for.  Here, we determine intersection of included sources
    #with file read and add the intersection.
    def add_includes_as_src(used_sv, used_incl)
      all_sv = (@opts.sv + used_sv).uniq
      candidates = (all_sv & used_incl) - used_sv
      return used_sv if (candidates.empty?) #nothing to do
      #return in order of original list
      new_sv = []
      todo = (candidates + used_sv).flatten
      all_sv.each do |e|
        new_sv << e if todo.find_index(e)
      end
      new_sv
    end

    public
    def dump
      ixs = {:sv => 'sysvlogSrcs', :vhdl => 'vhdlSrcs', :slf => 'libSrcs'}
      data = @parser.get_used_srcs_by_type
      included_files = @parser.get_included_files
      define_only_files = get_define_only_files(data[:sv], included_files)
      data[:sv] = define_only_files + data[:sv]
      updated_sv = add_includes_as_src(data[:sv], included_files)
      data[:sv] = updated_sv
      Message.info(1, 'FILE-4', @fnm)
      File.open(@fnm, 'w') do |f|
        @ofid = f
        f << HEADER.gsub('@!', @line_cmnt)
        dump_scalar('topModule', @opts.top_mod)
        dump_list('defines', @opts.define, true)
        dump_list('inclDirs', @opts.incdir)
        dump_list('inclFiles', included_files)
        ixs.each { |k, vnm| dump_list(vnm, {}) } #reset all
        #Excluded to emit always first
        {:sv => 'sysvlogSrcs', :vhdl => 'vhdlSrcs'}.each do |k, vnm|
          dump_list(vnm, @opts.excl_emit[k][:files], false, false)
        end
        ixs.each { |k, vnm| dump_list(vnm, data[k], false, false) }
        dump_list('unresolved', @parser.unresolved)
      end
    end
  end

  private
  class Tcl < Base
    def initialize(opts, parser)
      super('#', opts.tcl, opts, parser)
    end

    def dump_list(vnm, vals, rm_loc=false, reset=true)
      did = Hash.new
      @ofid << "\nset #{vnm} \{\}\n" if reset
      vals.each do |v|
        v = v.split(Opts::DEFINE_LOC_DELIM).last if rm_loc
        unless did.has_key?(v)
          @ofid << "lappend #{vnm} \{#{v}\}\n"
          did[v] = true
        end
      end if vals
    end

    def dump_scalar(vnm, val)
      @ofid << "\nset #{vnm} \{#{val}\}\n"
    end
  end
end
