% DBsetupSciDB Sets up SciDB database connection
% Usage:
%       DB = DBsetupSciDB('my_database'); % default for LLGrid systems
%       DB = DBsetupSciDB('my_database', 'user', 'SciDBUser', 
%                                        'host', 'http://some-host.com', 
%                                        'passwdfile', '/tmp/passwd',
%                                        'port', 8080);
% The second method of calling this function requires a key-pair input to
% specify the username, host name, password file and port
%
% Inputs:
%       dbname = Name of the database to connect to
%
function DB = DBsetupSciDB(dbname, varargin)

% dbname is only being used on LLGrid to find the database host
if ~isempty(varargin)    
    [username, host, scidbShimPassword, port] = parseinputs(varargin);
else
    [username, host, scidbShimPassword, port] = getdefaults(dbname);
end

hostname = ['http://' host ':' num2str(port) '/'];
DB = DBserver(hostname, 'scidb', '', username, scidbShimPassword);  % Creates binding.

% try to connect to the database and throw error on failure
[stat, ~] = DBSciDBConnect(DB);
if stat>0
    switch stat
        case 4
            errmsg = 'Network error';
        case 6
            errmsg = 'Incorrect username/password';
        otherwise
            errmsg = sprintf('Received error code %d from wget', stat);
    end
    error('Unable to connect to SciDB\nCause : %s', errmsg)
end


function [username, hostname, shimpasswd, port] = parseinputs(inputs)

% what happens when all 6 inputs in varargin are not specified ?
if length(inputs) < 6
    error('Insufficient inputs provided');
end

username = 'SciDBUser';
hostname = 'localhost';
shimpasswd = '';
port = 8080; % default

while ~isempty(inputs)
    field = inputs{1};
    value = inputs{2};
    inputs = inputs(2:end);
    switch (field)
        case 'user'
            username = value;
        case 'host'
            hostname = value;
        case 'passwdfile'
            fid = fopen(value, 'rt');
            if fid < 0
                error('Unable to open SciDB shim password file');
            end
            shimpasswd = fgetl(fid);
            fclose(fid);
        case 'port'
            port = value;
        otherwise
            % nothing to do here
    end
end

function [username, hostname, scidbShimPassword, port] = getdefaults(dbname)

% use the startup script to find the location of the tools directory
% evalc('startup') will clobber the parallel setup
%evalc('startup'); % our startup script clears variables. Be careful here
% the evalc command will create the grid_config structure in this workspace

global MPI_COMM_WORLD;% this is breaking in older versions (<R2015a)
% if user has cleared the workspace, MPI_COMM_WORLD is gone
if exist('MPI_COMM_WORLD', 'var')
    grid_config = MPI_COMM_WORLD.grid_config;
else
    grid_config.llgrid_filesys_stub = '';
    grid_config.desktop_mnt_path = '';
    grid_config.desktop_filesys_stub = '';
end

% check for existence of llGrid directory
if exist(grid_config.llgrid_filesys_stub, 'dir')
    % are we on LLGrid system ?
    DBdir = fullfile(grid_config.llgrid_filesys_stub, 'tools', 'groups', 'databases', dbname);   % Get DBdir.
elseif exist(grid_config.desktop_mnt_path, 'dir')
    % perhaps on client remote desktop
    DBdir = fullfile(grid_config.desktop_mnt_path, 'tools', 'groups', 'databases', dbname);   % Get DBdir.
elseif exist(grid_config.desktop_filesys_stub, 'dir')
    % only thing left to try is desktop local folder
    DBdir = fullfile(grid_config.desktop_filesys_stub, 'tools', 'groups', 'databases', dbname);   % Get DBdir.
else
    DBdir = fullfile('/home/gridsan/groups/databases/', dbname); % fallback to default harcoded path. ugh.
    %error('Unable to find database configuration files');
end

dnsname = fullfile(DBdir, 'dnsname');
fid = fopen(dnsname, 'rt');
if ~exist(dnsname, 'file') || fid<0
    error('DNS not setup for %s. Cannot connect to database', dbname);
end

hostname = strtrim(fgetl(fid));
fclose(fid);

fid = fopen(fullfile(DBdir, 'scidb_shim_password.txt'), 'rt');
if fid < 0
    error('Unable to open SciDB shim password file');
end
scidbShimPassword = fgetl(fid);
fclose(fid);

% check if the shim password is a string
%if not, the databse has probably not been started
if ~ischar(scidbShimPassword)
    % fgetl reached end of file. password not available because db is
    % stopped
    scidbShimPassword = '';
end

% some defaults
username = 'SciDBUser';
port = 8080;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu),
%                    Dr. Vijay Gadepally (vijayg@ll.mit.edu),
%                    Dr. Siddharth Samsi (sid@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


