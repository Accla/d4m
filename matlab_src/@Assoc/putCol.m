function A = putCol(A,col)
%putCol: Replaces the column keys in an associative array; does no consistency checking.
%Associative array user function.
%  Usage:
%    A = putCol(A,col)
%  Inputs:
%    A = NxM associative array
%    col = string list of length M
%  Outputs:
%    A = associative array with new column keys
%  Example:
%    A = putCol(A,CatStr('Name,','|',Col(A)));    % Prepend 'Name|' to each column key.

   A.col = col;

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

