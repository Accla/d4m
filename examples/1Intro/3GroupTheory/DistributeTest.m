function AfuncDistribute = DistributeTest(AfuncPlus,AfuncMult);
% Test functions in Afunc to see if they distribute.

  AdistributeRange = noRow(ReadCSV('./Range/DistributeRange.csv'));   % Read in function ranges.
  Adistribute = str2num(PermuteRange(AdistributeRange));
%  displayFull(Adistribute)
  AdistributeSize = size(Adistribute);
  Nrow = AdistributeSize(1);

  AfuncDistribute = Assoc('','','');

  NfuncPlus= size(AfuncPlus);  NfuncPlus = NfuncPlus(1);
  NfuncMult= size(AfuncMult);  NfuncMult = NfuncMult(1);

  for iFuncMult=1:NfuncMult
    disp([num2str(iFuncMult) ' / ' num2str(NfuncMult)])
    AfuncMulti = AfuncMult(iFuncMult,:);
%    displayFull(AfuncMulti);
    AfuncMulti = putAdj(AfuncMulti,repmat(Adj(AfuncMulti),NfuncPlus,1));
    AfuncMulti = putRow(AfuncMulti,Row(AfuncPlus));
    for irow = 1:Nrow
      v1 = Val(Adistribute(irow,'v1,'));
      v2 = Val(Adistribute(irow,'v2,'));
      v3 = Val(Adistribute(irow,'v3,'));

      A1 = AfuncEval(AfuncPlus,v2,v3);
      A1 = AfuncEval(AfuncMulti,v1,A1);
      A21 = AfuncEval(AfuncMulti,v1,v2);
      A22 = AfuncEval(AfuncMulti,v1,v3);
      A2 = AfuncEval(AfuncPlus,A21,A22);

      AfuncDistribute = AfuncDistribute + putCol(EqualTest(A1,A2),Row(AfuncMult(iFuncMult,:)));
    end
  end

  AfuncDistribute = (AfuncDistribute == Nrow);  

return
end





