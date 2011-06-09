%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Tests various analysis on Reuters data via web function.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Script variables.
TABLECREATE=1;  % Create new tables.
TABLEDELETE=0;  % Delete tables after.
NODB = 1;  % Use associative arrays instead of DB;
LF = char(10); CR = char(13);  Q = '''';
line = '--------------------------------------------';

global D4MqueryGlobal

if NODB
  DB = 1;
  D4MqueryGlobal.DB = DB;
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

if 1
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

  QueryAnalysis5JSON = Assoc2JSONCSV(Ar('Data/Graph/,',:),J.rowSeparator,J.columnSeparator,'QueryResponse');
  ResponseAnalysis5JSON = D4MwebAnalysisResponse(QueryAnalysis5JSON);
  ResponseAnalysis5 = JSONCSV2assoc(ResponseAnalysis5JSON);

  QueryAnalysis6JSON = Assoc2JSONCSV(Ar('Data/Graph/Clutter/,',:),J.rowSeparator,J.columnSeparator,'QueryResponse');
  ResponseAnalysis6JSON = D4MwebAnalysisResponse(QueryAnalysis6JSON);
  ResponseAnalysis6 = JSONCSV2assoc(ResponseAnalysis6JSON);

  QueryAnalysis7JSON = Assoc2JSONCSV(Ar('Data/SpaceTime/,',:),J.rowSeparator,J.columnSeparator,'QueryResponse');
  ResponseAnalysis7JSON = D4MwebAnalysisResponse(QueryAnalysis7JSON);
  ResponseAnalysis7 = JSONCSV2assoc(ResponseAnalysis7JSON);

  QueryAnalysis8JSON = Assoc2JSONCSV(Ar('Data/Convolution/,',:),J.rowSeparator,J.columnSeparator,'QueryResponse');
  ResponseAnalysis8JSON = D4MwebAnalysisResponse(QueryAnalysis8JSON);
  ResponseAnalysis8 = JSONCSV2assoc(ResponseAnalysis8JSON);

  QueryAnalysis9JSON = Assoc2JSONCSV(Ar('Data/Type/Change/,',:),J.rowSeparator,J.columnSeparator,'QueryResponse');
  ResponseAnalysis9JSON = D4MwebAnalysisResponse(QueryAnalysis9JSON);
  ResponseAnalysis9 = JSONCSV2assoc(ResponseAnalysis9JSON);

  QueryAnalysis10JSON = Assoc2JSONCSV(Ar('Data/Pair/Check/,',:),J.rowSeparator,J.columnSeparator,'QueryResponse');
  ResponseAnalysis10JSON = D4MwebAnalysisResponse(QueryAnalysis10JSON);
  ResponseAnalysis10 = JSONCSV2assoc(ResponseAnalysis10JSON);

end


% Set default output.
ArRow = Row(Ar);
%Ar = Ar + Assoc(ArRow,'Output,','file://output.csv,');
%  Ar = Ar + Assoc(ArRow,'Output,','screen,');
Ar = Ar + Assoc(ArRow,'Output,','displayFull(A),');

% Display menu of choices.
AnalyticChoice = '12';
while (AnalyticChoice ~= 'z')

  clc;  disp(line);
  displayFull(Assoc(sprintf('%2.2d,',1:NumStr(Row(Ar))),'ANALYTIC,', ArRow))
  disp([line LF]);
  AnalyticDefault = '12';
  AnalyticChoice = input(['SELECT ANALYTIC (z=end)[' AnalyticDefault ']: '],'s');
  if isempty(AnalyticChoice)
    AnalyticChoice = AnalyticDefault;
  end
  if strcmp(AnalyticChoice,'z')

  else
    AnalyticChoice = str2num(AnalyticChoice);

    FieldChoice = 'r';
    while (FieldChoice ~= 'z')

      [row col val] = find(Ar(AnalyticChoice,:));
      clc;
      disp([line LF 'ANALYTIC: ' Row(Ar(AnalyticChoice,:)) LF line])
      A1 = Assoc(sprintf('%2.2d,',1:NumStr(col)),'FIELD,', col);
      A2 = Assoc(sprintf('%2.2d,',1:NumStr(col)),'VALUE,', val);
      displayFull(A1+A2);
      disp([line LF]);

      FieldDefault = 'r';
      FieldChoice = input(['SELECT FIELD (r=run,z=end)[' FieldDefault ']: '],'s');
      if isempty(FieldChoice)
        FieldChoice = FieldDefault;
      end
      if strcmp(FieldChoice,'r')
        tic;
          QueryAnalysisJSON = Assoc2JSONCSV(Ar(AnalyticChoice,:),J.rowSeparator,J.columnSeparator,'QueryResponse');
          ResponseAnalysisJSON = D4MwebAnalysisResponse(QueryAnalysisJSON);
          A = JSONCSV2assoc(ResponseAnalysisJSON);
        ResponseTime = toc;
        clc; disp(line); more('on');
        OutputStr = Val(Ar(AnalyticChoice,'Output,'));
        eval(OutputStr(1:end-1));
        disp(line); disp(['Analytic Time: ' num2str(ResponseTime)]);  disp(line); 
        more('off');
        temp = input('Hit return to continue.','s');

      elseif strcmp(FieldChoice,'z')
        % Return to main menu.

      else
        FieldChoice = str2num(FieldChoice);
        ArF = Ar(AnalyticChoice,:);
        ArF = ArF(1,FieldChoice);
        ValueChoice = input([Col(ArF) ' [' Val(ArF) ']: '],'s');
        if isempty(ValueChoice)
        else
          Ar = (Ar - ArF) + putVal(ArF,ValueChoice);
        end
      end
    end
  end
end


% Delete index and table.
if TABLEDELETE
  deleteForce(T); deleteForce(Ti);
end

% save([mfilename '.mat'],'-v6','QueryResponseGetTrackNamesJSON','QueryR<esponseMHtrackJSON');

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

