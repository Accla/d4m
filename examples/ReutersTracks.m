% Computes Facets in Reuters Entity data.

% Read in entities.
%ReutersEntityRead;

% Parse Doc ID into time entity.
tic;
  DocID = Row(A);
  DocIDmat = Str2mat(DocID);
  DocIDwTimeMat = DocIDmat(StrSubsref(DocID,'TXT/199*,'),:);
  [Nt temp] = size(DocIDwTimeMat);
  DocTimeEntMat = repmat('NE_TIME/    -  -  ,',Nt,1);
  DocTimeEntMat(:,[ 9 10 11 12 14 15 17 18]) = DocIDwTimeMat(:,[5 6 7 8 9 10 11 12]);
  DocIDwTime = Mat2str(DocIDwTimeMat);
  DocTime = Mat2str(DocTimeEntMat);
parseTime = toc

tic;
  % Add time entities to A.
  A = A + Assoc(DocIDwTime,DocTime,1);

  % Find docs that have person
  DocIDwPer = Row(A(:,'NE_PERSON/*,'));

  % Find docs that have person and location.
  DocIDwPerLoc = Row(A(DocIDwPer,'NE_LOCATION/*,'));

  % Find docs that have person, location and time.
  DocIDwPerLocTime = Row(A(DocIDwPerLoc,'NE_TIME/*,'));
findOverlapsTime = toc

tic;
% Get sub arrays.
  Aper = A(DocIDwPerLocTime,'NE_PERSON/*,');
  [TrackPer DocAper temp] = find(Aper.');

  Aloc = A(DocIDwPerLocTime,'NE_LOCATION/*,');
  [EntAloc DocAloc temp] = find(Aloc.');
  [DocAlocUniq in2out out2in] = StrUnique(DocAloc);
  DocAlocMatUniq = Str2mat(DocAlocUniq);
  EntAlocMat = Str2mat(EntAloc);
  EntAlocMatUniq = EntAlocMat(in2out,:);
  TrackLoc = Mat2str(EntAlocMatUniq(StrSearch(DocAlocUniq,DocAper),:));

  Atime = A(DocIDwPerLocTime,'NE_TIME/*,');
  [EntAtime DocAtime temp] = find(Atime.');
  DocAtimeMat = Str2mat(DocAtime);
  EntAtimeMat = Str2mat(EntAtime);
  TrackTime = Mat2str(EntAtimeMat(StrSearch(DocAtime,DocAper),:));
construtTracksTime = toc


tic;
  Atrack = Assoc(TrackTime,TrackPer,TrackLoc);
assocConstruct = toc

disp('displayFull(Atrack(:,NE_PERSON/DAMON HILL,NE_PERSON/GARY WONG,))')
displayFull(Atrack(:,'NE_PERSON/DAMON HILL,NE_PERSON/GARY WONG,'))

disp('Col(Atrack(NE_TIME/1996-09-06,:,NE_TIME/1996-09-13,,:) == NE_LOCATION/jordan,)')
disp(Col(Atrack('NE_TIME/1996-09-06,:,NE_TIME/1996-09-13,',:) == 'NE_LOCATION/jordan,'))
