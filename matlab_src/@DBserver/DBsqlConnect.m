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
    connStr = ''
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
    driver=org.gjt.mm.mysql.Driver;
    props = java.util.Properties;
    user = DB.user;
    sepLoc = strfind(user,'\');
    if sepLoc
        props.put('domain',user(1:sepLoc-1));   % Add to username and split out.
        user = user(sepLoc+1:end);
    end
    %connStr = ['jdbc:mysql://' DB.host '/' DB.instanceName '?user=' user '?socket=/state/partition1/vijay/lib/mysql.sock'];
    connStr = ['jdbc:mysql://' DB.host '/' DB.instanceName '?user=' user];
    %connStr = ['jdbc:mysql://' DB.host '/' DB.instanceName '?user=' user '&password=' DB.pass];
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

