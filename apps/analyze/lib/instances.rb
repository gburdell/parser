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
require 'parse'

class Instances
  java_import 'parser.Instances'

  def initialize(top_module, trackers)
    @instances = Instances.new(top_module, trackers)
  end

  def dump_insts(ofid = STDOUT)
    foreach do |ref_nm, jinst|
      ofid.puts "#{ref_nm} : #{jinst.get_full_name}"
    end
  end

  #Iterate (depth-first) returns |ref_name,jinst|
  #If ref_name is acceptable, use jinst.get_full_name.
  def foreach
    while @instances.has_more
      jmodule_inst = @instances.next
      ref_name = jmodule_inst.get_ref_name
      yield(ref_name,@instances)
    end
  end
end
