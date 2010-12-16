function data = parseJSON(string)

if exist('OCTAVE_VERSION','builtin')
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
