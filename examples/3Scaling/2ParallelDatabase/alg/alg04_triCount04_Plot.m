% Plot data taken from tricount experiments and create a table.
doplot = false;

%Aall is the Associative array containing the experiment data,
MOCK = true % Use Mocked data to practice plotting, or real data.
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
d.graphulo = struct();
d.graphulo.linespec = '-';
d.graphulo.nt = []; % time{i} is for nt(i)
d.graphulo.scale = {}; % x-axis 
d.graphulo.time = {}; % cell array; same length as nt. Each value is an array of times.
d.graphulo.rate = {}; % same

t = struct();
t.scale = [];
t.numpp = [];
t.adjnnz = [];

% Put data into structure - only tricount experiment data
rowmat = Str2mat(Row(Aall(StartsWith('DH_tricount,'),:)));
for rowmati = 1:size(rowmat,1)
	row = [deblank(rowmat(rowmati,:)) char(9)]; % deblank removes trailing space - put \t back on
	Arow = Aall(row,:);
    %displayFull(Arow)

	%graphulo or d4m
	% consider splicing out helper function getNum(A,colheader), getString(A,colheader)
	% nt = getString(Aall, 'NUMTAB');
	%[~,engine] = SplitStr(Col(Arow(:,StartsWith('engine,'))),'|');
    engine = Val(Arow(:,'engine,'));
    engine = engine(1:end-1); % remove ','
    if (~strcmp(engine,'d4m') && ~strcmp(engine,'graphulo'))
        error(['unrecognized engine: ' engine]);
    end

	nt = Val(str2num(Arow(:,'NUMTAB,')));
    scale = Val(str2num(Arow(:,'SCALE,')));
	adjnnz = Val(str2num(Arow(:,'adjnnz,')));
    
    if strcmp(engine,'graphulo')
        tricountTimeStr = 'triCountGraphulo,';
    else
        tricountTimeStr = 'd4mtricountTotal,';
    end
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
    end
%     scaleidx = find(d.(engine).scale{ntidx} == scale,1);
%     if isempty(scaleidx)
%         scaleidx = length(d.(engine).scale{ntidx})+1;
%         d.(engine).scale{ntidx}(scaleidx) = scale;
%     end
	
    newlen = length(d.(engine).scale{ntidx})+1;
	d.(engine).scale{ntidx}(newlen) = scale;
	d.(engine).time{ntidx}(newlen) = tricountTime;
	d.(engine).rate{ntidx}(newlen) = numpp./tricountTime;
    %fprintf('scale %d\n',scale);
end

mintime = 0.28118;
maxtime = 12233.0523;
mintime = min(min(structfun( @(e) min(cellfun(@min, e.time)) ,d)), mintime);
maxtime = max(max(structfun( @(e) max(cellfun(@max, e.time)) ,d)), maxtime);


% Plot data from structure
if doplot
figure;
end
legendarr = {};
legendarri = 1;
for engine = {{'graphulo', 'Graphulo'}} %, {'d4m', 'D4M'}, {'dense', 'MTJ'}}
	ens = engine{1};
    en = ens{1};
	for ntidx = 1:numel(d.(en).nt)
        nt = d.(en).nt(ntidx);
        legendarr{legendarri} = [ens{2} ' ' num2str(nt) ' Tablet'];
        legendarri = legendarri + 1;
		% sort
		[d.(en).scale{ntidx},sortidx] = sort(d.(en).scale{ntidx});
		d.(en).time{ntidx} = d.(en).time{ntidx}(sortidx);
		% plot
		x = d.(en).scale{ntidx};
		y = d.(en).time{ntidx};
        if doplot
		semilogy(x, y, d.(en).linespec, 'LineWidth', 1+(nt-1)*0.5);
		hold on;
        end
	end
end
if doplot
hold off;
xlabel('SCALE');
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

% Rate
if doplot
figure;
end
legendarr = {};
legendarri = 1;
for engine = {{'graphulo', 'Graphulo'}} %, {'d4m', 'D4M'}}
	ens = engine{1};
    en = ens{1};
	for ntidx = 1:numel(d.(en).nt)
        nt = d.(en).nt(ntidx);
            legendarr{legendarri} = [ens{2} ' ' num2str(nt) ' Tablet'];
        legendarri = legendarri + 1;
		% sort
		[d.(en).scale{ntidx},sortidx] = sort(d.(en).scale{ntidx});
		d.(en).rate{ntidx} = d.(en).rate{ntidx}(sortidx);
		% plot
		x = d.(en).scale{ntidx};
		y = d.(en).rate{ntidx};
        if doplot
		plot(x, y, d.(en).linespec, 'LineWidth', 1+(nt-1)*0.5);
		hold on;
        end
	end
end
if doplot
hold off;
xlabel('SCALE');
ylabel('Rate (entries written per sec.)');
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
    disp(ename)
    for ntnum = d.(ename).nt  %length(d.(ename).scale)
        toadd = max(d.(ename).scale{ntnum})+1:maxscale;
        d.(ename).scale{ntnum} = [d.(ename).scale{ntnum} toadd];
        toadd(:) = 0;
        d.(ename).time{ntnum} = [d.(ename).time{ntnum} toadd];
        d.(ename).rate{ntnum} = [d.(ename).rate{ntnum} toadd];
    end
end

[t.scale,sortidx] = sort(t.scale);
t.adjnnz = t.adjnnz(sortidx);

a = [minscale:maxscale; t.adjnnz; t.numpp; d.graphulo.time{1}; d.graphulo.time{2}]; %;  d.d4m.time{1}; d.d4m.time{2}];
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
inp.tableRowLabels = {'SCALE','$\operatorname{nnz}(\matr{A})$','Total Partial Products','Graphulo Time 1 Tablet (s)','Graphulo Time 2 Tablets (s)'};
inp.tableCaption = 'Comparison of Writes and Runtimes for Tricount algorithm'; %Figure~\ref{fKTrussAdjTime}
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
lat{4} = '\adjustbox{angle=30,lap={1.75em}{\width-3.8em},raise=-0.75em,scale=0.95}{SCALE} & $\operatorname{nnz}(\matr{A})$ & \begin{tabular}{@{}c@{}} Partial \\ Products \end{tabular} & \begin{tabular}{@{}c@{}} Graphulo \\ Overhead \end{tabular} & \begin{tabular}{@{}c@{}}Graphulo \\1 Tablet \end{tabular} & \begin{tabular}{@{}c@{}}Graphulo \\2 Tablets \end{tabular} \\';
lat{3}(25) = 'r';
fprintf('\n');
cellfun(@disp,lat)


% function out = getString(A, colhead)
% 	[~,out] = SplitStr(Col(A(:,StartsWith(colhead))),'|');
% 	out = out(1:end-1); % remote ','
% end
% function out = getNum(A, colhead)
% 	[~,out] = SplitStr(Col(A(:,StartsWith(colhead))),'|');
% 	out = str2num(out);
% end
