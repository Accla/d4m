% Load conf.
load -mat DBsetup.bin

% Create a DB.
%DB = DBserver('f-2-8.llgrid.ll.mit.edu','cloudbase','cloudbase','root',drowssap);
DB = DBserver('f-2-10.llgrid.ll.mit.edu','cloudbase','cloudbase','root',drowssap);
%[stat,host] = system('hostname -s');
%DB = DBserver([host(1:end-1) '.llgrid.ll.mit.edu'],'cloudbase');

clear drowssap

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

