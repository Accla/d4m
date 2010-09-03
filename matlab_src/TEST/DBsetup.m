% Create a DB.
%DB = DBserver('f-2-9.llgrid.ll.mit.edu','cloudbase');
DB = DBserver('f-2-9.llgrid.ll.mit.edu', 'cloudbase', 'cloudbase', 'root', 'secret');
%[stat,host] = system('hostname -s');
%DB = DBserver([host(1:end-1) '.llgrid.ll.mit.edu'],'cloudbase');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

