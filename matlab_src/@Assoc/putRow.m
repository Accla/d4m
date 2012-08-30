function A = putRow(A,row)
%putRow: Replaces the row keys in an associative array; does no consistency checking.
%Associative array user function.
%  Usage:
%    A = putRow(A,row)
%  Inputs:
%    A = NxM associative array
%    row = string list of length N
%  Outputs:
%    A = associative array with new row keys
%  Example:
%    A = putRow(A,CatStr('Name,','|',Row(A)));    % Prepend 'Name|' to each row key.

   A.row = row;

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

