function [tableValueStr] = ls(DB)
%LS Lists tables in a DB.
%   LS(DB) Lists all of the tables in DB

  if strcmp(DB.type,'BigTableLike')
     ops = DBaddJavaOps('edu.mit.ll.d4m.db.cloud.D4mDbInfo',DB.instanceName,DB.host,DB.user,DB.pass);
     tableValueStr = char(ops.getTableList());
  end

  if strcmp(DB.type,'sqlserver')
     conn = DBsqlConnect(DB);
     % Send SQL command:  SHOW FULL TABLES FROM db_name.
     q = conn.prepareStatement(['select * from sys.Tables']);
     results = q.executeQuery();
     md = results.getMetaData();
     numCols = md.getColumnCount();
     tableValueStr = '';
%     for j=1:numCols
     for j=[1 7 8 9]
       tableValueStr = [tableValueStr char(md.getColumnName(j)) ','];
     end
     tableValueStr = [tableValueStr char(10)];
     while results.next()
  %     for j=1:numCols
      for j=[1 7 8 9]
        tableValueStr = [ tableValueStr char(results.getString(j)) ','];
      end
      tableValueStr = [tableValueStr char(10)];
     end
     conn.close();
  end

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

