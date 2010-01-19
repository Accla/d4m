% Simple test of associative arrays.
% Note: requires access to ISVG dataset.

% Set data dir.
dataDir = '/Users/kepner/Documents/3D-GraphProcessor/Applications/CT-SNAIR/isvg_triples/';

% Load data.
tic;
fid = fopen([dataDir 'a.dat'], 'r');
rowStr = fread(fid, inf, 'uint8=>char').';
fclose(fid);
fid = fopen([dataDir 'b.dat'], 'r');
colStr = fread(fid, inf, 'uint8=>char').';
fclose(fid);
fid = fopen([dataDir 'c.dat'], 'r');
valStr = fread(fid, inf, 'uint8=>char').';
fclose(fid);
readTime = toc



% Create assoc array.
tic
A = Assoc(rowStr,colStr,valStr);
assocConstructTime = toc


% Get neighborhood around x.
nl = char(10);  % Newline character.
%x = ['country/Colombia/' nl];
x = ['group/Revolutionary Armed Forces of Colombia FARC/' nl];
xClique = A(x,x);
xNeigh = A(:,x) + A(x,:);
y = Key(xNeigh);
yClique = A(y,y);
xTri = yClique - xNeigh;
z = Key(xTri);

% Limit to individuals.
AA = A('individual/*/ ','individual/*/ ');
zClique = AA(z,z);
zNeigh = AA(:,z) + AA(z,:);
zLeaf = zNeigh - zClique;
zLeaf1 = ( zLeaf == 'Associate ' );


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

