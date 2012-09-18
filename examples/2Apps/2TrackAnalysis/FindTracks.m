function Tr = FindTracks(A,t,p,l);
%FINDTRACKS creates track associative array.

%A = double(logical(An));
%x = 'NE_PERSON_GENERIC/beth wilkinson,';
%y = 'NE_PERSON_GENERIC/edward d. jones,';
%p = 'NE_PERSON*,';   l = 'NE_LOCATION/*,';    t = 'TIME/*,';
%p = [x y];

  % Find docs that have person
  DocIDwPer = Row(A(:,p));

  % Find docs that have person and location.
  DocIDwPerLoc = Row(A(DocIDwPer,l));

  % Find docs that have person, location and time.
  DocIDwPerLocTime = Row(A(DocIDwPerLoc,t));

  % Limit to these documents.
  AA = A(DocIDwPerLocTime,:);

  % Get person sub array.
  Aper = AA(DocIDwPerLocTime,p);
  [TrackPer DocAper temp] = find(Aper.');

  % Get location sub array.
  Aloc = AA(DocIDwPerLocTime,l);
  [EntAloc DocAloc temp] = find(Aloc.');

  [DocAlocUniq in2out out2in] = StrUnique(DocAloc);
  DocAlocMatUniq = Str2mat(DocAlocUniq);
  EntAlocMat = Str2mat(EntAloc);
  EntAlocMatUniq = EntAlocMat(in2out,:);
  TrackLoc = Mat2str(EntAlocMatUniq(StrSearch(DocAlocUniq,DocAper),:));

  % Get time sub array.
  Atime = AA(DocIDwPerLocTime,t);
  [EntAtime DocAtime temp] = find(Atime.');

  [DocAtimeUniq in2out out2in] = StrUnique(DocAtime);
  DocAtimeMatUniq = Str2mat(DocAtimeUniq);
  EntAtimeMat = Str2mat(EntAtime);
  EntAtimeMatUniq = EntAtimeMat(in2out,:);
  TrackTime = Mat2str(EntAtimeMatUniq(StrSearch(DocAtimeUniq,DocAper),:));

  Tr = Assoc(TrackTime,TrackPer,TrackLoc);
