% Basic Jaccard Demo in Graphulo
% Jaccard currently only implemented for Adjacency Schema

% Set up DB Tables
dbTestSetup

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

% Do Jaccard
numpp = G.Jaccard(Aorig, ADeg, Rfinal, filterRowCol, Aauthorizations, RNewVisibility);

% Check against D4M Jaccard
J_D4M = Jaccard(A);
J_Graphulo = str2num(TadjJaccard(:,:));

incorrect = abs(J_Graphulo-J_D4M) > 1e-5; % TOLERANCE

if ~isempty(incorrect)
    error('NOT EQUAL RESULTS LOCAL AND DB VERSION');
end

%Remove result tables
tablesToDelete={Rfinal};

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
