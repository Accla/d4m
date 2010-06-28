function AAt = sqOut(A)
%SQOUT computes A * A.' effciently.

 % Convert to numeric.
 if not(isempty(A.val))
   AAt = A;
 else
   AAt = logical(A);
 end

 % Square numeric matric.
 AA = Adj(AAt);
 AAAAt = AA * AA.';

 % Insert back in associative array.
 AAt = putAdj(AAt,AAAAt);
 AAt = putCol(AAt,Row(AtA));

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

