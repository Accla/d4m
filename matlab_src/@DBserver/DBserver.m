function DB = DBserver(host,type,instanceName,varargin)
%DBSERVER constructions DBserver object.
% May need to add arguments for handling username/password?
% May need to add argument for handling multiple databases
% from the same server.

  DB.host = host;
  DB.type = type;
  DB.instanceName = instanceName;
  
  %set username and password
  if nargin < 5 
    DB.user = input('Username: ', 's');
    DB.pass = input('Password: ', 's');
  else
    DB.user = varargin{1};
    DB.pass = varargin{2};    
  end
  
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

