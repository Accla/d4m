util_Require('DB, G, tname, TNadjUU, TNedge, NUMTAB, infoFunc, SCALE, filterRowCol, durability') % , SPLITS_RATE_EXP, SPLITS_RATE_LINEAR')
timeStartStr = datestr(now,'yyyymmdd-HHMMSS');
tnameTmp = [TNadjUU '_triCount_tmpA'];

LSDB = ls(DB);
if StrSearch(LSDB,[TNadjUU ' ']) < 1 || StrSearch(LSDB,[TNedge ' ']) < 1 
    error(['Please create ' TNadjUU ]);
end
TadjUU = DB(TNadjUU); 
Tedge = DB(TNedge); 
%TadjUUDeg = DB(TNadjUUDeg);
% Ensure result table is fresh
if StrSearch(LSDB,[tnameTmp ' ']) >= 1
    Ttmp = DB(tnameTmp);
    if exist('DELETE_TABLE_TRIGGER','var') && DELETE_TABLE_TRIGGER
        deleteForce(Ttmp);
    else delete(Ttmp); end
end
% No need to pre-create result table for Graphulo

tic;
numEntries = nnz(TadjUU);
    
    splitPoints = G.findEvenSplits(TNadjUU, NUMTAB-1, numEntries / NUMTAB, SPLITS_RATE_LINEAR_INV, SPLITS_RATE_EXP_INV); % split point modifiers
    putSplits(TadjUU, splitPoints);
    disp(num2str(nnz(TadjUU)))
    G.Compact(TNadjUU); % force new splits
    disp(num2str(nnz(TadjUU)))
    [splitPointsA,splitSizesA] = getSplits(TadjUU);

    disp(num2str(nnz(Tedge)))
    splitPoints = G.findEvenSplits(TNedge, NUMTAB-1, numEntries / NUMTAB, SPLITS_RATE_LINEAR_INV, SPLITS_RATE_EXP_INV);
    putSplits(Tedge, splitPoints);
    G.Compact(TNedge);
    disp(num2str(nnz(Tedge)))
    [splitPointsE,splitSizesE] = getSplits(Tedge);

    % temp table
    Ttmp = DB(tnameTmp);
    % splitPoints = G.findEvenSplits(TNedge, NUMTAB-1, numEntries / NUMTAB, SPLITS_RATE_LINEAR, SPLITS_RATE_EXP);
    putSplits(Ttmp, splitPoints);
    G.Compact(tnameTmp);

splitCompact = toc; fprintf('Split %d & compact time: %f\n',NUMTAB,splitCompact);

%G.SetConfig(TNadjUU,'table.durability',durability);
% alternative: set this as a parameter in the kTrussAdj_Smart call at the end

pause(3)



    specialLongList = javaObject('java.util.ArrayList');
tic;
    triangles = G.triCountAdjEdge(TNadjUU, TNedge, filterRowCol, [], durability, specialLongList);
triCountTime = toc; fprintf('Graphulo TriCount Time: %f\n',triCountTime);
fprintf('Triangles: %f\n',triangles);
    numpp = specialLongList.get(0);
    clear specialLongList;

[splitPointsT,splitSizesT] = getSplits(Ttmp);
deleteForce(DB(tnameTmp));



nl = char(10);
row = ['DH_triCountAdjEdge__' tname '__nt' num2str(NUMTAB) '__graphulo|' timeStartStr nl];
Ainfo = Assoc('','','');
%num2str(graphuloJaccard,'%09.1f')
Ainfo = Ainfo + Assoc(row,['triCountGraphulo' nl],[num2str(triCountTime) nl]);
Ainfo = Ainfo + Assoc(row,['numpp' nl],[num2str(numpp) nl]);
if (NUMTAB > 1)
    % Ainfo = Ainfo + Assoc(row,['splitPointsA' nl],[splitPointsA nl]);
    Ainfo = Ainfo + Assoc(row,['splitSizesA' nl],[splitSizesA nl]);
    % Ainfo = Ainfo + Assoc(row,['splitPointsE' nl],[splitPointsE nl]);
    Ainfo = Ainfo + Assoc(row,['splitSizesE' nl],[splitSizesE nl]);
    % Ainfo = Ainfo + Assoc(row,['splitPointsT' nl],[splitPointsT nl]);
    Ainfo = Ainfo + Assoc(row,['splitSizesT' nl],[splitSizesT nl]);
%     Ainfo = Ainfo + Assoc(row,['splitPointsR' nl],[splitPointsR nl]);
%     Ainfo = Ainfo + Assoc(row,['splitSizesR' nl],[splitSizesR nl]);
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

