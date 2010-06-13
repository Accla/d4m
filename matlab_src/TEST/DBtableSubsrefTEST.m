% Create assoc array entries.
rowStr = 'a a a a a a a aa aaa b bb bbb a aa aaa b bb bbb ';
colStr = 'a aa aaa b bb bbb a a a a a a a aa aaa b bb bbb ';
valStr = 'a-a a-aa a-aaa a-b a-bb a-bbb a-a aa-a aaa-a b-a bb-a bbb-a a-a aa-aa aaa-aaa b-b bb-bb bbb-bbb ';

% Create assoc array.
A = Assoc(rowStr,colStr,valStr);



DBsetup;

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


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
