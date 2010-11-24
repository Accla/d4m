function data = parseJSON(string)

  jsonObj = org.json.JSONObject(java.lang.String(string));
  iter = jsonObj.keys;

  while(iter.hasNext) 
      key = iter.next;
      val = jsonObj.get(key);
      eval(sprintf('data.%s = val;', key));    
  end

end % if
