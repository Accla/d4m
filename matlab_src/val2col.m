function AA = val2col(A,splitSep)
%val2col: Append associative array values to column keys; inverse of col2type. 
%Associative array user function.
%  Usage:
%    AA = val2col(A,splitSep)
%  Inputs:
%    A = associative array with string column keys
%    splitSep = single character separator
% Outputs:
%    A = associative array with string column keys and numeric values

  [r cType cVal] = find(A);
  c = CatStr(cType,splitSep, cVal);
  AA = Assoc(r,c,1);
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
