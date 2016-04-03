function alg01_Gen(SCALE, SEED, DoDeleteDB, infoFunc)

% SCALE = 10; SEED = 20160331; infoFunc = @util_UpdateInfo;

% Constants
EdgesPerVertex = 16;
myPrefix = 'DH_';
Nfile = 8;
infoFunc = @util_UpdateInfo;
tname = [myPrefix 'pg' num2str(SCALE,'%02d') '_' num2str(SEED)];

tname = alg01_Gen_File(SCALE, SEED, EdgesPerVertex, Nfile, myPrefix, infoFunc);
alg01_Gen_Assoc(tname, infoFunc);

% myName = tname;
MyDBsetup; % create variables DB, G, INSTANCENAME
% DBsetup creates variables DB, G, INSTANCENAME, Tadj, TadjDeg, Tedge, TedgeDeg, Tedge1, Tedge2
% to detect if table already exists, StrSearch(ls(DB),[table ' ']) >= 1

TNadjUU = [tname '_TgraphAdjUU'];
TNadjUUDeg = [TNadjUU 'Deg'];
alg01_Gen_PutAdjUU(DB, G, tname, TNadjUU, infoFunc);
alg01_Gen_ComputeAdjUUDeg(G, tname, TNadjUU, TNadjUUDeg, infoFunc);

TadjUU = DB(TNadjUU); TadjUUDeg = DB(TNadjUUDeg);

% TNadj = [tname '_TgraphAdj']; TNadjT = [tname '_TgraphAdjT']; 
% TNadjDeg = [tname '_TgraphAdjDeg'];
% alg01_Gen_PutAdj(DB, G, tname, TNadj, TNadjT, TNadjDeg, infoFunc);
% Tadj = DB(TNadj, TNadjT); 
% TadjDeg = DB([tname '_TgraphAdjDeg']);

% Emergency code ------
% G.CancelCompact(TNadjUU);
% TadjUU = DB(TNadjUU); deleteTable(TadjUU); clear TadjUU;
% TadjUUDeg = DB(TNadjUUDeg); deleteTable(TadjUUDeg); clear TadjUUDeg;


