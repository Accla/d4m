AssocSetup;  % Create assoc array A.

A = double(logical(A));   % Convert to numeric.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Viewing an associative array.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%disp(A)
%spy(A.')


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Facet search.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

x = 'a ';       y = 'b ';
%F = ( noCol(A(:,x)) & noCol(A(:,y)) ).' * A;    % Octave 3.2.4 doesn't like X.' * A
F = transpose( noCol(A(:,x)) & noCol(A(:,y)) ) * A;
%displayFull(F.')

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Normalized facet search.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Fn = F ./ sum(A,1);
%displayFull(Fn.')

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Correlation (all facet pairs).
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

AtA = sqIn(A);     d = diag(Adj(AtA));
AtA = putAdj(AtA,Adj(AtA) - diag(d));
%disp(AtA)
%spy(AtA)

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Normalized correlation.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

[i j v] = find(Adj(AtA));
AtAn = putAdj(AtA, sparse(i,j, v./min(d(i),d(j))) );

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Multi-facet queries.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

x = 'a ';        p = 'b* ';
F2 = (AtA(x,p) > 2) & (AtAn(x,p) > 0.9);


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Triangles.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

x = 'a ';       p = 'a* ';
y = Row(AtA(p,x) + AtA(x,p));
AtAyy = AtA(y,y);
%spy(AtA(y,y));

z = Row(AtA(y,y) - (AtA(p,x) + AtA(x,p)));
AtAnzz = AtAn(z,z);
%AtAn(z,z)

save([mfilename '.mat'],'-v6','F','Fn','AtA','AtAn','F2','AtAyy','AtAnzz')

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

