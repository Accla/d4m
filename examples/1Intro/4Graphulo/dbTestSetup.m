% Set up for Running Graphulo DB Tests

% Setup binding to a database.
DBsetup

% Ingest Adj Data
Amat = [0 1 1 1 0; % Input adjacency matrix
    1 0 1 0 1;
    1 1 0 1 0;
    1 0 1 0 0;
    0 1 0 0 0];
A = Mat2Assoc(Amat,'v');
if nnz(Tadj)/2~=nnz(A) || nnz(str2num(Tadj(:,:))-A)
    put(Tadj,num2str(A));
    G.generateDegreeTable(TadjName, [TadjName 'Deg'], true, 'Degree');
end

% Ingest Incidence Data
Emat = [1 1 0 0 0;
    0 1 1 0 0;
    1 0 0 1 0;
    0 0 1 1 0;
    1 0 1 0 0;
    0 1 0 0 1];
E = Mat2Assoc(Emat,'e','v'); % Convert to Assoc, use node labels 'v1,v2,...,'
deg=putCol(sum(E,1).','in,')+putCol(sum(E,1).','out,');

[r,c,v]=find(E);
c=[CatStr('out,','|',c) CatStr('in,','|',c)];
E=Assoc([r r],c,[v;v]);
if nnz(Tedge)/2~=nnz(E) || nnz(str2num(Tedge(:,:))-E)
    put(Tedge,num2str(E));
    put(TedgeDeg,num2str(deg))
end

% Ingest Single Table Data
[r,c,v]=find(A);
rE=CatStr(r,'|',c);
deg=putCol(sum(A,2),'deg,');
S = deg + Assoc(rE,'edge,',v); % Convert to Assoc, use node labels 'v1,v2,...,'
if nnz(Tsingle)/2~=nnz(S) || nnz(str2num(Tsingle(:,:))-S)
    put(Tsingle,num2str(S))
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

