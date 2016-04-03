function alg01_Gen_PlotAdjUUDeg(DB, TNadjUUDeg);
% Plot degree distribution from the degree table.

TadjUUDeg = DB(TNadjUUDeg);
Adeg = TadjUUDeg(:,:);
[~,~,v] = find(str2num(Adeg));   % v contains the list of degrees - need to collect and sort them

x = 1:max(v);
y = zeros(1,length(x));
for deg = x
	y(deg) = nnz(v == deg);
end
keyboard
% plot(x,y);
figure; loglog(x,y,'^k');
xlabel('Degree'); ylabel('Count'); title('Degree distribution of Abs0($A+A^T$), SCALE=10','interpreter','latex'); 


        
