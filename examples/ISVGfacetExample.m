% Tests SKS facet search using associative arrays.
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


% Create facets.
nl = char(10);  % Newline character.
x = ['group/Al Qaeda Afghanistan/' nl];
y = ['group/Taliban Afghanistan/' nl];

% Facet search.
%`A = double(logical(A));
F = ( noCol(A(:,x)) & noCol(A(:,y)) ).' * A;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

