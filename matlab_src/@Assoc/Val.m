function v = Val(A)
%Val: Returns the unique column values of an associative array in sorted order.
%Associative array user function.
%  Usage:
%    v = Val(A)
%  Inputs:
%    A = associative array with M unique values
%  Outputs:
%    v = length M string list or numeric column vector

%VAL returns unique values of associative array.
   if not(isempty(A.val))
     v = A.val;
   else
     v = full(unique(A.A));
     v = v(1:end);
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

