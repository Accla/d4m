function s = nnz(T)
%SIZE returns number of elements in DB table.

   DB = struct(T.DB);
   table1 = T.name1;
   table2 = T.name2;
  if strcmp(DB.type,'cloudbase')
     ops = DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDbTableOperations',DB.instanceName,DB.host,DB.user,DB.pass);
     % Create an ArrayList
     % Add the table names to the list
     tabNameList = javaObject('java.util.ArrayList');
     tabNameList.add(table1);
     tabNameList.add(table2);
     s = ops.getNumberOfEntries(tabNameList);
  end

   % s = TBD

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

