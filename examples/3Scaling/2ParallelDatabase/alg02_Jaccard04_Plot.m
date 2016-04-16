% Plot data taken from Jaccard experiments.

%Aall is the Associative array containing the experiment data,
MOCK = true % Use Mocked data to practice plotting, or real data.
if MOCK
% 	r = 'DH_jaccard__DH_pg10_20160331__nt1__d4m|20160416-095404,';
% 	c = 'SCALE|10,NUMTAB|1,d4mJaccardTotal|4.04,engine|d4m,';
% 	v = '1,';
% 	Aall = Assoc(r,c,v);
% 	r = 'DH_jaccard__DH_pg10_20160331__nt1|20160404-025658,';
% 	c = 'SCALE|10,NUMTAB|1,graphuloJaccard|14.5,engine|graphulo,';
% 	v = '1,';
% 	Aall = Aall + Assoc(r,c,v);
% 	r = 'DH_jaccard__DH_pg10_20160331__nt1__d4m|20160416-095406,';
% 	c = 'SCALE|10,NUMTAB|2,d4mJaccardTotal|2.5,engine|d4m,';
% 	v = '1,';
% 	r = 'DH_jaccard__DH_pg10_20160331__nt1|20160404-025659,';
% 	c = 'SCALE|10,NUMTAB|1,graphuloJaccard|7.22,engine|graphulo,';
% 	v = '1,';
% 	Aall = Aall + Assoc(r,c,v);
%   r = 'DH_pg10_20160331__nt1,';
% 	c = 'SCALE|10,jaccardNumpp|1011708,';
% 	v = '1,';
    r = 'DH_jaccard__DH_pg10_20160331__nt1__d4m|20160416-095404,';
	c = 'SCALE|10,NUMTAB|1,d4mJaccardTotal|14.04,engine|d4m,tname|DH_pg10_20160331,';
	v = '1,';
	Aall = Assoc(r,c,v);
	r = 'DH_jaccard__DH_pg10_20160331__nt1|20160404-025658,';
	c = 'SCALE|10,NUMTAB|1,graphuloJaccard|4.5,engine|graphulo,tname|DH_pg10_20160331,';
	v = '1,';
	Aall = Aall + Assoc(r,c,v);
	r = 'DH_jaccard__DH_pg10_20160331__nt2__d4m|20160416-095406,';
	c = 'SCALE|10,NUMTAB|2,d4mJaccardTotal|16.5,engine|d4m,tname|DH_pg10_20160331,';
	v = '1,';
    Aall = Aall + Assoc(r,c,v);
	r = 'DH_jaccard__DH_pg10_20160331__nt2|20160404-025659,';
	c = 'SCALE|10,NUMTAB|2,graphuloJaccard|3.1,engine|graphulo,tname|DH_pg10_20160331,';
	v = '1,';
	Aall = Aall + Assoc(r,c,v);
    
    r = 'DH_jaccard__DH_pg11_20160332__nt1__d4m|20160417-095404,';
	c = 'SCALE|11,NUMTAB|1,d4mJaccardTotal|29.12,engine|d4m,tname|DH_pg11_20160332,';
	v = '1,';
	Aall = Aall + Assoc(r,c,v);
	r = 'DH_jaccard__DH_pg11_20160332__nt1|20160405-025658,';
	c = 'SCALE|11,NUMTAB|1,graphuloJaccard|17.1,engine|graphulo,tname|DH_pg11_20160332,';
	v = '1,';
	Aall = Aall + Assoc(r,c,v);
	r = 'DH_jaccard__DH_pg11_20160332__nt2__d4m|20160417-095406,';
	c = 'SCALE|11,NUMTAB|2,d4mJaccardTotal|14.1,engine|d4m,tname|DH_pg11_20160332,';
	v = '1,';
    Aall = Aall + Assoc(r,c,v);
	r = 'DH_jaccard__DH_pg11_20160332__nt2|20160405-025659,';
	c = 'SCALE|11,NUMTAB|2,graphuloJaccard|11.6,engine|graphulo,tname|DH_pg11_20160332,';
	v = '1,';
	Aall = Aall + Assoc(r,c,v);
    
    r = 'DH_pg10_20160331,';
	c = 'SCALE|10,jaccardNumpp|1011708,';
	v = '1,';
    Aall = Aall + Assoc(r,c,v);
    r = 'DH_pg11_20160332,';
	c = 'SCALE|11,jaccardNumpp|2011708,';
	v = '1,';
    Aall = Aall + Assoc(r,c,v);
else
	% get from DB or file
end

% this could be a class
d = struct();
d.graphulo = struct();
d.graphulo.linespec = '-o';
d.graphulo.nt = []; % time{i} is for nt(i)
d.graphulo.scale = {}; % x-axis 
d.graphulo.time = {}; % cell array; same length as nt. Each value is an array of times.
d.graphulo.rate = {}; % same
% do same for d4m
d.d4m = struct();
d.d4m.linespec = '--o';
d.d4m.nt = []; % time{i} is for nt(i)
d.d4m.scale = {}; % x-axis 
d.d4m.time = {}; % cell array; same length as nt. Each value is an array of times.
d.d4m.rate = {}; % same

