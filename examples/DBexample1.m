% Create a DB.
DB = DBserver('f-2-9.llgrid.ll.mit.edu', 'cloudbase', 'cloudbase', 'root', 'secret');
%[stat,host] = system('hostname -s');
%DB = DBserver([host(1:end-1) '.llgrid.ll.mit.edu'],'cloudbase');

% List the tables.

M = DB('!METADATA');
T = DB('example_table1');

DB

rows ='1,2,3,4,5,6,7,8,9,10,';
cols='1,22,333,4444,55555,666666,7777777,88888888,999999999,10101010101010101010,';
vals='val1,val2,val3,val4,val5,val6,val7,val8,val9,val10,';

A = Assoc(rows,cols,vals);

put(T,A);

% Print 1 row.
A('1,',:)

% Print 1 row.
T('1,',:)


%rows ='1,2,3,8,9,10,';
%cols = '1,22,333,88888888,999999999,10101010101010101010,';

%[retRows,retCols,retVals]=DBsubsrefFind(host,db,rows,cols)
