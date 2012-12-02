%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Show how to do joins..
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
DBsetup;                                    % Create binding to database and tables.
echo('on'); more('off')                     % Turn off echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

MaxElem = 1000;                             % Set max elements in iterator.

col1 = '1,';        col2 = '100,';          % Pick two columns to join.

Ajoin = Tadj(Row(sum(dblLogi(Tadj(:,[col1 col2])),2) == 2),:);     % Find all rows with these columns.
figure; spy(Ajoin); xlabel('end vertex'); ylabel('start vertex');  % Display.

colRange1 = StartsWith('111,');             % Set column range.
colRange2 = StartsWith('222,');             % Set column range.
 
TadjIt1 = Iterator(Tadj,'elements',MaxElem);  % Set up query iterator.
TadjIt2 = Iterator(Tadj,'elements',MaxElem);  % Set up query iterator.

A1 = dblLogi(TadjIt1(:,colRange1));         % Start first query iterator.
A1outDeg = Assoc('','','');
while nnz(A1)
  A1outDeg = A1outDeg + sum(A1,2);          % Combine.
  A1 = dblLogi(TadjIt1());                  % Run next query iterator.
end

A2 = dblLogi(TadjIt2(:,colRange2));         % Start second query iterator.
A2outDeg = Assoc('','','');
while nnz(A2)
  A2outDeg = A2outDeg + sum(A2,2);          % Combine.
  A2 = dblLogi(TadjIt2());                  % Run next query iterator.
end

AjoinRange = Tadj(Row(A1outDeg(Row(A2outDeg),:)),:);                    % Join columns.
echo('off'); figure; spy(AjoinRange); xlabel('end vertex'); ylabel('start vertex');  % Display.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

