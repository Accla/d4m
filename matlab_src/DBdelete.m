
function DBdelete(host,table)
% Delete Database Tables
%
% Returns nothing.
%
% Example:

%
% host='host_name'
% table='table_name'
% DBdelete(host,table)
%
%
%
% For help on creating database tables;
% type help DBcreate




import java.util.*;
import ll.mit.edu.d4m.db.cloud.*;

ops = D4mDbTableOperations(host);
ops.deleteTable(table);


