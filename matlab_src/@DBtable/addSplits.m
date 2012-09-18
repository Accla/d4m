function T = addSplits(T,splitString)
%addSplits: Adds row splits to a table for better load balancing across multiple servers.
%Database table utility function.
%  Usage:
%    T = addSplits(T,splitString)
%  Inputs:
%    T = database table object
%    splitString = string list row splits
% Outputs:
%    T = database table object with additional row splits

  T_s = struct(T);
  DB_s = struct(T_s.DB);
  javaOp = DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDbTableOperations',DB_s.instanceName, DB_s.host, DB_s.user, DB_s.pass, 'Accumulo');
  javaOp.addSplits(T_s.name, splitString);

end
