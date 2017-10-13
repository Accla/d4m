% Basic NMF Demo in Graphulo
% Using Incidence Schema

% NMF Params
k=3;
maxiter=2;
cutoffThreshold=0;
maxColsPerTopic=0;

% Setup DB Tables
dbTestSetup

% Set results table
tname_W=[TedgeName 'NMF_W'];
TedgeNMF_W = DB(tname_W,[tname_W 'T']);
if nnz(TedgeNMF_W)
    deleteForce(TedgeNMF_W);
    TedgeNMF_W = DB(tname_W,[tname_W 'T']);
end
tname_H=[TedgeName 'NMF_H'];
TedgeNMF_H = DB(tname_H,[tname_H 'T']);
if nnz(TedgeNMF_H)
    deleteForce(TedgeNMF_H);
    TedgeNMF_H = DB(tname_H,[tname_H 'T']);
end

% Set Params
Aorig=TedgeName;
ATorig=[TedgeName 'T'];
Wfinal= tname_W;
WTfinal= [tname_W 'T'];
Hfinal= tname_H;
HTfinal= [tname_H 'T'];
forceDelete=true;

% Do NMF
G.NMF(Aorig, ATorig, Wfinal, WTfinal, Hfinal, HTfinal, k, maxiter,...
    forceDelete, cutoffThreshold, maxColsPerTopic);

% Get Results
W_Graphulo = Abs0(TopColPerRow(str2num(TedgeNMF_W(:,:)),1));
H_Graphulo = Abs0(TopColPerRow(str2num(TedgeNMF_H(:,:)),1));

%% Remove Result and Data Tables
tablesToDelete={tname_W,[tname_W 'T'],tname_H,[tname_H 'T']...
    TadjName, [TadjName 'T'], [TadjName 'Deg'],...
    TedgeName, [TedgeName 'T'], [TedgeName 'Deg'], TsingleName};

for i=1:length(tablesToDelete)
    deleteForce(DB(tablesToDelete{i}));
end


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


