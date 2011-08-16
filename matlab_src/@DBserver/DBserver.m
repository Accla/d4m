function DB = DBserver(host,type,instanceName,varargin)
%DBSERVER constructions DBserver object.
%   DB = DBSERVER(HOST, TYPE, INSTANCENAME, USERNAME, PASSWORD) creates a
%   database connection
%
%   DB = DBSERVER(HOST, TYPE, INSTANCENAME) prompts for username and
%   password
%
%   DB(TABLENAME) creates a DBTable object referencing that table
%
%   DB(TABLENAME1, TABLENAME2) creates a DBTablePair object, where I
%   believe TABLENAME2 = TABLENAME1.'
%
%   May need to add arguments for handling username/password?
%   May need to add argument for handling multiple databases
%   from the same server.
%
%   See also: DBTABLE, DBTABLEPAIR

  DB.host = host;
  DB.type = type;
  DB.instanceName = instanceName;
  
  %set username and password
  if nargin < 5 
    %done in two lines to be consistent w/ password prompt  
    disp('Enter Your Username ')
    DB.user = input('', 's');
    
    disp('Enter Your Password')
    if isunix
        %only works on *nix systems
        [s,r] = system('read -s DROWSSAP && echo $DROWSSAP');    
        DB.pass = strtrim(r);
        clear r;
    elseif ispc
        %find a better way to do this in windows... 
        %for now, don't do masking
        DB.pass = input('Password: ', 's');
        clc
    else        
        %don't do masking
        DB.pass = input('Password: ', 's');
        clc
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

