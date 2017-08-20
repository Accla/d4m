util_Require('DB, G, tname, TNadjUU, NUMTAB, infoFunc, SCALE, filterRowCol, durability, SPLITS_RATE_EXP, SPLITS_RATE_LINEAR')
% experiment data format
% ROW: DH_triCount_graphulo__DH_pg10_20160331__nt1|20160403-225353
timeStartStr = datestr(now,'yyyymmdd-HHMMSS');
tnameTmp = [TNadjUU '_triCount_tmpA'];

LSDB = ls(DB);
if StrSearch(LSDB,[TNadjUU ' ']) < 1 
    error(['Please create ' TNadjUU ]);
end
TadjUU = DB(TNadjUU); 
%TadjUUDeg = DB(TNadjUUDeg);
% Ensure result table is fresh
if StrSearch(LSDB,[tnameTmp ' ']) >= 1
    Ttmp = DB(tnameTmp);
    if exist('DELETE_TABLE_TRIGGER','var') && DELETE_TABLE_TRIGGER
        deleteForce(Ttmp);
    else
        delete(Ttmp);
    end
end
% No need to pre-create result table for Graphulo

tic;
    G.CloneTable(TNadjUU, tnameTmp, true);

numEntries = nnz(TadjUU);
if ~exist('DODEG','var') || ~DODEG
    if 0 %javaMethod('setIntEncodeValueAndDropEmpty', 'edu.mit.ll.d4m.db.cloud.D4mDbInsert')
        G.setUniformSplits(TNadjUU, NUMTAB-1)
    else
        splitPoints = G.findEvenSplits(TNadjUU, NUMTAB-1, numEntries / NUMTAB, SPLITS_RATE_LINEAR, SPLITS_RATE_EXP); % split point modifiers
        putSplits(TadjUU, splitPoints);
    end
    G.Compact(TNadjUU); % force new splits
end
[splitPoints,splitSizes] = getSplits(TadjUU);
fprintf('A splitsSizes %s\n', splitSizes);

% % temp table - cloned from original

Ttmp = DB(tnameTmp);
% splitPoints = G.findEvenSplits(TNadjUU, NUMTAB-1, numEntries / NUMTAB, 1.0, SPLITS_RATE_EXP_INV);
putSplits(Ttmp, splitPoints);
G.Compact(tnameTmp);
[splitPointsT,splitSizesT] = getSplits(Ttmp);
fprintf('T splitsSizes %s\n', splitSizesT);

splitCompact = toc; fprintf('Split %d & compact time: %f\n',NUMTAB,splitCompact);
%G.SetConfig(TNadjUU,'table.durability',durability);
% alternative: set this as a parameter in the kTrussAdj_Smart call at the end

pause(3)



    specialLongList = javaObject('java.util.ArrayList');
tic;
    if javaMethod('setIntEncodeKeyAndPrependLastByteRev', 'edu.mit.ll.d4m.db.cloud.D4mDbInsert')
        triangles = G.triCountMagic(TNadjUU, filterRowCol, [], durability, specialLongList);
    else
        triangles = G.triCount(TNadjUU, filterRowCol, [], durability, specialLongList);
    end
triCountTime = toc; fprintf('Graphulo TriCount Time: %f\n',triCountTime);
fprintf('Triangles: %f\n',triangles);
    numpp = specialLongList.get(0);
    clear specialLongList;



nl = char(10);
row = ['DH_triCount__' tname '__nt' num2str(NUMTAB) '__graphulo|' timeStartStr nl];
Ainfo = Assoc('','','');
%num2str(graphuloJaccard,'%09.1f')
Ainfo = Ainfo + Assoc(row,['triCountGraphulo' nl],[num2str(triCountTime) nl]);
Ainfo = Ainfo + Assoc(row,['numpp' nl],[num2str(numpp) nl]);
if (NUMTAB > 1)
    % Ainfo = Ainfo + Assoc(row,['splitPoints' nl],[splitPoints nl]);
    Ainfo = Ainfo + Assoc(row,['splitSizes' nl],[splitSizes nl]);
%     Ainfo = Ainfo + Assoc(row,['splitPointsR' nl],[splitPointsR nl]);
    % Ainfo = Ainfo + Assoc(row,['splitSizesT' nl],[splitSizesT nl]);
end
Ainfo = Ainfo + Assoc(row,['splitCompact' nl],[num2str(splitCompact) nl]);
Ainfo = Ainfo + Assoc(row,['tname' nl],[tname nl]);
Ainfo = Ainfo + Assoc(row,['SCALE' nl],[num2str(SCALE) nl]);
Ainfo = Ainfo + Assoc(row,['NUMTAB' nl],[num2str(NUMTAB) nl]);
Ainfo = Ainfo + Assoc(row,['engine' nl],['graphulo' nl]);
Ainfo = Ainfo + Assoc(row,['triangles' nl],[num2str(triangles) nl]);
Ainfo = Ainfo + Assoc(row,['adjnnz' nl],[num2str(numEntries) nl]);
Ainfo = Ainfo + Assoc(row,['splitsLinear' nl],[num2str(SPLITS_RATE_LINEAR) nl]);
Ainfo = Ainfo + Assoc(row,['splitsExp' nl],[num2str(SPLITS_RATE_EXP) nl]);
% if ~doClient
%     Ainfo = Ainfo + Assoc([tname nl], ['kTrussAdjNumpp_' num2str(maxiter) nl], [num2str(numpp) nl]);
% end
if ~isempty(filterRowCol)
    Ainfo = Ainfo + Assoc(row,['SCALEsampled' nl],[num2str(log2(NumStr(filterRowCol))) nl]);
end
%Ainfo = Ainfo + Assoc(row,['numiter' nl],[num2str(numiter) nl]);
Ainfo
infoFunc(Ainfo);

