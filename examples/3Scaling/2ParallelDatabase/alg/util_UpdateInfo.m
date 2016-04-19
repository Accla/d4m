function Ainfo = util_UpdateInfo(AinfoNew)
% Save info to a file and return all the info.
if (nargin < 1)
    AinfoNew = Assoc('','','');
end
finfo = [pwd filesep 'info.csv'];
if exist(finfo, 'file') == 2
  Ainfo = ReadCSV(finfo);
else
  Ainfo = Assoc('','','');
end
Ainfo = Ainfo + AinfoNew;
if ~isempty(Ainfo)
    Assoc2CSV(Ainfo, char(10), ',', finfo);
end
end
