function J = Assoc2JSONCSV(A,rowSep,colSep,name)
%Assoc2JSONCSV: Converts an associative array to a JSON formatted CSV string.
%IO user function.
%  Usage:
%    J = Assoc2JSONCSV(A,rowSep,colSep,name)
%  Inputs:
%    A = associative array
%    rowSep = character used to separate rows; usually newline (char(10))
%    colSep = character used to separate columns; usually comma (',')
%    name = string name of object
%  Outputs:
%    J = JSON formatted string with an associative array formatted as CSV string inside it

%Converts an associative array to a JSONCSV str.

  % Convert Assoc to CSV string.
  CsvStr = Assoc2CSVstr(A,rowSep,colSep);

  % Get sizes.
  s = size(A);
  Nrows = num2str(s(1));  Ncolumns = num2str(s(2));
  Nentries = num2str(nnz(A));

  % Correct any characters that need to be escaped.
  nl = char(10);
  if (rowSep == nl)
    rowSep = '\n';
    CsvStr = strrep(CsvStr,nl,rowSep);
  end

  J = ['{"name":"' name '",' ...
    '"rowSeparator":"' rowSep '","columnSeparator":"' colSep '",' ...
    '"Nrows":' Nrows ',"Ncolumns":' Ncolumns ',"Nentries":' Nentries ',' ...
    '"CSVstring":"' CsvStr '"}'];

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

