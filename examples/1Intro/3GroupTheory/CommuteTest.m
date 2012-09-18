function AfuncCommute = CommuteTest(Afunc);
% Test functions in Afunc to see if they commute.

  AcommuteRange = noRow(ReadCSV('./Range/CommuteRange.csv'));   % Read in function ranges.
  Acommute = str2num(PermuteRange(AcommuteRange));
  AcommuteSize = size(Acommute);
  Nrow = AcommuteSize(1);

%  displayFull(Acommute);

  AfuncCommute = Assoc('','','');

  for irow = 1:Nrow
     v1 = Val(Acommute(irow,'v1,'));
     v2 = Val(Acommute(irow,'v2,'));
     A1 = AfuncEval(Afunc,v1,v2);
     A2 = AfuncEval(Afunc,v2,v1);
     AfuncCommute = AfuncCommute + putCol(EqualTest(A1,A2),'commute,');
  end

  AfuncCommute = (AfuncCommute == Nrow);  

return
end





