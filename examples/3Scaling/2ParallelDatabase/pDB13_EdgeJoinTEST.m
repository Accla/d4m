%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Show how to do joins with and incidence matrix.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
DBsetup;                                    % Create binding to database.                                                
echo('on'); more('off')                     % Turn off echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

MaxElem = 1000;                             % Set max elements in iterator.

col1 = 'In/1,';    col2 = 'In/100,';       % Pick two columns to join.

E1 = Tedge(Row(Tedge(:,col1)),:);           % Get edges with in vertex col1.
E2 = Tedge(Row(Tedge(:,col2)),:);           % Get edges with in vertex col2.
Ejoin  = E2(:,Col(E1));                     % Find out vertices that that are in both.

figure; spy(Ejoin); xlabel('vertex'); ylabel('edge');       % Display.

colRange1 = StartsWith('In/111,');          % Set column range.
colRange2 = StartsWith('In/222,');          % Set column range.
 
TedgeIt1 = Iterator(Tedge,'elements',MaxElem);  % Set up query iterator.
TedgeIt2 = Iterator(Tedge,'elements',MaxElem);  % Set up query iterator.

E1 = dblLogi(TedgeIt1(:,colRange1));         % Start first query iterator.
E1outDeg = Assoc('','','');
while nnz(E1)
  E1 = dblLogi(Tedge(Row(E1),:));            % Get edges containing these in vertices.
  E1outDeg = E1outDeg + sum(E1(:,StartsWith('Out/,')),1);    % Compute out degree.
  E1 = dblLogi(TedgeIt1());                  % Get next query.
end

E2 = dblLogi(TedgeIt2(:,colRange2));         % Start first query iterator.
E2outDeg = Assoc('','','');
while nnz(E2)
  E2 = dblLogi(Tedge(Row(E2),:));            % Get edges containing these in vertices.
  E2outDeg = E2outDeg + sum(E2(:,StartsWith('Out/,')),1);    % Compute out degree.
  E2 = dblLogi(TedgeIt2());                  % Get next query.
end

E12outDeg = E1outDeg + E2outDeg;             % Add together.
EjoinRange = E12outDeg(:,Col(E2outDeg(:,Col(E1outDeg))));      % Join columns.
echo('off'); figure; plot(EjoinRange); xlabel('vertex'); ylabel('degree');       % Display.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

