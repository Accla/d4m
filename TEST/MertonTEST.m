% Test coordinate Mertonized search appraoch.

% Create random coordinates.
N = 10;
irow = (1:N).';
A = num2str(Assoc(irow,'Lat,',180*(rand(N,1) - 0.5)) + Assoc(irow,'Lon,',360*(rand(N,1) - 0.5)));

% Unfurl columns.
[r c v] = find(A);
A = Assoc(r,CatStr(c,'/',v),1);



[rLat cLat vLat] = find(A(:,'Lat\/*,'));
[rLon cLon vLlon] = find(A(:,'Lon\/*,'));

% Mertonize.
%cLatLon = Mertonize(cLat,cLon);
cLatLon = MertonizeLatLon(cLat,cLon);

