
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



if exist('OCTAVE_VERSION','builtin')

%Do Octave
    ops=java_new('ll.mit.edu.d4m.db.cloud.D4mDbTableOperations',host);
 else

   import java.util.*;
   import ll.mit.edu.d4m.db.cloud.*;

   ops = D4mDbTableOperations(host);
end
ops.createTable(table);


