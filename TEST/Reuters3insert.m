% Insert Reuters data into database.

if not(exist('NODB','var'))
  NODB = 0;
end

fdir = 'reuters_entities3/mergedfiles/';

fnames = dir([fdir 'output_*_A.mat']);

% Create a DB.
if NODB

else
  T = DB('ReutersDataTEST','ReutersDataTESTt');
  deleteForce(T);
  T = DB('ReutersDataTEST','ReutersDataTESTt');
end
nl = char(10);

for i = 1:numel(fnames)
  tic;
    fname = fnames(i).name;
    disp(fname);
    load([fdir fname],'As');
%    put(T,As);
%   Change seperator to nl (since | causes an error).
    [r c v] = find(As);
    r(r == r(end)) = nl;  c(c == c(end)) = nl;  v(v == v(end)) = nl;
    A = Assoc(r,c,v);

    % Add coordinates.
    rowStr = Row(A);
    N = NumStr(rowStr);
    Acoord = num2str(Assoc(rowStr,'Lat,',180*(rand(N,1) - 0.5)) + Assoc(rowStr,'Lon,',360*(rand(N,1) - 0.5)));
    % Unfurl columns.
    [r c v] = find(Acoord);
    Acoord = Assoc(r,CatStr(c,'/',v),'1,');
    % Mertonize.
%    [rLat cLat vLat] = find(Acoord(:,'Lat/ *,'));
%    [rLon cLon vLon] = find(Acoord(:,'Lon/ *,'));
    [cLat rLat vLat] = find(Acoord(:,'Lat/ *,').');
    [cLon rLon vLon] = find(Acoord(:,'Lon/ *,').');
    cLatLon = MertonizeLatLon(cLat,cLon);

%%%% Something wrong with using rlat as row coordinate.
%    Acoord = Acoord + Assoc(rLat,cLatLon,vLat);
    A = A + Acoord;
  convertTime = toc; disp(['Convert time: ' num2str(convertTime)]);
  if NODB
    T = A;
  else
    tic;
      put(T,A);
    putTime = toc; disp(['DB put time: ' num2str(putTime)]);
    putRate = nnz(As) / putTime; disp(['DB put rate: ' num2str(putRate)]);
  end
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%



