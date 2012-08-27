function A = putAssoc(row,col,val,AdjA)
%putAssoc: Constructs all elements of an associative array from its components.
%Associative array utility function.
%  Usage:
%    A = putAssoc(row,col,val,AdjA))
%  Inputs:
%    row = string list of N row keys (or empty)
%    col = string list of M col keys (or empty)
%    val = string list of nnz(AdjA) values (or empty)
%    AdjA = sparse matrix connecting rows, columns, and values
%  Outputs:
%    A = associative array
%  Example:
%    A = putAssoc('r1,r2,','c1,c2,c3,','v1,',ones(2,3))

  A = Assoc('','','');
  A = putRow(A,row);
  A = putCol(A,col);
  A = putVal(A,val);
  A = putAdj(A,AdjA);
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

