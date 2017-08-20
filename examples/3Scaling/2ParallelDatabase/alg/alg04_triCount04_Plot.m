% Plot data taken from tricount experiments and create a table.
doplot = true;

%Aall is the Associative array containing the experiment data,
MOCK = false; % Use Mocked data to practice plotting, or real data.
if MOCK
    Aall = Assoc('','','');
 %    r = 'DH_tricount__DH_pg10_20160331__nt1__d4m|20160416-095404,';
	% c = 'SCALE,NUMTAB,d4mtricountTotal,engine,tname,';
	% v = '10,1,14.04,d4m,DH_pg10_20160331,';
	% Aall = Assoc(r,c,v);
	r = 'DH_tricount__DH_pg10_20160331__nt1__graphulo|20160404-025658,';
	c = 'SCALE,NUMTAB,triCountGraphulo,engine,tname,numpp,adjnnz,';
	v = '10,1,4.5,graphulo,DH_pg10_20160331,1011708,1024,';
	Aall = Aall + Assoc(r,c,v);
	% r = 'DH_tricount__DH_pg10_20160331__nt2__d4m|20160416-095406,';
	% c = 'SCALE,NUMTAB,d4mtricountTotal,engine,tname,';
	% v = '10,2,16.5,d4m,DH_pg10_20160331,';
 %    Aall = Aall + Assoc(r,c,v);
	r = 'DH_tricount__DH_pg10_20160331__nt2__graphulo|20160404-025659,';
	c = 'SCALE,NUMTAB,triCountGraphulo,engine,tname,numpp,adjnnz,';
	v = '10,2,3.1,graphulo,DH_pg10_20160331,1011708,1024,';
	Aall = Aall + Assoc(r,c,v);
    
 %    r = 'DH_tricount__DH_pg11_20160332__nt1__d4m|20160417-095404,';
	% c = 'SCALE,NUMTAB,d4mtricountTotal,engine,tname,';
	% v = '11,1,29,d4m,DH_pg11_20160332,';
	% Aall = Aall + Assoc(r,c,v);
	r = 'DH_tricount__DH_pg11_20160332__nt1__graphulo|20160405-025658,';
	c = 'SCALE,NUMTAB,triCountGraphulo,engine,tname,numpp,adjnnz,';
	v = '11,1,17.1,graphulo,DH_pg11_20160332,2011708,2048,';
	Aall = Aall + Assoc(r,c,v);
	% r = 'DH_tricount__DH_pg11_20160332__nt2__d4m|20160417-095406,';
	% c = 'SCALE,NUMTAB,d4mtricountTotal,engine,tname,';
	% v = '11,2,14.1,d4m,DH_pg11_20160332,';
 %    Aall = Aall + Assoc(r,c,v);
	r = 'DH_tricount__DH_pg11_20160332__nt2__graphulo|20160405-025659,';
	c = 'SCALE,NUMTAB,triCountGraphulo,engine,tname,numpp,adjnnz,';
	v = '11,2,11.6,graphulo,DH_pg11_20160332,2011708,2048,';
	Aall = Aall + Assoc(r,c,v);
    
 %    r = 'DH_pg10_20160331,';
	% c = 'SCALE,numpp,';
	% v = '10,1011708,';
 %    Aall = Aall + Assoc(r,c,v);
 %    r = 'DH_pg11_20160332,';
	% c = 'SCALE,numpp,';
	% v = '11,2011708,';
 %    Aall = Aall + Assoc(r,c,v);
else
	Ainfo = util_UpdateInfo;
    Aall = Ainfo; %Ainfo('DH_tricount__DH_pg10_20160331__nt1__d4m|20160417-160354,DH_tricount__DH_pg11_20160331__nt1__d4m|20160417-155755,DH_tricount_graphulo__DH_pg10_20160331__nt1|20160417-160345,DH_tricount_graphulo__DH_pg11_20160331__nt1|20160417-155735,DH_pg10_20160331,DH_pg11_20160331,',:);

end

% this could be a class
d = struct();
d.Adj = struct();
d.Adj.linespec = '-';
d.Adj.nt = []; % time{i} is for nt(i)
d.Adj.scale = {}; % x-axis 
d.Adj.time = {}; % cell array; same length as nt. Each value is an array of times.
d.Adj.rate = {}; % same
d.Adj.numpp = {}; % same
d.AdjEdge = struct();
d.AdjEdge.linespec = '--';
d.AdjEdge.nt = []; % time{i} is for nt(i)
d.AdjEdge.scale = {}; % x-axis 
d.AdjEdge.time = {}; % cell array; same length as nt. Each value is an array of times.
d.AdjEdge.rate = {}; % same
d.AdjEdge.numpp = {}; % same

