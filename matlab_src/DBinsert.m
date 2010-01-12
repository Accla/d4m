
function DBinsert(host, db, rowInputString, colInputString, valueInputString)

% Inserting Database Entries
%
% Inserts rows, columns, and values using delimited strings with the index of
% each representing a single row in the database.
%
% Example:
% Note; Specify the delimiter used as the last character in the list.
%
% host='host_name'
% db='table_name'
% rows = {'1,2,3,4,5,6,7,8,9,10,'}
% columns={'1,22,333,4444,55555,666666,7777777,88888888,999999999,10101010101010101010,'}
% values={'val1,val2,val3,val4,val5,val6,val7,val8,val9,val10,'}
%
% DBinsert(host, db, rows, columns, values)
%
%
%
% For help on retrieving database entries;
% type help DBsubsrefFind



import java.util.*;
import ll.mit.edu.d4m.db.cloud.*;


%tic;

insert = D4mDbInsert(host, db, rowInputString, colInputString, valueInputString);

%insertObjCreateTime = toc

%tic;
insert.doProcessing();
%insertObjProcTime = toc

