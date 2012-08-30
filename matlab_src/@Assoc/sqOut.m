function AAt = sqOut(A)
%sqOut: Computes A * A.' efficiently.
%Associative array user function.
%  Usage:
%    AAt = sqOut(A)
%  Inputs:
%    A = associative array with numeric values
%  Outputs:
%    AAt = associative array that is the matrix multiply of A and A.'.

 % Convert to numeric.
 if isempty(A.val)
   AAt = A;
 else
   AAt = double(logical(A));
 end


 % Square numeric matric.
 AA = Adj(AAt);
 AAAAt = AA * AA.';

 % Insert back in associative array.
 AAt = putAdj(AAt,AAAAt);
 AAt = putCol(AAt,Row(AAt));

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

