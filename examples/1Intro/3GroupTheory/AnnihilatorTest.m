function AfuncAnnihilator = AnnihilatorTest(Afunc);
% Test functions in Afunc to see if they have an annihilator elemetn.

  v1 = 10;
  Aannihilator = Assoc(['NaN,-Inf,+Inf,'],'v2,',[NaN -Inf +Inf]);
  Nannihilator = numel(Aannihilator);

  AfuncAnnihilator = Assoc('','','');

  for irow = 1:Nannihilator
     v2 = Val(Aannihilator(irow,'v2,'));
     A2 = Assoc(Row(Afunc),'v12,',v2);
     A1 = AfuncEval(Afunc,v1,v2);
     AfuncAnnihilator = AfuncAnnihilator + putVal(putCol(EqualTest(A1,A2),['O/' Row(Aannihilator(irow,'v2,'))]),'O,');
  end

return
end





