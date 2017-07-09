DELETE_TABLE_TRIGGER = true;

javaMethod('setMagicInsert', 'edu.mit.ll.d4m.db.cloud.D4mDbInsert', false);
javaMethod('setMagicInsert2', 'edu.mit.ll.d4m.db.cloud.D4mDbInsert', false);

EdgesPerVertex = 16;
myPrefix = 'DH_';
Nfile = 8;
infoFunc = @util_UpdateInfoAndDB; %@util_UpdateInfo
SEED = 20160331;


for SCALE=10; %10:20
if SCALE > 17
  Nfile = 8*(2^min(SCALE-17,4));
end

% alg01_Gen00_Driver; 
tname = [myPrefix 'pg' num2str(SCALE,'%02d') '_' num2str(SEED)];
alg01_Gen01_File;
MyDBsetup;
TNadjUU = [tname '_TgraphAdjUU'];

TI = DBaddJavaOps('edu.mit.ll.graphulo.tricount.TriangleIngestor',INSTANCENAME,ZKHOSTS,'root','secret');
TI.ingestDirectory([pwd filesep tname], TNadjUU, [], true); %upper triangle


for NUMTAB=4; %8:8:40

SPLITS_RATE_LINEAR = 1;
SPLITS_RATE_EXP = 1;
SPLITS_RATE_EXP_INV = 1;




alg04_triCount00_Driver

deleteForce(DB(tnameTmp));
deleteForce(TadjUU);
pause(3)

end
end