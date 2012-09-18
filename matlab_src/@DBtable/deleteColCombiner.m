function T = deleteColCombiner(T,colNames)
%deleteColCombiner: Deletes the combiners for specific column names.
%Database table user function.
%  Usage:
%    T = deleteColCombiner(T,colNames)
%  Inputs:
%    T = database table object with combiners
%    colNames = string list of column names
% Outputs:
%    T = database table object with fewer column combiners

  if nargin < 2
    error('2 input arguments required')
  end
  T_s = struct(T);
  DB_s = struct(T_s.DB);
  javaOp = DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDbTableOperations',DB_s.instanceName, DB_s.host, DB_s.user, DB_s.pass, 'Accumulo');
  javaOp.revokeCombiningColumns(T_s.name, colNames, T_s.columnfamily)

end