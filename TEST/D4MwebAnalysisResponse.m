function queryJSONCSV = D4MwebAnalysisResponse(queryJSONCSV)

  global D4MqueryGlobal
  DB = D4MqueryGlobal.DB;
  T = D4MqueryGlobal.T;  Ti = D4MqueryGlobal.Ti;

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
    DBsetup;  % Initialize DB.

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

    TimeRange = Val(Aq(1,'TimeRange,'));    TimeRange = TimeRange(1:end-1);
    TimeRangeNum = str2num(TimeRange);
    if ((TimeRange(end) == ';') | (TimeRange(end) == ':'))
      TimeRangeNum = '';
    end
    ColumnSeed = Val(Aq(1,'ColumnSeed,'));    ColumnSeed = ColumnSeed(1:end-1);
    ColumnSeedNum = str2num(ColumnSeed);
    if ((ColumnSeed(end) == ';') | (ColumnSeed(end) == ':'))
      ColumnSeedNum = '';
    end
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
       A = T(TimeRange,:);      % Get rows.
       A = A(:,ColumnSeed);      % Get columns.
       A = A(:,ColumnTypeExp);          % Restrict to ColumnType.
    end

    % Eliminate clutter rows.
    if (nnz(ColumnClutter) > 1)
      Aclut = A(:,ColumnClutter);
      if nnz(Aclut)
        A = A - A(Row(Aclut),:);
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

      c0 = Col(A);
      disp(['Start set size: ' num2str(NumStr(c0))]);
      k = Val(str2num(Aq(1,'GraphDepth,')));
      c1 = columnNeighbors(T,c0,ColumnTypeExp,ColumnClutter,k);
      disp(['Graph set size: ' num2str(NumStr(c1))]);
      Ar = Assoc(1,c1,1);

    elseif strcmp(qName,'Data/Graph/Clutter/')

      c0 = Col(A);
      disp(['Start set size: ' num2str(NumStr(c0))]);
      k = Val(str2num(Aq(1,'GraphDepth,')));
      c1 = columnNeighbors(T,c0,ColumnTypeExp,ColumnClutter,k);
      disp(['Graph set size: ' num2str(NumStr(c1))]);
      Ar = Assoc(1,c1,1);
      Thresh = Val(str2num(Aq(1,'Thresh,')));
      disp('Look for clutter.')
      Ar = sum(T(:,c1),1) > Thresh;

    elseif strcmp(qName,'Data/SpaceTime/')

      LatLonPoints = Val(Aq(1,'LatLonPoints,'));
      [LatLonType LatLon] = SplitStr(LatLonPoints(1:end-1),'/');
      LatLon = str2num(LatLon);
      LatLonTypeMat = Str2mat(LatLonType);
      LatLonTypeExp = CatStr(Mat2str(LatLonTypeMat(1:2,:)),' ','*,');

      Axy = str2num(col2type(A(:,LatLonTypeExp),'/'));
      inS = inpolygon( Adj(Axy(:,1)), Adj(Axy(:,2)), LatLon(1:2:end), LatLon(2:2:end) );
      Ar = A(Row(Axy(find(inS),:)),:);

    elseif strcmp(qName,'Data/Convolution/')

      f = ones(str2num(Val(Aq(1,'FilterWidth,'))),1);          % Set filter width.
      Av = double(logical(col2val(sum(A,1),'/')));
      Ar = conv(Av,f) > str2num(Val(Aq(1,'FilterThreshold,')));

    elseif strcmp(qName,'Data/Pair/Check/')

      PairList = Val(Aq(1,'PairList,'));
      PairList = PairList(1:end-1);
      Ar = PairCheck(A,PairList,'|');

    elseif strcmp(qName,'Data/Type/Change/')

      ColumnTypeExpMat = Str2mat(ColumnTypeExp);
      colSb = Mat2str(ColumnTypeExpMat(1,:));
      colSc = Mat2str(ColumnTypeExpMat(2,:));
      r = Row(sum(A(Row(sum(A(:,colSb),2) == 1),[colSb colSc]),2) == 2);

      [tmp c1 tmp] = find(A(r,colSb));
      [tmp c2 tmp] = find(A(r,colSc));
      A12 = Assoc(c1,c2,1);
      Ar = (sum(A12,2) > 1) + (sum(A12,1) > 1' );

    elseif strcmp(qName,'Semantic/Pair/Extend/')

      %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
      % Semantic extension of pairs using type data.
      %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
      % (1) TIME/,  (2) TIMELOCAL/, (3) NE_ORGANIZATION/, (4) NE_PERSON/,
      % (5) NE_PERSON_GENERIC/, (6) NE_PERSON_MILITARY/, (7) GEO/, (8) NE_LOCATION/,
      % (9) ProperName/,

      PairList = Val(Aq(1,'PairList,'));
      PairList = PairList(1:end-1);
      x12o = PairList;

      PairReplace = Val(Aq(1,'PairReplace,'));
      PairReplace = PairReplace(1:end-1);
      PairReplaceMat = Str2mat(PairReplace);

      % Append flips.
      [x1o x2o] = SplitStr(x12o,ss);
      x12 = [x12o CatStr(x2o,ss,x1o)];
      x12o = [x12o x12o]; 

      Ax12o_x12 = Assoc('','','');

      % Replace (9) in x1 with (4).
      % ProperName/|NE_PERSON/|;|ProperName/|NE_PERSON_MILITARY/;|ProperName/|NE_LOCATION/;
      for i=1:NumStr(PairReplace);
        iPairReplace = Mat2str(PairReplaceMat(iPairReplace,:));
        iPairReplace = iPairReplace(1:end-1);

        if (iPairReplace(end) == '|')
          [x1 x2] = SplitStr(x12,'|');
          iPairReplaceMat = Str2mat(iPairReplace);
          iPairReplace1 = Mat2str(iPairReplaceMat(1,:));
          iPairReplace2 = Mat2str(iPairReplaceMat(2,:));

          x1 = strrep(x1,iPairReplace1(1:end-1),iPairReplace2(1:end-1));
          x12 = CatStr(x1,ss,x2);
          Ax12o_x12 = Ax12o_x12 + Assoc(x12o,x12,1);
                
        elseif (iPairReplace(1) == '|')

  % Replace (9) in x2 with (6) and (8).
  B = Ax12o_x12(:,[ss colS9]);
  BB = putCol(B,strrep(Col(B),colT9(1:end-1),colT6(1:end-1))) ...
     + putCol(B,strrep(Col(B),colT9(1:end-1),colT8(1:end-1)));
  Ax12o_x12 = (Ax12o_x12 - B) + BB;
          
        end
      end


  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
  % Semantic extension of pairs using meta data.
  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

  % Replace (1) in x2 with a found (4).
  B = Ax12o_x12(:,[ss colS1]);
  BB = ExtendPair(Ax12o_x12,colS1,ss,T,colS4,right,colTclut);
  Ax12o_x12 = (Ax12o_x12 - B) + BB;


  % Replace (3) in x2 with a found (4).
  B = Ax12o_x12(:,[ss colS3]);
  BB = ExtendPair(Ax12o_x12,colS3,ss,T,colS4,right,colTclut);
  Ax12o_x12 = (Ax12o_x12 - B) + BB;


  % Replace (4) in x2 with (6) and (8).
  B = Ax12o_x12(:,[ss colS3 ss colS8]);
  BB = putCol(B,strrep(Col(B),colT4(1:end-1),colT6(1:end-1))) ...
     + putCol(B,strrep(Col(B),colT4(1:end-1),colT8(1:end-1)));
  Ax12o_x12 = (Ax12o_x12 - B) + BB;


  % Extend (6) and (8) in x2 with any found (5) or (7).
  BB = ExtendPair(Ax12o_x12,[colS6 colS8],ss,T,[colS5 colS7],right,colTclut);
  Ax12o_x12 = Ax12o_x12 + BB;


  % Extend (1)-(4) in x1 with any found (1)-(4).
  Ax12o_x12 = Ax12o_x12 + ...
    ExtendPair(Ax12o_x12,Mat2str(colSmat([1 2 3 4],:)),ss,T,Mat2str(colSmat([1 2 3 4],:)),left,colTclut);

  Ar = Ax12o_x12;


    elseif strcmp(qName,'Semantic/Pair/Check/')

    elseif strcmp(qName,'Semantic/Seed/Extend/')

    elseif strcmp(qName,'Semantic/Seed/Graph/')

    end
  end

  queryJSONCSV = Assoc2JSONCSV(Ar,J.rowSeparator,J.columnSeparator,'QueryResponse');
  %queryJSONCSV = Ar;

end

