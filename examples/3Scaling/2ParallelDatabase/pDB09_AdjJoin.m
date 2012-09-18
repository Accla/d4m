%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Show how to do joins..
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('off'); more('off')                    % Turn off echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

DB = DBsetupD4Muser;                        % Create binding to database.

Tadj = DB('TgraphAdj','TgraphAdjT');        % Bind to adjacency matrix table.
TadjDeg = DB('TgraphAdjDeg');               % Bind to degree table.

MaxDeg = 1000;                              % Set max allowable degree.
MaxElem = 100000;                           % Set max elements in iterator.

col1 = 
col2 = 

Ajoin = Tadj(Row(sum(dblLog(Tadj(:,[col1 col2])),2) == 2),:);  % Simple join.


type1 = StartsWith('1111,');
type2 = StartsWith('2222,');
 
TadjIt1 = Iterator(Tadj,'elements',MaxElem);
TadjIt2 = Iterator(Tadj,'elements',MaxElem);

TadjIt1(




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

