function AB = CatKeySqInNoDiag(A);
% Perform matrix multiply and concatenate colliding row/col keys into the value.
  
   B = A;   A = transpose(A);


  if ((IsClass(A,'Assoc') && ischar(Col(A))) && (IsClass(B,'Assoc') && ischar(Row(B))))
    AB = Abs0(A)*Abs0(B);
    AB = putAdj(AB,Adj(AB) - diag(diag(Adj(AB))));
    AB = AB(find(sum(Adj(AB),2)),find(sum(Adj(AB),1)).');

    % Trim down to just what we need.
    A1 = A(Row(AB),:);     B1 = B(:,Col(AB));
    A2 = A1(:,Row(B1));    B2 = B1(Col(A1),:);
    A2size = size(A2);     B2size = size(B2);

    A2adj = Adj(A2);
    A2colMat = Str2mat(Col(A2));

    B2adj = Adj(B2);
    clear A1 A2 B1 B2

    v = zeros(0,1);
    % Loop over each column of B2.
    for i = 1:B2size(2)

       % Get columns that have a collision with B2col rows.
       [rrr tmp tmp] = find(B2adj(:,i));       
       [rrrr vvvv tmp] = find(A2adj(:,rrr));
%       [rrrr vvvv tmp] = find(A2adj(:,rrr(rrr ~= i)));
       [tmp rrrri] = sort(rrrr);
       vv = rrr(vvvv(rrrri));

       % Append.
       v = [v; vv];

    end
    clear A2adj B2adj tmp rrr rrrr vvvv rrrri vv


    v = Mat2str(A2colMat(v,:));
    
    [ABr ABc ABv] = find(Adj(AB));    % Get indices of AB;

    ABvCum = cumsum(ABv);     % Accumulate to find concatenation ends.

    % Set concatenation separator.
    sep = v(end);
    catSep = ';';
    if (sep == catSep)
      catSep = ',';
    end

    v = strrep(v,sep,[catSep char(0)]);    % Insert concatenation separator.
    vind0 = find(v == char(0));


keyboard

    v(vind0(ABvCum)) = sep;                % Insert string separator.

    v = v(v ~= char(0));                  % Remove leftover empties.
    if (v(end) ~= sep)
      v = [v sep];
    end

    [v in2out out2in] = StrUnique(v);

    AB = putAdj(AB,sparse(ABr,ABc,out2in));    % Replace Adj.
    AB = putVal(AB,v);     % Replace values (still need to sort).

  else
    AB = A*B;
    AB = putAdj(AB,Adj(AB) - diag(diag(Adj(AB))));
    AB = A(find(sum(Adj(A),2)),find(sum(Adj(A),1)));
  end

end 
