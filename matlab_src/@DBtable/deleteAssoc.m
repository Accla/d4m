function T = deleteAssoc(T,A)
%DELETEASSOC deletes the entries given in an associative array in a DB table.
%
%    T  table
%    A  associative array
%
%Only works in Accumulo.

chunkBytes = T.putBytes;    % Set chunk size in chars.
M = nnz(A);

[row col val] = find(A);


rowMat = Str2mat(row);
colMat = Str2mat(col);
[~, rowMax] = size(rowMat);
[~, colMax] = size(colMat);

totMax = rowMax + colMax;
chunkSize = min(max(1,round(chunkBytes/totMax)),M);

DB = struct(T.DB);
if ~strcmp(DB.type, 'Accumulo')
    error('Triple deleting only supported on Accumulo');
end
deleter = DBaddJavaOps('edu.mit.ll.d4m.db.cloud.accumulo.AccumuloDelete', DB.instanceName, DB.host, DB.user, DB.pass);

for i=1:chunkSize:M
    delete_t = tic;
    i1 = min(i + chunkSize - 1,M);
    rr = Mat2str(rowMat(i:i1,:)); cc = Mat2str(colMat(i:i1,:));
    
    deleter.deleteRows(T.name, rr, cc, T.columnfamily, T.security);    
    delete_t = toc(delete_t);  disp(['Delete time: ' num2str(delete_t)]);
    
end

end
