%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Setup binding to a database.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

myName = ['mytable_' num2str(SCALE(s)) '_'];      % SET LOCAL LABEL TO AVOID COLLISIONS.

[DB,G] = DBsetupLLGrid('txg-graphulotest-02');            % Create binding to database.  Shorthand for:
% DB = DBserver('llgrid-db-00.llgrid.ll.mit.edu:2181','Accumulo','accumulo','AccumuloUser',password);

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