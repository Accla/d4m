function AfuncIdentity = IdentityTest(Afunc);
% Test functions in Afunc to see if they have an identity elemetn.

  v1 = 10;
  Aidentity = Assoc(['NaN,-Inf,+Inf,'],'v2,',[NaN -Inf +Inf]);
  Nidentity = numel(Aidentity);

  AfuncIdentity = Assoc('','','');

  for irow = 1:Nidentity
     v2 = Val(Aidentity(irow,'v2,'));
     A1 = Assoc(Row(Afunc),'v12,',v1);
     A2 = AfuncEval(Afunc,v1,v2);
     AfuncIdentity = AfuncIdentity + putVal(putCol(EqualTest(A1,A2),['I/' Row(Aidentity(irow,'v2,'))]),'I,');
  end

return
end





