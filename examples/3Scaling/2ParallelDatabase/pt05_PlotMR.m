DBsetup;
Tinfo = DB('DH_info','DH_infoT');
nl = char(10);

xSCALE = 10:15;
aNUMTAB = [1,2].';%,4,8].';
yTimeGraphulo = zeros(numel(aNUMTAB),numel(xSCALE));
yTimeD4M = zeros(numel(aNUMTAB),numel(xSCALE));
yTimeD4M2 = zeros(numel(aNUMTAB),numel(xSCALE));
yRateGraphulo = zeros(numel(aNUMTAB),numel(xSCALE));
yRateD4M = zeros(numel(aNUMTAB),numel(xSCALE));
yRateD4M2 = zeros(numel(aNUMTAB),numel(xSCALE));
yPP = zeros(1,numel(xSCALE));

for iNUMTAB=1:numel(aNUMTAB)
NUMTAB=aNUMTAB(iNUMTAB);
for iSCALE=1:numel(xSCALE)
SCALE=xSCALE(iSCALE);
tname = ['DH_' num2str(SCALE,'%02d') '_TgraphAdj'];
tname2 = ['DHB_' num2str(SCALE,'%02d') '_TgraphAdj'];
rname = [tname '_t' 'X' tname2]; 
mrname = [rname '_mr'];
row = [rname '_nt' num2str(NUMTAB) nl];
numpp = Val(str2num(Tinfo(row,'numpp,')));
yPP(iSCALE) = numpp;
gra = Val(str2num(Tinfo(row,'graphuloMult,')));
if isempty(gra)
    gra = 0;
    yRateGraphulo(iNUMTAB,iSCALE) = 0;
else
    yRateGraphulo(iNUMTAB,iSCALE) = numpp ./ gra;
end
yTimeGraphulo(iNUMTAB,iSCALE) = gra;
d4m = Val(sum(str2num(Tinfo(row,'mrTime_1,')),2));
if isempty(d4m)
    d4m = 0;
    yRateD4M(iNUMTAB,iSCALE) = 0;
else
    yRateD4M(iNUMTAB,iSCALE) = numpp ./ d4m;
end
yTimeD4M(iNUMTAB,iSCALE) = d4m;

d4m = Val(sum(str2num(Tinfo(row,'mrTime_2,')),2));
if isempty(d4m)
    d4m = 0;
    yRateD4M2(iNUMTAB,iSCALE) = 0;
else
    yRateD4M2(iNUMTAB,iSCALE) = numpp ./ d4m;
end
yTimeD4M2(iNUMTAB,iSCALE) = d4m;
end
end

legs = cell(1,3*numel(aNUMTAB)); % from 2
figure
hold on
for iNUMTAB=1:numel(aNUMTAB)
grarow = yTimeGraphulo(iNUMTAB,:);
grarow = log2(grarow);
plot(xSCALE(grarow~=0),grarow(grarow~=0),'-x')
msg = ['Graphulo ' num2str(iNUMTAB) ' Tablet'];
if aNUMTAB(iNUMTAB) > 1
    msg = [msg 's'];
end
legs{iNUMTAB} = msg;
end
for iNUMTAB=1:numel(aNUMTAB)
d4mrow = yTimeD4M(iNUMTAB,:);
d4mrow = log2(d4mrow);
%fprintf('d4mrow %d\n',d4mrow);
plot(xSCALE(d4mrow~=0),d4mrow(d4mrow~=0),'--o')
msg = ['MapReduce ' num2str(aNUMTAB(iNUMTAB)) ' Tablet 1 Reducer'];
% if aNUMTAB(iNUMTAB) > 1
%     msg = [msg 's'];
% end
legs{numel(aNUMTAB)+iNUMTAB} = msg;
end
for iNUMTAB=1:numel(aNUMTAB)
d4mrow = yTimeD4M2(iNUMTAB,:);
d4mrow = log2(d4mrow);
%fprintf('d4mrow %d\n',d4mrow);
plot(xSCALE(d4mrow~=0),d4mrow(d4mrow~=0),':*')
msg = ['MapReduce ' num2str(aNUMTAB(iNUMTAB)) ' Tablet 2 Reducer'];
% if aNUMTAB(iNUMTAB) > 1
%     msg = [msg 's'];
% end
legs{2*numel(aNUMTAB)+iNUMTAB} = msg;
end
%ax = gca;
%ax.XTick = 10:13;
legend(legs,'Location','SouthEast');
xlabel('SCALE');
ylabel('log_2( Time (s) )');
axis([-inf,+inf,0,+inf])
title('TableMult Runtime Scaling');
savefig('TableMultTime');
%print('TableMultTime','-depsc')
print('TableMultTime','-dpng')

legs = cell(1,3*numel(aNUMTAB));
figure
gca.XTick=xSCALE;
hold on
for iNUMTAB=1:numel(aNUMTAB)
grarow = yRateGraphulo(iNUMTAB,:);
plot(xSCALE(grarow~=0),grarow(grarow~=0),'-x')
msg = ['Graphulo ' num2str(aNUMTAB(iNUMTAB)) ' Tablet'];
if aNUMTAB(iNUMTAB) > 1
    msg = [msg 's'];
