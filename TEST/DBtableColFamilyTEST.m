AssocSetup;  % Create assoc array A.

DBsetup;  % Setup database DB.

% Delete and create table.
T = DB('DBtableColFamilyTEST');
deleteForce(T);
T = DB('DBtableColFamilyTEST');

try
    % Insert data into the "montegue" family...
    T = putColumnFamily(T,'montegue');
    put(T,A);
    pause(1);
    % Get data from the "montegue" family... shouldn't be empty
    T = putColumnFamily(T,'montegue');
    Tmontegue = T('a ',:);

    % Get data from the "capulet" family... should be empty
    T = putColumnFamily(T,'capulet');
    Tcapulet = T('a ',:);
    T=close(T);
    % Delete Table
    deleteForce(T);

    
catch e
    deleteForce(T);
    throw(e);
end

% Check Results
if(not(isempty(Tcapulet)))
    throw(MException('ResultChk:SecurityError', '"montegue" data should be inaccessable to "capulet" queries'))
end

if(isempty(Tmontegue))
    throw(MException('ResultChk:DataInaccessable', '"montegue" data should be accessable to "montegue" queries'))
end

save([mfilename '.mat'],'-v6','Tmontegue', 'Tcapulet');     

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
