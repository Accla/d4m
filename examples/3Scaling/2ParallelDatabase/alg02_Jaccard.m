

MyDBsetup;
tname = 'DH_pg10_20160331';
TNadjUU = 'DH_pg10_20160331_TgraphAdjUU';
TNadjUUDeg = 'DH_pg10_20160331_TgraphAdjUUDeg';
TNadjJaccard = 'DH_pg10_20160331_TgraphAdjJaccard';
TNadjJaccardD4M = 'DH_pg10_20160331_TgraphAdjJaccardD4M';
NUMTAB = 1;
infoFunc = @util_UpdateInfo;

alg02_Jaccard_Graphulo(DB, G, tname, TNadjUU, TNadjUUDeg, TNadjJaccard, NUMTAB, infoFunc);

alg02_Jaccard_D4M(DB, G, tname, TNadjUU, TNadjJaccardD4M, NUMTAB, infoFunc)


TadjJaccard = DB(TNadjJaccard);
TadjJaccardD4M = DB(TNadjJaccardD4M);

alg02_Jaccard_Verify(DB, G, TNadjJaccard, TNadjJaccardD4M)