end
legs{iNUMTAB} = msg;
end
for iNUMTAB=1:numel(aNUMTAB)
d4mrow = yRateD4M(iNUMTAB,:);
%disp(d4mrow)
plot(xSCALE(d4mrow~=0),d4mrow(d4mrow~=0),'--o')
msg = ['MapReduce ' num2str(aNUMTAB(iNUMTAB)) ' Tablet 1 Reducer'];
% if aNUMTAB(iNUMTAB) > 1
%     msg = [msg 's'];
% end
legs{numel(aNUMTAB)+iNUMTAB}   = msg;
end
for iNUMTAB=1:numel(aNUMTAB)
d4mrow = yRateD4M2(iNUMTAB,:);
%disp(d4mrow)
plot(xSCALE(d4mrow~=0),d4mrow(d4mrow~=0),':*')
msg = ['MapReduce ' num2str(aNUMTAB(iNUMTAB)) ' Tablet 2 Reducer'];
% if aNUMTAB(iNUMTAB) > 1
%     msg = [msg 's'];
% end
legs{2*numel(aNUMTAB)+iNUMTAB}   = msg;
end
%ax.XTick = 10:13;
legend(legs,'Location','SouthEast');
xlabel('SCALE');
ylabel('Rate (partial products/s)');
title('TableMult Rate Scaling');
axis([-inf,+inf,0,+inf])
savefig('TableMultRate');
%print('TableMultRate','-depsc')
print('TableMultRate','-dpng')


% Compact all
if 0
for iSCALE=8:9%1:numel(xSCALE)
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
Ainfo = Ainfo + Assoc(row,'nnz,',[num2str(numEntries) nl]);
put(Tinfo,Ainfo);
fprintf('NUMTAB %d SCALE %d \n R     %s RBest %s R     %s RBest %s\n',NUMTAB,SCALE,Val(Tinfo(row,'splitPointsR,')),...
        Val(Tinfo(row,'splitPointsRBest,')),Val(Tinfo(row,'splitSizesR,')),Val(Tinfo(row,'splitSizesRBest,')));
end
end
end

yNNZ = zeros(1,numel(xSCALE));

% Other analyses
% for iSCALE=1:numel(xSCALE)
% for iNUMTAB=2:numel(aNUMTAB) % skip first
% NUMTAB=aNUMTAB(iNUMTAB);
% SCALE=xSCALE(iSCALE);
% tname = ['DH_' num2str(SCALE,'%02d') '_TgraphAdj'];
% tname2 = ['DHB_' num2str(SCALE,'%02d') '_TgraphAdj'];
% rname = [tname '_t' 'X' tname2];
% row = [rname '_nt' num2str(NUMTAB) nl];
% numpp = Val(str2num(Tinfo(row,'numpp,')));
% md = maxdiff(Val(Tinfo(row,'splitSizesR,')));
% yNNZ(iSCALE) = Val(str2num(Tinfo(row,'nnz,')));
% fprintf('NUMTAB %d SCALE %d MaxDiff %9d NumPP %9d nnzCompact %9d\n',NUMTAB,SCALE,md,numpp,yNNZ(iSCALE));
% end
% end

%for iSCALE=1:numel(xSCALE)
%    yPP(iSCALE) = 
%end

a = [xSCALE; yPP; yNNZ; yTimeGraphulo(1,:); yRateGraphulo(1,:); yTimeD4M(1,:); yRateD4M(1,:); yTimeGraphulo(2,:); yRateGraphulo(2,:); yTimeD4M(2,:); yRateD4M(2,:)];
% & & & \multicolumn{2}{|c|}{Graphulo 1 Tablet} & \multicolumn{2}{|c|}{D4M 1 Tablet} & \multicolumn{2}{|c|}{Graphulo 2 Tablets} & \multicolumn{2}{|c|}{D4M 2 Tablets} \\
% Using: https://www.mathworks.com/matlabcentral/answers/96131-is-there-a-format-in-matlab-to-display-numbers-such-that-commas-are-automatically-inserted-into-the
%b = a;%num2bank(a);
b = arrayfun(@addmatlatex, a, 'UniformOutput', false);
%b = arrayfun(@(x) num2str(x,'%f') , a, 'UniformOutput', false);
%b = cellfun(@elim0,b,'UniformOutput',false);
%b = cellfun(@addmatlatex,b,'UniformOutput',false);
% Using: https://www.mathworks.com/matlabcentral/fileexchange/44274-converting-matlab-data-to-latex-table
inp = struct();
inp.data = b;
inp.tableRowLabels = {'SCALE','\#PartialProducts','nnz(C)','Time (s)','Rate','Time (s)','Rate','Time (s)','Rate','Time (s)','Rate'};
inp.tableCaption = 'Numerical results and parameters for Figure~\ref{fTableMultPerf}';
inp.tableLabel = 'lResultsParams';
inp.dataFormat = {'%s'};
inp.transposeTable = true;
inp.tableColumnAlignment = 'l';
lat = latexTable(inp)


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dylan Hutchison (dhutchis@mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2015> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

