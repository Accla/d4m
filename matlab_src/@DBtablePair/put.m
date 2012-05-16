function T = put(T,A)
%PUT inserts associative array and its transpose in DB tables.
%   T = PUT(T, A) Puts associative array A and it's transpose into
%   DBtablePair T.
%
%   See also: DBTABLEPAIR/PUTTRIPLE

  % Set chunk size in chars.
  chunkBytes = 20e5;
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
  insert_t = tic;
    i1 = min(i + chunkSize - 1,M);
    r = Mat2str(rowMat(i:i1,:)); c = Mat2str(colMat(i:i1,:));  v = Mat2str(valMat(i:i1,:));
    DBinsert(DB.instanceName, DB.host, T.name1, DB.user, DB.pass, r, c, v, T.columnfamily, T.security, DB.type );
    DBinsert(DB.instanceName, DB.host, T.name2, DB.user, DB.pass, c, r, v, T.columnfamily, T.security, DB.type );    % Insert transpose.
  insert_t = toc(insert_t);  disp(['Insert time: ' num2str(insert_t)]);
  end

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

