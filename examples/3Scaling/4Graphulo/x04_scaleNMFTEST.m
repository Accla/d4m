% NMF Demo in Graphulo
% NMF currently only implemented for Incidence Schema
% Note: will take a very long time for large scales

% NMF Params
k=5;
maxiter=2;
cutoffThreshold=0;
maxColsPerTopic=0;

if ~exist('SCALE','var')
    SCALE=5;
end

for s=1:length(SCALE)
    
    % Set up DBs
    DBsetup
    
    %% Incidence Schema
    % Set results tables
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
    
end

deleteTables

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
