%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Setup binding to a database.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


% Setup tables relative to a base.

%INSTANCENAME = 'classdb54';
%[DB,G] = DBsetupLLGrid('classdb54', '/home/gridsan/');                                      % Create binding to database.  Shorthand for:
INSTANCENAME = 'accumulo-1.8-snap';
DB = DBserver('localhost:2181','Accumulo',INSTANCENAME,'root','secret');
G = DBaddJavaOps('edu.mit.ll.graphulo.MatlabGraphulo',INSTANCENAME,'localhost:2181','root','secret');
% DB = DBserver('llgrid-db-00.llgrid.ll.mit.edu:2181','Accumulo','accumulo','AccumuloUser',password);
% Check status of database at http://llgrid-db-00.llgrid.ll.mit.edu:50095                                                

% Tadj = DB([base 'TgraphAdj'],[base 'TgraphAdjT']);    % Create database table pair for holding adjacency matrix.
% TadjDeg = DB([base 'TgraphAdjDeg']);                    % Create database table for counting degree.

% Tedge = DB([base 'TgraphEdge'],[base 'TgraphEdgeT']); % Create database table pair for holding incidense matrix.
% TedgeDeg = DB([base 'TgraphEdgeDeg']);                  % Create database table for counting degree.
% Tedge1 = DB([base 'TgraphEdge']);                       % Create binding for setting split.
% Tedge2 = DB([base 'TgraphEdgeT']);                      % Create binding for setting split.


