EdgesPerVertex = 16;
myPrefix = 'DH_';
Nfile = 1;
infoFunc = @disp; %@util_UpdateInfo
ND = true; % no diagonal
SEED = 20160331;

for SCALE=17; %10:20
% if SCALE > 17
%   Nfile = 8*(2^min(SCALE-17,4));
% end
tname = [myPrefix 'pg' num2str(SCALE,'%02d') '_' num2str(SEED)];

alg01_Gen01_File;
alg01_Gen02_Assoc;
clear edgeStrMat edgeStr rowStr colStr
  load([fname '.A.mat']);
  load([fname '.E.mat']);

A = Adj(A+A.');
E = Adj(E);


t0 = clock;
C = A*E;
numTriangles = nnz( C==2 ) / 3;
t_triangle_count = etime(clock, t0);

fprintf('number of triangles : %d\ntime to count triangles : %f seconds\n', ...
    numTriangles, t_triangle_count );

% tic
% As = Adj(Abs0(A+A.'));
% As(logical(speye(size(A)))) = 0; % NoDiag.
% L = tril(As,-1);
% U = triu(As,1);
% B = tril(L * U);
% C = B .* As;
% trianglesD4M = full(sum(sum(C,1),2));
% tTriD4M = toc;
% fprintf('SCALE %2d Triangles %d Time %f\n', SCALE, triangles, tTriD4M)


% for i = myFiles
%   fname = [dname filesep num2str(i)];    % Create filename.
%   load([fname '.A.mat']);
%   load([fname '.E.mat']);
% end



end
