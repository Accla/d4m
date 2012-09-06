function revokeCombiningColumns(T, colNames)
%REVOKECOMBININGCOLUMNS revokes columns' combiner designations
%   T table
%   colNames A list of column names in the format "colName1,colName2,"
%       where ',' is an arbitrary seperator.  TODO: also accept an array of
%       column names.
%As there is only 1 combiner per column, there is no need to specify the
%type of the combiner to remove.
%Note this affects only the currently set column family of T.
if nargin < 2
    error('2 input arguments required')
end
T_s = struct(T);
DB_s = struct(T_s.DB);
javaOp = DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDbTableOperations',DB_s.instanceName, DB_s.host, DB_s.user, DB_s.pass, 'Accumulo');
javaOp.revokeCombiningColumns(T_s.name, colNames, T_s.columnfamily)

end