%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Compute graphs from entity edge data.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('on'); more('off')               % Turn on echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
load('Entity.mat');                   % Load edge incidence matrix.
E = dblLogi(E);                       % Convert to numeric.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Compute entity (all facet pairs).
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

A = sqIn(E);                          % Same E.' * E, but faster.
d = diag( Adj(A) );                   % Get diagonal of sparse matrix.
A = putAdj(A,  Adj(A) - diag(d)  );   % Subtract diagonal and put back in assoc.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Normalized correlation.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

[i j v] = find( Adj(A) );             % Get triples from sparse matrix.
An = putAdj(A, sparse(i,j, v./min(d(i),d(j))) );  % Normalize values.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Multi-facet queries.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

x = 'LOCATION/new york,';             % Pick location.
p = StartsWith('PERSON/,');           % Limit to people.
displayFull( (A(p,x) > 4) & (An(p,x) > 0.3) );   % Find high correlations.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Triangles.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

p0 = 'PERSON/john kennedy,';          % Pick a person.

p1 = Row( A(p,p0) + A(p0,p) );        % Get neighbors of x.
spy( A(p1,p1) );                      % Plot neighborhood.

p2 = Row( A(p1,p1) - (A(p,p0) + A(p0,p)) );  % Get triangles of x.
A(p2,p2) > 1                          % Show popular triangles.


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
