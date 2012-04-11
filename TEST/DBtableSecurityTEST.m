AssocSetup;  % Create assoc array A.

DBsetup;  % Setup database DB.

% Delete and create table.
T = DB('DBtableSecurityTEST');
deleteForce(T);
T = DB('DBtableSecurityTEST');

try 
    % Insert 'foo' data...
    T = putSecurity(T,'foo');
    put(T,A);

    % Get 'foo' data... shouldn't be empty
    T = putSecurity(T,'foo');
    Tfoo = T('a ',:);

    % Get 'bar' data... should be empty
    T = putSecurity(T,'bar');
    Tbar = T('a ',:);
    T=close(T);
    % Delete Table
    deleteForce(T);

catch e        
    deleteForce(T);
    throw(e);    
end

% Check Results
if(not(isempty(Tbar)))
    throw(MException('ResultChk:SecurityError', '"foo" data should be inaccessable to "bar" queries'))
end

if(isempty(Tfoo))
    throw(MException('ResultChk:DataInaccessable', '"foo" data should be accessable to "foo" queries'))
end

save([mfilename '.mat'],'-v6','Tfoo', 'Tbar');     

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
