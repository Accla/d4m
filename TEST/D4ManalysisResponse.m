function Ar = D4ManalysisResponse(Aq)
% D4ManalysisResponse: Framework for interacting with multiple analysis techniques.
% TODO: How to handle different Time/Seed combos (may need Tables with row+col query).
% TODO: Debug Semantic/Seed/Graph/


  global D4MqueryGlobal
  DB = D4MqueryGlobal.DB;
  T = D4MqueryGlobal.T;  Ti = D4MqueryGlobal.Ti;

  PairSep = '|';  TypeSep = '/';
  NL = char(10); CR = char(13);
  Operator = '=+~*';
  UNTERMINATED = 'Unknown/TBD';

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
       ColumnSeed = Col(A);
    elseif not(isempty(ColumnSeedNum))
       A = dblLogi(DBtableRandRow(T,Ti,ColumnSeedNum));     % Get a random set of rows.
       A = A(TimeRange,ColumnTypeExp);  % Restrict to TimeRange and ColumnType.
       A = randCol(A,ColumnSeedNum);       % Get random columns from this set.
       ColumnSeed = Col(A);
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

    % Parse optional arguments.
    PairList = Val(Aq(1,'PairList,'));
    PairList = PairList(1:end-1);

    PairReplace = Val(Aq(1,'PairReplace,'));
    PairReplace = PairReplace(1:end-1);

    PairCross = Val(Aq(1,'PairCross,'));
    PairCross = PairCross(1:end-1);


    if strcmp(qName,'Stats/Type/Count/')

       Ar = sum(col2type(A,'/'),1);

    elseif strcmp(qName,'Stats/Type/Correlation/')

       Ar = sqIn(col2type(A,'/'));

    elseif strcmp(qName,'Stats/Value/Count/')

       Ar = sum(A,1);

    elseif strcmp(qName,'Stats/Value/Correlation/')

       Ar = sqIn(A);

    elseif strcmp(qName,'Data/Graph/')

%      c0 = Col(A);
      disp(['Start set size: ' num2str(NumStr(ColumnSeed))]);
      k = Val(str2num(Aq(1,'GraphDepth,')));
      c1 = columnNeighbors(T,ColumnSeed,ColumnTypeExp,ColumnClutter,k);
      disp(['Graph set size: ' num2str(NumStr(c1))]);
      Ar = Assoc(1,c1,1);

    elseif strcmp(qName,'Data/Graph/Clutter/')

%      c0 = Col(A);
      disp(['Start set size: ' num2str(NumStr(ColumnSeed))]);
      k = Val(str2num(Aq(1,'GraphDepth,')));
      c1 = columnNeighbors(T,ColumnSeed,ColumnTypeExp,ColumnClutter,k);
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
      Ar = conv(Av,f) > str2num(Val(Aq(1,'Thresh,')));

    elseif strcmp(qName,'Data/Pair/Check/')

      Ar = PairCheck(A,PairList,PairSep);

    elseif strcmp(qName,'Data/Type/Change/')

      ColumnTypeExpMat = Str2mat(ColumnTypeExp);
      colSb = Mat2str(ColumnTypeExpMat(1,:));
      colSc = Mat2str(ColumnTypeExpMat(2,:));

      AA = CatKeyMul(transpose(A(:,colSb)),A(:,colSc));
      Ar = (sum(dblLogi(AA),2) > 1) + transpose(sum(dblLogi(AA),1) > 1);

    elseif strcmp(qName,'Semantic/Pair/Extend/')

 %      PairReplace = 'ProperName/|=NE_PERSON/|;|ProperName/|+NE_PERSON_MILITARY/;|ProperName/|=NE_LOCATION/;';

      Ar = SemanticPairExtend(A,PairList,PairReplace,ColumnClutter);

    elseif strcmp(qName,'Semantic/Pair/Check/')

      %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
      % Semantic pair check.
      %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
 
      % Extend pairs semantically.
      Ax12o_x12 = SemanticPairExtend(A,PairList,PairReplace,ColumnClutter);

      % Check for the existence of pairs.
      ArowT_x12 = PairCheck(A,Col(Ax12o_x12),PairSep);

      Ar = Assoc('','','');

      if nnz(ArowT_x12)
        % Tie found pairs back to original pairs.
        [x12o rowT x12] = find(CatKeyMul(NewSep(Ax12o_x12,NL),NewSep(transpose(ArowT_x12),NL)));
        % Replace ',' with ';' so that CSV translation works.
        Ax12o_x12_rowT = Assoc(x12o,strrep(x12,',',';'),rowT,@AssocCatStrFunc);
        Ax12o_x12_rowT = putVal(Ax12o_x12_rowT,strrep(Val(Ax12o_x12_rowT),',',';'));
        Ar = Ax12o_x12_rowT;
      end

    elseif strcmp(qName,'Semantic/Seed/Extend/')

      % Create seeds as unterminated pairs.
      x1o = ColumnSeed;
      y1o = x1o;
      y2o = [UNTERMINATED y1o(end)];
      PairList = CatStr(x1o,PairSep,y2o);

      if (TimeRange(end) == ';')
        AT = T(TimeRange,:);
      end
      Ar = SemanticPairExtend(AT,PairList,PairReplace,ColumnClutter);

    elseif strcmp(qName,'Semantic/Seed/Graph/')


      % Create seeds as unterminated pairs.
      x1o = ColumnSeed;
      y1o = x1o;
      y2o = [UNTERMINATED y1o(end)];
      PairList = CatStr(x1o,PairSep,y2o);

      if (TimeRange(end) == ';')
        AT = T(TimeRange,:);
      end
      Ay12o_y12 = SemanticPairExtend(AT,PairList,PairReplace,ColumnClutter);

      % Look for crossover.
      Ay12_z12 = SemanticPairExtend(AT,Col(Ay12o_y12),PairCross,ColumnClutter);

      % Split rows and columns.
      Ay12_y1z2 = Assoc('','','');
      if nnz(Ay12_z12);
        [r c v] = find(Ay12_z12);  [r1 r2] = SplitStr(r,PairSep);  [c1 c2] = SplitStr(c,PairSep);
        Ay12_y1z2 = Assoc(r,CatStr(r1,PairSep,c1),1);
        % + Assoc(r,CatStr(r1,PairSep,c2),1) + Assoc(r,CatStr(r2,PairSep,c1),1) + Assoc(r,CatStr(r2,PairSep,c2),1);
      end

      % Tie back to original seeds.
      Ay12o_z12 = Ay12o_y12 * Ay12_z12;
      Ar = Ay12o_z12;

    end
  end


end

