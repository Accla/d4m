function [rowString, colString, valueString] = DBsubsrefFind(instanceName, host, db, user, pass, rowInputString, colInputString, colFamily, security, numResults, cloudType)
%DBsubsrefFind: DEPRECATED. Queries triples from a database table using row and column keys.
%Database internal function.
%  Usage:
%    [rowString, colString, valueString] = 
%        DBsubsrefFind(instanceName, host, db, user, pass,
%          rowInputString, colInputString,colFamily, security, numResults, cloudType)
%  Inputs:
%    instanceName = database instance name
%    host = database host name
%    table = name of table to create
%    user = username on database
%    pass = password on database
%    rowInputString = list of row strings or row ranges (e.g., 'r1,r2,r3,' or 'r1,:,r2,')
%    colInputString = list of columns strings or column ranges  (e.g., 'c1,c2,c3,' or 'c1,:,c2,')
%    colFamily = single column family that will be used with all of these inserted triples
%    security = single security label that will be used with all of these inserted triples
%    numResults = number of entries to return in one query
%    cloudType = type of database
%  Outputs:
%    rowString = list of n row strings 
%    colString = list of n column strings
%    valueString = list of n value strings


% !!! DB does not exist.
%if strcmp(DB.type,'BigTableLike')

  query=DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDataSearch',instanceName, host, db, user, pass);
  query.setCloudType(cloudType);
  query.setLimit(numResults);

  query.doMatlabQuery(rowInputString, colInputString, colFamily, security);

  rowString = query.getRowReturnString;
  colString = query.getColumnReturnString;
  valueString = query.getValueReturnString;
%end


if 0
if strcmp(DB.type,'jdbc')

  query=DBaddJavaOps('edu.mit.ll.d4m.db.sql.D4mDbOperations',host);
  query.doMatlabQuery(db, rowInputString, colInputString);


  rowString = query.getRowReturnString;
  colString = query.getColumnReturnString;
  valueString = query.getValueReturnString;
end
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu),
%  Mr. Charles Yee (yee@ll.mit.edu), Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