% Put data into structure - only Jaccard experiment data
rowmat = Str2mat(Row(Aall(StartsWith('DH_jaccard,'),:)));
for rowmati = 1:size(rowmat,1)
	row = deblank(rowmat(rowmati,:)); % deblank removes trailing space
	Arow = Aall(row,:);
    %displayFull(Arow)

	%graphulo or d4m
	% consider splicing out helper function getNum(A,colheader), getString(A,colheader)
	% nt = getString(Aall, 'NUMTAB');
	[~,engine] = SplitStr(Col(Arow(:,StartsWith('engine,'))),'|');
	engine = engine(1:end-1); % remove ','
    if (~strcmp(engine,'d4m') && ~strcmp(engine,'graphulo'))
        error(['unrecognized engine: ' engine]);
    end

	[~,nt] = SplitStr(Col(Arow(:,StartsWith('NUMTAB,'))),'|');
	nt = str2num(nt);
	[~,scale] = SplitStr(Col(Arow(:,StartsWith('SCALE,'))),'|');
	scale = str2num(scale);
    if (strcmp(engine,'graphulo'))
        jaccardTimeStr = 'graphuloJaccard,';
    else
        jaccardTimeStr = 'd4mJaccardTotal,';
    end
	[~,jaccardTime] = SplitStr(Col(Arow(:,StartsWith(jaccardTimeStr))),'|');
	jaccardTime = str2num(jaccardTime);
    % get numpp from tname
    [~,tname] = SplitStr(Col(Arow(:,StartsWith('tname,'))),'|');
    %fprintf('tname %s\n', tname)
    Atname = Aall(tname,:);
%     [~,SCALE] = SplitStr(Col(Atname(:,StartsWith('SCALE,'))),'|');
%     SCALE = str2num(SCALE);
	[~,numpp] = SplitStr(Col(Atname(:,StartsWith('jaccardNumpp,'))),'|');
	numpp = str2num(numpp);

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
	d.(engine).time{ntidx}(newlen) = jaccardTime;
	d.(engine).rate{ntidx}(newlen) = numpp./jaccardTime;
    %fprintf('scale %d\n',scale);
end


% Plot data from structure
figure;
hold on;
legendarr = {};
legendarri = 1;
for engine = {'graphulo', 'd4m'}
	en = engine{1};
	for ntidx = 1:numel(d.(en).nt)
        nt = d.(en).nt(ntidx);
        legendarr{legendarri} = [en ' ' num2str(nt)];
        legendarri = legendarri + 1;
		% sort
		[d.(en).scale{ntidx},sortidx] = sort(d.(en).scale{ntidx});
		d.(en).time{ntidx} = d.(en).time{ntidx}(sortidx);
		% plot
		x = d.(en).scale{ntidx};
		y = d.(en).time{ntidx};
		plot(x, y, d.(en).linespec);
	end
end
hold off;
xlabel('SCALE');
ylabel('Time (s)');
title('Jaccard Time Scaling');
axis([-inf,+inf,0,+inf])
legend(legendarr);
% savefig('JaccardTime');
% print('JaccardTime','-depsc')
% print('JaccardTime','-dpng')

% Rate
figure;
hold on;
legendarr = {};
legendarri = 1;
for engine = {'graphulo', 'd4m'}
	en = engine{1};
	for ntidx = 1:numel(d.(en).nt)
        nt = d.(en).nt(ntidx);
        legendarr{legendarri} = [en ' ' num2str(nt)];
        legendarri = legendarri + 1;
		% sort
		[d.(en).scale{ntidx},sortidx] = sort(d.(en).scale{ntidx});
		d.(en).rate{ntidx} = d.(en).rate{ntidx}(sortidx);
		% plot
		x = d.(en).scale{ntidx};
		y = d.(en).rate{ntidx};
		plot(x, y, d.(en).linespec);
	end
end
hold off;
xlabel('SCALE');
ylabel('Rate (entries written per sec.)');
title('Jaccard Rate Scaling');
axis([-inf,+inf,0,+inf])
legend(legendarr);

% boxplot - https://www.mathworks.com/help/stats/boxplot.html
% add legend - https://www.mathworks.com/matlabcentral/answers/127195-how-do-i-add-a-legend-to-a-boxplot-in-matlab
% overlay boxplots; manually give position; https://www.mathworks.com/matlabcentral/answers/22-how-do-i-display-different-boxplot-groups-on-the-same-figure-in-matlab



% function out = getString(A, colhead)
% 	[~,out] = SplitStr(Col(A(:,StartsWith(colhead))),'|');
% 	out = out(1:end-1); % remote ','
% end
% function out = getNum(A, colhead)
% 	[~,out] = SplitStr(Col(A(:,StartsWith(colhead))),'|');
% 	out = str2num(out);
% end
