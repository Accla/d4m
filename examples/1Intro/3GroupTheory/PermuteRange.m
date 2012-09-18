function Afunc = PermuteRange(AfuncRange);
% Take a table of ranges and creates all permutations.

  AfuncCnt = sum(dblLogi(AfuncRange),1);  % Count permuations in each column.
  Nfunc = prod(Adj(AfuncCnt));  % Compute total number of permutations.

  AfuncRangeAdj = Adj(AfuncRange);  % Get adj of range.
  Ncol = size(AfuncRangeAdj,2);  % Get sizes.

  % Initialize adj.
  AfuncRangeAdjCol = AfuncRangeAdj(:,1);
  AfuncAdj = full(AfuncRangeAdjCol(AfuncRangeAdjCol ~= 0));

  % Create all permutations based on ranges.
  for icol = 2:Ncol
    AfuncRangeAdjCol = AfuncRangeAdj(:,icol);   % Get a column.
    AfuncRangeAdjCol = full(AfuncRangeAdjCol(AfuncRangeAdjCol ~= 0));  % Restrict to non-zero.
    NicolRep = size(AfuncRangeAdjCol,1);   
    AfuncAdj0 = repmat(AfuncAdj,size(AfuncRangeAdjCol,1),1);
    AfuncAdj1 = reshape(transpose(repmat(AfuncRangeAdjCol,1,size(AfuncAdj,1))),size(AfuncAdj0,1),1);
    AfuncAdj = [AfuncAdj0 AfuncAdj1];
  end

  Afunc = putAdj(AfuncRange,AfuncAdj);
  AfuncRow = sprintf('%5.5g,',1:Nfunc);
  AfuncRow(AfuncRow == ' ') = '0';
  Afunc = reAssoc(putRow(Afunc,AfuncRow));

return
end
