%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Query incidence matrix in a database table.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
DBsetup;                                    % Create binding to database and tables.
echo('on'); more('off')                     % Turn off echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Nv0 = 100;
v0 = ceil(10000.*rand(Nv0,1));              % Create a starting set of vertices.

myV = 1:numel(v0);
%myV = global_ind(zeros(numel(v0),1,map([Np 1],{},0:Np-1)));    % PARALLEL.

v0str = sprintf('Out/%d,',v0(myV));         % Convert to string list.

Edeg = str2num(TedgeDeg(v0str,:));          % Get degrees of vertices.

degMin = 5;  degMax = 10;                   % Select vertices in an out degree range.
v1str = Row( (Edeg(:,'Degree,') > degMin) < degMax );

E = dblLogi( Tedge(Row(Tedge(:,v1str)),:) );       % Get vertex neighbors.

%E = gagg(E);                               % PARALLEL.

echo('off'); figure; spy(E); xlabel('in/out vertex'); ylabel('edge');    % Show.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

