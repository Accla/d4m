function putSplits(T, splitString, splitStringTranspose)
%PUTSPLITS replaces the split state of a table.
%   splitString In the format: "row1,row2,row3," where ',' can be any separator
%       Use "" to take away all splits
%   splitStringTranspose acts the same for the transpose table
%Splits in the table not present in splitString will be merged together.
%  Remember, merging is an expensive operation!
%Use addSplits() to avoid merging already present splits.
%
T_s = struct(T);
DB_s = struct(T_s.DB);
javaOp = DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDbTableOperations',DB_s.instanceName, DB_s.host, DB_s.user, DB_s.pass, 'Accumulo');
javaOp.putSplits(T_s.name1, splitString);
javaOp.putSplits(T_s.name2, splitStringTranspose);
end
