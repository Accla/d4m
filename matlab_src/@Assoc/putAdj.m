function A = putAdj(A,AA)
%putAdj: Replaces the adjacency matrix in an associative array; does no consistency checking.
%Associative array user function.
%  Usage:
%    A = putAdj(A,AA)
%  Inputs:
%    A = associative array
%    AA = sparse matrix connecting rows, columns, and values
%  Outputs:
%    A = associative array with a new adjacency matrix
%  Example:
%    A = putAdj(A,2.*Adj(A));    % Multiply each entry in adjacency matrix by 2.

   A.A = AA;

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

