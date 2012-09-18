%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Query adjacency matrix in a database table.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('off'); more('off')                    % Turn off echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

DB = DBsetupD4Muser;                        % Create binding to database.

Tadj = DB('TgraphAdj','TgraphAdjT');        % Bind to adjacency matrix table.
TadjDeg = DB('TgraphAdjDeg');               % Bind to degree table.

MaxDeg = 1000;                              % Set max allowable degree.
MaxElem = 100000;                           % Set max elements in iterator.

v0 =                                        % Create a starting set of vertices.

A0deg = sum(str2num(TadjDeg(v0deg,:)),2);   % Get combined degrees of those vertices.

v0ok = Row(A0deg < MaxDeg);                 % Eliminate high degree nodes.

v1 = '';                                    % Initialize v1 set.

TadjIt = Iterator(Tadj,'elements',MaxElem); % Set up query iterator.

A1  = dblLogi(TadjIt(v0ok,:));              % Start query.

if nnz(A1)
  % Check v1 against degree threshold and append to v1.
  v1 = [v1 Row(sum(str2num(TadjDeg(Col(A1),:)),2) < MaxDeg)];
  A1  = dblLogi(TadjIt());                  % Continue query.
end

v1 = Assoc(v1,1,1) - Assoc(v0ok,1,1);       % Reduce to just new vertices.

% Next steps:
% - Insert Adjacency matrix into accumulo.
%   - Depth first search with iterators.
% - Insert using splits. Check splits.
% - Insert degree distribution into accumulator columns.
%   - Depth first search avoiding high degree nodes.
% - Insert Incidence matrix and accumulator columns.
%   - Depth first search with iterators avoiding high degree nodes?
%   - Joins using iterators.


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

