% Computes Facets Reuters Entity data.

% Read in entities.
%ReutersEntityRead;

% Sum in each diretion. 
% Replace with queries as needed?
EntDeg = sum(A,1);
DocDeg = sum(A,2);

% Select facet keys.
xE = 'NE_LOCATION/new york,';
yE = 'NE_PERSON/JOHN HOWARD,';
zE = 'NE_ORGANIZATION/united states department of agriculture,';

% Compute Entity/Entity covariance.
% Can be done with matrics since diag(AtA) is dense.
AtA = logical(A);
AA = Adj(AtA);
AAtAA = AA.' * AA;
d = diag(AAtAA);
AAtAA = AAtAA - diag(d);
AtA = putAdj(AtA,AAtAA);
AtA = putRow(AtA,Col(AtA));


% Compute Entity/Entity normalized covariance.
% Can be done with matrics since diag(AtA) is dense.
AtAn = AtA;
[i j v] = find(AAtAA);
%vn = v ./ sqrt( d(i) .* d(j) );
vn = v ./ min( d(i), d(j) );
%AAtAAn = AAtAA ./ Adiv;
AAtAAn = sparse(i,j,vn);
AtAn = putAdj(AtAn,AAtAAn);

disp('*********');
disp('(AtAn(NE_LOCATION/new york,NE_PERSON/*,) > 0.9) &')
disp('(AtA(NE_LOCATION/new york,,NE_PERSON/*,) > 20)')
(AtAn(xE,'NE_PERSON/*,') > 0.9) & (AtA(xE,'NE_PERSON/*,') > 20)
pause(5);

disp('*********');
disp('AtAn(NE_PERSON/AL YOON,,NE_PERSON/*,)')
AtAn('NE_PERSON/AL YOON,','NE_PERSON/*,')
pause(5);

disp('*********');
AtAperPer = AtA('NE_PERSON/*,','NE_PERSON/*');
x = 'NE_PERSON/AL YOON,';
xClique = AtAperPer(x,x);
xNeigh = AtAperPer(:,x) + AtAperPer(x,:);
y = Row(xNeigh);
yClique = AtAperPer(y,y);
xTri = yClique - xNeigh;
z = Row(xTri);
disp(['Triangles of: ' x]);
AtAn(z,z)
