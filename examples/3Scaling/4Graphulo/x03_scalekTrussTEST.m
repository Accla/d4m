% Basic Breadth First Search Demo in Graphulo
% Using all Three Schemas (Adjacency, Incidence, Single Table)

% K-Truss Params
k=3;

if ~exist('SCALE','var')
    SCALE=5;
end

for s=1:length(SCALE)
    DBsetup
    
    %% Adjancency Schema
    % Set results table
    DBsetup
    Rfinal=[TadjName 'kTruss'];
    TadjkTruss = DB(Rfinal);
    if nnz(TadjkTruss)
        deleteForce(TadjkTruss)
        TadjkTruss = DB(Rfinal);
    end
    
    % Set Params
    Aorig=TadjName;
    filterRowCol='';
    forceDelete=true; % Delete temporary tables if they exist
    Aauthorizations=[];
    RNewVisibility=[];
    
    G.kTrussAdj(Aorig, Rfinal, k, filterRowCol, forceDelete, Aauthorizations, RNewVisibility);
    
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
    
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
