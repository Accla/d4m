EdgesPerVertex = 16;
myPrefix = 'DH_';
Nfile = 1; % Single file!
infoFunc = @disp; %@util_UpdateInfo
ND = true; % no diagonal
SEED = 20160331;
HSCALE = [];
HTIME = [];
HTRI = [];

for SCALE=10:15
tname = [myPrefix 'pg' num2str(SCALE,'%02d') '_' num2str(SEED)];

alg01_Gen01_File;

t0 = clock;
SPECIAL_INCIDENCE = true;
alg01_Gen02_Assoc;
clear edgeStrMat edgeStr rowStr colStr
  % load([fname '.A.mat']);
  % load([fname '.E.mat']);

A = Adj(A+A.');
A = A - diag(diag(A));
E = Adj(E);

C = A*E;
numTriangles = nnz( C==2 ) / 3;
t_triangle_count = etime(clock, t0);

fprintf('number of triangles : %d\ntime to count triangles : %f seconds\n', ...
    numTriangles, t_triangle_count );

HSCALE = [HSCALE SCALE];
HTIME = [HTIME t_triangle_count];
HTRI = [HTRI numTriangles];

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


end
[HSCALE; HTIME; HTRI]
