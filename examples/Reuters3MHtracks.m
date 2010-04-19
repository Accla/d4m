% Computes Facets in Reuters Entity data.

% Read in entities.
%A=ReutersEntityRead;

% Parse into time stamp and add to A.
%A=ReutersEntityTimeStamp(A);


% Turn into a general function.
% Atrack = FindTracks('NE_PERSON/*,','NE_LOCATION/*,','NE_TIME/*,')
% function Atrack = FindTracks(colItem,colLoc,colTime);
%
% Also can create Multiple Hypothesis tracks.
% Returns array of associative arrays contain MH for each item.
% MHtrack = FindMHT( É
%


A = As;
x = 'NE_PERSON_GENERIC/edward d. jones,';
y = 'NE_PERSON_GENERIC/beth wilkinson,';
p = 'NE_PERSON*,';   l = 'NE_LOCATION/*,';    t = 'TIME/*,';

disp(['x=' x]);

tic;
  % Find docs that have person
  DocIDwPer = Row(A(:,x));

  Ax = A(DocIDwPer,:);

  % Find docs that have person and location.
  DocIDwPerLoc = Row(Ax(DocIDwPer,l));

  % Find docs that have person, location and time.
  DocIDwPerLocTime = Row(Ax(DocIDwPerLoc,t));
findOverlapsTime = toc; disp(['findOverlapsTime = ' num2str(findOverlapsTime)]);

tic;
% Get sub arrays.
  Ax = Ax(DocIDwPerLocTime,:);

  Aper = Ax(:,x);

  Aloc = Ax(:,l);
  [EntAxLoc DocAxLoc PosAxLoc] = find(Aloc.');

  Atime = Ax(:,t);
  [EntAxTime DocAxTime PosAxTime] = find(Atime.');

  DocIDwPerLocTimeMat = Str2mat(DocIDwPerLocTime);
  Nd = NumStr(DocIDwPerLocTime);

  EntLoc = '';  EntTime = '';  MinLocDiff = zeros(1,0); MinTimeDiff = zeros(1,0);
  sep = PosAxLoc(end);  subsep = PosAxLoc(end-1);
  for idoc = 1:Nd
     doc = Mat2str(DocIDwPerLocTimeMat(idoc,:));

     % Get position(s) of entity in document.
     dEntPerPos = Val(Aper(doc,x));
     dEntPerPos(dEntPerPos == subsep) = ' ';
     dEntPerPosNum = str2num(dEntPerPos(1:end-1));
     dEntPerPosN = numel(dEntPerPosNum);

     [temp dEntLoc dEntLocPos] = find(Aloc(doc,:));
     dNl = NumStr(dEntLoc);
     dEntLocPosMat = Str2mat(dEntLocPos);
     dEntLocPosMat(dEntLocPosMat == sep) = ' ';
     dEntLocPosMat(dEntLocPosMat == subsep) = ' ';
     dMinLocDiff = zeros(1,dNl);
     for iLoc = 1:dNl
        dMinLocDiff(iLoc) = min(str2num(dEntLocPosMat(iLoc,:)) - dEntPerPosNum);
     end

     [temp dEntTime dEntTimePos] = find(Atime(doc,:));
     dNt = NumStr(dEntTime);
     dEntTimePosMat = Str2mat(dEntTimePos);
     dEntTimePosMat(dEntTimePosMat == sep) = ' ';
     dEntTimePosMat(dEntTimePosMat == subsep) = ' ';
     dMinTimeDiff = zeros(1,dNt);
     for iTime = 1:dNt
        dMinTimeDiff(iTime) = min(str2num(dEntTimePosMat(iTime,:)) - dEntPerPosNum);
     end

     EntLoc = [EntLoc repmat(dEntLoc,[1 dNt])];
     MinLocDiff = [MinLocDiff repmat(dMinLocDiff,[1 dNt])];
     EntTime = [EntTime Mat2str(repmat(Str2mat(dEntTime),[1 dNl]))];

%  Need to replicate.
     MinTimeDiff = [MinTimeDiff dMinTimeDiff];
  end


%  DocAtimeMat = Str2mat(DocAtime);
%  EntAtimeMat = Str2mat(EntAtime);
%  LocTime = Mat2str(EntAtimeMat(StrSearch(DocAtime,DocAloc),:));
constructTracksTime = toc; disp(['constructTracksTime = ' num2str(constructTracksTime)]);

tic;
%  AmhTrack = Assoc(LocTime,EntAloc,1,@sum);
  AmhTrack = Assoc(EntTime,EntLoc,1,@sum);
assocConstruct = toc; disp(['assocConstruct = ' num2str(assocConstruct)]);

%spy(AmhTrack.');
