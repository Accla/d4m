DBsetup;
Tinfo = DB('DH_info','DH_infoT');
nl = char(10);

xSCALE = 10:16;
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
numpp = Val(str2num(Tinfo(row,'numpp,')));
gra = Val(str2num(Tinfo(row,'graphuloMult,')));
if isempty(gra)
    gra = 0;
    yRateGraphulo(iNUMTAB,iSCALE) = 0;
else
    yRateGraphulo(iNUMTAB,iSCALE) = numpp ./ gra;
end
yTimeGraphulo(iNUMTAB,iSCALE) = gra;
d4m = Val(sum(str2num(Tinfo(row,'d4mScanMult,d4mPutResult,')),2));
if isempty(d4m)
    d4m = 0;
    yRateD4M(iNUMTAB,iSCALE) = 0;
else
    yRateD4M(iNUMTAB,iSCALE) = numpp ./ d4m;
end
yTimeD4M(iNUMTAB,iSCALE) = d4m;
end
end

legs = cell(1,2*numel(aNUMTAB));
figure
hold on
for iNUMTAB=1:numel(aNUMTAB)
grarow = yTimeGraphulo(iNUMTAB,:);
plot(xSCALE(grarow~=0),grarow(grarow~=0),'.-')
msg = ['Graphulo ' num2str(iNUMTAB) ' Tablet'];
if aNUMTAB(iNUMTAB) > 1
    msg = [msg 's'];
end
legs{iNUMTAB} = msg;
end
for iNUMTAB=1:numel(aNUMTAB)
d4mrow = yTimeD4M(iNUMTAB,:);
%fprintf('d4mrow %d\n',d4mrow);
plot(xSCALE(d4mrow~=0),d4mrow(d4mrow~=0),'--.')
msg = ['D4M ' num2str(aNUMTAB(iNUMTAB)) ' Tablet'];
if aNUMTAB(iNUMTAB) > 1
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
gca.XTick=xSCALE;
hold on
for iNUMTAB=1:numel(aNUMTAB)
grarow = yRateGraphulo(iNUMTAB,:);
plot(xSCALE(grarow~=0),grarow(grarow~=0),'.-')
msg = ['Graphulo ' num2str(aNUMTAB(iNUMTAB)) ' Tablet'];
if aNUMTAB(iNUMTAB) > 1
    msg = [msg 's'];
end
legs{iNUMTAB} = msg;
end
for iNUMTAB=1:numel(aNUMTAB)
d4mrow = yRateD4M(iNUMTAB,:);
%disp(d4mrow)
plot(xSCALE(d4mrow~=0),d4mrow(d4mrow~=0),'--.')
msg = ['D4M ' num2str(aNUMTAB(iNUMTAB)) ' Tablet'];
if aNUMTAB(iNUMTAB) > 1
    msg = [msg 's'];
end
legs{numel(aNUMTAB)+iNUMTAB}   = msg;
end
%ax.XTick = 10:13;
legend(legs);
xlabel('SCALE');
ylabel('Rate (partial products/s)');
title('TableMult Rate Scaling');
savefig('TableMultRate');
print('TableMultRate','-depsc')
print('TableMultRate','-dpng')


% Compact all
if 0
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
