function AT = transpose(A)
%.',transpose: Performs a matrix transpose on an associative array.
%Associative array user function.
%  Usage:
%    AT = A.'
%    AT = transpose(A)
%  Inputs:
%    A = NxM associative array
%  Outputs:
%    A = MxN associative array

  % Transpose A.
  AT = A;
  AT.col = A.row;
  AT.row = A.col; 
  AT.A = transpose(A.A);

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

