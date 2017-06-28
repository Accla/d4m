util_Require('DB, G, tname, TNadjUU, NUMTAB, infoFunc, SCALE, filterRowCol, durability')
% experiment data format
% ROW: DH_jaccard_graphulo__DH_pg10_20160331__nt1|20160403-225353
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
numEntries = nnz(TadjUU);
splitPoints = G.findEvenSplits(TNadjUU, NUMTAB-1, numEntries / NUMTAB);
putSplits(TadjUU, splitPoints);
G.Compact(TNadjUU); % force new splits
[splitPoints,splitSizes] = getSplits(TadjUU);
splitCompact = toc; fprintf('Split %d & compact time: %f\n',NUMTAB,splitCompact);

%G.SetConfig(TNadjUU,'table.durability',durability);
% alternative: set this as a parameter in the kTrussAdj_Smart call at the end

pause(3)

tic;
    % specialLongList = java.util.ArrayList();
    triangles = G.triCount(TNadjUU, filterRowCol, [], [], durability);
    % numpp = specialLongList.get(0);
    % clear specialLongList;
triCountTime = toc; fprintf('Graphulo TriCount Time: %f\n',triCountTime);
fprintf('Triangles: %f\n',triangles);


nl = char(10);
% DH_jaccard__DH_pg10_20160331__nt1|20160403-225353
row = ['DH_triCount__' tname '__nt' num2str(NUMTAB) '__graphulo|' timeStartStr nl];
Ainfo = Assoc('','','');
%num2str(graphuloJaccard,'%09.1f')
Ainfo = Ainfo + Assoc(row,['triCountGraphulo' nl],[num2str(triCountTime) nl]);
% Ainfo = Ainfo + Assoc(row,['numpp' nl],[num2str(numpp) nl]);
if (NUMTAB > 1)
    Ainfo = Ainfo + Assoc(row,['splitPoints' nl],[splitPoints nl]);
    Ainfo = Ainfo + Assoc(row,['splitSizes' nl],[splitSizes nl]);
%     Ainfo = Ainfo + Assoc(row,['splitPointsR' nl],[splitPointsR nl]);
%     Ainfo = Ainfo + Assoc(row,['splitSizesR' nl],[splitSizesR nl]);
end
Ainfo = Ainfo + Assoc(row,['splitCompact' nl],[num2str(splitCompact) nl]);
Ainfo = Ainfo + Assoc(row,['tname' nl],[tname nl]);
Ainfo = Ainfo + Assoc(row,['SCALE' nl],[num2str(SCALE) nl]);
Ainfo = Ainfo + Assoc(row,['NUMTAB' nl],[num2str(NUMTAB) nl]);
Ainfo = Ainfo + Assoc(row,['engine' nl],['graphulo' nl]);
% if ~doClient
%     Ainfo = Ainfo + Assoc([tname nl], ['kTrussAdjNumpp_' num2str(maxiter) nl], [num2str(numpp) nl]);
% end
if ~isempty(filterRowCol)
    Ainfo = Ainfo + Assoc(row,['SCALEsampled' nl],[num2str(log2(NumStr(filterRowCol))) nl]);
end
%Ainfo = Ainfo + Assoc(row,['numiter' nl],[num2str(numiter) nl]);
Ainfo
infoFunc(Ainfo);

