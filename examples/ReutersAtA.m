% Computes Facets Reuters Entity data.

% Read in entities.
%A = ReutersEntityRead;

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
d = diag(AAtAA);
AAtAA = AAtAA - diag(d);
AtA = putAdj(AtA,AAtAA);
AtA = putRow(AtA,Col(AtA));

% Compute histograms.
[temp temp v] = find(AAtAA);
AtAhist = sparse(v,1,1);

%[temp temp v] = find(AAtAA - diag(diag(AAtAA)));
AtAmDhist = sparse(v,1,1);


AtAperPer = AtA('NE_PERSON/*,','NE_PERSON/*');
[temp temp v] = find(AtAperPer);
AtAperPerHist = sparse(v,1,1);

AtAlocLoc = AtA('NE_LOCATION/*,','NE_LOCATION/*,');
[temp temp v] = find(AtAlocLoc);
AtAlocLocHist = sparse(v,1,1);

AtAorgOrg = AtA('NE_ORGANIZATION/*,','NE_ORGANIZATION/*,');
[temp temp v] = find(AtAorgOrg);
AtAorgOrgHist = sparse(v,1,1);

AtAperLoc = AtA('NE_PERSON/*,','NE_LOCATION/*');
[temp temp v] = find(AtAperLoc);
AtAperLocHist = sparse(v,1,1);

AtAperOrg = AtA('NE_PERSON/*,','NE_ORGANIZATION/*,');
[temp temp v] = find(AtAperOrg);
AtAperOrgHist = sparse(v,1,1);

AtAlocOrg = AtA('NE_LOCATION/*,','NE_ORGANIZATION/*,');
[temp temp v] = find(AtAlocOrg);
AtAlocOrgHist = sparse(v,1,1);

figure;
loglog(AtAhist,'o');
xlabel('Common Documents per Entity Pair');
ylabel('Number of Entity Pairs');
hold on
loglog(AtAperPerHist,'+');
loglog(AtAlocLocHist,'x');
loglog(AtAorgOrgHist,'.');
legend('all-all','person-person','place-place','org-org');
hold off

figure;
loglog(AtAhist,'o');
xlabel('Common Documents per Entity Pair');
ylabel('Number of Entity Pairs');
hold on
loglog(AtAperLocHist,'+');
loglog(AtAperOrgHist,'x');
loglog(AtAlocOrgHist,'.');
legend('all-all','person-place','person-org','place-org');
hold off


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

disp('*********');
disp('AtAn(NE_PERSON/AL YOON,,NE_PERSON/*,)')
AtAn('NE_PERSON/AL YOON,','NE_PERSON/*,')
disp('*********');

x = 'NE_PERSON/AL YOON,';
xClique = AtAperPer(x,x);
xNeigh = AtAperPer(:,x) + AtAperPer(x,:);
y = Row(xNeigh);
yClique = AtAperPer(y,y);
xTri = yClique - xNeigh;
z = Row(xTri);

disp('*********');
disp(['Triangles of: ' x]);
AtAn(z,z)

