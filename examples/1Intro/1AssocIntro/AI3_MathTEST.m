%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Show different ways to index an associative array.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('on'); more('off');          % Turn on echoing and paging.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

A = ReadCSV('A.csv');            % Read CSV file into associative array.
A = dblLogi(A);                  % Convert to numeric. Short for double(logical()).
displayFull(A);                  % Show in tabular form.

       disp(   sum(A,1)   );     % Sum into row vector.
displayFull(   sum(A,2)   );     % Sum into column vector.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Compute a simple join.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
Aa = A(:,'a,');                  % Get column a.
Ab = A(:,'b,');                  % Get column b.
Aab = noCol(Aa) & noCol(Ab);     % Find both a and b.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Compute histogram (facets) of other columns
% that are in rows with both a and b.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
F = transpose( noCol(A(:,'a,')) & noCol(A(:,'b,')) ) * A;
displayFull(F.');                % Show in tabular form.

Fn = F ./ sum(A,1);              % Normalized facets.
displayFull(Fn.');               % Show in tabular form.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Compute correlation.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
AtA = sqIn(A);                   % Same as A'*A, but faster.
d = diag(Adj(AtA));              % Get diagonal of adjacency matrix.
AtA = putAdj(AtA,Adj(AtA) - diag(d));   % Subtract diagonal, works on square matrix.
displayFull(AtA);                % Show in tabular form.

displayFull(NoDiag(A.' * A));    % Same thing another way.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

