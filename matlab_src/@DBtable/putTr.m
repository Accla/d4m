function T = putTr(T1,T2,A);
%PUTTR inserts associative array and its transpose in DB tables.
  % Set chunk size in chars.
  chunkBytes = 5e5;
  M = nnz(A);

  [row col val] = find(A);

  rowMat = Str2mat(row);
  colMat = Str2mat(col);
  valMat = Str2mat(val);
  [temp rowMax] = size(rowMat);
  [temp colMax] = size(colMat);
  [temp valMax] = size(valMat);

  totMax = rowMax + colMax + valMax;
  chunkSize = min(max(1,round(chunkBytes/totMax)),M);


  DB = struct(T1.DB);

  for i=1:chunkSize:M
    i1 = min(i + chunkSize - 1,M);
    r = Mat2str(rowMat(i:i1,:)); c = Mat2str(colMat(i:i1,:));  v = Mat2str(valMat(i:i1,:));
    DBinsert(DB.host, T1.name, r, c, v );
    DBinsert(DB.host, T2.name, c, r, v );    % Insert transpose.
  end

end

