DELETE_TABLE_TRIGGER = true;

javaMethod('setMagicInsert', 'edu.mit.ll.d4m.db.cloud.D4mDbInsert', false);
javaMethod('setMagicInsert2', 'edu.mit.ll.d4m.db.cloud.D4mDbInsert', false);

EdgesPerVertex = 16;
myPrefix = 'DH_';
Nfile = 8;
infoFunc = @util_UpdateInfoAndDB; %@util_UpdateInfo
SEED = 20160331;
durability = 'log'; % choices: none, log, flush, sync (default)
filterRowCol = [];


for SCALE=10; %10:20
if SCALE > 17
  Nfile = 8*(2^min(SCALE-17,4));
end


SPLITS_RATE_LINEAR = 0.70;
SPLITS_RATE_EXP = 1.1;

tname = [myPrefix 'pg' num2str(SCALE,'%02d') '_' num2str(SEED)];
alg01_Gen01_File;
MyDBsetup;
TNadjUU = [tname '_TgraphAdjUULower'];
TNedge = [tname '_TgraphEdge'];
tnameTmp = [TNadjUU '_triCount_tmpA'];

% delete tables if they exist
LSDB = ls(DB);
if StrSearch(LSDB,[TNadjUU ' ']) >= 1
    Ttmp = DB(TNadjUU);
    if exist('DELETE_TABLE_TRIGGER','var') && DELETE_TABLE_TRIGGER
        deleteForce(Ttmp);
    else delete(Ttmp); end
end
if StrSearch(LSDB,[TNedge ' ']) >= 1
    Ttmp = DB(TNedge);
    if exist('DELETE_TABLE_TRIGGER','var') && DELETE_TABLE_TRIGGER
        deleteForce(Ttmp);
    else delete(Ttmp); end
    clear Ttmp
end


TI = DBaddJavaOps('edu.mit.ll.graphulo.tricount.TriangleIngestor',INSTANCENAME,ZKHOSTS,'root','secret');
TI.ingestDirectory([pwd filesep tname], TNadjUU, TNedge, false, false);



for NUMTAB=4; %8:8:40

SPLITS_RATE_LINEAR = 1; 
SPLITS_RATE_EXP = 1;
SPLITS_RATE_LINEAR_INV = 1; 
SPLITS_RATE_EXP_INV = 1;


alg04_triCount01_GraphuloEdge

pause(5)

end
end
