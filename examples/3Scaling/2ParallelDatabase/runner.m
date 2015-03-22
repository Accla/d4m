
DoGenData = true;
SCALE = 12; EdgesPerVertex = 16;
DoDeleteDB = true;
DoPutDB = true;
DoTableMult = true;
%%
TOPgetTime = [];
TOPmultTimeDB = [];
TOPmultTimeDBBW = [];
TOPmultTimeLocal = [];
TOPSCALE = [];

for SCALE=12:13
DBsetup
if DoGenData
    pDB02_FileTEST
    pDB03_AssocTEST
end
if DoDeleteDB
    deleteForce(Tadj); 
    deleteForce(TadjDeg); 
    deleteForce(Tedge); 
    deleteForce(TedgeDeg);
end
if DoPutDB
    pDB05_SetupTEST
    pDB06_AdjInsertTEST
end
if DoTableMult
    fprintf('Starting SCALE = %d\n',SCALE);
    pDB20_AdjMultTEST 
end
TOPgetTime = [TOPgetTime getTime];
TOPmultTimeDB = [TOPmultTimeDB multTimeDB];
TOPmultTimeDBBW = [TOPmultTimeDBBW multTimeDBBW];
TOPmultTimeLocal = [TOPmultTimeLocal multTimeLocal];
TOPSCALE = [TOPSCALE SCALE];
end

% experiment
% 1. Time Matlab in-memory
% 2. Time Accumulo TableMult compact
% 3. Time Accumulo TableMult BatchWrite
% 4. 
Atime = Assoc('','','');
for i=1:numel(TOPSCALE)
    Atime = Atime + Assoc('getTime,multTimeDB,multTimeDBBW,multTimeLocal,',num2str(repmat(TOPSCALE(i),1,4),'%d,'), ...
                          [TOPgetTime(i), TOPmultTimeDB(i), TOPmultTimeDBBW(i), TOPmultTimeLocal(i)]);
end
Assoc2CSV(Atime,char(10),',',['AtimeTableMult' datestr(datetime,'yy-mm-dd-HH-MM-SS') '.csv']);

figure;
plot(TOPSCALE,TOPmultTimeDB,'r*',TOPSCALE,TOPmultTimeDBBW,'g*',TOPSCALE,TOPmultTimeLocal,'b*',TOPSCALE,TOPgetTime,'k*');
xlabel('SCALE'); ylabel('time'); 
legend('multTimeDB','multTimeDBBW','multTimeLocal','getTime');



% Emergency code
%G.CancelCompact(rname);
