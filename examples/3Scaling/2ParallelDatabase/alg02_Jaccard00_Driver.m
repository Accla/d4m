
MyDBsetup;
myPrefix = 'DH_';
SCALE = 10;
infoFunc = @util_UpdateInfo;

for SCALE = 10:10
for SEED = 20160331
for NUMTAB = 1:1

tname = [myPrefix 'pg' num2str(SCALE,'%02d') '_' num2str(SEED)];
TNadjUU = [tname '_TgraphAdjUU'];
TNadjUUDeg = [tname '_TgraphAdjUUDeg'];
TNadjJaccard = [tname '_TgraphAdjJaccard'];
TNadjJaccardD4M = [tname '_TgraphAdjJaccardD4M'];

alg02_Jaccard01_Graphulo;

alg02_Jaccard02_D4M;

TadjJaccard = DB(TNadjJaccard); TadjJaccardD4M = DB(TNadjJaccardD4M);
alg02_Jaccard03_Verify;

end
end
end