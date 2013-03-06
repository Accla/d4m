function A = Abs0(A)
%Abs0: Converts associative array adjacency matrix to a double of ones and zeros.
%Equivalent to the zero norm.
%Associative array user function.
%  Usage:
%    A = Abs0(A)
%  Inputs:
%    A = associative array
%  Outputs:
%    A = associative array with numeric values of 1 or 0.

  A = double(logical(A));

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

