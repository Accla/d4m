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

  if iscell(T)
    T1 = T{1};
    T2 = T{2};
  else
    T1 = T;
  end


  DB = struct(T1.DB);

  for i=1:chunkSize:M
    i1 = min(i + chunkSize - 1,M);
    r = Mat2str(rowMat(i:i1,:)); c = Mat2str(colMat(i:i1,:));  v = Mat2str(valMat(i:i1,:));
    DBinsert(DB.host, T1.name, r, c, v );
    if iscell(T)
      % Insert transpose.
      DBinsert(DB.host, T2.name, c, r, v );
    end
  end

end

