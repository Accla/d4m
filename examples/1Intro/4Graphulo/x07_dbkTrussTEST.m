% Basic k-Truss Demo in Graphulo
% Using Adjacency and Incidence Schemas

% K-Truss Params
k=3;

% Set up DB Tables
dbTestSetup

%% Adjancency Schema

% Set results table
DBsetup
tname=[TadjName 'kTruss'];
TadjkTruss = DB(tname);
if nnz(TadjkTruss)
    deleteForce(TadjkTruss)
    TadjkTruss = DB(tname);
end

% Set Params
Aorig=TadjName;
Rfinal=tname;
filterRowCol='';
forceDelete=true; % Delete temporary tables if they exist
Aauthorizations=[];
RNewVisibility=[];

G.kTrussAdj(Aorig, Rfinal, k, filterRowCol, forceDelete, Aauthorizations, RNewVisibility);

% Check against D4M kTruss
kT_D4M = kTrussAdj(A,k);
kT_Graphulo = str2num(TadjkTruss(:,:));

incorrect = abs(kT_D4M-kT_Graphulo) > 1e-6; % TOLERANCE

if ~isempty(incorrect)
    error('NOT EQUAL RESULTS LOCAL AND DB VERSION');
end

%% Incidence/Edge Schema

% Set results table
Rtable=[TedgeName 'kTruss'];
TedgekTruss = DB(Rtable,[Rtable 'T']);
if nnz(TedgekTruss)
    deleteForce(TedgekTruss)
    TedgekTruss = DB(Rtable,[Rtable 'T']);
end

% Set Params
Eorig=TedgeName;
ETorig=[TedgeName 'T'];
Rfinal=Rtable;
RTfinal=[Rtable 'T'];
edgeFilter='';
forceDelete=true;
Eauthorizations=[];

G.kTrussEdge(Eorig, ETorig, Rfinal, RTfinal, k, edgeFilter, forceDelete, Eauthorizations);

% Check against D4M kTruss
kT_D4M = kTrussEdge(E,k);
kT_Graphulo=TedgekTruss(:,:);

incorrect = abs(kT_D4M-kT_Graphulo) > 1e-6; % TOLERANCE

if ~isempty(incorrect)
    error('NOT EQUAL RESULTS LOCAL AND DB VERSION');
end

%% Remove result tables
tablesToDelete={[TadjName 'kTruss'],[TedgeName 'kTruss'],[TedgeName 'kTrussT']};

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
