% Computes Facets Reuters Entity data.
% Unfotunately this data is too sparse.

% Read in entities.
%ReutersEntityRead;

% Sum in each diretion. 
EntDeg = sum(A,1);
DocDeg = sum(A,2);

% Select facet keys.
xE = 'NE_LOCATION/new york,';
yE = 'NE_PERSON/JOHN HOWARD,';
zE = 'NE_ORGANIZATION/united states department of agriculture,';

xD = 'TXT/19960916_55174.txt,';

% Here is a Joint facet search, 
% but  data is too sparse, 
% so just doing single facet searches.
%F = ( noCol(A(:,x)) & noCol(A(:,y)) ).' * A;

% These can be done as one one line:
% If A is a database, then better to do as a multiline statement.
disp('********************************************');
disp(['Keyword: ' xE]);
disp('******');
disp('Facets: ');
Q1xE = A(:,xE);
Q2xE = A(Row(Q1xE),:);
FxE = Q1xE.' * Q2xE;
FxE
disp('******');
disp('Normalized Facets: ');
FxEn = FxE ./ EntDeg;
FxEn
% Second order facets.
disp('******');
disp('2nd Order Facets: ');
Q3xE = A(:,Col(Q2xE));
Q4xE = A(Row(Q3xE),:);
F2xE = sum(Q4xE,1);
F2xE
disp('******');
disp('2nd Order Normalized Facets: ');
F2xEn = F2xE ./ EntDeg;
F2xEn

disp('********************************************');
disp(['Keyword: ' yE]);
disp('******');
disp('Facets: ');
Q1yE = A(:,yE);
Q2yE = A(Row(Q1yE),:);
FyE = Q1yE.' * Q2yE;
FyE
disp('******');
disp('Normalized Facets: ');
FyEn = FyE ./ EntDeg;
FyEn
% Second order facets.
disp('******');
disp('2nd Order Facets: ');
Q3yE = A(:,Col(Q2yE));
Q4yE = A(Row(Q3yE),:);
F2yE = sum(Q4yE,1);
F2yE
disp('******');
disp('2nd Order Normalized Facets: ');
F2yEn = F2yE ./ EntDeg;
F2yEn

disp('********************************************');
disp(['Keyword: ' zE]);
disp('******');
disp('Facets: ');
Q1zE = A(:,zE);
Q2zE = A(Row(Q1zE),:);
FzE = Q1zE.' * Q2zE;
FzE
disp('******');
disp('Normalized Facets: ');
FzEn = FzE ./ EntDeg;
FzEn
% Second order facets.
disp('******');
disp('2nd Order Facets: ');
Q3zE = A(:,Col(Q2zE));
Q4zE = A(Row(Q3zE),:);
F2zE = sum(Q4zE,1);
F2zE
disp('******');
disp('2nd Order Normalized Facets: ');
F2zEn = F2zE ./ EntDeg;
F2zEn

% Combine 2nd order xE and zE?
disp('********************************************');
disp(['Keywords: ' xE ' & ' zE]);
disp('******');
disp('2nd Order Facets: ');
Q4xzE = Q4xE & Q4zE;
F2xzE = sum(Q4xzE,1);
F2xzE
disp('******');
disp('2nd Order Normalized Facets: ');
F2xzEn = F2xzE ./ EntDeg;
F2xzEn

% Repeat for xD, yD and zD.





%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

