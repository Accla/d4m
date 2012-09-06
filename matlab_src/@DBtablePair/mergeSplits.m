function mergeSplits(T, startRow, endRow, whichTable)
%MERGESPLITS merges tablets together between the given rows
%   startRow is the starting row to merge exclusive, or the empty string or
%       [] to start at the first tablet
%   endRow is the last row to merge inclusive, or the empty string or [] to
%       end at the last tablet
%   whichTable is either 'first', 'transpose', or 'both'
T_s = struct(T);
DB_s = struct(T_s.DB);
javaOp = DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDbTableOperations',DB_s.instanceName, DB_s.host, DB_s.user, DB_s.pass, 'Accumulo');
if strcmpi(whichTable,'first')
    javaOp.mergeSplits(T_s.name1, startRow, endRow);
elseif strcmpi(whichTable,'transpose')
    javaOp.mergeSplits(T_s.name2, startRow, endRow);
elseif strcmpi(whichTable,'both')
    javaOp.mergeSplits(T_s.name1, startRow, endRow);
    javaOp.mergeSplits(T_s.name2, startRow, endRow);
else
    error('Please specify whichTable correctly');
end