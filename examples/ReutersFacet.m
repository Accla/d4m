% Computes Facets in Reuters Entity data.

% Read in entities.
%A=ReutersEntityRead;

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

% Here is a Joint facet search, 
% but  data is too sparse, 
% so just doing single facet searches.
%F = ( noCol(A(:,x)) & noCol(A(:,y)) ).' * A;

% These can be done as one one line:
% If A is a database, then better to do as a multiline statement.
disp('********************************************');
disp(['Entities: ' xE yE]);
disp('******');
disp('Facets: ');
tic
  Q1xyE = A(:,[xE yE]);
  A1xyE = noCol(Q1xyE(:,xE)) & noCol(Q1xyE(:,yE));
  Q2xyE = A(Row(A1xyE),:);
  FxyE = sum(Q2xyE,1);
facetTime = toc;
FxyE
disp(['Time: ' num2str(facetTime)]);
disp('******');
disp('Normalized Facets: ');
% Replace with query?
% EntDeg = sum(A(:,Col(FxyE),1);
FxyEn = FxyE ./ EntDeg;
FxyEn

disp('********************************************');
disp(['Entities: ' xE zE]);
disp('******');
disp('Facets > 5: ');
tic;
  Q1xzE = A(:,[xE zE]);
  A1xzE = noCol(Q1xzE(:,xE)) & noCol(Q1xzE(:,zE));
  Q2xzE = A(Row(A1xzE),:);
  FxzE = sum(Q2xzE,1);
facetTime = toc;
FxzE > 5
disp(['Time: ' num2str(facetTime)]);
disp('******');
disp('Normalized Facets > 0.1: ');
FxzEn = FxzE ./ EntDeg;
FxzEn > 0.1

% These can be done as one one line:
% If A is a database, then better to do as a multiline statement.
disp('********************************************');
disp(['Documents: ' xD yD]);
disp('******');
disp('Facets > 15: ');
tic
  Q1xyD = A([xD yD],:);
  A1xyD = noRow(Q1xyD(xD,:)) & noRow(Q1xyD(yD,:));
  Q2xyD = A(:,Col(A1xyD));
  FxyD = sum(Q2xyD,2);
facetTime = toc;
FxyD > 15
disp(['Time: ' num2str(facetTime)]);
disp('******');
disp('Normalized (Facets > 10) > 0.4: ');
FxyDn = FxyD ./ DocDeg;
(FxyDn > 0.4) & (FxyD > 10)


% These can be done as one one line:
% If A is a database, then better to do as a multiline statement.
disp('********************************************');
disp(['Documents: ' xD zD]);
disp('******');
disp('Facets > 9: ');
tic
  Q1xzD = A([xD zD],:);
  A1xzD = noRow(Q1xzD(xD,:)) & noRow(Q1xzD(zD,:));
  Q2xzD = A(:,Col(A1xzD));
  FxzD = sum(Q2xzD,2);
facetTime = toc;
FxzD > 9
disp(['Time: ' num2str(facetTime)]);
disp('******');
disp('Normalized (Facets > 6) > 0.4: ');
FxzDn = FxzD ./ DocDeg;
(FxzDn > 0.4) & (FxzD > 6)

