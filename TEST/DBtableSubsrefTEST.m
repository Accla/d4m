AssocSetup;  % Create assoc array A.

DBsetup;  % Setup database DB.

% Delete and create table.
T = DB('DBtableSubsrefTEST');
deleteForce(T);
T = DB('DBtableSubsrefTEST');

put(T,A);

% Get subarrays.
T1r = T('a b ',:);
T2r = T('a* ',:);
T3r = T('a : b ',:);
T1c = T(:,'a b ');
T2c = T(:,'a* ');
T3c = T(:,'a : b ');

save([mfilename '.mat'],'-v6','T1r','T2r','T3r','T1c','T2c','T3c');     
T=close(T);
deleteForce(T);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
