% Computes Facets Reuters Entity data.
% Unfotunately this data is too sparse.

% Read in entities.
%ReutersEntityRead;

% Sum in each diretion. 
EntDeg = sum(A,1);
DocDeg = sum(A,2);

% Select facet keys.
xE = 'NE_LOCATION/new york,';
%xD =   doc w/lots of entities
yE = 'NE_PERSON/JOHN HOWARD,';
%yD =   doc w/lots of entities
zE = 'NE_ORGANIZATION/united states department of agriculture,';
%zD =   doc w/lots of entities

% Here is a Joint facet search, 
% but  data is too sparse, 
% so just doing single facet searches.
%F = ( noCol(A(:,x)) & noCol(A(:,y)) ).' * A;



% These can be done as one one line:
% If A is a database, then better to do as a multiline statement.
Q1xE = A(:,xE);
Q2xE = A(Row(Q1xE),:);
FxE = Q1xE.' * Q2xE;
disp(['Keyword: ' xE]);
disp('Facets: ');
FxE
%FxEn = FxE ./ EntDeg;
%disp('Normalized Facets: ');
%FxEn
% Second order facets.
%Q3xE = A(:,Col(Q2xE));
%Q4xE = A(Row(Q3xD),:);
%F2xE = sum(Q4xE,1);

disp(['Keyword: ' y]);
disp('Facets: ');
Q1 = A(:,y);
Q2 = A(Row(Q1),:);
Fy = Q1.' * Q2


disp(['Keyword: ' z]);
disp('Facets: ');
Q1 = A(:,z);
Q2 = A(Row(Q1),:);
Fz = Q1.' * Q2

% Combine 2nd order xE and yE?

% Repeat for xD, yD and zD.





%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

