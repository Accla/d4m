function data = parseJSON(string)
% DATA = P_JSON(string)
% This function parses a JSON string and returns a cell array with the
% parsed data. JSON objects are converted to structures and JSON arrays are
% converted to cell arrays.
%
%  =========================================================
%  ***  HIGHLY PORTABLE VERSION ***
%
%     based on work from:
%
% http://json-schema.org/
%      &
% http://www.mathworks.com/matlabcentral/fileexchange/
%
% F.Glineur  23393-another-json-parser: Faster, Clearer & More robust than #20565
% J.Feenstra 20565-json-parser
%
%  ---------------------------------------------------------
% parts (c) Nedialko Krouchev  Nov.2009
%

%  =========================================================
global pos inStr len  esc index_esc len_esc

pos = 1; len = length(string); inStr = string;

% String delimiters and escape chars identified to improve speed:
esc = find(inStr=='"' | inStr=='\' ); % comparable to: regexp(inStr, '["\\]');
index_esc = 1; len_esc = length(esc);

if pos <= len
    switch(next_char)
        case '{'
            data = parse_object;
        case '['
            data = parse_array;
        otherwise
            error_pos('Outer level structure must be an object or an array');
    end
end % if

    function object = parse_object
        parse_char('{');
        object = [];
        if next_char ~= '}'
            while 1
                str = parseStr;
                if isempty(str)
                    error_pos('Name of value at position %d cannot be empty');
                end
                parse_char(':');
                val = parse_value;
                eval( sprintf( 'object.%s  = val;', valid_field(str) ) );
                if next_char == '}'
                    break;
                end
                parse_char(',');
            end
        end
        parse_char('}');
%   end ----------------------------------------------------------------

    function object = parse_array
        parse_char('[');
        object = cell(0, 1);
        if next_char ~= ']'
            while 1
                val = parse_value;
                object{end+1} = val;
                if next_char == ']'
                    break;
                end
                parse_char(',');
            end
        end
        parse_char(']');
%   end ----------------------------------------------------------------

    function parse_char(c)
global pos inStr len
        skip_whitespace;
        if pos > len | inStr(pos) ~= c
            error_pos(sprintf('Expected %c at position %%d', c));
        else
            pos = pos + 1;
            skip_whitespace;
        end
%   end ----------------------------------------------------------------

    function c = next_char
global pos inStr len
        skip_whitespace;
        if pos > len
            c = [];
        else
            c = inStr(pos);
        end
%   end ----------------------------------------------------------------

    function skip_whitespace
global pos inStr len
        while pos <= len & isspace(inStr(pos))
            pos = pos + 1;
        end
%   end ----------------------------------------------------------------
     function str = parseStr
global pos inStr len  esc index_esc len_esc
     % len, ns = length(inStr), keyboard
        if inStr(pos) ~= '"'
            error_pos('String starting with " expected at position %d');
        else
            pos = pos + 1;
        end
        str = '';
        while pos <= len
            while index_esc <= len_esc & esc(index_esc) < pos
                index_esc = index_esc + 1;
            end
            if index_esc > len_esc
                str = [str inStr(pos:len)];
                pos = len + 1;
                break;
            else
                str = [str inStr(pos:esc(index_esc)-1)];
                pos = esc(index_esc);
            end
nstr = length(str); switch inStr(pos)
                case '"'
                    pos = pos + 1;
                    return;
                case '\'
                    if pos+1 > len
                        error_pos('End of file reached right after escape character');
                    end
                    pos = pos + 1;
                    switch inStr(pos)
                        case {'"' '\' '/'}
                            str(nstr+1) = inStr(pos);
                            pos = pos + 1;
                        case {'b' 'f' 'n' 'r' 't'}
                            str(nstr+1) = sprintf(['\' inStr(pos)]);
                            pos = pos + 1;
                        case 'u'
                            if pos+4 > len
                                error_pos('End of file reached in escaped unicode character');
                            end
                            str(nstr+(1:6)) = inStr(pos-1:pos+4);
                            pos = pos + 5;
                    end
                otherwise % should never happen
                    str(nstr+1) = inStr(pos), keyboard
                    pos = pos + 1;
            end
        end
        error_pos('End of file while expecting end of inStr');
%   end ----------------------------------------------------------------

    function num = parse_number
global pos inStr len
        [num, one, err, delta] = sscanf(inStr(pos:min(len,pos+20)), '%f', 1);
        if ~isempty(err)
            error_pos('Error reading number at position %d');
        end
        pos = pos + delta-1;
%   end ----------------------------------------------------------------

    function val = parse_value
global pos inStr len
true = 1; false = 0;

        switch(inStr(pos))
            case '"'
                val = parseStr;
                return;
            case '['
                val = parse_array;
                return;
            case '{'
                val = parse_object;
                return;
            case {'-','0','1','2','3','4','5','6','7','8','9'}
                val = parse_number;
                return;
            case 't'
                if pos+3 <= len & strcmp(lower(inStr(pos:pos+3)), 'true')
                    val = true;
                    pos = pos + 4;
                    return;
                end
            case 'f'
                if pos+4 <= len & strcmp(lower(inStr(pos:pos+4)), 'false')
                    val = false;
                    pos = pos + 5;
                    return;
                end
            case 'n'
                if pos+3 <= len & strcmp(lower(inStr(pos:pos+3)), 'null')
                    val = [];
                    pos = pos + 4;
                    return;
                end
        end
        error_pos('Value expected at position %d');
%   end ----------------------------------------------------------------

    function error_pos(msg)
global pos inStr len
        poShow = max(min([pos-15 pos-1 pos pos+20],len),1);
        if poShow(3) == poShow(2)
            poShow(3:4) = poShow(2)+[0 -1];  % display nothing after
        end
        msg = [sprintf(msg, pos) ': ' ...
        inStr(poShow(1):poShow(2)) '<error>' inStr(poShow(3):poShow(4)) ];
        error( ['JSONparser:invalidFormat: ' msg] );
%   end ----------------------------------------------------------------

    function str = valid_field(str)
    % From MATLAB doc: field names must begin with a letter, which may be
    % followed by any combination of letters, digits, and underscores.
    % Invalid characters will be converted to underscores, and the prefix
    % "s_" will be added if first character is not a letter.
        if ~isletter(str(1))
            str = ['s_' str];
        end
        str(~isletter(str) & ~('0' <= str & str <= '9')) = '_';
%   end ----------------------------------------------------------------

% end  % of p0json (main)


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Copyright (c) 2009, Nedialko
% All rights reserved.
% 
% Redistribution and use in source and binary forms, with or without 
% modification, are permitted provided that the following conditions are 
% met:
% 
%     * Redistributions of source code must retain the above copyright 
%       notice, this list of conditions and the following disclaimer.
%     * Redistributions in binary form must reproduce the above copyright 
%       notice, this list of conditions and the following disclaimer in 
%       the documentation and/or other materials provided with the distribution
%       
% THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
% AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
% IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
% ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
% LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
% CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
% SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
% INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
% CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
% ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
% POSSIBILITY OF SUCH DAMAGE.

