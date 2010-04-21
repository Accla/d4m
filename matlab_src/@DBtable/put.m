function T = put(T,A);
%PUT inserts associative array in DB table.
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

  DB = struct(T.DB);
  for i=1:chunkSize:M
    i1 = min(i + chunkSize - 1,M);
    DBinsert(DB.host, T.name, ...
      Mat2str(rowMat(i:i1,:)), Mat2str(colMat(i:i1,:)),Mat2str(valMat(i:i1,:)) );
  end

%  DBinsert(DB.host, T.name, row, col, val);

end

