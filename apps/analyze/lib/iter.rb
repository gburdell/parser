# 
#   The MIT License
#  
#   Copyright 2006-2011 Karl W. Pfalzer.
#   Copyright 2011-     George P. Burdell
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
#A base class.
class Iter
	protected
  def next
    nil
	end

  protected
	def has_more?
    nil
	end

	protected
	def initialize
	end
end

class ArrayIter < Iter
  def initialize(ar)
    @ar = ar
    @i = 0
    @n = @ar ? @ar.length : 0
  end

  def <<(vals)
    #insert new ones before [i], so [i] returns new
    nar = @ar.insert(@i, vals.compact).flatten
    @ar = nar
    @n = @ar.length
  end

  def has_more?
    @i < @n
  end

  def next
    if has_more?
      r = @ar[@i]
      @i += 1
    else
      r = nil
    end
    return r
  end
end