t = struct();
t.scale = [];
t.numpp = [];
t.adjnnz = [];

% Put data into structure - only tricount experiment data
% if AdjEdge
%     startStr = 'DH_triCountAdjEdge,';
% else
%     startStr = 'DH_triCount_,';
% end
startStr = 'DH_triCount,';
rowmat = Str2mat(Row(Aall(StartsWith(startStr),:)));
for rowmati = 1:size(rowmat,1)
	row = [deblank(rowmat(rowmati,:)) char(9)]; % deblank removes trailing space - put \t back on
	Arow = Aall(row,:);
    %displayFull(Arow)

	%graphulo or d4m
	% consider splicing out helper function getNum(A,colheader), getString(A,colheader)
	% nt = getString(Aall, 'NUMTAB');
	%[~,engine] = SplitStr(Col(Arow(:,StartsWith('engine,'))),'|');

    % engine = Val(Arow(:,'engine,'));
    % engine = engine(1:end-1); % remove ','
    % if (~strcmp(engine,'d4m') && ~strcmp(engine,'graphulo'))
    %     error(['unrecognized engine: ' engine]);
    % end
    if startsWith(row,'DH_triCount_')
        engine='Adj';
    else
        engine='AdjEdge';
    end

	nt = 24; %Val(str2num(Arow(:,'NUMTAB,')));
    scale = Val(str2num(Arow(:,'SCALE,')));
	adjnnz = Val(str2num(Arow(:,'adjnnz,')));
    

    % if strcmp(engine,'graphulo')
        tricountTimeStr = 'triCountGraphulo,';
    % else
    %     tricountTimeStr = 'd4mtricountTotal,';
    % end
    tricountTime = Val(str2num(Arow(:,tricountTimeStr))); %#ok<*ST2NM>
	numpp = Val(str2num(Arow(:,'numpp,'))); %#ok<*ST2NM>
    
    tidx = find(t.scale == scale,1);
    if isempty(tidx)
        newlen = length(t.scale)+1;
        t.scale(newlen) = scale;
        t.numpp(newlen) = numpp;
        t.adjnnz(newlen) = adjnnz;
    end

	ntidx = find(d.(engine).nt == nt,1);
    if isempty(ntidx)
		ntidx = length(d.(engine).nt)+1;
        d.(engine).nt(ntidx) = nt;
		d.(engine).scale{ntidx} = [];
		d.(engine).time{ntidx} = [];
        d.(engine).rate{ntidx} = [];
		d.(engine).numpp{ntidx} = [];
    end
%     scaleidx = find(d.(engine).scale{ntidx} == scale,1);
%     if isempty(scaleidx)
%         scaleidx = length(d.(engine).scale{ntidx})+1;
%         d.(engine).scale{ntidx}(scaleidx) = scale;
%     end
	
    newlen = length(d.(engine).scale{ntidx})+1;
	d.(engine).scale{ntidx}(newlen) = scale;
	d.(engine).time{ntidx}(newlen) = tricountTime;
    d.(engine).rate{ntidx}(newlen) = 2*numpp./tricountTime;
	d.(engine).numpp{ntidx}(newlen) = numpp;
    %fprintf('scale %d\n',scale);
end

mintime = 0;
maxtime = .5e5;
mintime = min(min(structfun( @(e) min(cellfun(@min, e.time)) ,d)), mintime);
maxtime = max(max(structfun( @(e) max(cellfun(@max, e.time)) ,d)), maxtime);

[t.scale,sortidx] = sort(t.scale);
t.adjnnz = t.adjnnz(sortidx);


