%function alg01_Gen(SCALE, SEED, DoDeleteDB, infoFunc)

% SCALE = 10; SEED = 20160331; infoFunc = @util_UpdateInfo;

EdgesPerVertex = 16;
myPrefix = 'DH_';
Nfile = 8;
if SCALE > 17
  Nfile = 8*(2^min(SCALE-17,4));
end
infoFunc = @util_UpdateInfoAndDB; %@util_UpdateInfo
ND = true; % no diagonal
%DELETE_TABLE_TRIGGER = true;

% NUMTAB = NUMTAB;
DODEG = false;

for SCALE = SCALE
for SEED = 20160331
tname = [myPrefix 'pg' num2str(SCALE,'%02d') '_' num2str(SEED)];
% dname = [pwd filesep tname];

alg01_Gen01_File;
NO_INCIDENCE = true;
alg01_Gen02_Assoc;
clear NO_INCIDENCE;
disp ' '

% myName = tname;
MyDBsetup; % create variables DB, G, INSTANCENAME
% DBsetup creates variables DB, G, INSTANCENAME, Tadj, TadjDeg, Tedge, TedgeDeg, Tedge1, Tedge2
% to detect if table already exists, StrSearch(ls(DB),[table ' ']) >= 1

TNadjUU = [tname '_TgraphAdjUU']; 
TNadjUUDeg = [TNadjUU 'Deg'];

LSDB = ls(DB);
if StrSearch(LSDB,[TNadjUU ' ']) >= 1
    TadjUU = DB(TNadjUU); 
    if exist('DELETE_TABLE_TRIGGER','var') && DELETE_TABLE_TRIGGER
        deleteForce(TadjUU);
    else
        delete(TadjUU);
    end
end
TadjUU = DB(TNadjUU);

if DODEG
  alg01_Gen03_PutAdjUUDeg;
  disp ' '
  G.setPowerLawTriangleSplits(TNadjUUDeg, NUMTAB-1, TNadjUU);
end

DELETE_TABLE_NOT = true;
  alg01_Gen03_PutAdjUU;
clear DELETE_TABLE_NOT;
% alg01_Gen04_ComputeAdjUUDeg;

TadjUU = DB(TNadjUU); 
% TadjUUDeg = DB(TNadjUUDeg);

% TNadj = [tname '_TgraphAdj']; TNadjT = [tname '_TgraphAdjT']; 
% TNadjDeg = [tname '_TgraphAdjDeg'];
% alg01_Gen_PutAdj(DB, G, tname, TNadj, TNadjT, TNadjDeg, infoFunc);
% Tadj = DB(TNadj, TNadjT); 
% TadjDeg = DB([tname '_TgraphAdjDeg']);

% Emergency code ------
% G.CancelCompact(TNadjUU);
% TadjUU = DB(TNadjUU); deleteTable(TadjUU); clear TadjUU;
% TadjUUDeg = DB(TNadjUUDeg); deleteTable(TadjUUDeg); clear TadjUUDeg;

end
end
