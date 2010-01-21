function Tr = FindTracks(A,t,p,l);
%FINDTRACKS creates track associative array.

  % Find docs that have person
  DocIDwPer = Row(A(:,p));

  % Find docs that have person and location.
  DocIDwPerLoc = Row(A(DocIDwPer,l));

  % Find docs that have person, location and time.
  DocIDwPerLocTime = Row(A(DocIDwPerLoc,t));

  % Get sub arrays.
  Aper = A(DocIDwPerLocTime,p);
  [TrackPer DocAper temp] = find(Aper.');

  Aloc = A(DocIDwPerLocTime,l);
  [EntAloc DocAloc temp] = find(Aloc.');
  [DocAlocUniq in2out out2in] = StrUnique(DocAloc);
  DocAlocMatUniq = Str2mat(DocAlocUniq);
  EntAlocMat = Str2mat(EntAloc);
  EntAlocMatUniq = EntAlocMat(in2out,:);
  TrackLoc = Mat2str(EntAlocMatUniq(StrSearch(DocAlocUniq,DocAper),:));

  Atime = A(DocIDwPerLocTime,t);
  [EntAtime DocAtime temp] = find(Atime.');
  DocAtimeMat = Str2mat(DocAtime);
  EntAtimeMat = Str2mat(EntAtime);
  TrackTime = Mat2str(EntAtimeMat(StrSearch(DocAtime,DocAper),:));

  Tr = Assoc(TrackTime,TrackPer,TrackLoc);

end
