function [rowString, colString, valueString] = DBsubsrefFind(instanceName, host, db, user, pass, rowInputString, colInputString, colFamily, security, numResults, cloudType)
% Finding Database Entries
%
% Returns row, column, and value delimited strings with the index of
% each representing a single row in the database.
%
% Example:
% Note; Specify the delimiter used as the last character in the list.
%
% host='localhost';
% db='table_name'
% rowsToFind ={'1,2,3,8,9,10,'}
% columnsToFind = {'1,22,333,88888888,999999999,10101010101010101010,'}
%
% [rowsString, columnsString, valuesString] = DBsubsrefFind(host, db, rowsToFind, columnsToFind)
%
%  INPUT
%      instanceName   name of the cloudbase instance
%      host   host name of cloudbase/zookeeper
%      db     table name
%      user   user name
%      pass   user's password
%      rowInputString  row keys
%      colInputString  column qualifiers
%      colFamily     column family
%      security    security or authorizations
%      numResults   the max number of results to return in the query
%
%
%
% For help on inserting database entries;
% type help DBinsert

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
