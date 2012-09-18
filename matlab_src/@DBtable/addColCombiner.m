function T = addColCombiner(T,colNames,combineType)
%addColCombiner: Adds combiners to specific column names.
%Database table user function.
%  Usage:
%    T = addColCombiner(T,colNames,combineType)
%  Inputs:
%    T = database table object
%    colNames = string list of column names
%    combineType = combiner name "min", "max", or "sum";
%      IF D4M_API_JAVA.jar is installed on the Accumulo instance, you can
%      specify "min_decimal", "max_decimal", or "sum_decimal"
%      to obtain the ability to combine on decimals
%  Outputs:
%    T = database table object with combiners

if nargin < 3
    error('3 input arguments required')
end
T_s = struct(T);
DB_s = struct(T_s.DB);
javaOp = DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDbTableOperations',DB_s.instanceName, DB_s.host, DB_s.user, DB_s.pass, 'Accumulo');
javaOp.designateCombiningColumns(T_s.name, colNames, combineType, T_s.columnfamily)

end