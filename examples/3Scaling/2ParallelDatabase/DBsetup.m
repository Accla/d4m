%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Setup binding to a database.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

myName = 'kepner_';      % SET LOCAL LABEL TO AVOID COLLISIONS.

DB = DBsetupLLGrid('classdb01');                                      % Create binding to database.  Shorthand for:
% DB = DBserver('llgrid-db-00.llgrid.ll.mit.edu:2181','Accumulo','accumulo','AccumuloUser',password);
% Check status of database at http://llgrid-db-00.llgrid.ll.mit.edu:50095                                                

Tadj = DB([myName 'TgraphAdj'],[myName 'TgraphAdjT']);    % Create database table pair for holding adjacency matrix.
TadjDeg = DB([myName 'TgraphAdjDeg']);                    % Create database table for counting degree.

Tedge = DB([myName 'TgraphEdge'],[myName 'TgraphEdgeT']); % Create database table pair for holding incidense matrix.
TedgeDeg = DB([myName 'TgraphEdgeDeg']);                  % Create database table for counting degree.
Tedge1 = DB([myName 'TgraphEdge']);                       % Create binding for setting split.
Tedge2 = DB([myName 'TgraphEdgeT']);                      % Create binding for setting split.


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

