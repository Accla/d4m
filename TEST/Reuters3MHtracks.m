function MHtrack = Reuters3MHtracks(A,p,t,l);

disp(['p=' p]);

x = p;

%tic;
  % Find docs that have person  
  DocIDwPer = Row(A(:,x));

  Ax = A(DocIDwPer,:);

  % Find docs that have person and location.
  DocIDwPerLoc = Row(Ax(DocIDwPer,l));

  % Find docs that have person, location and time.
  DocIDwPerLocTime = Row(Ax(DocIDwPerLoc,t));
%findOverlapsTime = toc; disp(['findOverlapsTime = ' num2str(findOverlapsTime)]);

%tic;
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
     dNp = numel(dEntPerPosNum);

     [temp dEntLoc dEntLocPos] = find(Aloc(doc,:));
     dNl = NumStr(dEntLoc);
     dEntLocPosMat = Str2mat(dEntLocPos);
     dEntLocPosMat(dEntLocPosMat == sep) = ' ';
     dEntLocPosMat(dEntLocPosMat == subsep) = ' ';
     dMinLocDiff = zeros(1,dNl);

     for iLoc = 1:dNl
        dELpos =str2num(dEntLocPosMat(iLoc,:));
        dNel = numel(dELpos);
        dMinLocDiff(iLoc) = min(repmat(dELpos,[1 dNp]) ...
          - reshape(repmat(dEntPerPosNum,[dNel 1]),[1 dNp*dNel]));
        % Still need to handle dEntPerPosN > 1.
     end

     [temp dEntTime dEntTimePos] = find(Atime(doc,:));
     dNt = NumStr(dEntTime);
     dEntTimePosMat = Str2mat(dEntTimePos);
     dEntTimePosMat(dEntTimePosMat == sep) = ' ';
     dEntTimePosMat(dEntTimePosMat == subsep) = ' ';
     dMinTimeDiff = zeros(1,dNt);


     for iTime = 1:dNt
        dETpos = str2num(dEntTimePosMat(iTime,:));
        dNet = numel(dETpos);
        dMinTimeDiff(iTime) = min( repmat(dETpos,[1 dNp]) ...
        - reshape(repmat(dEntPerPosNum,[dNet 1]),[1 dNp*dNet]));
     end

     EntLoc = [EntLoc repmat(dEntLoc,[1 dNt])];
     MinLocDiff = [MinLocDiff repmat(dMinLocDiff,[1 dNt])];
     EntTime = [EntTime Mat2str(repmat(Str2mat(dEntTime),[1 dNl]))];

     MinTimeDiff = [MinTimeDiff reshape(repmat(dMinTimeDiff,[dNl 1]),[1 dNt*dNl])];
  end


%constructTracksTime = toc; disp(['constructTracksTime = ' num2str(constructTracksTime)]);

%tic;
  MHtrack = Assoc(EntTime,EntLoc,complex(MinTimeDiff,MinLocDiff));
%assocConstruct = toc; disp(['assocConstruct = ' num2str(assocConstruct)]);

%spy(AmhTrack.');


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

