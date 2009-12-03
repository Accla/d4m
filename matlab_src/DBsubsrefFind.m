
function [rowString, colString, valueString] = DBsubsrefFind(host, db, rowInputString, colInputString)
% Finding Database Entries
%
% Returns row, column, and value delimited strings with the index of
% each representing a single row in the database.
%
% Example:
% Note; Specify the delimiter used as the last character in the list.
%
% host='localhost';
% db='table_name'
% rowsToFind ={'1,2,3,8,9,10,'}
% columnsToFind = {'1,22,333,88888888,999999999,10101010101010101010,'}
%
% [rowsString, columnsString, valuesString] = DBsubsrefFind(host, db, rowsToFind, columnsToFind)
%
%
%
% For help on inserting database entries;
% type help DBinsert




import java.util.*;
import ll.mit.edu.d4m.db.cloud.*;



query = D4mDbQuery(host, db);
query.doMatlabQuery(rowInputString, colInputString);

rowString = query.getRowReturnString;
colString = query.getColumnReturnString;
valueString = query.getValueReturnString;

