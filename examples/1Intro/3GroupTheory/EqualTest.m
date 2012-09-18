function Av12 = EqualTest(A1,A2);
% See where A1 and A2 are the same.

  [r c v1] = find(A1);
  [r c v2] = find(A2);

  v12 = v1;   v12(:) = 0;   % Initialize v12.
  v12(v1 == v2) = 1;
  v12(isnan(v1) & isnan(v2)) = 1;
  Av12 = reAssoc(Assoc(r,'same,',v12));

return
end





