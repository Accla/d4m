
% Creates and Ingests Unweighted, Undirected Graphs in Incidence,
% Adjancency, and Single Table Schemas

EdgesPerVertex = 16;
SCALE=5;

if ~exist('data')
    mkdir('data');
end

for s=1:length(SCALE)
    
    % Setup DBs
    DBsetup;
    deleteTables;
    DBsetup;

    if ~exist(['data/' num2str(SCALE(s))],'dir')
        mkdir(['data/' num2str(SCALE(s))]);
    end
    
    % Generate Graph
    [v1,v2] = KronGraph500NoPerm(SCALE(s),EdgesPerVertex);
    edgeNums=1:length(v1);
    
    % Create Incidence Data
    idx=1:10000:length(v1);
    if length(idx)==1
        idx=[idx length(v1)];
    end
    numOn=1;
    for i=2:length(idx)
        v1Str=CatStr('out,','|',sprintf('v%d,',v1(idx(i-1):idx(i))));
        v2Str=CatStr('in,','|',sprintf('v%d,',v2(idx(i-1):idx(i))));
        edges=sprintf('e%d,',edgeNums(idx(i-1):idx(i)));
        numOn=numOn+NumStr(v1Str);
        r=[edges edges edges edges];
        c=[v1Str v2Str strrep(v1Str,'out|','in|') strrep(v2Str,'in|','out|')]; % make undirected
        
        E=Assoc(r,c,1);
        save(['data/'  num2str(SCALE(s)) '/E_' num2str(i-1,'%03i') '.mat'],'E')
    end
    
    % Ingest Incidence Data
    
    allDeg=Assoc('','','');
    for i=1:length(idx)-1
        load(['data/'  num2str(SCALE(s)) '/E_' num2str(i,'%03i')])
        put(Tedge,num2str(E));
    end
    
    G.generateDegreeTable([TedgeName 'T'], [TedgeName 'DegTemp'], true, 'out');
    
    
    tmpTedgeDeg=DB([TedgeName 'DegTemp']);
    inDeg=tmpTedgeDeg(StartsWith('in|,'),:);
    [r,~,v]=find(inDeg);
    [~,r]=SplitStr(r,'|');
    inDeg=Assoc(r,'in,',v);
    
    put(TedgeDeg,inDeg);
    
    outDeg=tmpTedgeDeg(StartsWith('out|,'),:);
    [r,~,v]=find(outDeg);
    [~,r]=SplitStr(r,'|');
    outDeg=Assoc(r,'out,',v);
    
    put(TedgeDeg,outDeg);
    
    deleteForce(tmpTedgeDeg);
    
    % Convert to Adj Data
    fnames=dir(['data/'  num2str(SCALE(s)) '/E_*']);
    for i=1:length(fnames)
        load(['data/'  num2str(SCALE(s)) '/E_' num2str(i,'%03i')])
        Eout=E(:,StartsWith('out|,'));
        Ein=E(:,StartsWith('in|,'));
        A=Eout.'*Ein;
        A=dblLogi(A+A.'); % make unweighted, undirected for jaccard
        
        [r,c,v]=find(A);
        
        [~,r]=SplitStr(r,'|'); % remove in/out prefix
        [~,c]=SplitStr(c,'|');
        
        A=Assoc(r,c,v);
        
        save(['data/'  num2str(SCALE(s)) '/A_' num2str(i,'%03i')],'A')
    end
    
    % Ingest Adj Data
    for i=1:length(fnames)
        load(['data/'  num2str(SCALE(s)) '/A_' num2str(i,'%03i')])
        
        put(Tadj,num2str(A));
    end
    G.generateDegreeTable(TadjName, [TadjName 'Deg'], true, 'out');
    
    % Create Single Data
    fnames=dir(['data/'  num2str(SCALE(s)) '/A_*']);
    for i=1:length(fnames)
        load(['data/'  num2str(SCALE(s)) '/A_' num2str(i,'%03i')])
        [r,c,v]=find(A);
        r=CatStr(r,'|',c);
        S=Assoc(r,'edge,',v);
        save(['data/'  num2str(SCALE(s)) '/S_' num2str(i,'%03i')],'S')
    end
    
    % Ingest Single Data
    
    deg=TadjDeg(:,:);
    for i=1:length(fnames)
        load(['data/'  num2str(SCALE(s)) '/S_' num2str(i,'%03i')])
        
        put(Tsingle,num2str(S));
    end
    put(Tsingle,putCol(deg,'deg,')); % iterator for larger degree tables?
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

