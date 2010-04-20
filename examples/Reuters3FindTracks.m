function Tr = Reuters3FindTracks(A,t,p,l);
%FINDTRACKS creates track associative array.

%A = double(logical(An));
%x = 'NE_PERSON_GENERIC/beth wilkinson,';
%y = 'NE_PERSON_GENERIC/edward d. jones,';
p = 'NE_PERSON*,';   l = 'NE_LOCATION/*,';    t = 'TIME/*,';
%p = [x y];

  % Find docs that have person
  DocIDwPer = Row(A(:,p));

  % Find docs that have person and location.
  DocIDwPerLoc = Row(A(DocIDwPer,l));

  % Find docs that have person, location and time.
  DocIDwPerLocTime = Row(A(DocIDwPerLoc,t));

  AA = A(DocIDwPerLocTime,:);

  % Get sub arrays.
  Aper = AA(DocIDwPerLocTime,p);
  Per = Col(Aper);
  Nper = NumStr(Per);
  PerMat = Str2mat(Per);

  PerLoc = '';  PerTime = '';  PerPer = '';

  % Look over each person.
  for i = 1:Nper
    % Get sub-array.
    iPer = Mat2str(PerMat(i,:));
    AAper = AA(Row(Aper(:,iPer),:);


    % Get MHTrack.
    MHtrack = Reuter3MHTrack(AAper,iPer,t,l);

    % Minimimize to find unique track.

    % Get [r c v]  and append.

  end

  % Construct associative array.




  [TrackPer DocAper temp] = find(Aper.');

  Aloc = AA(DocIDwPerLocTime,l);
  [EntAloc DocAloc temp] = find(Aloc.');
  [DocAlocUniq in2out out2in] = StrUnique(DocAloc);
  DocAlocMatUniq = Str2mat(DocAlocUniq);
  EntAlocMat = Str2mat(EntAloc);
  EntAlocMatUniq = EntAlocMat(in2out,:);
  TrackLoc = Mat2str(EntAlocMatUniq(StrSearch(DocAlocUniq,DocAper),:));

  Atime = AA(DocIDwPerLocTime,t);
  [EntAtime DocAtime temp] = find(Atime.');

  [DocAtimeUniq in2out out2in] = StrUnique(DocAtime);
  DocAtimeMatUniq = Str2mat(DocAtimeUniq);
  EntAtimeMat = Str2mat(EntAtime);
  EntAtimeMatUniq = EntAtimeMat(in2out,:);
  TrackTime = Mat2str(EntAtimeMatUniq(StrSearch(DocAtimeUniq,DocAper),:));

  Tr = Assoc(TrackTime,TrackPer,TrackLoc);
