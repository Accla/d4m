%DoRunMatlab = false;
%javaaddpath /home/dhutchis/gits/lara-graphulo/target/lara-graphulo-1.0-SNAPSHOT-all.jar 
DBsetup;
Tinfo = DB('DH_info','DH_infoT');
nl = char(10);

REDUCERS=[1,2];
for SCALE=12%10:18
DoRunGraphulo = true;
DoRunMR = true;%SCALE < 16; % Matlab runs out of memory at 16. 15 is tough.
arrr = [1,2];
%if SCALE==18
%    arrr = 2;
%end
for NUMTAB=arrr%[1,2]%,4,8]
fprintf('Starting TableMult SCALE=%d NUMTAB=%d\n',SCALE,NUMTAB);
myName = ['DH_' num2str(SCALE,'%02d') '_'];
tname = [myName 'TgraphAdj'];
Tadj = DB(tname); %,'DH_TgraphAdjT');
tname2 = ['DHB_' num2str(SCALE,'%02d') '_TgraphAdj'];
Tadj2 = DB(tname2); %,'DH2_TgraphAdjT');
rname = [tname '_t' 'X' tname2];
mrname = [rname '_mr'];

% test existence: strfind(ls(DB),[rname ' '])
Tres = DB(rname);
deleteForce(Tres); % Removes overhead of creating table to hold results from timing.
Tres = DB(rname);

% Split input tables if necessary.
%G = DBaddJavaOps('edu.mit.ll.graphulo.MatlabGraphulo',INSTANCENAME,'localhost:2181','root','secret');
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
if UseBestSplitsR && NUMTAB > 1
    row = [rname '_nt' num2str(NUMTAB) nl];
    splitPointsRBest = Val(Tinfo(row,'splitPointsRBest,'));
    splitPointsR = splitPointsRBest;
    putSplits(Tres,splitPointsR);
else
    putSplits(Tres,splitPoints); % arbitrary between 1 and 2
    splitPointsR = splitPoints;
end
G.Compact(getName(Tres));

pause(3)

if DoRunGraphulo
tic;
presumCacheSize = -1;
numpp = G.TableMult(tname,tname2,rname,'','','','',presumCacheSize,-1,false);
graphuloMult = toc; fprintf('Graphulo TableMult Time: %f\n',graphuloMult);
fprintf('Result Table %s #entries: %d\n',rname,nnz(Tres));

[splitPointsR,splitSizesR] = getSplits(Tres);
pause(3)
end



if DoRunMR

if 0
javaClass = 'edu.washington.cs.laragraphulo.mr.MatMulJob';
[~, jPath, jFunc] = fileparts(javaClass);
import([jPath '.*']);
MR = feval(jFunc(2:end));
%,'-i', 'accumulo-1.8', '-z', 'localhost:2181', '-t1', tname, '-t2', tname2, '-o', mrname, '-u', 'secret', '-p', 'root', '--reducers', '1');
javaClass = 'org.apache.hadoop.util.ToolRunner';
[~, jPath, jFunc] = fileparts(javaClass);
import([jPath '.*']);
TR = feval(jFunc(2:end));
javaClass = 'org.apache.hadoop.conf.Configuration';
[~, jPath, jFunc] = fileparts(javaClass);
import([jPath '.*']);
CONF = feval(jFunc(2:end));

ars0 = {'-i', 'accumulo-1.8', '-z', 'localhost:2181', '-t1', tname, '-t2', tname2, '-o', mrname, '-u', 'root', '-p', 'secret', '--reducers', '1', '--noDelete'};
ars = javaArray('java.lang.String',length(ars0));
for i = 1:length(ars0)
    ars(i) = java.lang.String(ars0{i});
end
end

mrTime = zeros(1, numel(REDUCERS));
for reducersi = 1:numel(REDUCERS)
reducers = REDUCERS(reducersi);
fprintf('reducers: %d\n', reducers);

TresMat = DB(mrname);
deleteForce(TresMat);
TresMat = DB(mrname);
putSplits(TresMat,splitPointsR);
G.Compact(getName(TresMat));
pause(3)

tic;
%TR.run(CONF, MR, ars);
system(['$ACCUMULO_HOME/bin/tool.sh /home/dhutchis/gits/lara-graphulo/target/lara-graphulo-1.0-SNAPSHOT-all.jar edu.washington.cs.laragraphulo.mr.MatMulJob -i accumulo-1.8 -z localhost:2181 -t1 ' tname ' -t2 ' tname2 ' -o ' mrname ' -u root -p secret --reducers ' num2str(reducers) ' --noDelete'], '-echo')
mrTime(reducersi) = toc; fprintf('MR Time: %f\n',mrTime(reducersi));
pause(2)
end

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
end

% Record number of partial products to determine rate
%numpp = G.countPartialProductsTableMult(tname,tname2,false);

if exist('Tinfo','var')
    row = [rname '_nt' num2str(NUMTAB) nl];
    Ainfo = Assoc('','','');
    if DoRunGraphulo
        Ainfo = Ainfo + Assoc(row,['graphuloMult' nl],[num2str(graphuloMult) nl]);
    end
    if DoRunMR
        for i = 1:numel(REDUCERS)
            reducers = REDUCERS(i);
            Ainfo = Ainfo + Assoc(row,['mrTime_' num2str(reducers) nl],[num2str(mrTime(i)) nl]);
        end
    end
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

pause(10)

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
