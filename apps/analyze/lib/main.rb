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
require 'analyze'
require 'instances'

#Extend analyze to add instance dumping

class Main < Analyze
  def initialize(argv)
    super(argv)
  end

  def usage
    super
    usage=<<L1
    --inst_tcl <file>        Dump {reference instance} pair to <file> in 
                               Tcl format.  Can use --inst_rex to dump only 
                               specific reference(s).
    --inst_rex <rex|@file>   Only dump instances matching (Ruby-style)
                               regular expression <rex>.  @file is file
                               containing single-line regular expression.
L1
    STDERR << "#{usage}"
  end

  def process
    super {|argv| @opts = MainOpts.new(argv)}
    ofn = @opts.more_opts[:inst_tcl]
    rex = @opts.more_opts[:inst_rex]
    if rex
      if rex =~ /^@(.+)/
        nrex = ""
        IO.foreach($1) {|ln| nrex += ln}
        rex = nrex.strip
      end
      rex = Regexp.new(rex)
      puts "Info: inst_rex: #{rex}"
    end
    insts = Instances.new(@opts.top_mod, @parser.get_trackers)
    unless ofn.nil?
      Message.message('I', 'FILE-4', ofn)
      File.open(ofn, 'w') do |f|
        f << Dump::HEADER.gsub('@!','#')
        vnm = "refInsts"
        f.puts "set #{vnm} \{\}"
        insts.foreach do |refnm,jinst|
          if (rex.nil? or rex.match(refnm))
            f.puts "lappend #{vnm} \{#{refnm} #{jinst.get_full_name}\}"
          end
        end
      end
    end
  end
end

class MainOpts < Opts
  def initialize(argv)
    super(argv)
  end

  def option_else
    case @ai
      when '--inst_tcl'
        expect_arg do |v|
          unless @more_opts[:inst_tcl]
            @more_opts[:inst_tcl] = v if as_writable_file(v, @ai)
          else
            error('ARG-5',@ai)
          end
        end
      when '--inst_rex'
        expect_arg do |v|
          unless @more_opts[:inst_rex]
            @more_opts[:inst_rex] = v
          else
            error('ARG-5',@ai)
          end
        end
    else
      super
    end
  end
end

#
# START of main
#
# To just use analyze w/o --inst_tcl,do
#   main = Analyze.new(ARGV)
main = Main.new(ARGV)
main.process

exit 0
