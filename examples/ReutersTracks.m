% Computes Facets in Reuters Entity data.

% Read in entities.
%ReutersEntityRead;

% Parse into time stamp and add to A.
ReutersEntityTimeStamp;


% Turn into a general function.
% Atrack = FindTracks('NE_PERSON/*,','NE_LOCATION/*,','NE_TIME/*,')
% function Atrack = FindTracks(colItem,colLoc,colTime);
%
% Also can create Multiple Hypothesis tracks.
% Returns array of associative arrays contain MH for each item.
% MHtrack = FindMHT( É
%




  % Find docs that have person
  DocIDwPer = Row(A(:,'NE_PERSON/*,'));

  % Find docs that have person and location.
  DocIDwPerLoc = Row(A(DocIDwPer,'NE_LOCATION/*,'));

  % Find docs that have person, location and time.
  DocIDwPerLocTime = Row(A(DocIDwPerLoc,'NE_TIME/*,'));
findOverlapsTime = toc; disp(['findOverlapsTime = ' num2str(findOverlapsTime)]);

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
constructTracksTime = toc; disp(['constructTracksTime = ' num2str(constructTracksTime)]);


tic;
  Atrack = Assoc(TrackTime,TrackPer,TrackLoc);
assocConstruct = toc; disp(['assocConstruct = ' num2str(assocConstruct)]);
