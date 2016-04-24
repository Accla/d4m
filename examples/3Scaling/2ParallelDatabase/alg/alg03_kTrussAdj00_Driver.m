
MyDBsetup;
myPrefix = 'DH_';
infoFunc = @util_UpdateInfo;
fused = true; % Use Graphulo fused kTruss or normal kTruss

%DELETE_TABLE_TRIGGER = true;

for k = 3
for SCALE = 11
for SEED = 20160331
for NUMTAB = 2

tname = [myPrefix 'pg' num2str(SCALE,'%02d') '_' num2str(SEED)];
TNadjUU = [tname '_TgraphAdjUU'];
TNadjUUDeg = [tname '_TgraphAdjUUDeg'];
TNadjkTruss = [tname '_TgraphAdj' num2str(k) 'Truss'];
TNadjkTrussD4M = [tname '_TgraphAdj' num2str(k) 'TrussD4M'];

alg03_kTrussAdj01_Graphulo
pause(5);
alg03_kTrussAdj02_D4M

%TadjkTruss = DB(TNadjkTruss); TadjkTrussD4M = DB(TNadjkTrussD4M);

% Verification runs out of Java heap memory at SCALE 12 and larger
if SCALE < 12
    alg03_kTrussAdj03_Verify
end

end
end
end
end
