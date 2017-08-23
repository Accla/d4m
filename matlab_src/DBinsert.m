function DBinsert(instanceName, host, table, user, pass, rowInputString, colInputString, valueInputString, colFamily, security, cloudType)
%DBinsert: Inserts triples into a database table.
%Database internal function for putTriple.
%  Usage:
%    DBinsert(instanceName, host, table, user, pass,
%      rowInputString, colInputString, valueInputString, colFamily, security, cloudType)
%  Inputs:
%    instanceName = database instance name
%    host = database host name
%    table = name of table to create
%    user = username on database
%    pass = password on database
%    rowInputString = list of n row strings (e.g., 'r1,r2,r3,')
%    colInputString = list of n column strings (e.g., 'c1,c2,c3,')
%    valueInputString = list of n value strings (e.g., 'v1,v2,v3,')
%    colFamily = single column family that will be used with all of these inserted triples
%    security = single security label that will be used with all of these inserted triples
%    cloudType = type of database
%  Outputs:
%    


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
