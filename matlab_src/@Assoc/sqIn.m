function AtA = sqIn(A)
%sqIn: Computes A.' * A efficiently.
%Associative array user function.
%  Usage:
%    AtA = sqIn(A)
%  Inputs:
%    A = associative array with numeric values
%  Outputs:
%    AtA = associative array that is the matrix multiply of A.' and A.

 % Convert to numeric.
 if isempty(A.val)
   AtA = A;
 else
   AtA = double(logical(A));
 end

 % Square numeric matrix.
 AA = Adj(AtA);
 AAtAA = AA.' * AA;

 % Insert back in associative array.
 AtA = putAdj(AtA,AAtAA);
 AtA = putRow(AtA,Col(AtA));

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

