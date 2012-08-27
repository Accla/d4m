function cLatLonStr = Mertonize(cLat,cLon)
%Mertonize: DEPRECATED. Interleave the values of two decimal strings.
%String list utility function.
%  Usage:
%    cLatLonStr = Mertonize(cLat,cLon)
%  Inputs:
%    cLat = string list of decimal stings formatted as 'type/yyyy.yy,type/yy.yyyy,'
%    cLon = string list of decimal stings formatted as 'type/xxxxx.xxxx,type/xx.xx,'
% Outputs:
%    cLatLonStr = interleaved string list formatted as 'type/ xyxyxyx.yxyx x ,type/yxyx.yxyxy y ,' 

% Interleave the coordinates of two decimal strings.

sepLat = cLat(end);  sepLon = cLon(end);

[cLat0 cLat1] = SplitStr(cLat,'/');
[cLon0 cLon1] = SplitStr(cLon,'/');

% Need to deal with cases without decimal.
cLat1mat = Str2mat(cLat1);
nopLat = find(not(sum(double(cLat1mat == '.'),2)));
if not(isempty(nopLat))
  cLat1matTmp = Str2mat(strrep(Mat2str(cLat1mat(nopLat,:)),sepLat,['.0' sepLat]));
  cLat1mat(nopLat,1:length(cLat1matTmp(1,:))) = cLat1matTmp;
  cLat1 = Mat2str(cLat1mat);
end

cLon1mat = Str2mat(cLon1);
nopLon = find(not(sum(double(cLon1mat == '.'),2)));
if not(isempty(nopLon))
  cLon1matTmp = Str2mat(strrep(Mat2str(cLon1mat(nopLon,:)),sepLon,['.0' sepLon]));
  cLon1mat(nopLon,1:length(cLon1matTmp(1,:))) = cLon1matTmp;
  cLon1 = Mat2str(cLon1mat);
end

[cLat10 cLat11] = SplitStr(cLat1,'.');
[cLon10 cLon11] = SplitStr(cLon1,'.');

if 0
  NumStr(cLat10)
  NumStr(cLat11)
  NumStr(cLon10)
  NumStr(cLon11)
end

cLat10mat = Str2mat(cLat10);   cLat11mat = Str2mat(cLat11);
cLon10mat = Str2mat(cLon10);   cLon11mat = Str2mat(cLon11);

% Handle left side of decimal.
xt = cLat10mat.';
yt = xt;  yt(:) = 0;
yt(flipud(xt) > 0) = xt(xt > 0);
cLat10mat = yt.';

xt = cLon10mat.';
yt = xt;  yt(:) = 0;
yt(flipud(xt) > 0) = xt(xt > 0);
cLon10mat = yt.';

[Nrow NcolLat10] = size(cLat10mat);
[Nrow NcolLon10] = size(cLon10mat);
Ncol = 2*max(NcolLat10,NcolLon10)+1;


cLatLon10mat = char(zeros(Nrow,Ncol));
%cLatLon10mat(:) = ' ';
cLatLon10mat(:,1:2:(2*NcolLat10)) = fliplr(cLat10mat);
cLatLon10mat(:,2:2:(2*NcolLon10)) = fliplr(cLon10mat);
cLatLon10mat = fliplr(cLatLon10mat);
cLatLon10mat = cLatLon10mat(:,1:end-1);

cLatLon10matMask = double(cLatLon10mat > 0);
cLatLon10matMask = double((cLatLon10matMask + circshift(cLatLon10matMask,[0 1])) > 0);
cLatLon10matMask(:,1) = 0;
cLatLon10mat(not(cLatLon10mat) & cLatLon10matMask) = ' ';
cLatLon10str = Mat2str(cLatLon10mat);

%cLat10mat(cLat10mat == sepLat) = 0;
%cLon10mat(cLon10mat == sepLon) = 0;


%[tmp cLat10matSepInd tmp] = find(cLat10mat == sepLat);
%[tmp cLon10matSepInd tmp] = find(cLon10mat == sepLon);
%cLatLon10matSepInd = 2*max(cLat10matSepInd,cLon10matSepInd)+1;


% Handle right side of decimal.

[Nrow NcolLat11] = size(cLat11mat);
[Nrow NcolLon11] = size(cLon11mat);
Ncol = 2*max(NcolLat11,NcolLon11)+1;

cLat11mat(cLat11mat == sepLat) = 0;
cLon11mat(cLon11mat == sepLon) = 0;

cLatLon11mat = char(zeros(Nrow,Ncol));
%cLatLon11mat(:) = ' ';
cLatLon11mat(:,1:2:(2*NcolLat11)) = cLat11mat;
cLatLon11mat(:,2:2:(2*NcolLon11)) = cLon11mat;

cLatLon11matMask = double(cLatLon11mat > 0);
cLatLon11matMask = double((cLatLon11matMask + circshift(cLatLon11matMask,[0 -1])) > 0);
cLatLon11matMask(:,end) = 0;

cLatLon11matEnd = sum(cLatLon11matMask,2);

cLatLon11mat(sub2ind(size(cLatLon11mat),(1:Nrow).',cLatLon11matEnd+1)) = sepLon;
cLatLon11mat(not(cLatLon11mat) & cLatLon11matMask) = ' ';
cLatLon11str = Mat2str(cLatLon11mat);

cLatLonStr = CatStr(cLat0,'/',CatStr(cLon0,'/',CatStr(cLatLon10str,'.',cLatLon11str)));
