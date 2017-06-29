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

    switchBack = false;
    if javaMethod('isMagicInsert', 'edu.mit.ll.d4m.db.cloud.D4mDbInsert')
      switchBack = true;
      javaMethod('setMagicInsert', 'edu.mit.ll.d4m.db.cloud.D4mDbInsert', false);
    end

    Tinfo = DB('Tinfo');
    put(Tinfo,AinfoNew);

    if switchBack
      javaMethod('setMagicInsert', 'edu.mit.ll.d4m.db.cloud.D4mDbInsert', true);
    end

end

end

