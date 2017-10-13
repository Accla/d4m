%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Setup binding to a database.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


% Setup tables relative to a base.

%INSTANCENAME = 'class-db05';
%[DB,G] = DBsetupLLGrid('class-db05');                                      % Create binding to database.  Shorthand for:
INSTANCENAME = 'uno';
ZKHOSTS = 'localhost:2181';
DB = DBserver(ZKHOSTS,'Accumulo',INSTANCENAME,'root','secret');
G = DBaddJavaOps('edu.mit.ll.graphulo.MatlabGraphulo',INSTANCENAME,ZKHOSTS,'root','secret');
% AC = edu.washington.cs.laragraphulo.opt.AccumuloConfigImpl(INSTANCENAME,'localhost:2181','root','secret');
% DB = DBserver('llgrid-db-00.llgrid.ll.mit.edu:2181','Accumulo','accumulo','AccumuloUser',password);
% Check status of database at http://llgrid-db-00.llgrid.ll.mit.edu:50095                                                

% Tadj = DB([base 'TgraphAdj'],[base 'TgraphAdjT']);    % Create database table pair for holding adjacency matrix.
% TadjDeg = DB([base 'TgraphAdjDeg']);                    % Create database table for counting degree.

% Tedge = DB([base 'TgraphEdge'],[base 'TgraphEdgeT']); % Create database table pair for holding incidense matrix.
% TedgeDeg = DB([base 'TgraphEdgeDeg']);                  % Create database table for counting degree.
% Tedge1 = DB([base 'TgraphEdge']);                       % Create binding for setting split.
% Tedge2 = DB([base 'TgraphEdgeT']);                      % Create binding for setting split.


