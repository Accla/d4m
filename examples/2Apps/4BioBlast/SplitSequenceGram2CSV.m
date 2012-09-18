function [An Ap] = SplitSequenceGram2CSV(CSVfile,wordsize)
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Script for reading in sequence data and putting to an Associative array.
% An holds counts.  Ap holds positions.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

[r c v] = FindCSV(CSVfile);   % Read in file
v = lower(v);   % Convert sequence to lower case.

vMat = Str2mat(v).';    % Convert a matrix.
vMat(vMat == ',') = 0;

Nrow = size(vMat,1);  % Get rows of matrix.
Ncol = size(vMat,2);   % Get columns.
%NcolSplit = ceil(Ncol.*(1.05 + 1./wordsize));    % Get size of larger matrix.

NrowSplit = ceil(Nrow/wordsize)*wordsize;
vMatSplit = char(zeros(NrowSplit,Ncol,wordsize));

for i=1:wordsize
%  vMatSplit(i,:,1:(Ncol-(i-1))) = vMat(:,i:Ncol);
  vMatSplit(1:(Nrow-(i-1)),:,i) = vMat(i:Nrow,:);
end

vMatSplit = reshape(vMatSplit,wordsize,NrowSplit/wordsize,Ncol,wordsize);

vMatSplit1 = char(zeros(size(vMatSplit) + [1 0 0 0]));
vMatSplit1(1:wordsize,:,:,:) = vMatSplit;
vMatSplit1(wordsize+1,:,:,:) = ',';
vMatSplit1(vMatSplit1 == 'n') = 0;   % Eliminate n.

vMatSplit = reshape(vMatSplit1,wordsize+1,NrowSplit*Ncol).';

ii = zeros(NrowSplit/wordsize,Ncol,wordsize);
jj = ii;
for i=1:Ncol
  ii(:,i,:) = i;
end

ii = reshape(ii,NrowSplit*Ncol,1);

%jtemp = reshape(1:NrowSplit,NrowSplit/wordsize,wordsize);
jtemp = reshape(1:NrowSplit,wordsize,NrowSplit/wordsize).';
for i=1:Ncol
  jj(:,i,:) = jtemp;
end

jj = reshape(jj,NrowSplit*Ncol,1);

%vMatSplit = reshape(vMatSplit1,wordsize+1,NrowSplit,Ncol);

[iOK tmp tmp] = find( sum(vMatSplit == 0,2) == 0 );    % Find words of the proper length.


A = Assoc(ii(iOK),jj(iOK),Mat2str(vMatSplit(iOK,:)));

A = putRow(A,r);

[rr cc vv] = find(A);
An = Assoc(rr,vv,1,@sum);
Ap = Assoc(rr,vv,sprintf('%g,',cc),@AssocCatStrFunc);


return
end