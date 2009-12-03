
function [tableValueString] = DBLS(host)
% Finding Database Tables
%
% Returns a list of tables in the database.
%
% Example:

%
% host='host_name'
% [tableValuesString] = DBLS(host)
%
%
%
% For help on inserting database entries;
% type help DBinsert




import java.util.*;
import ll.mit.edu.d4m.db.cloud.*;

info = D4mDbInfo(host);
tableValueString - info.getTableList();


