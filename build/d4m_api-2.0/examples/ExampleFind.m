% Example Find
%
% host='localhost'
% db='example_table'
% rows ='1,2,3,8,9,10,'
% cols = '1,22,333,88888888,999999999,10101010101010101010,'

% [retRows,retCols,retVals]=DBsubsrefFind(host,db,rows,cols)
%

host='localhost'
db='example_table'
rows ='1,2,3,8,9,10,'
cols = '1,22,333,88888888,999999999,10101010101010101010,'

[retRows,retCols,retVals]=DBsubsrefFind(host,db,rows,cols)