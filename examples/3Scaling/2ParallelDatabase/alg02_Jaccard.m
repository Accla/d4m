

MyDBsetup;
tname = 'DH_pg10_20160331';
TNadjUU = 'DH_pg10_20160331_TgraphAdjUU';
TNadjUUDeg = 'DH_pg10_20160331_TgraphAdjUUDeg';
TNadjJaccard = 'DH_pg10_20160331_TgraphAdjJaccard';
NUMTAB = 1;
infoFunc = @util_UpdateInfo;

alg02_Jaccard_Graphulo(DB, G, tname, TNadjUU, TNadjUUDeg, TNadjJaccard, NUMTAB, infoFunc);

TadjJaccard = DB(TNadjJaccard);
