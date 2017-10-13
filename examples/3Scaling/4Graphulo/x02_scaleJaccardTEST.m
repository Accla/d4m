% Jaccard Demo in Graphulo
% Jaccard currently only implemented for Adjacency Schema

if ~exist('SCALE','var')
    SCALE=5;
end

for s=1:length(SCALE)
    
    % Set up DBs
    DBsetup
    
    %% Adjancency Schema
    % Set results table
    % Set up Results Table
    tname=[TadjName 'Jaccard'];
    TadjJaccard = DB(tname);
    if nnz(TadjJaccard)
        deleteForce(TadjJaccard)
        TadjJaccard = DB(tname);
    end
    
    % Set Params
    Aorig=TadjName;
    ADeg=[TadjName 'Deg'];
    Rfinal=tname;
    filterRowCol=[];
    Aauthorizations=[];
    RNewVisibility=[];
    
    % Do BFS
    G.Jaccard(Aorig, ADeg, Rfinal, filterRowCol, Aauthorizations, RNewVisibility);
    
end


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
