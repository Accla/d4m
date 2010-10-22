%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Tests various analysis on Reuters data.

% Setup data.
%Reuters3parse;     % Parse reuters data.
DBsetup;
%Reuters3insert;      % Insert doc/entity into DB.  Creates T.
T = DB('ReutersDataTEST','ReutersDataTESTt');
Ti = DB('ReutersDataTEST_index');

% Hack when no DB access.
%T = double(logical(An)); [r c v] = find(T);
%sep = ',';  r(r == r(end)) = sep;  c(c == c(end)) = sep;
%T = Assoc(r,c,v);


% Create an index table for drawing random rows from T.
%Ti = DBtableIndexRow(T,Ti);


% Column type keys.
colT = 'GEO/,NE_LOCATION/,NE_ORGANIZATION/,NE_PERSON/,NE_PERSON_GENERIC/,NE_PERSON_MILITARY/,TIME/,TIMELOCAL/,';

colT = 'TIME/,TIMELOCAL/,NE_ORGANIZATION/,NE_PERSON/,NE_PERSON_GENERIC/,NE_PERSON_MILITARY/,GEO/,NE_LOCATION/,';
colTmat = Str2mat(colT);
colS = CatStr(colT,'/','*,');
colSmat = Str2mat(colS);


% Numerical filter column types.
colTa = Mat2str(colTmat(1,:));  colSa = Mat2str(colSmat(1,:));
filtWidth = 20000;

% Type change columns.
colTb = Mat2str(colTmat(1,:));  colSb = Mat2str(colSmat(1,:));
colTc = Mat2str(colTmat(3,:));  colSc = Mat2str(colSmat(3,:));

% Time/space window search columns.
timeRange = '19970725,:,19970729,';
spaceRange = 'NE_LOCATION/indonesia,';

% colTd = Mat2str(colTmat(1,:));  colSd = Mat2str(colSmat(1,:));

% Meta nearest neighbor search.
Npair = 100;
colTe = Mat2str(colTmat([1 3 4 6 8],:));  colSe = Mat2str(colSmat([1 3 4 6 8],:));
colTf = Mat2str(colTmat([4 6 8],:));  colSf = Mat2str(colSmat([4 6 8],:));
colT1 =  Mat2str(colTmat(2,:));   colS1 = Mat2str(colSmat(1,:));
colT4 =  Mat2str(colTmat(4,:));   colS4 = Mat2str(colSmat(4,:));
colT6 =  Mat2str(colTmat(6,:));
colT8 =  Mat2str(colTmat(8,:));
colT9 = 'ProperName/,';    colS9 = 'ProperName//*,';


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Get random rows from T.
tic;
  Nrand = 1000;
  ATr = double(logical(DBtableRandRow(T,Ti,Nrand)));
%  ATr = randRow(T,Nrand);
  ATr = ATr(:,colS);   % Limit to column types of interest.
timeRandRow = toc;  disp(['Rand row time: ' num2str(timeRandRow)]);


% Display summary stats.
dispTypeStats(ATr,'/');



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Get random cols.
startVertex = Col(randCol(ATr,100));
disp(['Start set size: ' num2str(NumStr(startVertex))]);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Do simple nearest neighbor search.
graphDepth = 1;
tic;
  graphSet = columnNeighbors(T,startVertex,colS,graphDepth);
timeNearestNeighbors = toc;  disp(['Nearest neighbors time: ' num2str(timeNearestNeighbors)]);
disp(['Graph set size: ' num2str(NumStr(graphSet))]);


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Convolve numeric column.
tic;
  filter = ones(filtWidth,1);   % Set filter width.
  ATg = T(:,graphSet);
  ATg = ATg(:,colSa);
  [tmp c tmp] = find(ATg);
  [cType cVal] = SplitStr(c,'/');
  ATgf = conv(Assoc(cVal,1,1),filter);
  ATgf = putRow(ATgf,c);
