function AfuncIO = IOTest(AfuncI,AfuncO)

AfuncIsub = AfuncI(Row(AfuncO),:);
AfuncOsub = AfuncO(Row(AfuncI),:);
AfuncIsub = dblLogi(putCol(AfuncIsub,strrep(Col(AfuncIsub),'I/','')));
AfuncOsub = dblLogi(putCol(AfuncOsub,strrep(Col(AfuncOsub),'O/','')));
AfuncIsubSize = size(AfuncIsub);
AfuncOsubSize = size(AfuncOsub);


r = '';  c = '';  v= '';
for iFuncPlus=1:AfuncIsubSize(1)
  for iFuncMult=1:AfuncOsubSize(1)
    fI = AfuncIsub(iFuncPlus,:);
    gI =AfuncIsub(iFuncMult,:);
    gO = AfuncOsub(iFuncMult,:);
    if (nnz(fI) & nnz(gO) & nnz(gI))
      fIgO = (noRow(fI) & noRow(gO));
      if nnz(fIgO)
        for iO=1:nnz(fIgO)
          gImO = gI - gI(1,Col(fIgO(1,iO)));
          if nnz(gImO)
            for iI=1:nnz(gImO)
              r = [r Row(fI)]; c = [c Row(gI)];  v = [v CatStr(['O/' Col(fIgO(1,iO))],' ',['I/' Col(gImO(1,iI))])];
            end
          end
        end
      end
    end
  end
end

AfuncIO = Assoc(r,c,v,@AssocCatStrFunc);

return
end
