function r = Row(A)
%Row: Returns row keys of an associative array.
%Associative array user function.
%  Usage:
%    c = Row(A)
%  Inputs:
%    A = NxM associative array
%  Outputs:
%    Row = length N string list or index column vector

   [N M] = size(A.A);
   if not(isempty(A.row))
     r = A.row;
   else
     r = 1:N;
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

