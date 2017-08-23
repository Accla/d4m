%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Setup binding to a database.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

myName = 'mytable_';      % SET LOCAL LABEL TO AVOID COLLISIONS.

[DB,G] = DBsetupLLGrid('class-db05');            % Create binding to database.  Shorthand for:
%INSTANCENAME = 'instance-1.7.0';
%DB = DBserver('localhost:2181','Accumulo',INSTANCENAME,'root','secret');
%G = DBaddJavaOps('edu.mit.ll.graphulo.MatlabGraphulo',INSTANCENAME,'localhost:2181','root','secret');

% Create Adj Tables
TadjName=[myName 'Tadj'];
Tadj = DB(TadjName,[TadjName 'T']);    % Create database table pair for holding adjacency matrix.
TadjDeg = DB([myName 'TadjDeg']);                    % Create database table for counting degree.

% Create Incidence Tables
TedgeName=[myName 'Tedge'];
Tedge = DB(TedgeName,[TedgeName 'T']); % Create database table pair for holding incidense matrix.
TedgeDeg = DB([myName 'TedgeDeg']);                  % Create database table for counting degree.

% Create Single Table
TsingleName=[myName 'Tsingle'];
Tsingle = DB(TsingleName);    % Create database table pair for holding single table matrix.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

