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
require 'message'

module Util
  DEBUG = false;

  #Return true if list contains same file
  def Util.has_same_file(list, file)
    list.each do |f|
      return true if File.identical?(f, file)
    end
    false
  end
    
  #Cant get keys directly from java.HashMap
  def Util.keys_of_map(map)
    return [] unless map
    r = []
    map.each {|k,v| r << k}
    return r
  end

  def Util.debug(msg)
    STDOUT << "DBG#{msg}\n" if DEBUG
  end

  #Has there been any errors?
  def Util.has_error?
    0 < Message::MessageMgr.getErrorCnt
  end

  #Convert parser.Location to [string,int]
  def Util.jlocation_to_a(jloc)
    [jloc.get_filename,jloc.get_lineno]
  end

  #Utility for searching for file using paths and suffixes
  class FileSeeker
    attr_reader :paths, :sfxs

    def initialize(paths=[],sfxs=[])
      @paths = paths
      @sfxs = sfxs
      #did will track filename found/returned (key).
      #value will be triplet of [module,path,sfx] which generated it
      @did = Hash.new
    end

    def add(paths,sfxs)
      @paths = (@paths + paths).uniq
      @sfxs  = (@sfxs + sfxs).uniq
    end

    def add_path(path)
      add([path],[])
    end

    def add_sfx(sfx)
      add([],[sfx])
    end

    def dump_outf(ofid,vsfx=nil)
      sw = vsfx ? "--y#{vsfx}" : '-y'
      @paths.each {|i| ofid << "#{sw} #{i}\n"}
      @sfxs.each  {|i| ofid << "+libext#{vsfx}+#{i}\n"}
    end

    #Return next valid file
    def get_next(mod)
      rval = nil
      @paths.each do |path|
        @sfxs.each do |sfx|
          fn = File::expand_path("#{mod}#{sfx}", path)
          if File::readable?(fn)
            @did[fn] = [mod,path,sfx]
            rval = fn
          else
            Message::message('W', 'FILE-6', [fn,path,sfx])
          end if File::file?(fn) unless @did.key?(fn)
          break if rval
        end
        break if rval
      end
      rval
    end
    
  end
end
