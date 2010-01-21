% Computes Facets in Reuters Entity data.

% Read in entities.
%ReutersEntityRead;

% Sum in each diretion. 
% Replace with queries as needed?
EntDeg = sum(A,1);
DocDeg = sum(A,2);

% Select facet keys.
xE = 'NE_LOCATION/new york,';
yE = 'NE_PERSON/JOHN HOWARD,';

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
pause(5);
disp('******');
disp('Normalized Facets: ');
% Replace with query?
% EntDeg = sum(A(:,Col(FxyE),1);
FxyEn = FxyE ./ EntDeg;
FxyEn
