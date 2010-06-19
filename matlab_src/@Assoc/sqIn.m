function AtA = sqIn(A)
%SQIN computes A.' * A efficiently.

 % Convert to numeric.
 if isempty(A.val)
   AtA = A;
 else
   AtA = double(logical(A));
 end

 % Square numeric matrix.
 AA = Adj(AtA);
 AAtAA = AA.' * AA;

 % Insert back in associative array.
 AtA = putAdj(AtA,AAtAA);
 AtA = putRow(AtA,Col(AtA));

end
