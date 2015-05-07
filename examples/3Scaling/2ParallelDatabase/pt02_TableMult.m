
DBsetup;
Tinfo = DB('DH_info','DH_infoT');
nl = char(10);

for NUMTAB=[1,2,4,8]
if NUMTAB==1
    arr=10:1:20;
else
    arr=10:1:20;
end
for SCALE=arr%14:1:20
fprintf('Starting TableMult SCALE=%d\n',SCALE);
myName = ['DH_' num2str(SCALE,'%02d') '_'];
DBsetup;

tname = [myName 'TgraphAdj'];
Tadj = DB(tname); %,'DH_TgraphAdjT');
tname2 = ['DHB_' num2str(SCALE,'%02d') '_TgraphAdj'];
Tadj2 = DB(tname2); %,'DH2_TgraphAdjT');
rname = [tname '_t' 'X' tname2];
% test existence: strfind(ls(DB),[rname ' '])
Tres = DB(rname);
deleteForce(Tres); % Removes overhead of creating table to hold results from timing.
Tres = DB(rname);

% Split input tables if necessary.
G = DBaddJavaOps('edu.mit.ll.graphulo.MatlabGraphulo','instance','localhost:2181','root','secret');
tic;
numEntries = nnz(Tadj);
splitPoints = G.findEvenSplits(tname, NUMTAB-1, numEntries / NUMTAB);
putSplits(Tadj, splitPoints);
G.Compact(tname); % force new splits
[splitPoints,splitSizes] = getSplits(Tadj);

numEntries2 = nnz(Tadj2);
splitPoints2 = G.findEvenSplits(tname2, NUMTAB-1, numEntries2 / NUMTAB);
putSplits(Tadj2, splitPoints2);
G.Compact(tname2); % force new splits
[splitPoints2,splitSizes2] = getSplits(Tadj2);
splitCompact = toc; fprintf('Both Split %d & compact time: %f\n',NUMTAB,splitCompact);

% Pre-splitting
UseBestSplitsR = false;
if UseBestSplitsR
    splitPointsR = Val(Tinfo(row,'splitPointsRBest,'));
    putSplits(Tres,splitPointsR);
else
    putSplits(Tres,splitPoints); % arbitrary between 1 and 2
end

tic;
G.TableMult(tname,tname2,rname,'','','',-1,false);
graphuloMult = toc; fprintf('Graphulo TableMult Time: %f\n',graphuloMult);
fprintf('Result Table %s #entries: %d\n',rname,nnz(Tres));

[splitPointsR,splitSizesR] = getSplits(Tres);

rnameman = [rname '_mat'];
TresMat = DB(rnameman);
deleteForce(TresMat);
TresMat = DB(rnameman);

tic;
A = str2num(Tadj(:,:));
B = str2num(Tadj2(:,:));
AtB = A.'*B;
d4mScanMult = toc; fprintf('D4M Scan&Mult  : %f\n',d4mScanMult);

tic;
put(TresMat, num2str(AtB));
d4mPutResult = toc; fprintf('D4M WriteResult: %f\n',d4mPutResult);
clear AtB A B;
% Check correctness
% Disabled because out of memory error gathering all the result table in Matlab memory
if 0
AtBDB = str2num(Tres(:,:));
correct = isequal(AtB,AtBDB);
if ~correct
    fprintf('NOT EQUAL RESULTS LOCAL AND DB VERSION\n');
    %return
end
end

% Record number of partial products to determine rate
numpp = G.countPartialProductsTableMult(tname,tname2,false);

if exist('Tinfo','var')
    row = [rname '_nt' num2str(NUMTAB) nl];
    Ainfo = Assoc('','','');
    Ainfo = Ainfo + Assoc(row,['graphuloMult' nl],[num2str(graphuloMult) nl]);
    Ainfo = Ainfo + Assoc(row,['d4mScanMult' nl],[num2str(d4mScanMult) nl]);
    Ainfo = Ainfo + Assoc(row,['d4mPutResult' nl],[num2str(d4mPutResult) nl]);
    %Ainfo = Ainfo + Assoc(row,['correct' nl],[num2str(correct) nl]);
    Ainfo = Ainfo + Assoc(row,['numpp' nl],[num2str(numpp) nl]);
    if (NUMTAB > 1)
        Ainfo = Ainfo + Assoc(row,['splitPoints' nl],[splitPoints nl]);
        Ainfo = Ainfo + Assoc(row,['splitSizes' nl],[splitSizes nl]);
        Ainfo = Ainfo + Assoc(row,['splitPoints2' nl],[splitPoints2 nl]);
        Ainfo = Ainfo + Assoc(row,['splitSizes2' nl],[splitSizes2 nl]);
        Ainfo = Ainfo + Assoc(row,['splitPointsR' nl],[splitPointsR nl]);
        Ainfo = Ainfo + Assoc(row,['splitSizesR' nl],[splitSizesR nl]);
    end
    Ainfo = Ainfo + Assoc(row,['splitCompact' nl],[num2str(splitCompact) nl]);
    put(Tinfo,Ainfo);
end

end
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dylan Hutchison (dhutchis@mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2015> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
