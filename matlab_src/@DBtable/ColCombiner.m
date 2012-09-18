function colNames = ColCombiner(T)
%ColCombiner: List the columns in the table with combiners.
%Database table user function.
%  Usage:
%    retString = ColCombiner(T)
%  Inputs:
%    T = database table or table pair object
%  Outputs:
%    colNames = string list of column names

  if nargin < 1
    error('1 input argument required')
  end
  T_s = struct(T);
  DB_s = struct(T_s.DB);
  javaOp = DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDbTableOperations',DB_s.instanceName, DB_s.host, DB_s.user, DB_s.pass, 'Accumulo');
  colNames = javaOp.listCombiningColumns(T_s.name);

end