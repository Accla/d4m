%function alg02_Jaccard_D4M(DB, G, tname, TNadjUU, TNadjJaccardD4M, NUMTAB, infoFunc)
util_Require('DB, G, tname, TNadjUU, TNadjJaccardD4M, NUMTAB, infoFunc, SCALE');
% experiment data format
% ROW: DH_jaccard_d4m__DH_pg10_20160331__nt1|20160403-225353
timeStartStr = datestr(now,'yyyymmdd-HHMMSS');

LSDB = ls(DB);
if StrSearch(LSDB,[TNadjUU ' ']) < 1
    error(['Please create ' TNadjUU]);
end
TadjUU = DB(TNadjUU); 
% Ensure result table is fresh
if StrSearch(LSDB,[TNadjJaccardD4M ' ']) >= 1
    TadjJaccardD4M = DB(TNadjJaccardD4M);
    if exist('DELETE_TABLE_TRIGGER','var') && DELETE_TABLE_TRIGGER
        deleteForce(TadjJaccardD4M)
    else
        delete(TadjJaccardD4M);
    end
end
% Pre-create result table
TadjJaccardD4M = DB(TNadjJaccardD4M);

tic;
numEntries = nnz(TadjUU);
splitPoints = G.findEvenSplits(TNadjUU, NUMTAB-1, numEntries / NUMTAB);
putSplits(TadjUU, splitPoints);
G.Compact(TNadjUU); % force new splits
[splitPoints,splitSizes] = getSplits(TadjUU);
splitCompact = toc; fprintf('Split %d & compact time: %f\n',NUMTAB,splitCompact);

% Todo: split result table

pause(2)

tic;
A = str2num(TadjUU(:,:));
d4mScan = toc; fprintf('D4M Scan               : %f\n',d4mScan);

tic;
J = Jaccard(A);
d4mJaccard = toc; fprintf('D4M Jaccard               : %f\n',d4mJaccard);
clear A;
Jnnz = nnz(J);

tic;
[r,c,v] = find(J);
clear J;
v = sprintf(['%d' r(end)],v);
putTriple(TadjJaccardD4M, r,c,v);
d4mWrite = toc; fprintf('D4M Write: %f\n',d4mWrite);
clear r c v;

d4mJaccardTotal = d4mScan + d4mJaccard + d4mWrite;

numEntriesRightAfter = nnz(TadjJaccardD4M);
fprintf('numEntriesRightAfter   %d\n', numEntriesRightAfter);
G.Compact(TNadjJaccardD4M);
numEntriesAfterCompact = nnz(TadjJaccardD4M);
fprintf('numEntriesAfterCompact %d\n', numEntriesAfterCompact);

nl = char(10);
% DH_jaccard__DH_pg10_20160331__nt1__d4m|20160403-225353
row = ['DH_jaccard_d4m__' tname '__nt' num2str(NUMTAB) '__d4m|' timeStartStr nl];
Ainfo = Assoc('','','');
Ainfo = Ainfo + Assoc(row,['d4mScan' nl],[num2str(d4mScan) nl]);
Ainfo = Ainfo + Assoc(row,['d4mJaccard' nl],[num2str(d4mJaccard) nl]);
Ainfo = Ainfo + Assoc(row,['d4mWrite'  nl],[num2str(d4mWrite) nl]);
Ainfo = Ainfo + Assoc(row,['d4mJaccardTotal' nl],[num2str(d4mJaccardTotal) nl]);
%Ainfo = Ainfo + Assoc(row,['correct' nl],[num2str(correct) nl]);
Ainfo = Ainfo + Assoc(row,['Jnnz|' num2str(Jnnz,'%09d') nl],[num2str(Jnnz) nl]);
if (NUMTAB > 1)
    Ainfo = Ainfo + Assoc(row,['splitPoints' nl],[splitPoints nl]);
    Ainfo = Ainfo + Assoc(row,['splitSizes' nl],[splitSizes nl]);
%     Ainfo = Ainfo + Assoc(row,['splitPointsR' nl],[splitPointsR nl]);
%     Ainfo = Ainfo + Assoc(row,['splitSizesR' nl],[splitSizesR nl]);
end
% There was a minor compaction during Jaccard if numEntriesRightAfter
% differs from numpp.
Ainfo = Ainfo + Assoc(row,['numEntriesRightAfter' nl],[num2str(numEntriesRightAfter) nl]);
Ainfo = Ainfo + Assoc(row,['numEntriesAfterCompact' nl],[num2str(numEntriesAfterCompact) nl]);
Ainfo = Ainfo + Assoc(row,['splitCompact' nl],[num2str(splitCompact) nl]);
Ainfo = Ainfo + Assoc(row,['tname' nl],[tname nl]);
Ainfo = Ainfo + Assoc(row,['SCALE' nl],[num2str(SCALE) nl]);
Ainfo = Ainfo + Assoc(row,['NUMTAB' nl],[num2str(NUMTAB) nl]);
Ainfo = Ainfo + Assoc(row,['engine' nl],['d4m' nl]);
Ainfo
infoFunc(Ainfo);

