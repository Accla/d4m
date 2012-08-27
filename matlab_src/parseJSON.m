function data = parseJSON(string)
%parseJSON: Converts a JSON formatted string into a data structure.
%IO user function.
%  Usage:
%    data = parseJSON(string)
%  Inputs:
%    string = JSON formatted string
%  Outputs:
%    data = data structure


if exist('OCTAVE_VERSION','builtin')
   % Check if java is working.
   str = java_new('java.lang.String', string);
   jsonObj = java_new('org.json.JSONObject', str); 
else
    jsonObj = org.json.JSONObject(java.lang.String(string));    
end
  
  iter = jsonObj.keys;

  while(iter.hasNext) 
      key = iter.next;
      val = jsonObj.get(key);
      eval(sprintf('data.%s = val;', key));    
  end

end % if
