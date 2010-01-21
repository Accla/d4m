% Computes Facets in Reuters Entity data.

% Read in entities.
%ReutersEntityRead;

% Parse into time stamp and add to A.
ReutersTracks;

disp(' ')
disp('*********');
x = 'NE_PERSON/DAMON HILL,';
y = 'NE_PERSON/GARY WONG,';
disp(['x=' x '  y=' y]);
disp('displayFull(Atrack(:,[x y]))')
displayFull(Atrack(:,[x y]))
pause(5)

disp(' ')
disp('*********');
t1 = 'NE_TIME/1996-09-06,';
t2 = 'NE_TIME/1996-09-13,';
l1 = 'NE_LOCATION/jordan,';
disp(['t1=' t1 '  t2=' t2 '  l1=' l1 ]);
disp('Col(Atrack([t1 :, t2],:) == l1)')
disp(Col(Atrack([t1 ':,' t2],:) == l1))
