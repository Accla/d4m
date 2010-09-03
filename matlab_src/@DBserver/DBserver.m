function DB = DBserver(host,type,instanceName,user,pass)
%DBSERVER constructions DBserver object.
% May need to add arguments for handling username/password?
% May need to add argument for handling multiple databases
% from the same server.

  DB.host = host;
  DB.type = type;
  DB.instanceName = instanceName;
  DB.user = user;
  DB.pass = pass;
  
  DB=class(DB,'DBserver');

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

