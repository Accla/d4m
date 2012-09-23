%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Setup database tables.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('on'); more('off')                        % Turn off echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%DB = DBsetupD4Muser;    % Create binding to database.  Shorthand for:
% DB = DBserver('llgrid-db-00.llgrid.ll.mit.edu:2181','Accumulo','accumulo', 'd4muser',password);
DB = DBserver('llgrid-db-00.llgrid.ll.mit.edu:2181','Accumulo','accumulo', 'root','secret');
% Check status of database at http://llgrid-db-00.llgrid.ll.mit.edu:50095

Tadj = DB('TgraphAdj','TgraphAdjT');        % Create database table pair for holding adjacency matrix.
TadjDeg = DB('TgraphAdjDeg');               % Create database table for counting degree.
TadjDeg = addColCombiner(TadjDeg,'OutDeg,InDeg,','sum');  % Set accumulator columns.

Tedge = DB('TgraphEdge','TgraphEdgeT');     % Create database table pair for holding incidense matrix.
TedgeDeg = DB('TgraphEdgeDeg');             % Create database table for counting degree.
TgraphOutDeg = addColCombiner(TgraphOutDeg,'Degree,','sum');  % Set accumulator columns.

% Set splits.


% Check splits.


% Delete all tables.
% delete(Tadj); delete(TadjDeg); delete(Tedge); delete (TedgeDeg);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

