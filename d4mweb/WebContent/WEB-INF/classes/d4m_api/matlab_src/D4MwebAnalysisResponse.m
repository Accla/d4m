function queryJSONCSV = D4MwebAnalysisResponse(queryJSONCSV)

  global D4MqueryGlobal

  % ************************************************
  % Initialize database
  if(! exist('D4mMqueryGlobal.DB'))
     DBsetup;
  end
  if(!exist('D4MqueryGlobal.T'))
     T = DB('ReutersDataTEST','ReutersDataTESTt');
  end
  if(exist('D4MqueryGlobal.Ti') == 0)
     Ti = DB('ReutersDataTEST_index');
  end
  % Create globals for query functions.
  D4MqueryGlobal.DB = DB;
  D4MqueryGlobal.T=T;
  D4MqueryGlobal.Ti = Ti;
%  D4MwebAnalysisResponseInit
  T = D4MqueryGlobal.T;  Ti = D4MqueryGlobal.Ti;
  % ************************************************
  % ************************************************
  J = parseJSON(queryJSONCSV);

  Aq = JSONCSV2assoc(queryJSONCSV);

  qName = Row(Aq(1,1));
  qName = qName(1:end-1);
  disp(qName);

  if strcmp(qName,'GetAnalysisDefaults')

    thisFuncHome = fileparts(mfilename('fullpath'));
    Ar = ReadCSV([thisFuncHome '/Reuters3D4ManalysisDefaults.csv']);
    Ar = Ar - Ar(:,'ZZZallRows,');    % Delete last placeholder column. 

  elseif nnz(qName)
   % DBsetup;  % Initialize DB.

    % Parse fields that are common to all queries.
%    v = Val(Aq(1,'Table,'));  TableStr = v(1:end-1);
%    v = Val(Aq(1,'TableTranspose,'));
%    if nnz(v)
%      TableTransposeStr = v(1:end-1);
%      T = DB(TableStr,TableTransposeStr);
%    else
%      T = DB(TableStr);
%    end    
%    v = Val(Aq(1,'TableInded,'));
%    if nnz(v)
%      TableIndedStr = v(1:end-1);
%      Ti = DB(TableIndexStr);
%    end

    TimeRange = Val(Aq(1,'TimeRange,'));          TimeRange = TimeRange(1:end-1);
    TimeRangeNum = str2num(TimeRange);
    ColumnSeed = Val(Aq(1,'ColumnSeed,'));        ColumnSeed = ColumnSeed(1:end-1);
    ColumnSeedNum = str2num(ColumnSeed);
    ColumnClutter = Val(Aq(1,'ColumnClutter,'));  ColumnClutter = ColumnClutter(1:end-1);
    ColumnType = Val(Aq(1,'ColumnType,'));        ColumnType = ColumnType(1:end-1);
    ColumnTypeExp = CatStr(ColumnType,' ','*,');  % Column type regular expressions.

    % Determine if TimeRange or ColumnSeed is a number.

    if not(isempty(TimeRangeNum))
       A = dblLogi(DBtableRandRow(T,Ti,TimeRangeNum));      % Get a random set of rows.
       A = A(:,ColumnTypeExp);          % Restrict to ColumnType.
       A = A(Row(A(:,ColumnSeed)),:);  % Restrict to rows containing ColumnSeed.
    elseif not(isempty(ColumnSeedNum))
       A = dblLogi(DBtableRandRow(T,Ti,ColumnSeedNum));     % Get a random set of rows.
       A = A(TimeRange,ColumnTypeExp);  % Restrict to TimeRange and ColumnType.
       A = randCol(A,ColumnSeedNum);       % Get random columns from this set.
    else
       A = dblLogi(T(TimeRange,ColumnSeed));      % Get rows and columns.
       A = A(:,ColumnTypeExp);          % Restrict to ColumnType.
    end

    % Eliminate clutter rows.
    if (nnz(ColumnClutter) > 1)
      Aclut = A(:,ColumnClutter);
      if nnz(Aclut)
        A = A - A(Row(Aclut));
      end
    end

    if strcmp(qName,'Stats/Type/Count/')

       Ar = sum(col2type(A,'/'),1);

    elseif strcmp(qName,'Stats/Type/Correlation/')

       Ar = sqIn(col2type(A,'/'));

    elseif strcmp(qName,'Stats/Value/Count/')

       Ar = sum(A,1);

    elseif strcmp(qName,'Stats/Value/Correlation/')

       Ar = sqIn(A);

    elseif strcmp(qName,'Data/Graph/')

    elseif strcmp(qName,'Data/SpaceTime/')

    elseif strcmp(qName,'Data/Convolution/')

    elseif strcmp(qName,'Data/Pair/Check/')

    elseif strcmp(qName,'Data/Type/Change/')

    elseif strcmp(qName,'Semantic/Pair/Extend/')

    elseif strcmp(qName,'Semantic/Pair/Check/')

    elseif strcmp(qName,'Semantic/Seed/Extend/')

    elseif strcmp(qName,'Semantic/Seed/Graph/')

    end
  end

  queryJSONCSV = Assoc2JSONCSV(Ar,J.rowSeparator,J.columnSeparator,'QueryResponse');
  %queryJSONCSV = Ar;

end

