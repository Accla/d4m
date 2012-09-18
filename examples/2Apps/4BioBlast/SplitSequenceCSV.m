function A = SplitSequenceCSV(CSVfile,wordsize)
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Script for reading in sequence data and putting to an Associative array.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

[r c v] = FindCSV(CSVfile);   % Read in file
v = lower(v);   % Convert sequence to lower case.

vMat = Str2mat(v);    % Convert a matrix.
Nrow = size(vMat,1);  % Get rows of matrix.
Ncol = size(vMat,2);   % Get columns.
NcolSplit = ceil(Ncol.*(1.05 + 1./wordsize));    % Get size of larger matrix.
vMatSplit = char(zeros(Nrow,NcolSplit));    % Create larger matrix.
jSplit = 1:NcolSplit;    % Create index into larger matrix.
jSplit((wordsize+1):(wordsize+1):end) = 0;    % Eliminate every wordsize element.
jSplit = jSplit(jSplit > 0);
jSplit = jSplit(1:Ncol);    % Limit index to size of input matrix.
vMatSplit(:,jSplit) = vMat;    % Copy data into split matrix.
vMatSplit((vMatSplit(:,1:end-1) == 0) & (vMatSplit(:,2:end) > 0)) = ';';      % Insert ; in blanks.
vSplit = Mat2str(vMatSplit);     % Convert  back to a string.
vSplit = strrep(vSplit,';,',',');   % Eliminate trailing ;.
vMatSplit = Str2mat(vSplit);    % Convert back to matrix.
vMatSplit(vMatSplit == ';') = ',';     % Replace ; with ,
[j i tmp]  = find((vMatSplit == ',').');   % Find locations of ,
[ju ju1 ju2] = unique(j);     % Compress row locations of ,

vMatSplit = Str2mat(Mat2str(vMatSplit));    % Unfurl and convert back to matrix.
[iOK tmp tmp] = find( sum(vMatSplit > 0,2) == (wordsize + 1) );    % Find words of the proper length.

A = Assoc(i(iOK),ju2(iOK),Mat2str(vMatSplit(iOK,:)));    % Construct assoc array.
A = putRow(A,r);     % Tag rows with row string.
[r c v] = find(A);    % Get triples.
A = Assoc(r,v,1);    % Construct final associative array.

return
end