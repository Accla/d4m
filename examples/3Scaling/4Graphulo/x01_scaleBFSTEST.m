% Breadth First Search Demo in Graphulo
% Using all Three Schemas (Adjacency, Incidence, Single Table)

% BFS Params
numStart=5;
k=3;
minDegree=1;
maxDegree=100;

if ~exist('SCALE','var')
    SCALE=5;
end

for s=1:length(SCALE)
    
    % Set up DBs
    DBsetup
    
    % Choose starting vertices for this scale
    deg=str2num(TadjDeg(:,:))<maxDegree;
    deg=deg>minDegree;
    idx=randperm(size(deg,1)); %randsample(size(deg,1),numStart);
    idx=idx(1:numStart);
    
    v0=Row(deg(idx,:));
    
    %% Adjancency Schema
    % Set results table
    Rtable=[TadjName 'BFS'];
    TadjBFS = DB(Rtable);
    if nnz(TadjBFS)
        deleteForce(TadjBFS)
        TadjBFS = DB(Rtable);
    end
    
    % Set Params
    Atable=TadjName;
    RtableTranspose=[Rtable 'T'];
    ADegtable=[TadjName 'Deg'];
    degColumn='out';
    degInColQ=false;
    
    % Do BFS
    G.AdjBFS(Atable, v0, k, Rtable, RtableTranspose, ADegtable, degColumn, degInColQ, minDegree, maxDegree);
    
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
    G.EdgeBFS(Etable, v0, k, Rtable, RTtable, startPrefixes, endPrefixes,...
        ETDegtable, degColumn, degInColQ, minDegree, maxDegree,...
        plusOp, EScanIteratorPriority, Eauthorizations, EDegauthorizations, newVisibility,...
        useNewTimestamp, outputUnion, numEntriesWritten);
    
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
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
