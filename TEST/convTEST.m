% Create assoc array entries.
indexStr =  '01 02 03 04 21 22 23 24 41 51 61 62 63 64 ';
val      = [  1  1  1  1  4  3  2  1 4  5  6  6  6  6 ].';

win2 = [0.5 0.5];
win3 = [0.5 1 0.5];

% Create assoc arrays.
Acol = Assoc(indexStr,'Col1 ',val);
Arow = Assoc('Row1 ',indexStr,val);


Acrow2 = conv(Arow,win2);
Acrow3 = conv(Arow,win3);
Accol2 = conv(Acol,win2);
Accol3 = conv(Acol,win3);

save([mfilename '.mat'],'-v6','Acrow2','Acrow3','Accol2','Accol3');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

