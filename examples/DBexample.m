% Create a DB.
%DB = DBserver('f-2-9.llgrid.ll.mit.edu','cloudbase');
[stat,host] = system('hostname -s');
DB = DBserver([host(1:end-1) '.llgrid.ll.mit.edu'],'cloudbase');

% List the tables.
DB

