% Example Insert
%
% host='localhost';
% db='example_table';
% rows ='1,2,3,4,5,6,7,8,9,10,'
% cols='1,22,333,4444,55555,666666,7777777,88888888,999999999,10101010101010101010,'
% vals='val1,val2,val3,val4,val5,val6,val7,val8,val9,val10,'
%
% DBinsert(db,rows,cols,vals);
%

host='localhost';
% host='f-2-1.llgrid.ll.mit.edu';
db='example_table';
rows ='1,2,3,4,5,6,7,8,9,10,'
cols='1,22,333,4444,55555,666666,7777777,88888888,999999999,10101010101010101010,'
vals='val1,val2,val3,val4,val5,val6,val7,val8,val9,val10,'

DBinsert(host,db,rows,cols,vals);
