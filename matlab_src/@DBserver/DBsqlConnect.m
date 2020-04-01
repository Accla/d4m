function conn = DBsqlConnect(DB)
%DBsqlConnect: Constructs a connector to an SQL database.
%Database internal function used by ls and DBtable.
%  Usage:
%    conn = DBsqlConnect(DB)
%  Inputs:
%    DB = database object with a binding to a specific database
% Outputs:
%    conn = SQL connector object

% DBsqlConnect constructs SQL connector.c

if strcmp(DB.type,'BigTableLike')
    connStr = '';
end

%DB = struct(DB);

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

if strcmp(DB.type,'mysql')
    if(exist('OCTAVE_VERSION'))
	driver=javaObject('org.gjt.mm.mysql.Driver');
	props=javaObject('java.util.Properties');     
    else
        driver=org.gjt.mm.mysql.Driver;
        props = java.util.Properties;
    end
    user = DB.user;
    sepLoc = strfind(user,'\');
    if sepLoc
        props.put('domain',user(1:sepLoc-1));   % Add to username and split out.
        user = user(sepLoc+1:end);
    end
    %connStr = ['jdbc:mysql://' DB.host '/' DB.instanceName '?user=' user '?socket=/state/partition1/vijay/lib/mysql.sock'];
    if length(DB.pass) == 0
        connStr = ['jdbc:mysql://' DB.host '/' DB.instanceName '?user=' user];
    else
        connStr = ['jdbc:mysql://' DB.host '/' DB.instanceName '?user=' user '&password=' DB.pass];
    end
    conn = driver.connect(connStr,props);
end

if strcmp(DB.type,'pgres')
    %javaaddpath("postgresql-42.2.8.jar") 
    if(exist('OCTAVE_VERSION'))
	driver=javaObject('org.postgresql.Driver');
	props=javaObject('java.util.Properties');     
    else
	driver=org.postgresql.Driver;
	props=java.util.Properties;
    end
    
    user = DB.user;
    sepLoc = strfind(user,'\');
    if sepLoc
        props.put('domain',user(1:sepLoc-1));   % Add to username and split out.
        user = user(sepLoc+1:end);
    end

    connStr = ['jdbc:postgresql://' DB.host '/' DB.instanceName '?user=' ...
               user '&password=' DB.pass];
    conn = driver.connect(connStr,props);
end


end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu),
% Dr. Vijay Gadepally (vijayg@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

