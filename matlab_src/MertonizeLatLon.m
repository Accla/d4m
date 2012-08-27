function cLatLonStr = MertonizeLatLon(cLat,cLon)
%MertonizeLatLon: DEPRECATED. Interleave the values of two decimal strings.
%String list utility function.
%  Usage:
%    cLatLonStr = MertonizeLatLon(cLat,cLon)
%  Inputs:
%    cLat = string list of decimal stings formatted as 'type/yyyy.yy,type/yy.yyyy,'
%    cLon = string list of decimal stings formatted as 'type/xxxxx.xxxx,type/xx.xx,'
% Outputs:
%    cLatLonStr = interleaved string list formatted as 'type/ xyxyxyx.yxyx x ,type/yxyx.yxyxy y ,' 
% Interleave the coordinates of two decimal strings.

Nsign = 1; Nint = 3; Nper = 1; Ndec = 6; Nsep = 1;

sepLat = cLat(end);  sepLon = cLon(end);

[cLat0 cLat1] = SplitStr(cLat,'/');
[cLon0 cLon1] = SplitStr(cLon,'/');


% Convert into numeric values.
cLatTmp = cLat1;  cLonTmp = cLon1;
cLatTmp(cLatTmp == sepLat) = ' ';
cLonTmp(cLonTmp == sepLon) = ' ';
cLatLonCplx = complex(sscanf(cLatTmp,'%f'),sscanf(cLonTmp,'%f'));

% Get sign, int and dec parts.
cLatLonSign = complex(sign(real(cLatLonCplx)),sign(imag(cLatLonCplx)));
cLatLonCplx = complex(abs(real(cLatLonCplx)),abs(imag(cLatLonCplx)));
cLatLonInt = complex(floor(real(cLatLonCplx)),floor(imag(cLatLonCplx)));
cLatLonDec = cLatLonCplx - cLatLonInt;

% Populate results with defaults.
Nrow = numel(cLatLonCplx);
Ncol = 2*(Nsign + Nint + Ndec) + Nper + Nsep;
cLatLonMat = char(zeros(Nrow,Ncol));
cLatLonMat(:) = '0';
%cLatLonMat(:,1:2) = '+';
cLatLonMat(:,1:2) = '0';
cLatLonMat(real(cLatLonSign) == -1,1) = '-';
cLatLonMat(imag(cLatLonSign) == -1,2) = '-';
cLatLonMat(:,2*(Nsign + Nint) + Nper) = '.';
cLatLonMat(:,end) = sepLat;


% Convert back to strings.
cLat10mat = Str2mat(sprintf(['%' num2str(Nint) '.' num2str(Nint) 'd' sepLat],real(cLatLonInt)));
cLat11mat = Str2mat(sprintf(['%' num2str(Ndec) '.' num2str(Ndec) 'f' sepLat],real(cLatLonDec)));
cLat11mat = cLat11mat(:,3:end-1);

cLon10mat = Str2mat(sprintf(['%' num2str(Nint) '.' num2str(Nint) 'd' sepLon],imag(cLatLonInt)));
cLon11mat = Str2mat(sprintf(['%' num2str(Ndec) '.' num2str(Ndec) 'f' sepLon],imag(cLatLonDec)));
cLon11mat = cLon11mat(:,3:end-1);

% Handle left side of decimal.
xt = cLat10mat.';
yt = xt;
%yt(:) = 0;
yt(:) = '0';
yt(flipud(xt) > 0) = xt(xt > 0);
cLat10mat = yt.';
cLat10mat = cLat10mat(:,1:end-1);

cLatLonMat(:,(2*Nsign+1):2:2*(Nsign+Nint)) = cLat10mat;

xt = cLon10mat.';
yt = xt;
%yt(:) = 0;
yt(:) = '0';
yt(flipud(xt) > 0) = xt(xt > 0);
cLon10mat = yt.';
cLon10mat = cLon10mat(:,1:end-1);

cLatLonMat(:,(2*Nsign+2):2:2*(Nsign+Nint)) = cLon10mat;

% Handle right side of decimal.
cLatLonMat(:,(2*(Nsign+Nint)+Nper+1):2:(end-1)) = cLat11mat;

cLatLonMat(:,(2*(Nsign+Nint)+Nper+2):2:(end-1)) = cLon11mat;

cLatLonStr = CatStr(cLat0,'/',CatStr(cLon0,'/',Mat2str(cLatLonMat)));
