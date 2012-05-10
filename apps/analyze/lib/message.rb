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
# Defer messages to central parser.MessageMgr.
#
require 'jinit'
	
class Message
  java_import 'parser.MessageMgr'

  attr_accessor :verbose
  @@verbose = 0

  new_msgs = "#{Initialize::TOOL_ROOT}/apps/analyze/messages.txt"
  MessageMgr.addMessages(new_msgs)
  
  def self.warning(code, args)
    Message.message('W', code, args)
  end
  
  def self.error(code, args=[])
    Message.message('E', code, args)
  end

  def self.smessage(type, code, args)
    svr = (type==:info) ? 'I' : ((type==:warn) ? 'W' : 'E')
    message(svr, code, args)
  end

  def self.message(severity, code, args)
    jargs = args.to_java
    MessageMgr.message(severity, code, jargs)
  end

  def self.fatal(msg)
    STDERR << "Fatal: #{msg}\n"
    exit 666
  end
end    
