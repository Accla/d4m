% Create assoc array entries.
[rowText colText valText] = FindCSV('Text2word.tsv');

Atext = ReadCSV('Text2word.tsv');

Aword = Text2word(rowText,valText,' ');

[rowWord colWord valWord] = Text2word(rowText,valText,' ');

AwordPos = Assoc(rowWord,colWord,valWord,@min);


%save([mfilename '.mat'],'-v6','A01','A02','A03','A04','A05','A06');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

