function AB = CatValMul(A,B);
% Perform matrix multiply and concatenate colliding values.

  AB = A*B;
  if ((IsClass(A,'Assoc') && ischar(Val(A))) && (IsClass(B,'Assoc') && ischar(Val(B))))

    % Trim down to just what we need.
    A1 = A(Row(AB),:);     B1 = B(:,Col(AB));
    A2 = A1(:,Row(B1));    B2 = B1(Col(A1),:);
    A2size = size(A2);     B2size = size(B2);

    r = '';   c = '';   v = '';
    % Loop over each column of B2.
    for i = 1:B2size(2)
      B2col = B2(:,i);   % Get column.
      cc = Col(B2col);
      vMax = ['~' cc(end)];

      % Get relevant columns from A2.
      A2sub = A2(:,Row(B2col));
      A2subSize = size(A2sub);

      % Transpose and replicate B2 column.
      B2sub = transpose(B2col);
      B2sub = putRow(B2sub,Row(A2sub));
      B2sub = putAdj(B2sub,repmat(Adj(B2sub),A2subSize(1),1));

      % Find collisions and append A values to list.
      [rA tmp vA]  = find(A2sub & putVal(dblLogi(B2sub),vMax));
      [r c v] = CatTriple(r,c,v,rA,repmat(cc,1,NumStr(rA)),vA);

      % Find collisons and append B values to list.
      [rB tmp vB]  = find(putVal(dblLogi(A2sub),vMax) & B2sub);
      [r c v] = CatTriple(r,c,v,rB,repmat(cc,1,NumStr(rB)),vB);

    end

   AB = Assoc(r,c,v,@AssocCatStrFunc);

  else
    AB = A*B;
  end

end 
