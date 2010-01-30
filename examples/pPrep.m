% Create a DB.
%DB = DBserver('f-2-1.llgrid.ll.mit.edu','cloudbase');
[stat,host] = system('hostname -s');
DB = DBserver([host(1:end-1) '.llgrid.ll.mit.edu'],'cloudbase');
T = DB('pPrepTable');
DB


deleteForce(T);


