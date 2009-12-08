% Simple test.

% Create assoc array entries.
rowStr = 'a a a a a a a aa aaa b bb bbb a aa aaa b bb bbb ';
colStr = 'a aa aaa b bb bbb a a a a a a a aa aaa b bb bbb ';
valStr = 'a-a a-aa a-aaa a-b a-bb a-bbb a-a aa-a aaa-a b-a bb-a bbb-a a-a aa-aa aaa-aaa b-b bb-bb bbb-bbb ';


% Create assoc array.
A = Assoc(rowStr,colStr,valStr);

% Get subarrays.
A1 = A('a b ',:);
A2 = A('a* ',1:3);
A3 = (A < 'b ');


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
