function A = ReadCSV(fname);
%ReadCSV: Reads a CSV (or TSV) file into an associative array.
%IO user function.
%  Usage:
%    A = ReadCSV(fname)
%  Inputs:
%    fname = CSV or TSV formatted file
%  Outputs:
%    A = associative array of file, with 1st column as row keys, and 1st row as column keys

  [row col val] = FindCSV(fname);

  A = Assoc(row,col,val);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

