function A = JSONCSV2assoc(JSONCSVstr)

  % Convert str to a matlab structure.
  J = parseJSON(JSONCSVstr);

  A = CSVstr2assoc(J.CSVstring,J.rowSeparator,J.columnSeparator);

end

