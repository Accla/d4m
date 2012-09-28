%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Setup database tables.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('on'); more('off')                        % Turn off echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

DBsetup;                                    % Create binding to database.

Tadj = DB('TgraphAdj','TgraphAdjT');        % Create database table pair for holding adjacency matrix.
TadjDeg = DB('TgraphAdjDeg');               % Create database table for counting degree.
TadjDeg = addColCombiner(TadjDeg,'OutDeg,InDeg,','sum');  % Set accumulator columns.

Tedge = DB('TgraphEdge','TgraphEdgeT');     % Create database table pair for holding incidense matrix.
TedgeDeg = DB('TgraphEdgeDeg');             % Create database table for counting degree.
TedgeDeg = addColCombiner(TedgeDeg,'Degree,','sum');  % Set accumulator columns.

% Set splits.


% Check splits.


% Delete all tables.
%delete(Tadj); delete(TadjDeg); delete(Tedge); delete (TedgeDeg);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

