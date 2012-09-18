function T = putSplits(T,splitString)
%putSplits: Replaces all row splits in a table; used for better load balancing across multiple servers.
%Database table utility function.
%  Usage:
%    T = addSplits(T,splitString)
%  Inputs:
%    T = database table object
%    splitString = string list row splits
% Outputs:
%    T = database table object with new row splits

  T_s = struct(T);
  DB_s = struct(T_s.DB);
  javaOp = DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDbTableOperations',DB_s.instanceName, DB_s.host, DB_s.user, DB_s.pass, 'Accumulo');
  javaOp.putSplits(T_s.name, splitString);

end
