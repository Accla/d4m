%function alg02_Jaccard_Graphulo(DB, G, tname, TNadjUU, TNadjUUDeg, TNadjkTruss, NUMTAB, infoFunc)
util_Require('DB, G, tname, TNadjUU, TNadjkTruss, NUMTAB, infoFunc, SCALE, k, fused, filterRowCol, doClient, doClientSparse, maxiter, durability')
% experiment data format
% ROW: DH_jaccard_graphulo__DH_pg10_20160331__nt1|20160403-225353
timeStartStr = datestr(now,'yyyymmdd-HHMMSS');

LSDB = ls(DB);
if StrSearch(LSDB,[TNadjUU ' ']) < 1 
    error(['Please create ' TNadjUU ]);
end
TadjUU = DB(TNadjUU); 
%TadjUUDeg = DB(TNadjUUDeg);
% Ensure result table is fresh
if StrSearch(LSDB,[TNadjkTruss ' ']) >= 1
    TadjkTruss = DB(TNadjkTruss);
    if exist('DELETE_TABLE_TRIGGER','var') && DELETE_TABLE_TRIGGER
        deleteForce(TadjkTruss);
    else
        delete(TadjkTruss);
    end
end
% No need to pre-create result table for Graphulo
if doClient
    TadjkTruss = DB(TNadjkTruss);
end

tic;
numEntries = nnz(TadjUU);
splitPoints = G.findEvenSplits(TNadjUU, NUMTAB-1, numEntries / NUMTAB);
putSplits(TadjUU, splitPoints);
G.Compact(TNadjUU); % force new splits
[splitPoints,splitSizes] = getSplits(TadjUU);
if doClient
    % Split result table with same splits
    putSplits(TadjkTruss, splitPoints);
end
splitCompact = toc; fprintf('Split %d & compact time: %f\n',NUMTAB,splitCompact);

G.SetConfig(TNadjUU,'table.durability',durability);

pause(3)

tic;
if doClient
    numpp = G.kTrussAdj_Client(TNadjUU, TNadjkTruss, k, filterRowCol, [], [], doClientSparse, maxiter);
else
    if fused
        specialLongList = java.util.ArrayList();
        %numpp = G.kTrussAdj_Fused(TNadjUU, TNadjkTruss, k, filterRowCol, true, [], [], 1e32, maxiter, specialLongList);
        numpp = G.kTrussAdj_Smart(TNadjUU, TNadjkTruss, k, filterRowCol, true, [], [], maxiter, specialLongList);
        numpp = specialLongList.get(0);
        clear specialLongList;
    else
        numpp = G.kTrussAdj(TNadjUU, TNadjkTruss, k, filterRowCol, true, [], [], maxiter);
    end
end
graphulokTruss = toc; fprintf('Graphulo (%d)-Truss Time: %f\n',k,graphulokTruss);
TadjkTruss = DB(TNadjkTruss);

numEntriesRightAfter = nnz(TadjkTruss);
fprintf('numEntriesRightAfter   %d\n', numEntriesRightAfter);
%G.Compact(TNadjkTruss);
%numEntriesAfterCompact = nnz(TadjkTruss);
%fprintf('numEntriesAfterCompact %d\n', numEntriesAfterCompact);

nl = char(10);
% DH_jaccard__DH_pg10_20160331__nt1|20160403-225353
row = ['DH_kTrussAdj__' tname '__k' num2str(k) '__nt' num2str(NUMTAB) '__graphulo|' timeStartStr nl];
Ainfo = Assoc('','','');
%num2str(graphuloJaccard,'%09.1f')
Ainfo = Ainfo + Assoc(row,['kTrussAdjGraphulo' nl],[num2str(graphulokTruss) nl]);
Ainfo = Ainfo + Assoc(row,['numpp' nl],[num2str(numpp) nl]);
if (NUMTAB > 1)
    Ainfo = Ainfo + Assoc(row,['splitPoints' nl],[splitPoints nl]);
    Ainfo = Ainfo + Assoc(row,['splitSizes' nl],[splitSizes nl]);
%     Ainfo = Ainfo + Assoc(row,['splitPointsR' nl],[splitPointsR nl]);
%     Ainfo = Ainfo + Assoc(row,['splitSizesR' nl],[splitSizesR nl]);
end
% There was a minor compaction during Jaccard if numEntriesRightAfter differs from numpp.
%num2str(numEntriesRightAfter,'%09d')
Ainfo = Ainfo + Assoc(row,['numEntriesRightAfter' nl],[num2str(numEntriesRightAfter) nl]);
%Ainfo = Ainfo + Assoc(row,['numEntriesAfterCompact' nl],[num2str(numEntriesAfterCompact) nl]);
Ainfo = Ainfo + Assoc(row,['splitCompact' nl],[num2str(splitCompact) nl]);
Ainfo = Ainfo + Assoc(row,['tname' nl],[tname nl]);
Ainfo = Ainfo + Assoc(row,['SCALE' nl],[num2str(SCALE) nl]);
Ainfo = Ainfo + Assoc(row,['NUMTAB' nl],[num2str(NUMTAB) nl]);
Ainfo = Ainfo + Assoc(row,['engine' nl],['graphulo' nl]);
if ~doClient
    Ainfo = Ainfo + Assoc([tname nl], ['kTrussAdjNumpp_' num2str(maxiter) nl], [num2str(numpp) nl]);
end
Ainfo = Ainfo + Assoc(row,['k' nl],[num2str(k) nl]);
Ainfo = Ainfo + Assoc(row,['fused' nl],[num2str(fused) nl]);
Ainfo = Ainfo + Assoc(row,['doClient' nl],[num2str(doClient) nl]);
Ainfo = Ainfo + Assoc(row,['doClientSparse' nl],[num2str(doClientSparse) nl]);
Ainfo = Ainfo + Assoc(row,['maxiter' nl],[num2str(maxiter) nl]);
if ~isempty(filterRowCol)
    Ainfo = Ainfo + Assoc(row,['SCALEsampled' nl],[num2str(log2(NumStr(filterRowCol))) nl]);
end
%Ainfo = Ainfo + Assoc(row,['numiter' nl],[num2str(numiter) nl]);
Ainfo
infoFunc(Ainfo);

