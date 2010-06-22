function Tr = Reuters3FindTracks(A,t,p,l);
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

  AA = A(DocIDwPerLocTime,:);

  % Get sub arrays.
  Aper = AA(DocIDwPerLocTime,p);
  Per = Col(Aper);
  Nper = NumStr(Per);
  PerMat = Str2mat(Per);

  PerTime = '';  PerPer = ''; PerLoc = '';  

  % Look over each person.
  for i = 1:Nper
    % Get sub-array.
    iPer = Mat2str(PerMat(i,:));
    AAper = AA(Row(Aper(:,iPer)),:);


    % Get MHTrack.
    MHtrack = Reuters3MHtracks(AAper,iPer,t,l);

    % Minimimize to find unique track.
    Atmp = full(Adj(MHtrack));
    Atmp(Atmp == 0) = NaN;
    [lmin lind] = min(Atmp,[],2);
    [r c v] = find(putAdj(MHtrack,sparse(1:numel(lind),lind,lmin)));

    PerPer = [PerPer repmat(iPer,[1 numel(lind)])];
    PerTime = [PerTime r];
    PerLoc = [PerLoc c];
  end

  % Construct associative array.

  Tr = Assoc(PerTime,PerPer,PerLoc);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

