
function DBcreate(host,table)
% Create Database Tables
%
% Returns nothing.
%
% Example:

%
% host='host_name'
% table='table_name'
% DBcreate(host,table)
%
%
%
% For help on deleting database tables;
% type help DBdelete




import java.util.*;
import ll.mit.edu.d4m.db.cloud.*;

ops = D4mDbTableOperations(host);
ops.createTable(table);


