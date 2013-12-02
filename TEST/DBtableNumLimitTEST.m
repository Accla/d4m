% Test the ability to limit the number of search results.
AssocSetup;

DBsetup;  % Setup database DB.
%instanceName='cloudbase';
%hostname='f-2-10.llgrid.ll.mit.edu';
%usrname='root';

tableName='DBtableNumLimitTEST';
columnFamily='';
security='';



% Delete and create table.
T = DB(tableName);
deleteForce(T);
pause(0.5);
T = DB(tableName);
% Insert some data
    put(T,A);
pause(1);



% Set the limit  to infinite (0). Should get back all results - 6x 6.
T = putNumLimit(T,0);
T0 = T(:,:);
szT0 = size(T0);
m0 = szT0(1);
n0 = szT0(2);
disp(['Test 0:: size of assoc array T0 =' num2str(m0) ' , '  num2str(n0)]);
assert (n0 == 6,'ERROR - size(2) should be 6');
% Set limit to 2.  Get 2 results back - 1x2
T = putNumLimit(T,2);
T1 = T('a ',:);
szT1 = size(T1);
m1 = szT1(1);
n1 = szT1(2);
disp(['Test 1:: size of assoc array T1 =' num2str(m1) ' , ' num2str(n1)]);
assert (n1 == 2,'ERROR - size(2) should be 2');
if(n0 ~= 6)
     throw(MException('ResultChk:UnexpectedResult', [' Test0- Should have gotten back 6, instead of ' n0]))
end

if(n1 ~= 2)
     throw(MException('ResultChk:UnexpectedResult', [' Test1 - Should have gotten back 2, instead of ' n1]))
end
% save results
save([mfilename '.mat'],'-v6','T0', 'T1');     

T=close(T);

% delete table
deleteForce(T);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
