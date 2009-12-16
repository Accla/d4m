% Example Find Rows
%
% host='localhost'
% db='example_table'
% rows ='1,2,3,8,9,10,'
% cols = ':'

% [retRows,retCols,retVals]=DBsubsrefFind(host,db,rows,cols)
%

%host='localhost'
host='f-2-1.llgrid.ll.mit.edu'
db='example_table'
rows ='1,2,3,8,9,10,'
cols = ':'

[retRows,retCols,retVals]=DBsubsrefFind(host,db,rows,cols)
