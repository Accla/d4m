function AB = CatKeyMul(A,B);
% Performance matrix multiply and store colliding row/col in value.

  AB = A*B;
  if ((IsClass(A,'Assoc') && ischar(Col(A))) && (IsClass(B,'Assoc') && ischar(Row(B))))

    % Trim down to just what we need.
     A1 = A(Row(AB),:);       B1 = B(:,Col(AB));
     A2 = A1(:,Row(B1));     B2 = B1(Col(A1),:);

     A2size = size(A2);     B2size = size(B2);

    r = '';   c = '';   v = '';
    % Loop over each column of B2.
    for i = 1:B2size(2)
        B2col = B2(:,i);

       % Get columns that have a collision with B2col rows.
        [rr vv tmp] = find(A2(:,Row(B2col)));
        cc = Col(B2col);
        cc = repmat(cc,1,NumStr(rr));

        % Append.
        [r c v] = CatTriple(r,c,v,rr,cc,vv);

    end

   AB = Assoc(r,c,v,@AssocCatStrFunc);

  else
    AB = A*B;
  end

end 
