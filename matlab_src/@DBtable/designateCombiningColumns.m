function designateCombiningColumns(T, colNames, combineType)
%DESIGNATECOMBININGCOLUMNS designates columns with a combiner
%   T table
%   colNames A list of column names in the format "colName1,colName2,"
%       where ',' is an arbitrary seperator.  TODO: also accept an array of
%       column names.
%   combineType One of the strings "min", "max", or "sum"
%		IF D4M_API_JAVA.jar is installed on the Accumulo instance, you can
% 		specify "min_decimal", "max_decimal", or "sum_decimal"
%		to obtain the ability to combine on decimals
%The column names do not have to exist when designated. Only one combiner 
%allowed per column. 
%Note this will take into account the current column family.
if nargin < 3
    error('3 input arguments required')
end
T_s = struct(T);
DB_s = struct(T_s.DB);
javaOp = DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDbTableOperations',DB_s.instanceName, DB_s.host, DB_s.user, DB_s.pass, 'Accumulo');
javaOp.designateCombiningColumns(T_s.name, colNames, combineType, T_s.columnfamily)

end