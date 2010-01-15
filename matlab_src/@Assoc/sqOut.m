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
