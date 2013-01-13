function A = Splits(T)
%Splits: List the splits and entries for each split.
%Database table utility function.
%  Usage:
%    A = Splits(T)
%  Inputs:
%    T = database table or table pair object
%  Outputs:
%    A = associative array of splits and the entries after each split.

  T_s = struct(T);
  DB_s = struct(T_s.DB);
  javaOp = DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDbTableOperations',DB_s.instanceName, DB_s.host, DB_s.user, DB_s.pass, 'Accumulo');
  %splitString = javaOp.getSplits(T_s.name);
  % User wants the count in each tablet
  retArray = javaOp.getSplits(T_s.name, true);
  %splitString = cell2mat(cell(retArray(1)));
  %splitCounts = cell(retArray(2));

  nl = char(10);

  A = Assoc([char(retArray(1)) '~,'],['SplitCount' nl],str2num(char(retArray(2))));
 
end
