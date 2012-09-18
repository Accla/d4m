function AfuncAssociate = AssociateTest(Afunc);
% Test functions in Afunc to see if they associate.

  AassociateRange = noRow(ReadCSV('./Range/AssociateRange.csv'));   % Read in function ranges.
  Aassociate = str2num(PermuteRange(AassociateRange));
%displayFull(Aassociate)
  AassociateSize = size(Aassociate);
  Nrow = AassociateSize(1);

  AfuncAssociate = Assoc('','','');

  for irow = 1:Nrow
    v1 = Val(Aassociate(irow,'v1,'));
    v2 = Val(Aassociate(irow,'v2,'));
    v3 = Val(Aassociate(irow,'v3,'));

    A1 = AfuncEval(Afunc,v1,v2);
    A1 = AfuncEval(Afunc,A1,v3);
    A2 = AfuncEval(Afunc,v2,v3);
    A2 = AfuncEval(Afunc,v1,A2);

    AfuncAssociate = AfuncAssociate + putCol(EqualTest(A1,A2),'associate,');
  end

  AfuncAssociate = (AfuncAssociate == Nrow);  

return
end





