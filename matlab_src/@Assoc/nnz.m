function n = nnz(A)
%nnz: Returns number of non-zeros in an associative array or database table.
%Associative array or database table user function.
%  Usage:
%    n = nnz(A)
%  Inputs:
%    A = associative array or database table
%  Outputs:
%    n = number of non-zero (or non-empty) entries in an associative array or database table; same as the number of triples

  n = nnz(A.A);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