timeFilter = toc;  disp(['Filter time: ' num2str(timeFilter)]);
disp('Filter > 1:');
ATgf > 1



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Look for type changes between colTb and colTc.
tic;
  ATg = double(logical(T(:,graphSet)));
  % Restrict columns to types.
%  rowBoth = Row(sum(ATg(:,[colSb colSc]),2) == 2);
%  rowBoth = Row(sum(ATg(Row(ATg(:,colSb)),colSc),2) == 2);
  rowBoth = Row(sum(ATg(Row(sum(ATg(:,colSb),2) == 1),[colSb colSc]),2) == 2);
  [tmp c1 tmp] = find(ATg(rowBoth,colSb));
  [tmp c2 tmp] = find(ATg(rowBoth,colSc));
  A12 = Assoc(c1,c2,1);
timeTypeChange = toc;  disp(['Type change time: ' num2str(timeTypeChange)]);
TwoPerOne = sum(A12,2) > 1
OnePerTwo = sum(A12,1) > 1


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Time/space window search.
tic;
  ATt = T(timeRange,:);
  windowCol = Col(ATt(Row(ATt(:,spaceRange)),colSe));
timeWindow = toc;  disp(['Window time: ' num2str(timeWindow)]);
windowCol


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
  x12o = CatStr(x1o,'|',x2o);
timeRandPair = toc;  disp(['Rand pair time: ' num2str(timeRandPair)]);
disp(['Number of pairs: ' num2str(NumStr(x12o))]);


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Do simple pair check.
tic;
  AoPair = PairCheck(T,x12o,'|')
timePairCheck = toc;  disp(['Pair check time: ' num2str(timePairCheck)]);


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Extend pairs using meta data.

% Change some tags to more generic values.
colTfMat = Str2mat(colTf);
for i=1:NumStr(colTf)
  iType = Mat2str(colTfMat(i,:));
  x12o = strrep(x12o,iType(1:end-1),colT9(1:end-1));
end
[x1o x2o] = SplitStr(x12o,'|');

% Append flips.
x12 = [x12o CatStr(x2o,'|',x1o)];
x12o = [x12o x12o]; 


% Replace (9) in x1 with (4).
[x1 x2] = SplitStr(x12,'|');
x1 = strrep(x1,colT9(1:end-1),colT4(1:end-1));
x12 = CatStr(x1,'|',x2);
Ax12o_x12 = Assoc(x12o,x12,1);


% Replace (9) in x2 with (6) and (8).
B = Ax12o_x12(:,['|' colS9]);
Ax12o_x12 = (Ax12o_x12 - B) ...
   + putCol(B,strrep(Col(B),colT9(1:end-1),colT6(1:end-1))) ...
   + putCol(B,strrep(Col(B),colT9(1:end-1),colT8(1:end-1)));


% Replace (1) in x2 with a found (4).
B = ExtendPair(Ax12o_x12,['|' colS1],'|',T,['|' colS4]);

if 0

% Replace (3) in x2 with a found (4).
B = ExtendPair(Ax12o_x12,colS3,'|',T,colS4);

% Replace (4) in x2 with (6) and (8).
Ax12o_x12 = (Ax12o_x12 - Ax12o_x12(:,'|(4)//*,')) ...
  + putCol(B,strrep(Col(B),colT4,colT6)) + putCol(B,strrep(Col(B),colT4,colT8));

Ax12o_x12 = Ax12o_x12 + ...
  ExtendPair(Ax12o_x12,Mat2str(colSmat([1 2 3 4],:)),'|',T,Mat2str(colSmat([1 2 3 4],:)));

Ax12o_x12 = Ax12o_x12 + ...
  ExtendPair(Ax12o_x12,Mat2str(colSmat([6 8],:)),'|',T,Mat2str(colSmat([5 7],:)));


% save([mfilename '.mat'],'-v6','QueryResponseGetTrackNamesJSON','QueryResponseMHtrackJSON');



% Delete index and table.
% deleteForce(Ti);
% deleteForce(T);


end % If 0


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%





