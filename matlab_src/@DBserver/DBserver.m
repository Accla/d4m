function DB = DBserver(host,type,instanceName,varargin)
%DBserver: Create DBserver object with a binding to a specific database.
%Database user function.
%  Usage:
%    DB = DBserver(host,type,instanceName)
%    DB = DBserver(host,type,instanceName,username,password)
%  Inputs:
%    host = database host name
%    type = type of database (e.g., 'Accumulo', 'sqlserver')
%    instanceName = database instance name
%    username = username in database
%    password = password associated with username
% Outputs:
%    DB = database object with a binding to a specific database

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
%                    Dr. Vijay Gadepally (vijayg@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