% Plot data from structure
if doplot
figure;
end
legendarr = {};
legendarri = 1;
for engine = {{'Adj', 'Adj-only'}, {'AdjEdge', 'Adj+Edge'}}
	ens = engine{1};
    en = ens{1};
	for ntidx = 1:numel(d.(en).nt)
        nt = d.(en).nt(ntidx);
        legendarr{legendarri} = ens{2}; %[ens{2} ' ' num2str(nt) ' Tablet'];
        legendarri = legendarri + 1;
		% sort
		[d.(en).scale{ntidx},sortidx] = sort(d.(en).scale{ntidx});
		d.(en).time{ntidx} = d.(en).time{ntidx}(sortidx);
		% plot
		% x = d.(en).scale{ntidx};
  %       y = d.(en).time{ntidx};
  %       if doplot
		% semilogy(x, y, d.(en).linespec, 'LineWidth', 2);
		% hold on;
  %       end
        y = d.(en).time{ntidx};
        x = t.adjnnz(1:numel(y)); %d.(en).scale{ntidx};
        if doplot
        loglog(x, y, d.(en).linespec, 'LineWidth', 2);
        hold on;
        end
	end
end

% Add Matlab plot
xb = t.adjnnz(1:6); %10:15
% y = [0.11857    0.34756    1.01997    3.03599    8.75455   25.80607]; % no data load times
yb = [0.60008    1.26140    2.95400    7.10646   17.44859   44.25882];
if doplot
loglog(xb,yb,'-','LineWidth',2);
legendarr{legendarri} = 'MATLAB Baseline';
legendarri = legendarri + 1;
plot(t.adjnnz(6), 44.25882, 'x','LineWidth',2,'MarkerSize',10); %  DRAW AN X AT LAST POINT FOR OOM

hold off;
% xlabel('SCALE');
xlabel('# of edges = nnz(triu(A))');
ylabel('Time (s)');
title('Tricount Time Scaling');
axis([-inf,+inf,mintime,maxtime])
legend(legendarr, 'Location', 'southeast');
timeSaveStr = datestr(now,'yyyymmdd-HHMMSS');
[~,~,~] = mkdir('img'); 
fileName = ['img/tricountTime-' timeSaveStr];
savefig(fileName);
%print('tricountTime','-depsc')
print(fileName,'-dpng')
end

% Rate\
if doplot
figure;
end
legendarr = {};
legendarri = 1;
for engine = {{'Adj', 'Adj-only'}, {'AdjEdge', 'Adj+Edge'}}
	ens = engine{1};
    en = ens{1};
	for ntidx = 1:numel(d.(en).nt)
        nt = d.(en).nt(ntidx);
            legendarr{legendarri} = ens{2}; %[ens{2} ' ' num2str(nt) ' Tablet'];
        legendarri = legendarri + 1;
		% sort
		[d.(en).scale{ntidx},sortidx] = sort(d.(en).scale{ntidx});
		d.(en).rate{ntidx} =  d.(en).rate{ntidx}(sortidx);
		% plot
        y = d.(en).rate{ntidx};
		x = t.adjnnz(1:numel(y)); %d.(en).scale{ntidx};
        if doplot
		loglog(x, y, d.(en).linespec, 'LineWidth', 2);
		hold on;
        end
	end
end
if doplot
hold off;
% xlabel('SCALE');
xlabel('# of edges = nnz(triu(A))');
ylabel('Rate (entries/sec) = 2*nppf/runtime');
title('Tricount Rate Scaling');
axis([-inf,+inf,0,+inf])
legend(legendarr, 'Location', 'southeast');
fileName = ['img/tricountRate-' timeSaveStr];
savefig(fileName);
%print('tricountTime','-depsc')
print(fileName,'-dpng')
end

% boxplot - https://www.mathworks.com/help/stats/boxplot.html
% add legend - https://www.mathworks.com/matlabcentral/answers/127195-how-do-i-add-a-legend-to-a-boxplot-in-matlab
% overlay boxplots; manually give position; https://www.mathworks.com/matlabcentral/answers/22-how-do-i-display-different-boxplot-groups-on-the-same-figure-in-matlab



% fill in missing times
%iif  = @(varargin) varargin{2*find([varargin{1:2:end}], 1, 'first')}();
minscale = min(structfun( @(e) min(cellfun(@min, e.scale)) ,d));
maxscale = max(structfun( @(e) max(cellfun(@max, e.scale)) ,d));
enames = fieldnames(d);
for enamenum = 1:length(enames)
    ename = enames{enamenum};
    for ntnum = 1:numel(d.(ename).nt)  %length(d.(ename).scale)
        toadd = max(d.(ename).scale{ntnum})+1:maxscale;
        d.(ename).scale{ntnum} = [d.(ename).scale{ntnum} toadd];
        toadd(:) = 0;
        d.(ename).time{ntnum} = [d.(ename).time{ntnum} toadd];
        d.(ename).rate{ntnum} = [d.(ename).rate{ntnum} toadd];
        d.(ename).numpp{ntnum} = [d.(ename).numpp{ntnum} toadd];
    end
