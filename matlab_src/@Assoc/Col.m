function c = Col(A)
%Col: Returns column keys of an associative array.
%Associative array user function.
%  Usage:
%    c = Col(A)
%  Inputs:
%    A = NxM associative array
%  Outputs:
%    col = length M string list or index column vector

   [N M] = size(A.A);
   if not(isempty(A.col))
     c = A.col;
   else
     c = 1:M;
   end
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

