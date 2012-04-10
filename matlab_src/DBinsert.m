function DBinsert(instanceName, host, table, user, pass, rowInputString, colInputString, valueInputString, colFamily, security, cloudType)
%
% Inserting Database Entries
%
% INPUT:
%    instanceName  instance name of cloud
%    host    address of zookeeper, format 'host:port'
%    table   name of table 
%    user    user authorized to write to the  table
%    pass    password
%    rowInputString  row input string
%    colInputString  column input string
%    valueInputString values input string
%    colFamily  column family
%    security    security authorization of the user
%    cloudType   cloud type is BigTableLike or Accumulo
%
%
% Inserts rows, columns, and values using delimited strings with the index of
% each representing a single row in the database.
%
% Example:
% Note; Specify the delimiter used as the last character in the list.
%
% host='host_name'
% db='table_name'
% rows = {'1,2,3,4,5,6,7,8,9,10,'}
% columns={'1,22,333,4444,55555,666666,7777777,88888888,999999999,10101010101010101010,'}
% values={'val1,val2,val3,val4,val5,val6,val7,val8,val9,val10,'}
%
% DBinsert(host, table, rows, columns, values, columnFamily, security, cloudType)
%
%
%
% For help on retrieving database entries;
% type help DBsubsrefFind

%tic;

% !!! DB does not exist !!!
%  if strcmp(DB.type,'BigTableLike')
      insert=DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDbInsert', instanceName, host, table, user, pass);
      insert.setCloudType(cloudType);
      insert.doProcessing(rowInputString, colInputString, valueInputString, colFamily, security);
%  end
  
if 0  
  if strcmp(lower(DB.type),'jdbc')
        db = DBaddJavaOps('edu.mit.ll.d4m.db.sql.D4mDbOperations',host) ;
        db.insert(table, rowInputString, colInputString, valueInputString);
  end
end  

%insertObjProcTime = toc



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu),
%  Mr. Charles Yee (yee@ll.mit.edu), Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
