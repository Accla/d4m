% Plot data taken from Jaccard experiments.

%Aall is the Associative array containing the experiment data,
MOCK = true % Use Mocked data to practice plotting, or real data.
if MOCK
	r = 'jaccard_10_graphlo_nt1|20160330-230000,';
	c = 'SCALE|10,NUMTAB|1,engine|graphulo,jaccardTime|4.5,numpp|12,numEntriesRightAfter|9,numEntriesAfterCompact|5,';
	v = '1,';
	Aall = Assoc(r,c,v);
	r = 'jaccard_11_graphlo_nt1|20160330-233000,';
	c = 'SCALE|11,NUMTAB|1,engine|graphulo,jaccardTime|14.5,numpp|25,numEntriesRightAfter|20,numEntriesAfterCompact|11,';
	v = '1,';
	Aall = Aall + Assoc(r,c,v);
	r = 'jaccard_10_graphlo_nt2|20160330-234500,';
	c = 'SCALE|10,NUMTAB|2,engine|graphulo,jaccardTime|2.5,numpp|12,numEntriesRightAfter|9,numEntriesAfterCompact|5,';
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

% Put data into structure
rowmat = Str2mat(Row(Aall));
for rowmati = 1:size(rowmat,1)
	row = deblank(rowmat(rowmati,:)); % deblank removes trailing space
	Arow = Aall(row,:);

	%graphulo or d4m
	% consider splicing out helper function getNum(A,colheader), getString(A,colheader)
	% nt = getString(Aall, 'NUMTAB');
	[~,engine] = SplitStr(Col(Arow(:,StartsWith('engine,'))),'|');
	engine = engine(1:end-1); % remote ','

	[~,nt] = SplitStr(Col(Arow(:,StartsWith('NUMTAB,'))),'|');
	nt = str2num(nt);
	[~,scale] = SplitStr(Col(Arow(:,StartsWith('SCALE,'))),'|');
	scale = str2num(scale);
	[~,jaccardTime] = SplitStr(Col(Arow(:,StartsWith('jaccardTime,'))),'|');
	jaccardTime = str2num(jaccardTime);
	[~,numpp] = SplitStr(Col(Arow(:,StartsWith('numpp,'))),'|');
	numpp = str2num(numpp);

	if isempty(find(d.(engine).nt == nt))
		d.(engine).nt = [d.(engine).nt nt];
		newlen = length(d.(engine).nt);
		d.(engine).scale{newlen} = [];
		d.(engine).time{newlen} = [];
		d.(engine).rate{newlen} = [];
	end
	ntidx = find(d.(engine).nt == nt);
	newlen = length(d.(engine).scale{nt})+1;
	d.(engine).scale{nt}(newlen) = scale;
	d.(engine).time{nt}(newlen) = jaccardTime;
	d.(engine).rate{nt}(newlen) = numpp./jaccardTime;
end


% Plot data from structure
figure;
hold on;
for engine = {'graphulo'} %, 'd4m'
	engine = engine{1};
	for nt = d.(engine).nt
		% sort
		[d.(engine).scale{nt},sortidx] = sort(d.(engine).scale{nt});
		d.(engine).time{nt} = d.(engine).time{nt}(sortidx);
		d.(engine).rate{nt} = d.(engine).rate{nt}(sortidx);
		% plot
		x = d.(engine).scale{nt};
		y = d.(engine).time{nt};
		plot(x, y, d.(engine).linespec);
	end
end
hold off;
xlabel('SCALE');
ylabel('Time (s)');
title('Jaccard Time Scaling');
axis([-inf,+inf,0,+inf])
% savefig('JaccardTime');
% print('JaccardTime','-depsc')
% print('JaccardTime','-dpng')



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
