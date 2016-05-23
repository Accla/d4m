function Ainfo = util_UpdateInfoAndDB(AinfoNew)
% Save info to a file and return all the info.
if (nargin < 1)
    AinfoNew = Assoc('','','');
end
finfo = [pwd filesep 'info.tsv'];
if exist(finfo, 'file') == 2
  Ainfo = ReadCSV(finfo);
else
  Ainfo = Assoc('','','');
end
Ainfo = Ainfo + AinfoNew;
if ~isempty(AinfoNew)
    Assoc2CSV(Ainfo, char(10), char(9), finfo);
end

if ~isempty(AinfoNew)
    MyDBsetup;
    Tinfo = DB('Tinfo');
    put(Tinfo,AinfoNew);
end

end

