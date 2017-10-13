% Basic Breadth First Search Demo in Graphulo
% Using all Three Schemas (Adjacency, Incidence, Single Table)

% BFS Params
v0 = 'v4,v5,';
k=3;
minDegree=0;
maxDegree=100;

% Set up DB Tables
dbTestSetup

%% Adjancency Schema

% Set results table
Rtable=[TadjName 'BFS'];
TadjBFS = DB(Rtable);
if nnz(TadjBFS)
    deleteForce(TadjBFS)
    TadjBFS = DB(Rtable);
end

% Do BFS
v = G.AdjBFS(TadjName, v0, k, Rtable, [Rtable 'T'], [TadjName 'Deg'], 'Degree', false, minDegree, maxDegree);

% Check against D4M BFS
BFS_D4M = AdjBFS(A,sum(A,2),'',v0,k,minDegree,maxDegree,false);
BFS_Graphulo = TadjBFS(:,:);

incorrect = abs(BFS_Graphulo-BFS_D4M) > 1e-6; % TOLERANCE

if ~isempty(incorrect)
    error('NOT EQUAL RESULTS LOCAL AND DB VERSION');
end

%% Incidence/Edge Schema

% Set results table
Rtable=[TedgeName 'BFS'];
TedgeBFS = DB(Rtable);
if nnz(TedgeBFS)
    deleteForce(TedgeBFS)
    TedgeBFS = DB(Rtable);
end

% Set Params for BFS
Etable=TedgeName;
RTtable=[Rtable 'T'];
startPrefixes='out|,';
endPrefixes='in|,';
ETDegtable=[TedgeName 'Deg'];
degColumn='out';
degInColQ=false;
plusOp=[];
EScanIteratorPriority=-1;
Eauthorizations=[];
EDegauthorizations=[];
newVisibility=[];
useNewTimestamp=true;
outputUnion=true;
numEntriesWritten=[];

% Do BFS
vGraphulo = G.EdgeBFS(Etable, v0, k, Rtable, RTtable, startPrefixes, endPrefixes,...
    ETDegtable, degColumn, degInColQ, minDegree, maxDegree,...
    plusOp, EScanIteratorPriority, Eauthorizations, EDegauthorizations, newVisibility,...
    useNewTimestamp, outputUnion, numEntriesWritten);

% Check against D4M BFS
[~,~,BFS_D4M] = EdgeBFS(E,'out,','in,','|',sum(E,1).',v0,k,minDegree,maxDegree,false);
BFS_Graphulo = TedgeBFS(:,:);

vGraphulo=Col(BFS_Graphulo(:,StartsWith('in|,')));
vD4M=Col(NewSep(BFS_D4M(:,StartsWith('in|,')),char(10)));

incorrect = ~strcmp(vGraphulo,vD4M);

if incorrect
    error('NOT EQUAL RESULTS LOCAL AND DB VERSION');
end

%% Single Table Schema

% Set results table
Rtable=[TsingleName 'BFS'];
TsingleBFS = DB(Rtable);
if nnz(TsingleBFS)
    deleteForce(TsingleBFS)
    TsingleBFS = DB(Rtable);
end

% Set Params for BFS
Stable=TsingleName;
edgeColumn='edge';
edgeSep='|';
SDegtable=Stable;
degColumn='deg';
copyOutDegrees=true;
computeInDegrees=true;
degSumType=[];
newVisibility=[];
plusOp=[];
outputUnion=true;
Sauthorizations=[];
numEntriesWritten=[];

% Do BFS
vGraphulo = G.SingleBFS(Stable, edgeColumn, edgeSep, v0, k, Rtable, SDegtable, degColumn,...
    copyOutDegrees, computeInDegrees, degSumType, newVisibility,...
    minDegree, maxDegree, plusOp, outputUnion, Sauthorizations, numEntriesWritten);

% No D4M SingleBFS, check against Edge BFS result
[~,~,BFS_D4M] = EdgeBFS(E,'out,','in,','|',sum(E,1).',v0,k,minDegree,maxDegree,false);
BFS_Graphulo = TsingleBFS(:,:);

vGraphulo=Row(BFS_Graphulo(:,'deg,'));
[~,vD4M]=SplitStr(Col(NewSep(BFS_D4M(:,StartsWith('in|,')),char(10))),'|');

incorrect = ~strcmp(vGraphulo,vD4M);

if incorrect
    error('NOT EQUAL RESULTS LOCAL AND DB VERSION');
end

%% Remove result tables
tablesToDelete={[TadjName 'BFS'], [TadjName 'BFST'], [TedgeName 'BFS'], [TedgeName 'BFST'], [TsingleName 'BFS']};

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
