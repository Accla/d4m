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

xD = 'TXT/19970704_706858.txt,';
yD = 'TXT/19970217_383544.txt,';
zD = 'TXT/19970815_801431.txt,';

% Compute Entity/Entity covariance.
% Can be done with matrics since diag(AtA) is dense.
AtA = logical(A);
AA = Adj(AtA);
AAtAA = AA.' * AA;
AtA = putAdj(AtA,AAtAA);
AtA = putRow(AtA,Col(AtA));

AtAn = AtA;
d = diag(AAtAA);
[i j v] = find(AAtAA);
%vn = v ./ sqrt( d(i) .* d(j) );
vn = v ./ min( d(i), d(j) );
%AAtAAn = AAtAA ./ Adiv;
AAtAAn = sparse(i,j,vn);
AtAn = putAdj(AtAn,AAtAAn);

disp('*********');
(AtAn(xE,'NE_PERSON/*,') > 0.9) & (AtA(xE,'NE_PERSON/*,') > 20)

disp('*********');
AtAn('NE_PERSON/AL YOON,','NE_PERSON/*,')

