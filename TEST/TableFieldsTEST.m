DBsetup;  % Setup database DB.

% Delete and create table.
T = DB('DBtableFieldsTEST');
deleteForce(T);
T = DB('DBtableFieldsTEST');

T = putSecurity(T,'FOUO');
Security(T)

putColumnFamily(T,'vertexFamily');
ColumnFamily(T)

%save([mfilename '.mat'],'-v6','T1r','T2r','T3r','T1c','T2c','T3c');     

deleteForce(T);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