end

% take secs/3600 = hrs * (0.266*8) = $/hr %only count tablet server work - this is the variable cost (master and NameNode is fixed cost)
a = [minscale:maxscale; t.adjnnz; d.Adj.numpp{1}; d.Adj.time{1}; d.Adj.rate{1}; d.AdjEdge.numpp{1}; d.AdjEdge.time{1}; d.AdjEdge.rate{1}; [yb zeros(1,numel(minscale:maxscale)-numel(yb))]]; %;  d.d4m.time{1}; d.d4m.time{2}];
% & & & \multicolumn{2}{|c|}{Graphulo 1 Tablet} & \multicolumn{2}{|c|}{D4M 1 Tablet} & \multicolumn{2}{|c|}{Graphulo 2 Tablets} & \multicolumn{2}{|c|}{D4M 2 Tablets} \\
% Using: https://www.mathworks.com/matlabcentral/answers/96131-is-there-a-format-in-matlab-to-display-numbers-such-that-commas-are-automatically-inserted-into-the
%b = a;%num2bank(a);
b = arrayfun(@addmatlatex, a, 'UniformOutput', false);


toplabel='\multirow{2}{1.75em}{\adjustbox{angle=30,lap=\width-3.75em}{SCALE}} &  & \multicolumn{3}{c|}{Adjacency-only Algorithm} & \multicolumn{3}{c|}{Adjacency+Incidence Algorithm} & Baseline \\';


%b = arrayfun(@(x) num2str(x,'%f') , a, 'UniformOutput', false);
%b = cellfun(@elim0,b,'UniformOutput',false);
%b = cellfun(@addmatlatex,b,'UniformOutput',false);
% Using: https://www.mathworks.com/matlabcentral/fileexchange/44274-converting-matlab-data-to-latex-table
inp = struct();
inp.data = b;
inp.tableRowLabels = {'','\# of edges','nppf (entries)','Time (s)','Rate (entries/s)','nppf (entries)','Time (s)','Rate (entries/s)','Time (s)'};
inp.tableCaption = 'Tricount algorithm experiment metrics. nppf = number of partial products after filtering to upper triangle.'; %Figure~\ref{fKTrussAdjTime}
inp.tableLabel = 'tTricountTable';
inp.dataFormat = {'%s'};
inp.transposeTable = true;
inp.tableColumnAlignment = 'c';
lat = latexTable(inp);
lat{1} = '\begin{table*}[tb]';
lat{end} = '\end{table*}';
pipe = find(lat{3} == '|',1);
lat{3} = [lat{3}(1:pipe-1) lat{3}(pipe+1:end)];
pipe = find(lat{3} == '|',1,'last');
lat{3} = [lat{3}(1:pipe-1) lat{3}(pipe+1:end)];
lat = [lat(1:3); lat(5:end-5); lat(end-3:end)];
lat{end-1} = [lat{end-1}(1:7) lat{end-1}(14:end)];
% lat{4} = ['\adjustbox{angle=30,lap={1.75em}{\width-3.8em},raise=-0.75em,scale=0.95}{SCALE} & $\operatorname{nnz}(\matr{A})$ ' ...
% '& \begin{tabular}{@{}c@{}} Partial \\ Products \end{tabular} & \begin{tabular}{@{}c@{}} Graphulo \\ Overhead \end{tabular} & \begin{tabular}{@{}c@{}}Graphulo \\1 Tablet \end{tabular} & \begin{tabular}{@{}c@{}}Graphulo \\2 Tablets \end{tabular} \\'];
% lat{3}(25) = 'r';
fprintf('\n');
lat = [lat(1:3); toplabel; lat(4:end)];
cellfun(@disp,lat)


% function out = getString(A, colhead)
% 	[~,out] = SplitStr(Col(A(:,StartsWith(colhead))),'|');
% 	out = out(1:end-1); % remote ','
% end
% function out = getNum(A, colhead)
% 	[~,out] = SplitStr(Col(A(:,StartsWith(colhead))),'|');
% 	out = str2num(out);
% end
