function AB = CatValMul(A,B);
%CatValMul: Perform matrix multiply and concatenate colliding values.
%Associative array user function.
%  Usage:
%    AB = CatValMul(A,B)
%  Inputs:
%    A = associative array with string values
%    B = associative array with string values
% Outputs:
%    AB = associative array with string value keys representing concatenated values of collisions.

  if ((IsClass(A,'Assoc') && ischar(Val(A))) && (IsClass(B,'Assoc') && ischar(Val(B))))
    AB = Abs0(A)*Abs0(B);

    % Trim down to just what we need.
    A1 = A(Row(AB),:);     B1 = B(:,Col(AB));
    A2 = A1(:,Row(B1));    B2 = B1(Col(A1),:);
    A2size = size(A2);     B2size = size(B2);

    A2adj = Adj(A2);
    A2val = Val(A2);
    A2valMat = Str2mat(A2val);

    B2adj = Adj(B2);
    B2valMat = Str2mat(Val(B2));

    % Set concatenation separator.
    sep = A2val(end);
    catSep = ';';
    if (sep == catSep)
      catSep = ',';
    end

    clear A1 A2 A2val B1 B2

    A2v = zeros(0,1);
    B2v = zeros(0,1);
    % Loop over each column of B2.
    for i = 1:B2size(2)

       % Get columns that have a collision with B2col rows.
       [B2rrr B2ccc B2vvv] = find(B2adj(:,i));       
       [A2rrrr A2cccc A2vvvv] = find(A2adj(:,B2rrr));
       [tmp A2rrrri] = sort(A2rrrr);
       B2vv = B2vvv(A2cccc(A2rrrri));
       A2vv = A2vvvv(A2rrrri);

       % Append.
       B2v = [B2v; B2vv];
       A2v = [A2v; A2vv];

    end
    clear A2adj B2adj

    v = CatStr(Mat2str(A2valMat(A2v,:)),catSep,Mat2str(B2valMat(B2v,:)));
   
    [ABr ABc ABv] = find(Adj(AB));    % Get indices of AB;

    ABvCum = cumsum(ABv);     % Accumulate to find concatenation ends.

    v = strrep(v,sep,[catSep char(0)]);    % Insert concatenation separator.
    vind0 = find(v == char(0));

    v(vind0(ABvCum)) = sep;                % Insert string separator.

    v = v(v ~= char(0));                  % Remove leftover empties.


    [v in2out out2in] = StrUnique(v);

    AB = putAdj(AB,sparse(ABr,ABc,out2in));    % Replace Adj.
    AB = putVal(AB,v);     % Replace values (still need to sort).


  else
    AB = A*B;
  end

end 
