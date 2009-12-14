% Example Find
%
% host='localhost'
% db='example_table'
% rows ='1,2,3,8,9,10,'
% cols = '1,22,333,88888888,999999999,10101010101010101010,'

% [retRows,retCols,retVals]=DBsubsrefFind(host,db,rows,cols)
%

%host='localhost'
host='f-2-1.llgrid.ll.mit.edu'
db='example_table1'
rows ='1,2,3,8,9,10,'
cols = '1,22,333,88888888,999999999,10101010101010101010,'

[retRows,retCols,retVals]=DBsubsrefFind(host,db,rows,cols)
