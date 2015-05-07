
DBsetup;
Tinfo = DB('DH_info','DH_infoT');
nl = char(10);

NUMTAB=1;
for NUMTAB=[1,2,4,8]
for SCALE=10:1:20
fprintf('Starting TableMult SCALE=%d\n',SCALE);
myName = ['DH_' num2str(SCALE,'%02d') '_'];
DBsetup;

tname = [myName 'TgraphAdj'];
Tadj = DB(tname); %,'DH_TgraphAdjT');
%Tadj2 = DB(tname); %,'DH2_TgraphAdjT');
rname = [tname '_t' 'X' tname];
% test existence: strfind(ls(DB),[rname ' '])
Tres = DB(rname);
deleteForce(Tres); % Removes overhead of creating table to hold results from timing.
Tres = DB(rname);

% Split input tables if necessary.
G = DBaddJavaOps('edu.mit.ll.graphulo.MatlabGraphulo','instance','localhost:2181','root','secret');
tic;
numEntries = nnz(Tadj);
if NUMTAB > 1
    splitPoints = G.findEvenSplits(tname, NUMTAB-1, numEntries / NUMTAB);
else
    splitPoints = '';
end
putSplits(Tadj, splitPoints);
G.Compact(tname); % just in case
[splitPoints,splitSizes] = getSplits(Tadj);
splitCompact = toc; fprintf('Split %d & compact time: %f\n',NUMTAB,splitCompact);
% Pre-splitting
putSplits(Tres,splitPoints);

tic;
G.TableMult(tname,tname,rname,'','','',-1,false);
graphuloMult = toc; fprintf('Graphulo TableMult Time: %f\n',graphuloMult);
fprintf('Result Table %s #entries: %d\n',rname,nnz(Tres));

rnameman = [rname '_mat'];
TresMat = DB(rnameman);
deleteForce(TresMat);
TresMat = DB(rnameman);

tic;
A = str2num(Tadj(:,:));
A2 = A;
AtA = A.'*A2;
d4mScanMult = toc; fprintf('D4M Scan&Mult  : %f\n',d4mScanMult);

tic;
put(TresMat, num2str(AtA));
d4mPutResult = toc; fprintf('D4M WriteResult: %f\n',d4mPutResult);

% Check correctness
AtADB = str2num(Tres(:,:));
correct = isequal(AtA,AtADB);
if ~correct
    fprintf('NOT EQUAL RESULTS LOCAL AND DB VERSION\n');
    %return
end

% Record number of partial products to determine rate
numpp = G.countPartialProductsTableMult(tname,tname,false);

if exist('Tinfo','var')
    row = [tname '_nt' num2str(NUMTAB) nl];
    Ainfo = Assoc('','','');
    Ainfo = Ainfo + Assoc(row,['graphuloMult' nl],[num2str(graphuloMult) nl]);
    Ainfo = Ainfo + Assoc(row,['d4mScanMult' nl],[num2str(d4mScanMult) nl]);
    Ainfo = Ainfo + Assoc(row,['d4mPutResult' nl],[num2str(d4mPutResult) nl]);
    Ainfo = Ainfo + Assoc(row,['correct' nl],[num2str(correct) nl]);
    Ainfo = Ainfo + Assoc(row,['numpp' nl],[num2str(numpp) nl]);
    if (NUMTAB > 1)
        Ainfo = Ainfo + Assoc(row,['splitPoints' nl],[splitPoints]);
        Ainfo = Ainfo + Assoc(row,['splitSizes' nl],[splitSizes]);
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
