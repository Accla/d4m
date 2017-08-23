function TsvStr = CSVstr2TSVstr(CsvStr)
%CSVstr2TSVstr: Converts a CSV formatted string to TSV formatted str.
%IO user function.
%  Usage:
%    TsvStr = CSVstr2assoc(CsvStr)
%  Inputs:
%    CsvStr = CSV formatted string
%  Outputs:
%    TsvStr = TSV formatted string


  nl = char(10);
  comma = ',';
  quote = char(34);
  tab = char(9);

  xcomma = double(CsvStr == comma);
  xquote = double(CsvStr == quote);
  iquote = find(xquote);
  xquote(iquote(2:2:end)) = -1;
  Xquote = cumsum(xquote);

  TsvStr = CsvStr;

  TsvStr(not(Xquote) & xcomma) = tab;

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

