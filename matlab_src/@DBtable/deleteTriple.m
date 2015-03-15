function T = deleteTriple(T,r,c)
%DELETETRIPLE removes the triple with the given row and column qualifier
%from the table.
%   r and c take the usual delimitted format ‘r1;r2;’ where ‘;’ is an 
%   arbitrary character not present in the row/column data.
%Takes the column family and security into account as well.
%Deletes occur in chunkBytes (from PutBytes(T)) blocks.
%Only works for Accumulo DBs.

chunkBytes = T.putBytes;    % Set chunk size in chars.

% Get number of bytes.
rByte = numel(r);   cByte = numel(r);
ir = [0 find(r == r(end))];
ic = [0 find(c == c(end))];
Nr = numel(ir)-1;

avgBytePerTriple = (rByte + cByte)/Nr;
chunkSize = min(max(1,round(chunkBytes/avgBytePerTriple)),Nr);

DB = struct(T.DB);
if ~strcmp(DB.type, 'Accumulo')
    error('Triple deleting only supported on Accumulo');
end
deleter = DBaddJavaOps('edu.mit.ll.d4m.db.cloud.accumulo.AccumuloDelete', DB.instanceName, DB.host, DB.user, DB.pass);

for i=1:chunkSize:Nr
    %  delete_t = tic;
    i1 = min(i + chunkSize,Nr+1);
    rr = r((ir(i)+1):ir(i1));
    cc = c((ic(i)+1):ic(i1));
    deleter.deleteRows(T.name, rr, cc, T.columnfamily, T.security);
    %  delete_t = toc(delete_t);  disp(['Delete time: ' num2str(delete_t)]);
end

end
