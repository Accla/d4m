function addSplits(T, splitString, splitStringTranspose)
%ADDSPLITS adds splits for a table.
%   splitString In the format: "row1,row2,row3," where ',' can be any separator
%   splitStringTranspose acts the same for the transpose table
%Note that the added splits are in addition to any splits already present
%in the table.
%Use putSplits() to merge away splits present but not in splitString.
T_s = struct(T);
DB_s = struct(T_s.DB);
javaOp = DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDbTableOperations',DB_s.instanceName, DB_s.host, DB_s.user, DB_s.pass, 'Accumulo');
javaOp.addSplits(T_s.name1, splitString);
javaOp.addSplits(T_s.name2, splitStringTranspose);

end
