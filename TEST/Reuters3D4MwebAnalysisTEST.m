%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Tests various analysis on Reuters data via web function.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Script variables.
TABLECREATE=1;  % Create new tables.
TABLEDELETE=1;  % Delete tables after.
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


% Create query for getting list of analysis and their default arguments.
QueryGetAnalysisDefaultsJSON = ['{"name":"QueryRequest",' ...
  '"rowSeparator":"\n","columnSeparator":",",' ...
  '"Nrows":2,"Ncolumns":2,"Nentries": 3,' ...
  '"CSVstring":",Col1\nGetAnalysisDefaults,1"}'];
J = parseJSON(QueryGetAnalysisDefaultsJSON);

% Pass in JSON query and get response.
ResponseGetAnalysisDefaultsJSON = D4MwebAnalysisResponse(QueryGetAnalysisDefaultsJSON);

% Convert JSON to assoc.
Ar = JSONCSV2assoc(ResponseGetAnalysisDefaultsJSON);

% Test each analytic.
D4MqueryGlobal.DB = DB;

QueryAnalysis1JSON = Assoc2JSONCSV(Ar('Stats/Type/Count/,',:),J.rowSeparator,J.columnSeparator,'QueryResponse');
ResponseAnalysis1JSON = D4MwebAnalysisResponse(QueryAnalysis1JSON);
ResponseAnalysis1 = JSONCSV2assoc(ResponseAnalysis1JSON);

QueryAnalysis2JSON = Assoc2JSONCSV(Ar('Stats/Type/Correlation/,',:),J.rowSeparator,J.columnSeparator,'QueryResponse');
ResponseAnalysis2JSON = D4MwebAnalysisResponse(QueryAnalysis2JSON);
ResponseAnalysis2 = JSONCSV2assoc(ResponseAnalysis2JSON);

QueryAnalysis3JSON = Assoc2JSONCSV(Ar('Stats/Value/Count/,',:),J.rowSeparator,J.columnSeparator,'QueryResponse');
ResponseAnalysis3JSON = D4MwebAnalysisResponse(QueryAnalysis3JSON);
ResponseAnalysis3 = JSONCSV2assoc(ResponseAnalysis3JSON);

QueryAnalysis4JSON = Assoc2JSONCSV(Ar('Stats/Value/Correlation/,',:),J.rowSeparator,J.columnSeparator,'QueryResponse');
ResponseAnalysis4JSON = D4MwebAnalysisResponse(QueryAnalysis4JSON);
ResponseAnalysis4 = JSONCSV2assoc(ResponseAnalysis4JSON);


% Delete index and table.
if TABLEDELETE
  deleteForce(T); deleteForce(Ti);
end

% save([mfilename '.mat'],'-v6','QueryResponseGetTrackNamesJSON','QueryResponseMHtrackJSON');

if 0
end % If 0


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

