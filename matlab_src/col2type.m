function AA =col2type(A,splitSep)
%col2type: Splits column keys of associative array and stores first part as column key and second part as value; inverse of val2col. 
%Associative array user function.
%  Usage:
%    AA =col2type(A,splitSep)
%  Inputs:
%    A = associative array with string column keys
%    splitSep = single character separator
% Outputs:
%    A = associative array with string column keys and string values

  [r c v] = find(A);
  [cType cVal] = SplitStr(c,splitSep);
  AA = Assoc(r,cType,cVal);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
