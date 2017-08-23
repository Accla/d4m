
MyDBsetup;
myPrefix = 'DH_';
infoFunc = @util_UpdateInfoAndDB;

nl = char(10);
Ainfo = Assoc('','','');

for SCALE = 10:17
for SEED = 20160331
tname = [myPrefix 'pg' num2str(SCALE,'%02d') '_' num2str(SEED)];
TNadjUU = [tname '_TgraphAdjUU'];
ne = G.countEntries(TNadjUU);
fprintf('nnz %s = %d\n',TNadjUU,ne);
Ainfo = Ainfo + Assoc([tname nl],['nnz' nl],[num2str(ne) nl]);
end
end
infoFunc(Ainfo);
        
