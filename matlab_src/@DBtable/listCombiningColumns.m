function retString = listCombiningColumns(T)
%LISTCOMBININGCOLUMNS lists the columns in table T with attached combiners
%   T table
if nargin < 1
    error('1 input argument required')
end
T_s = struct(T);
DB_s = struct(T_s.DB);
javaOp = DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDbTableOperations',DB_s.instanceName, DB_s.host, DB_s.user, DB_s.pass, 'Accumulo');
retString = javaOp.listCombiningColumns(T_s.name);

end