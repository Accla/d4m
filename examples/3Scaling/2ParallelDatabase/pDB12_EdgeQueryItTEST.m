%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Query incidence matrix in a database table using an iterator.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
DBsetup;                                    % Create binding to database and tables.
echo('on'); more('off')                     % Turn off echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

MaxElem = 1000;                             % Set max elements in iterator.
Nv0 = 100;
v0 = ceil(10000.*rand(Nv0,1));              % Create a starting set of vertices.

myV = 1:numel(v0);
%myV = global_ind(zeros(numel(v0),1,map([Np 1],{},0:Np-1)));    % PARALLEL.

v0str = sprintf('Out/%d,',v0(myV));         % Convert to string list.

TedgeIt = Iterator(Tedge,'elements',MaxElem);  % Set up query iterator.

E = dblLogi(TedgeIt(:,v0str));              % Start query iterator.

EinDeg = Assoc('','','');                   % Initialize.

while nnz(E)
  E1 = dblLogi(Tedge(Row(E),:));            % Get edges containing these out vertices.
  EinDeg = EinDeg + sum(E1(:,StartsWith('In/,')),1);    % Compute in degree.
  E = dblLogi(TedgeIt());                   % Get next query.
end

%EinDeg = gagg(EinDeg);                     % PARALLEL.
EmaxInDeg = (EinDeg == max(max(Adj(EinDeg))))

echo('off');
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

