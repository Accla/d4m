%function alg02_Jaccard_D4M(DB, G, tname, TNadjUU, TNadjkTrussD4M, NUMTAB, infoFunc)
util_Require('DB, G, tname, TNadjUU, TNadjkTrussD4M, NUMTAB, infoFunc, SCALE, k, filterRowCol');
% experiment data format
% ROW: DH_jaccard_d4m__DH_pg10_20160331__nt1|20160403-225353
timeStartStr = datestr(now,'yyyymmdd-HHMMSS');

LSDB = ls(DB);
if StrSearch(LSDB,[TNadjUU ' ']) < 1
    error(['Please create ' TNadjUU]);
end
TadjUU = DB(TNadjUU); 
% Ensure result table is fresh
if StrSearch(LSDB,[TNadjkTrussD4M ' ']) >= 1
    TadjkTrussD4M = DB(TNadjkTrussD4M);
    if exist('DELETE_TABLE_TRIGGER','var') && DELETE_TABLE_TRIGGER
        deleteForce(TadjkTrussD4M);
    else
        delete(TadjkTrussD4M);
    end
end
% Pre-create result table
TadjkTrussD4M = DB(TNadjkTrussD4M);

tic;
numEntries = nnz(TadjUU);
splitPoints = G.findEvenSplits(TNadjUU, NUMTAB-1, numEntries / NUMTAB);
putSplits(TadjUU, splitPoints);
G.Compact(TNadjUU); % force new splits
[splitPoints,splitSizes] = getSplits(TadjUU);
splitCompact = toc; fprintf('Split %d & compact time: %f\n',NUMTAB,splitCompact);

% Todo: split result table

pause(2)

% Iterator that does filtering server-side
if ~isempty(filterRowCol)
    enumFilter = JavaInnerEnum(edu.mit.ll.graphulo.skvi.D4mRangeFilter(), 'KeyPart', 'COLQ');
    filterIter = edu.mit.ll.graphulo.skvi.D4mRangeFilter.iteratorSetting(15, enumFilter, filterRowCol);
    G.ApplyIteratorScan(TNadjUU, filterIter);
end

tic;
A = TadjUU(filterRowCol,:);
A = A(:,filterRowCol);
A = str2num(A);

d4mScan = toc; fprintf('D4M Scan               : %f\n',d4mScan);

if ~isempty(filterRowCol)
    G.RemoveIterator(TNadjUU, filterIter);
end

tic;
[A, numiter] = kTrussAdj(A,k);
d4mkTruss = toc; fprintf('D4M (%d)-TrussAdj         : %f\n',k,d4mkTruss);
nnzFinal = nnz(A);

tic;
[r,c,v] = find(A);
clear A;
v = sprintf(['%d' r(end)],v);
putTriple(TadjkTrussD4M, r,c,v);
d4mWrite = toc; fprintf('D4M Write: %f\n',d4mWrite);
clear r c v;

d4mkTrussTotal = d4mScan + d4mkTruss + d4mWrite;

numEntriesRightAfter = nnz(TadjkTrussD4M);
fprintf('numEntriesRightAfter   %d\n', numEntriesRightAfter);
G.Compact(TNadjkTrussD4M);
numEntriesAfterCompact = nnz(TadjkTrussD4M);
fprintf('numEntriesAfterCompact %d\n', numEntriesAfterCompact);

nl = char(10);
% DH_jaccard__DH_pg10_20160331__nt1__d4m|20160403-225353
row = ['DH_kTrussAdj__' tname '__k' num2str(k) '__nt' num2str(NUMTAB) '__d4m|' timeStartStr nl];
Ainfo = Assoc('','','');
Ainfo = Ainfo + Assoc(row,['d4mScan' nl],[num2str(d4mScan) nl]);
Ainfo = Ainfo + Assoc(row,['d4mkTruss' nl],[num2str(d4mkTruss) nl]);
Ainfo = Ainfo + Assoc(row,['d4mWrite'  nl],[num2str(d4mWrite) nl]);
Ainfo = Ainfo + Assoc(row,['kTrussAdjD4MTotal' nl],[num2str(d4mkTrussTotal) nl]);
%Ainfo = Ainfo + Assoc(row,['correct' nl],[num2str(correct) nl]);
Ainfo = Ainfo + Assoc(row,['nnzFinal' nl],[num2str(nnzFinal) nl]);
if (NUMTAB > 1)
    Ainfo = Ainfo + Assoc(row,['splitPoints' nl],[splitPoints nl]);
    Ainfo = Ainfo + Assoc(row,['splitSizes' nl],[splitSizes nl]);
%     Ainfo = Ainfo + Assoc(row,['splitPointsR' nl],[splitPointsR nl]);
%     Ainfo = Ainfo + Assoc(row,['splitSizesR' nl],[splitSizesR nl]);
end
Ainfo = Ainfo + Assoc(row,['numEntriesRightAfter' nl],[num2str(numEntriesRightAfter) nl]);
Ainfo = Ainfo + Assoc(row,['numEntriesAfterCompact' nl],[num2str(numEntriesAfterCompact) nl]);
Ainfo = Ainfo + Assoc(row,['splitCompact' nl],[num2str(splitCompact) nl]);
Ainfo = Ainfo + Assoc(row,['tname' nl],[tname nl]);
Ainfo = Ainfo + Assoc(row,['SCALE' nl],[num2str(SCALE) nl]);
Ainfo = Ainfo + Assoc(row,['NUMTAB' nl],[num2str(NUMTAB) nl]);
Ainfo = Ainfo + Assoc(row,['engine' nl],['d4m' nl]);
Ainfo = Ainfo + Assoc(row,['k' nl],[num2str(k) nl]);
Ainfo = Ainfo + Assoc(row,['numiter' nl],[num2str(numiter) nl]);
if ~isempty(filterRowCol)
    Ainfo = Ainfo + Assoc(row,['SCALEsampled' nl],[num2str(log2(NumStr(filterRowCol))) nl]);
end
Ainfo
infoFunc(Ainfo);

