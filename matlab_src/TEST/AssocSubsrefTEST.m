
% Create assoc array entries.
rowStr = 'a a a a a a a aa aaa b bb bbb a aa aaa b bb bbb ';
colStr = 'a aa aaa b bb bbb a a a a a a a aa aaa b bb bbb ';
valStr = 'a-a a-aa a-aaa a-b a-bb a-bbb a-a aa-a aaa-a b-a bb-a bbb-a a-a aa-aa aaa-aaa b-b bb-bb bbb-bbb ';

% Create assoc array.
A = Assoc(rowStr,colStr,valStr);

% Get subarrays.
A1r = A('a b ',:);
A2r = A('a* ',1:3);
A3r = A('a : b ',:);
A1c = A(:,'a b ');
A2c = A(1:3,'a* ');
A3c = A(:,'a : b ');
A1v = (A < 'b ');

%save([mfilename '.mat'],'-v6','A1r','A2r','A3r','A1c','A2c','A3c','A1v')


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
