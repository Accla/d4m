%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Tests various analysis on Reuters data via web function.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Script variables.
TABLECREATE=1;  % Create new tables.
TABLEDELETE=0;  % Delete tables after.
NODB = 0;  % Use associative arrays instead of DB;
NOJAVA = 0;  % Don't call Java bindings (for Octave on Mac w/flat files).
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

QueryGetAnalysisDefaults = Assoc('GetAnalysisDefaults,','Col1,','1,');

if NOJAVA
  Ar = D4ManalysisResponse(QueryGetAnalysisDefaults);
else
  % Create query for getting list of analysis and their default arguments.
  %QueryGetAnalysisDefaultsJSON = ['{"name":"QueryRequest",' ...
  %  '"rowSeparator":"\n","columnSeparator":",",' ...
  %  '"Nrows":2,"Ncolumns":2,"Nentries": 3,' ...
  %  '"CSVstring":",Col1\nGetAnalysisDefaults,1"}'];
  QueryGetAnalysisDefaultsJSON = Assoc2JSONCSV(QueryGetAnalysisDefaults,LF,',','QueryResponse');
  J = parseJSON(QueryGetAnalysisDefaultsJSON);

  % Pass in JSON query and get response.
  ResponseGetAnalysisDefaultsJSON = D4MwebAnalysisResponse(QueryGetAnalysisDefaultsJSON);

  % Convert JSON to assoc.
  Ar = JSONCSV2assoc(ResponseGetAnalysisDefaultsJSON);
end


D4MqueryGlobal.DB = DB;


if 1
  % Test each analytic.
  ArSize = size(Ar);
  for i=1:ArSize(1)
    if NOJAVA
      Ari = D4ManalysisResponse(Ar(i,:));
    else
      QueryAnalysis1JSON = Assoc2JSONCSV(Ar(i,:),J.rowSeparator,J.columnSeparator,'QueryResponse');
      ResponseAnalysis1JSON = D4MwebAnalysisResponse(QueryAnalysis1JSON);
      ResponseAnalysis1 = JSONCSV2assoc(ResponseAnalysis1JSON);
    end
  end
end

% Set default output.
ArRow = Row(Ar);
%Ar = Ar + Assoc(ArRow,'Output,','file://output.csv,');
%  Ar = Ar + Assoc(ArRow,'Output,','screen,');
Ar = Ar + Assoc(ArRow,'Output,','display(A),');

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
          if NOJAVA
            A = D4ManalysisResponse(Ar(AnalyticChoice,:));
          else
            QueryAnalysisJSON = Assoc2JSONCSV(Ar(AnalyticChoice,:),J.rowSeparator,J.columnSeparator,'QueryResponse');
            ResponseAnalysisJSON = D4MwebAnalysisResponse(QueryAnalysisJSON);
            A = JSONCSV2assoc(ResponseAnalysisJSON);
          end
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

