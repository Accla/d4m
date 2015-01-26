function J = Jaccard(A)
% Compute Jaccard coefficients in upper triangle (no diagonal) 
% of unweighted, undirected adjacency matrix. 
% Ex: Jaccard(Mat2Assoc([0,1,1;1,0,1;1,1,0])
%          == Mat2Assoc([0,1/3,1/3;0,0,1/3;0,0,0])

% self edges? If not, diagonal is empty.
    d = sum(A,1);  % Degree row vector.
    
    mytriu = @(a) reAssoc(putAdj(a, triu(Adj(a)))); % triu function on Assoc
    %                               ^^^^^^^^^^^^ Alt impl.: triu(Adj(A),1) removes diagonal
    U = mytriu(A); % Take upper triangle
    % Initialize Jaccard to upper triangle of A^2.
    % Math: triu(A^2) = U^2 + U.'*U + U*U.'
    J = U*U + mytriu(U * U.') + mytriu(U.' * U);
    J = NoDiag(J); % Alt impl.: Graphulo.matfun(J,@(x) triu(x,1))
    if isempty(J)  % short circuit all-zero Jaccard coefficients
        return
    end

    % for each nonzero Jij do Jij := Jij ./ (di + dj - Jij)
    [r,c,v] = find(J);
    [~,dc,dv] = find(d);
    x1 = StrSearch(dc,c);
    x2 = StrSearch(dc,r);
    vnew = v ./ (dv(x1) + dv(x2) - v);
    J = Assoc(r,c,vnew); % might do faster by putAdj impl.
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
