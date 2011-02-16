%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Init script for D4MwebAnalysisResponse
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Script variables.
TABLECREATE=0;  % Create new tables.
TABLEDELETE=0;  % Delete tables after.
NODB = 0;  % Use associative arrays instead of DB;
LF = char(10); CR = char(13);  Q = '''';

global D4MqueryGlobal

if NODB

else
  DBsetup;
  T = DB('ReutersDataTEST','ReutersDataTESTt');
  Ti = DB('ReutersDataTEST_index');
  % Create globals for query functions.
  D4MqueryGlobal.DB = DB;
  D4MqueryGlobal.T = T;
  D4MqueryGlobal.Ti = Ti;
end

if TABLECREATE
  Reuters3parse;     % Parse reuters data.
  Reuters3insert;      % Insert doc/entity into DB.  Creates T.

  % Create an index table for drawing random rows from T.
  D4MqueryGlobal.T = T;
  if NODB
    D4MqueryGlobal.Ti = Assoc('','','');
  else
    deleteForce(Ti);
    Ti = DB('ReutersDataTEST_index');
    Ti = DBtableIndexRow(T,Ti,1);
    D4MqueryGlobal.Ti = Ti;
  end
end

nl = LF; cr = CR;


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

