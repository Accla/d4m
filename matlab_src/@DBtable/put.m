function T = put(T,A);
%PUT inserts associative array in DB table.
  [row col val] = find(A);
  DBinsert(T.DB.host, T.name, row, col, val);

end

