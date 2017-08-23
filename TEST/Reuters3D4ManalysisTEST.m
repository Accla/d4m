%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Tests various analysis on Reuters data.
% TODO: Replace these with new functions that use
% IndexAssocFiles to populate T and Ti and write out T using WriteDBtableIndex.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Script variables.
TABLECREATE=1;  % Create new tables.
TABLEDELETE=0;  % Delete tables after.
NODB = 0;  % Use associative arrays instead of DB;
LF = char(10); CR = char(13);  Q = '''';
UNTERMINATED = 'Unknown/TBD';


disp(repmat(char(' '),24,1));
disp('%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%')
disp('% Setup data.')
disp('%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%')

if NODB

else
  DBsetup;
  eval(MyEcho( 'T = DB($ReutersDataTEST$,$ReutersDataTESTt$); Ti = DB($ReutersDataTEST_index$);' ));
end

if TABLECREATE
  Reuters3parse;     % Parse reuters data.
  Reuters3insert;      % Insert doc/entity into DB.  Creates T.

  % Create an index table for drawing random rows from T.
  if NODB

  else
    deleteForce(Ti);
    Ti = DB('ReutersDataTEST_index');
    Ti = DBtableIndexRow(T,Ti,1);
  end
end

% disp(['Entries in T: ' num2str(nnz(T))]);  % Not currently compatible with Octave.

disp('% Approxmiate number of random rows to get.')
eval(MyEcho( 'Nrand = 500;' ));
disp([LF '% Column type keys.'])
eval(MyEcho( 'ct = $TIME/,TIMELOCAL/,NE_ORGANIZATION/,NE_PERSON/,NE_PERSON_GENERIC/,NE_PERSON_MILITARY/,GEO/,NE_LOCATION/,$;' ));
disp([LF '% Clutter columns.'])
eval(MyEcho( 'cl = $NE_LOCATION/Minnesota,NE_ORGANIZATION/IEEE,NE_PERSON/Billy Bob,$;' ));

colT = ct;  % Column type keys.
colTclut = cl; % Clutter columns.

colTmat = Str2mat(colT);
colS = CatStr(colT,'/','*,');
ct = colS;
colSmat = Str2mat(colS);

% Numerical filter column types.
colTa = Mat2str(colTmat(1,:));  colSa = Mat2str(colSmat(1,:));
filtWidth = 40000;

% Type change columns.
colTb = Mat2str(colTmat(1,:));  colSb = Mat2str(colSmat(1,:));
colTc = Mat2str(colTmat(3,:));  colSc = Mat2str(colSmat(3,:));

% Time/space window search columns.
%timeRange = '19970725,:,19970729,';
timeRange = '19960903,:,19970214,';
colTy = 'Lat,';    colTx = 'Lon,';
colSy = 'Lat/ *,';  colSx = 'Lon/ *,';
Npoly = 6;  Rpoly = 15;
spacePoly = complex(rand(Npoly,1),rand(Npoly,1)) - complex(.5,.5);
[tmp iSort] = sort(angle(spacePoly));
spacePoly = Rpoly*spacePoly(iSort) + 3*Rpoly*complex(1,1);
spacePoly = [spacePoly ; spacePoly(1)];
ApolyBox = num2str(Assoc((1:2).',colTy,[min(real(spacePoly)) ; max(real(spacePoly))]) ...
   + Assoc((1:2).',colTx,[min(imag(spacePoly)) ; max(imag(spacePoly))]));
[cLat rLat vLat] = find(ApolyBox(:,'Lat,').');   [cLon rLon vLon] = find(ApolyBox(:,'Lon,').');
ApolyMert = Assoc(rLat,MertonizeLatLon(CatStr(cLat,'/',vLat),CatStr(cLon,'/',vLon)),1);
spaceRange = [Col(ApolyMert(1,:)) ':,' Col(ApolyMert(2,:))];


% colTd = Mat2str(colTmat(1,:));  colSd = Mat2str(colSmat(1,:));

% Meta nearest neighbor search.
Npair = 10;
left = 1; right = 0;
ss = ';';  % Pair seperator.
colTe = Mat2str(colTmat([1 3 4 6 8],:));  colSe = Mat2str(colSmat([1 3 4 6 8],:));
colTf = Mat2str(colTmat([4 6 8],:));  colSf = Mat2str(colSmat([4 6 8],:));
colT1 =  Mat2str(colTmat(1,:));   colS1 = Mat2str(colSmat(1,:));
colT3 =  Mat2str(colTmat(3,:));   colS3 = Mat2str(colSmat(3,:));
colT4 =  Mat2str(colTmat(4,:));   colS4 = Mat2str(colSmat(4,:));
colT5 =  Mat2str(colTmat(5,:));   colS5 = Mat2str(colSmat(5,:));
colT6 =  Mat2str(colTmat(6,:));   colS6 = Mat2str(colSmat(6,:));
colT7 =  Mat2str(colTmat(7,:));   colS7 = Mat2str(colSmat(7,:));
colT8 =  Mat2str(colTmat(8,:));   colS8 = Mat2str(colSmat(8,:));
colT9 = 'ProperName/,';    colS9 = 'ProperName//*,';


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Analytics: Value Counts, Value Correlation, Type Counts, Type Correlation
% Arguments: TablePair (+index for rand);  Time Range; Nrand or Column Seeds; Column Types; Clutter Values;
% Get random rows from T.
tic;
  if NODB
    ATr = randRow(T,Nrand);
  else
    ATr = double(logical(DBtableRandRow(T,Ti,Nrand)));
  end
  r = Row(ATr);  % Set rows.
  c = colS;      % Set column types.
  ATr = ATr(:,colS);   % Limit to column types of interest.
  ATr = ATr - ATr(:,colTclut);   % Eliminaate clutter.
timeRandRow = toc;  disp(['Rand row time: ' num2str(timeRandRow)]);

disp(repmat(char(' '),24,1));
disp('%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%')
disp('% Display Statistics.')
disp('%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%')

eval(MyEcho( 'A = double(logical(T(r,:)));       % Get rows.' ));
eval(MyEcho( 'A = A(:,ct);      % Restrict to column types.' ));
eval(MyEcho( 'A = A - A(:,cl);  % Eliminate clutter columns.' ));

disp([LF '% Show common columns.'])
eval(MyEcho( 'sum(A,1) > 50' ));

disp([LF '% Get column types and show counts.']);
eval(MyEcho( 'A = double(logical(col2type(A,$/$)));' ));
eval(MyEcho( 'sum(A,1)' ));

disp([LF '% Compute and display type covariance.'])
eval(MyEcho( 'disp(full(Adj(  sqIn(A)  )))'  ));

% Display summary stats.
%dispTypeStats(ATr,'/');


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Analytic: Data Graph
% Arguments: TablePair (+index for rand);  Time Range; Nrand or Column Seeds; Column Types; Clutter Values; Graph Depth;
% Get random cols.
startVertex = Col(randCol(ATr,100));
c0 = startVertex;

tic;
  disp(repmat(char(' '),24,1));
  disp(['Start set size: ' num2str(NumStr(startVertex))]);
  disp('%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%')
  disp('% Compute data graph from a set of columns.')
  disp('%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%')
  eval(MyEcho( 'k = 1;  % Set graph depth.' ));
  eval(MyEcho( 'c1 = columnNeighbors(T,c0,ct,cl,k);' ));
  graphSet = c1;
  disp(['Graph set size: ' num2str(NumStr(graphSet))]);
  disp([LF '% Look for clutter.'])
  eval(MyEcho( 'sum(T(:,c1),1) > 500' ));
timeNearestNeighbors = toc;  disp(['Data graphs time: ' num2str(timeNearestNeighbors)]);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Analytic: Space Time
% Arguments: TablePair; Time Range; Column Seeds; Column Types; Clutter Values; LatRange; LongRange;

tic;
  disp(repmat(char(' '),24,1));
  disp('%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%')
  disp('% Spacetime window search.')
  disp('%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%')
  eval(MyEcho( 't = $19960903,:,19970214,$;    % Set time range.' ));
  eval(MyEcho( 's = complex([11 15 15 11 11],[15 15 11 11 15]);  % Set space range.' ));
  eval(MyEcho( 'A = double(logical(T(t,:)));   % Get rows.' ));

  disp([LF '% Get coordinates.'])
  eval(MyEcho( 'Axy = str2num(col2type(A(:,[colSy colSx]),$/$));' ));

  disp([LF '% Select columns in rows in space polygon.'])
  eval(MyEcho( 'inS = inpolygon( Adj(Axy(:,colTy)), Adj(Axy(:,colTx)), real(s), imag(s) );' ));
  eval(MyEcho( 'A(find(inS),ct)' ));

timeWindow = toc;  disp([LF 'Spacetime window time: ' num2str(timeWindow)]);


%  ATt = T(timeRange,:);
  % Setting spaceRange = ':' causes all coordinates in timeRange to be evaluated.
%  ATts = ATt(Row(ATt(:,spaceRange)),[colSy colSx]);
%  [r c tmp] = find(ATts - ATts(:,CatStr(colTy,'/',colSx)));
%  [c v] = SplitStr(c,'/');
%  ATtsCoord = str2num(Assoc(r,c,v));
%  ATtsIn = ATtsCoord(find(inpolygon(Adj(ATtsCoord(:,colTy)),Adj(ATtsCoord(:,colTx)),real(spacePoly),imag(spacePoly))),:);
%  displayFull(ATtsIn);
%  windowCol = Col(ATt(Row(ATtsIn),colSe));
%windowCol


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Analytic: Convolution
% Arguments: TablePair; Time Range; Column Seeds; Column Types; Clutter Values; LatRange; LongRange;

tic;
  disp(repmat(char(' '),24,1));
  disp('%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%')
  disp('% Convolve a numeric column.')
  disp('%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%')
  eval(MyEcho( 'f = ones(filtWidth,1);          % Set filter width.' ));
  eval(MyEcho( 'A = double(logical(T(:,c1)));   % Get columns.' ));

  disp([LF '% Get vector of numeric values.'])
  eval(MyEcho( 'Av = double(logical(col2val(sum(A(:,colSa),1),$/$)));' ));

  disp([LF '% Convolve with filter and show groups.'])
  %eval(MyEcho( 'conv(Av,f) > 3' ));


%  ATg = T(:,graphSet);
%  ATg = ATg(:,colSa);
%  [tmp c tmp] = find(ATg);
%  [cType cVal] = SplitStr(c,'/');
%  ATgf = conv(Assoc(cVal,1,1),filter);
%  ATgf = putRow(ATgf,c);
%disp('Filter > 1:');
%ATgf > 1

timeFilter = toc;  disp(['Filter time: ' num2str(timeFilter)]);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Analytic: Type Change
% Arguments: TablePair; Time Range; Column Seed (pairs); Column Types; Clutter Values; LatRange; LongRange;

tic;
  disp(repmat(char(' '),24,1));
  disp('%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%')
  disp('% Type pair looks for type changes.')
  disp('%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%')
  eval(MyEcho( 'A = double(logical(T(:,c1)));   % Get columns.' ));

  disp([LF '% Find rows containing both column types.']);
  eval(MyEcho( 'r = Row(sum(A(Row(sum(A(:,colSb),2) == 1),[colSb colSc]),2) == 2);' ));

  disp([LF '% Get columns in order for creating a pair mapping matrix.'])
  eval(MyEcho( '[tmp c1 tmp] = find(A(r,colSb)); [tmp c2 tmp] = find(A(r,colSc));' ));
  eval(MyEcho( 'A12 = Assoc(c1,c2,1);' ));

  disp([LF '% Find types more than one entry in the other type.'])
  eval(MyEcho( 'sum(A12,2) > 1' ));
  eval(MyEcho( 'sum(A12,1) > 1' ));

timeTypeChange = toc;  disp([LF 'Type pair time: ' num2str(timeTypeChange)]);


%  ATg = double(logical(T(:,graphSet)));
  % Restrict columns to types.
%  rowBoth = Row(sum(ATg(:,[colSb colSc]),2) == 2);
%  rowBoth = Row(sum(ATg(Row(ATg(:,colSb)),colSc),2) == 2);
%  rowBoth = Row(sum(ATg(Row(sum(ATg(:,colSb),2) == 1),[colSb colSc]),2) == 2);
%  [tmp c1 tmp] = find(ATg(rowBoth,colSb));
%  [tmp c2 tmp] = find(ATg(rowBoth,colSc));
%  A12 = Assoc(c1,c2,1);
%timeTypeChange = toc;  disp(['Type change time: ' num2str(timeTypeChange)]);
%TwoPerOne = sum(A12,2) > 1
%OnePerTwo = sum(A12,1) > 1



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% META NEAREST NEIGHBOR SEARCH.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Create random pairs.

tic;
  x1oMat = Str2mat(Col(randCol(ATr(:,colSe),Npair)));
  x2oMat = Str2mat(Col(randCol(ATr(:,colSe),Npair)));
  Npair = min(size(x1oMat,1),size(x2oMat,1));
  x1o = Mat2str(x1oMat(randperm(Npair),:));
  x2o = Mat2str(x2oMat(randperm(Npair),:));
  x12o = CatStr(x1o,ss,x2o);
timeRandPair = toc;
%disp(['Rand pair time: ' num2str(timeRandPair)]);
%disp(['Number of pairs: ' num2str(NumStr(x12o))]);

c12 = x12o;
%c12(c12 == c12(end)) = ',';

tic;
  disp(repmat(char(' '),24,1));
  disp(['Pair set size: ' num2str(NumStr(c12))]);
  disp('%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%')
  disp('%  Data pair check.')
  disp('%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%')
  command = 'PairCheck(T,c12,$;$)';
  command(command == '$') = Q; disp(command); eval(command);

timePairCheck = toc;  disp([LF 'Pair check time: ' num2str(timePairCheck)]);

%AoPair = PairCheck(T,x12o,ss)

tic;
  disp(repmat(char(' '),24,1));
  disp('%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%')
  disp('% Semantic extension of pairs using type data.')
  disp('%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%')

  % Change some tags to more generic values.
  colTfMat = Str2mat(colTf);
  for i=1:NumStr(colTf)
    iType = Mat2str(colTfMat(i,:));
    x12o = strrep(x12o,iType(1:end-1),colT9(1:end-1));
  end
  [x1o x2o] = SplitStr(x12o,ss);

  % Append flips.
  x12 = [x12o CatStr(x2o,ss,x1o)];
  x12o = [x12o x12o]; 


  % Replace (9) in x1 with (4).
  [x1 x2] = SplitStr(x12,ss);
  x1 = strrep(x1,colT9(1:end-1),colT4(1:end-1));
  x12 = CatStr(x1,ss,x2);
  Ax12o_x12 = Assoc(x12o,x12,1);


  % Replace (9) in x2 with (6) and (8).
  B = Ax12o_x12(:,[ss colS9]);
  BB = putCol(B,strrep(Col(B),colT9(1:end-1),colT6(1:end-1))) ...
     + putCol(B,strrep(Col(B),colT9(1:end-1),colT8(1:end-1)));
  Ax12o_x12 = (Ax12o_x12 - B) + BB;

disp(['Pair set size: ' num2str(NumStr(Col(Ax12o_x12)))]);
timeExtendType = toc;  disp(['Extend type time: ' num2str(timeExtendType)]);

tic;
  disp('%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%')
  disp('% Semantic extension of pairs using meta data.')
  disp('%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%')

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

disp(['Pair set size: ' num2str(NumStr(Col(Ax12o_x12)))]);
timeExtendMeta = toc;  disp(['Extend meta time: ' num2str(timeExtendMeta)]);


tic;
  disp(repmat(char(' '),24,1));
  disp('%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%')
  disp('% Semantic pair check.')
  disp('%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%')

  ArowT_x12 = PairCheck(T,Col(Ax12o_x12),ss);

  % Create different views of pairs.
  Ax12o_x12T = Ax12o_x12(:,Col(ArowT_x12));
  Ax12o_rowT = Ax12o_x12 * transpose(ArowT_x12);

  [rowT x12 tmp] = find(ArowT_x12);
  A_x12_rowT = Assoc(1,x12,rowT,@AssocCatStrFunc);
  Ax12o_x12pair = double(logical(Ax12o_x12(:,Col(A_x12_rowT))));
  sizA = size(Ax12o_x12pair);
  Adj_x12o_x12_rowT = repmat(Adj(A_x12_rowT),sizA(1),1) .* Adj(Ax12o_x12pair);
  Ax12o_x12_rowT = putAdj(Ax12o_x12pair,Adj_x12o_x12_rowT);
  Ax12o_x12_rowT = reAssoc(putVal(Ax12o_x12_rowT,Val(A_x12_rowT)))

timeExtendPairCheck = toc;  disp(['Extend pair check time: ' num2str(timeExtendPairCheck)]);


tic;
  disp(repmat(char(' '),24,1));
  disp('%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%')
  disp('% Semantic extension of seeds using type data.')
  disp('%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%')

  % Create seeds as unterminated pairs.
  y1o = x1o;
  y2o = [UNTERMINATED y1o(end)];
  y12o = CatStr(x1o,ss,y2o);

  % Append flips.
  y12 = [y12o CatStr(y2o,ss,y1o)];
  y12o = [y12o y12o]; 

  % Replace (9) in y1 with (4).
  [y1 y2] = SplitStr(y12,ss);
  y1 = strrep(y1,colT9(1:end-1),colT4(1:end-1));
  y12 = CatStr(y1,ss,y2);
  Ay12o_y12 = Assoc(y12o,y12,1);

  % Replace (9) in y2 with (6) and (8).
  B = Ay12o_y12(:,[ss colS9]);
  BB = putCol(B,strrep(Col(B),colT9(1:end-1),colT6(1:end-1))) ...
     + putCol(B,strrep(Col(B),colT9(1:end-1),colT8(1:end-1)));
  Ay12o_y12 = (Ay12o_y12 - B) + BB;

disp(['Seed set size: ' num2str(NumStr(Col(Ay12o_y12)))]);
timeExtendType = toc;  disp(['Extend type time: ' num2str(timeExtendType)]);

tic;
  disp('%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%')
  disp('% Semantic extension of seeds using meta data.')
  disp('%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%')

  % Replace (1) in y2 with a found (4).
  B = Ay12o_y12(:,[ss colS1]);
  BB = ExtendPair(Ay12o_y12,colS1,ss,T,colS4,right,colTclut);
  Ay12o_y12 = (Ay12o_y12 - B) + BB;


  % Replace (3) in y2 with a found (4).
  B = Ay12o_y12(:,[ss colS3]);
  BB = ExtendPair(Ay12o_y12,colS3,ss,T,colS4,right,colTclut);
  Ay12o_y12 = (Ay12o_y12 - B) + BB;


  % Replace (4) in y2 with (6) and (8).
  B = Ay12o_y12(:,[ss colS3 ss colS8]);
  BB = putCol(B,strrep(Col(B),colT4(1:end-1),colT6(1:end-1))) ...
     + putCol(B,strrep(Col(B),colT4(1:end-1),colT8(1:end-1)));
  Ay12o_y12 = (Ay12o_y12 - B) + BB;


  % Extend (6) and (8) in y2 with any found (5) or (7).
  BB = ExtendPair(Ay12o_y12,[colS6 colS8],ss,T,[colS5 colS7],right,colTclut);
  Ay12o_y12 = Ay12o_y12 + BB;

  % Extend (1)-(4) in y1 with any found (1)-(4).
  Ay12o_y12 = Ay12o_y12 + ...
    ExtendPair(Ay12o_y12,Mat2str(colSmat([1 2 3 4],:)),ss,T,Mat2str(colSmat([1 2 3 4],:)),left,colTclut);

disp(['Seed set size: ' num2str(NumStr(Col(Ay12o_y12)))]);
timeExtendMeta = toc;  disp(['Extend meta time: ' num2str(timeExtendMeta)]);

tic;
  disp(repmat(char(' '),24,1));
  disp('%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%')
  disp('% Construct semantic graph from extended seeds.')
  disp('%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%')

  % Create Ay12_y12
  Ay12_y12 = Assoc(Col(Ay12o_y12),Col(Ay12o_y12),1);

  % Eliminate unextended seeds.
  % Extend (1)-(4) in y1 with any found (5)-(6)
  Ay1u_z2u = ExtendPair(Ay12_y12,Mat2str(colSmat([1 2 3 4],:)),ss,T,Mat2str(colSmat([5 6],:)),left,colTclut);
  % Extend (1)-(4) in y2 with any found (7)-(8)
  Auy2_uz1 = ExtendPair(Ay12_y12,Mat2str(colSmat([1 2 3 4],:)),ss,T,Mat2str(colSmat([7 8],:)),right,colTclut);

  % Split rows and columns.
  Ay1u_y1z2 = Assoc('','','');
  if nnz(Ay1u_z2u);
    [r c v] = find(Ay1u_z2u);  [r1 r2] = SplitStr(r,ss);  [c1 c2] = SplitStr(c,ss);
    Ay1u_y1z2 = Assoc(r,CatStr(r1,ss,c1),1);
  end

  % Split rows and columns.
  Auy1_z1y2 = Assoc('','','');
  if nnz(Auy2_uz1)
    [r c v] = find(Auy2_uz1);  [r1 r2] = SplitStr(r,ss);  [c1 c2] = SplitStr(c,ss);
    Auy1_z1y2 = Assoc(r,CatStr(r1,ss,c1),1);
  end

  % Tie back to original seeds.
  Ay12o_y1z2_z1y2 = (Ay12o_y12 * Ay1u_y1z2) + (Ay12o_y12 * Auy1_z1y2)

timeSemanticGraph = toc;  disp(['Semantic graph time: ' num2str(timeSemanticGraph)]);


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

