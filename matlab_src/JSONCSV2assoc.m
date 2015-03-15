function A = JSONCSV2assoc(JSONCSVstr)
%JSONCSV2assoc: Converts a CSV string stored inside a JSON data structure to an associative array.
%IO user function.
%  Usage:
%    A = JSONCSV2assoc(JSONCSVstr)
%  Inputs:
%    JSONCSVstr = JSON data structure containing a CSV formatted string
%  Outputs:
%    A = associative array formed from JSON CSV formatted string

  % Convert str to a matlab structure.
  J = parseJSON(JSONCSVstr);

  A = CSVstr2assoc(J.CSVstring,J.rowSeparator,J.columnSeparator);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

