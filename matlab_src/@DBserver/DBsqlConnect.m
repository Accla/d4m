function conn = DBsqlConnect(DB)
% DBsqlConnect constructs SQL connector.

  if strcmp(DB.type,'BigTableLike')
     connStr = ''
  end

  if strcmp(DB.type,'sqlserver')
     driver = net.sourceforge.jtds.jdbc.Driver;
     props = java.util.Properties;
     user = DB.user;
     sepLoc = strfind(user,'\');
     if sepLoc
       props.put('domain',user(1:sepLoc-1));   % Add to username and split out.
       user = user(sepLoc+1:end);
     end
     connStr = ['jdbc:jtds:sqlserver://' DB.host '/' DB.instanceName ';user=' user ';password=' DB.pass];
     conn = driver.connect(connStr,props);
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

