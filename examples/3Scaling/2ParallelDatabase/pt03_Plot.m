DBsetup;
Tinfo = DB('DH_info','DH_infoT');
nl = char(10);

xSCALE = 10:13;
aNUMTAB = [1,2].';%,4,8].';
yTimeGraphulo = zeros(numel(aNUMTAB),numel(xSCALE));
yTimeD4M = zeros(numel(aNUMTAB),numel(xSCALE));
yRateGraphulo = zeros(numel(aNUMTAB),numel(xSCALE));
yRateD4M = zeros(numel(aNUMTAB),numel(xSCALE));


for iNUMTAB=1:numel(aNUMTAB)
NUMTAB=aNUMTAB(iNUMTAB);
for iSCALE=1:numel(xSCALE)
SCALE=xSCALE(iSCALE);
tname = ['DH_' num2str(SCALE,'%02d') '_TgraphAdj'];
tname2 = ['DHB_' num2str(SCALE,'%02d') '_TgraphAdj'];
rname = [tname '_t' 'X' tname2];
row = [rname '_nt' num2str(NUMTAB) nl];
yTimeGraphulo(iNUMTAB,iSCALE) = Val(str2num(Tinfo(row,'graphuloMult,')));
yTimeD4M(iNUMTAB,iSCALE) = Val(sum(str2num(Tinfo(row,'d4mScanMult,d4mPutResult,')),2));
numpp = Val(str2num(Tinfo(row,'numpp,')));
yRateGraphulo(iNUMTAB,iSCALE) = numpp ./ yTimeGraphulo(iNUMTAB,iSCALE);
yRateD4M(iNUMTAB,iSCALE) = numpp ./ yTimeD4M(iNUMTAB,iSCALE);
end
end

legs = cell(1,2*numel(aNUMTAB));
figure
hold on
for iNUMTAB=1:numel(aNUMTAB)
plot(xSCALE,yTimeGraphulo(iNUMTAB,:),'-')
msg = ['Graphulo ' num2str(iNUMTAB) ' Tablet'];
if iNUMTAB > 1
    msg = [msg 's'];
end
legs{iNUMTAB} = msg;
end
for iNUMTAB=1:numel(aNUMTAB)
plot(xSCALE,yTimeD4M(iNUMTAB,:),'--')
msg = ['D4M ' num2str(iNUMTAB) ' Tablet'];
if iNUMTAB > 1
    msg = [msg 's'];
end
legs{numel(aNUMTAB)+iNUMTAB} = msg;
end
%ax = gca;
%ax.XTick = 10:13;
legend(legs);
xlabel('SCALE');
ylabel('Time (s)');
title('TableMult Runtime Scaling');
savefig('TableMultTime');
print('TableMultTime','-depsc')
print('TableMultTime','-dpng')

legs = cell(1,2*numel(aNUMTAB));
figure
hold on
for iNUMTAB=1:numel(aNUMTAB)
plot(xSCALE,yRateGraphulo(iNUMTAB,:),'-')
msg = ['Graphulo ' num2str(iNUMTAB) ' Tablet'];
if iNUMTAB > 1
    msg = [msg 's'];
end
legs{iNUMTAB} = msg;
end
for iNUMTAB=1:numel(aNUMTAB)
plot(xSCALE,yRateD4M(iNUMTAB,:),'--')
msg = ['D4M ' num2str(iNUMTAB) ' Tablet'];
if iNUMTAB > 1
    msg = [msg 's'];
end
legs{numel(aNUMTAB)+iNUMTAB}   = msg;
end
%ax = gca;
%ax.XTick = 10:13;
legend(legs);
xlabel('SCALE');
ylabel('Rate (partial products/s)');
title('TableMult Rate Scaling');
savefig('TableMultRate');
print('TableMultRate','-depsc')
print('TableMultRate','-dpng')


% Compact all
if 1
for iSCALE=1:numel(xSCALE)
for iNUMTAB=2:numel(aNUMTAB)
NUMTAB=aNUMTAB(iNUMTAB);
SCALE=xSCALE(iSCALE);
tname = ['DH_' num2str(SCALE,'%02d') '_TgraphAdj'];
tname2 = ['DHB_' num2str(SCALE,'%02d') '_TgraphAdj'];
rname = [tname '_t' 'X' tname2];
row = [rname '_nt' num2str(NUMTAB) nl];
G = DBaddJavaOps('edu.mit.ll.graphulo.MatlabGraphulo','instance','localhost:2181','root','secret');
G.Compact(rname);

% Get best split point for result table
Tres = DB(rname);
numEntries = nnz(Tres);
splitPoints = G.findEvenSplits(rname, NUMTAB-1, numEntries / NUMTAB);
putSplits(Tres, splitPoints);
G.Compact(rname);
[splitPoints,splitSizes] = getSplits(Tres);
Ainfo = Assoc('','','');
Ainfo = Ainfo + Assoc(row,'splitPointsRBest,',[splitPoints nl]);
Ainfo = Ainfo + Assoc(row,'splitSizesRBest,',[splitSizes nl]);
put(Tinfo,Ainfo);
fprintf('NUMTAB %d SCALE %d \n R     %s RBest %s R     %s RBest %s\n',NUMTAB,SCALE,Val(Tinfo(row,'splitPointsR,')),...
        Val(Tinfo(row,'splitPointsRBest,')),Val(Tinfo(row,'splitSizesR,')),Val(Tinfo(row,'splitSizesRBest,')));
end
end
end
    
% Other analyses
for iSCALE=1:numel(xSCALE)
for iNUMTAB=2:numel(aNUMTAB) % skip first
NUMTAB=aNUMTAB(iNUMTAB);
SCALE=xSCALE(iSCALE);
tname = ['DH_' num2str(SCALE,'%02d') '_TgraphAdj'];
tname2 = ['DHB_' num2str(SCALE,'%02d') '_TgraphAdj'];
rname = [tname '_t' 'X' tname2];
row = [rname '_nt' num2str(NUMTAB) nl];
numpp = Val(str2num(Tinfo(row,'numpp,')));
md = maxdiff(Val(Tinfo(row,'splitSizesR,')));
% Not a superb source of information because flush/compaction may have occurred.
Tres = DB(rname);
fprintf('NUMTAB %d SCALE %d MaxDiff %9d NumPP %9d nnzCompact %9d\n',NUMTAB,SCALE,md,numpp,nnz(Tres));
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
