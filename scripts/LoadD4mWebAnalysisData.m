function LoadD4mWebAnalysisData(host,user,passwd)
% This script is used to load the Reuters data for the D4MWebAnalysis
% To run this script, make sure the TEST directory is included in the path.
%
%  DBsetup;

%% DB setup
    DB = DBserver(host,'BigTableLike','cloudbase',user,passwd);
%% Determine the path
d4m_home = fileparts(fileparts(mfilename('fullpath')));

  T = DB('ReutersData','ReutersDataT');
  Ti = DB('ReutersData_index');
  % Create globals for query functions.
  D4MqueryGlobal.DB = DB;
%% Edit fdir for the directory that contains the data
fdir = [d4m_home '/TEST/reuters_entities3/mergedfiles/'];

% Parse the data
  reuters3parse(fdir);     % Parse reuters data.
  reuters3insert(fdir,T);      % Insert doc/entity into DB.  Creates T.

    Ti = DBtableIndexRow(T,Ti,1);
    D4MqueryGlobal.Ti = Ti;

function reuters3parse(fdir)
fnames = dir([fdir 'output_*.txt']);

for i = 1:numel(fnames)
  fname = fnames(i).name;
  [As An]=ReutersEntity3Read([fdir fname]);
  [pathstr,name,ext] = fileparts(fname);
  save([fdir name '_A.mat'],'As','An','-v6');
end


function reuters3insert(fdir, T)
nl = char(10);
fnames = dir([fdir 'output_*_A.mat']);
NODB=0;
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

