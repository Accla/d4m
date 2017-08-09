
im = imread('peppers.png');
[nr, nc, ns] = size(im);

[ir, ic] = ind2sub([nr nc], 1:nr*nc);
ir = ir(:);
ic = ic(:);
slice = ones(size(ir));

dbname = 'txg-scidb01';
DB = DBsetupSciDB(dbname);

rowChunkSize = nr;
colChunkSize = nc;
sliceChunkSize = 1;

ls(DB)

T = DB( sprintf('D4Mtest_%s <val:uint8> [rows=1:%d,%d,0, cols=1:%d,%d,0, slice=1:%d,1,0]', datestr(now, 30), nr, nr, nc, nc, ns) );

% import data
putTriple( T, [ir ic slice],   double( reshape(im(:,:,1), [nr*nc 1] ) ) );
putTriple( T, [ir ic slice*2], double( reshape(im(:,:,2), [nr*nc 1] ) ) );
putTriple( T, [ir ic slice*3], double( reshape(im(:,:,3), [nr*nc 1] ) ) );

% extract data
v1 = T(:,:,1);
disp( nnz( v1 - double(im(:,:,1)) ) ); % this should be 0

v2 = T(:,:,2);
disp( nnz( v2 - double(im(:,:,2)) ) ); % this should be 0

v3 = T(:,:,3);
disp( nnz( v3 - double(im(:,:,3)) ) ); % this should be 0

% cleanup
deleteForce(T);

ls(DB)

