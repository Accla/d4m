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
    disp('Enter Your Username ')
    DB.user = input('', 's');
    
    disp('Enter Your Password')
    if isunix
        [s,r] = system('read -s DROWSSAP && echo $DROWSSAP');    
        DB.pass = strtrim(r);
        clear r;
    else
        DB.pass = input('Password: ', 's');
    end
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

