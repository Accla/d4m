%%  Example code to use DBTable.nnz()
DB = DBserver('f-2-10.llgrid.ll.mit.edu', 'cloudbase', 'cloudbase', 'root', 'secret');
DbTab = DBtable(DB,'ReutersData');
sz = nnz(DbTab);
disp(['ReutersData nnz = ',num2str(sz)])
