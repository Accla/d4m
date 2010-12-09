function [rowString, colString, valueString] = DBsubsrefFind(instanceName, host, db, user, pass, rowInputString, colInputString)
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
%
%
% For help on inserting database entries;
% type help DBinsert

% !!! DB does not exist.
%if strcmp(DB.type,'BigTableLike')

  query=DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDbQuery',instanceName, host, db, user, pass);
  query.doMatlabQuery(rowInputString, colInputString);


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
