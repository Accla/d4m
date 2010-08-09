% Create assoc array entries.
iStr =  '01 02 03 04 21 22 23 24 41 51 61 62 63 64 ';
iVal      = [  1  1  1  1  4  3  2  1 4  5  6  6  6  6 ].';

% 0x0 cases.
A01 = Assoc('',iStr,iVal);
A02 = Assoc(iVal,iStr,'');
A03 = Assoc(iVal,[],'');
A04 = Assoc('',[],[]);
A05 = Assoc('','',[]);
A06 = Assoc('',iVal,[]);

A11 = Assoc('a ',iStr,iVal);
A12 = Assoc(iVal,iStr,'a ');
A13 = Assoc(iVal,1,'a ');
A14 = Assoc('a ',1,1);
A15 = Assoc('a ','a ',1);
A16 = Assoc('a ',iVal,1);

%save([mfilename '.mat'],'-v6','A01','A02','A03','A04','A05','A06');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

