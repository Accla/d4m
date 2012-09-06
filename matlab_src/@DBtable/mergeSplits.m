function mergeSplits(T, startRow, endRow)
%MERGESPLITS merges tablets together between the given rows
%   startRow is the starting row to merge exclusive, or the empty string or
%       [] to start at the first tablet
%   endRow is the last row to merge inclusive, or the empty string or [] to
%       end at the last tablet
T_s = struct(T);
DB_s = struct(T_s.DB);
javaOp = DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDbTableOperations',DB_s.instanceName, DB_s.host, DB_s.user, DB_s.pass, 'Accumulo');
javaOp.mergeSplits(T_s.name, startRow, endRow);