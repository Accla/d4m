
MyDBsetup;
myPrefix = 'DH_';
infoFunc = @util_UpdateInfoAndDB;

durability = []; % choices: none, log, flush, sync (default)

%DELETE_TABLE_TRIGGER = true;

for SCALE = 10
for SEED = 20160331
tname = [myPrefix 'pg' num2str(SCALE,'%02d') '_' num2str(SEED)];
TNadjUU = [tname '_TgraphAdjUU'];
TNadjUUDeg = [tname '_TgraphAdjUUDeg'];

for SCALEsampled = SCALE

if SCALE ~= SCALEsampled
    % filterRowCol
    origN = 2^SCALE;
    sampledN = 2^SCALEsampled;
    % Randomly sample a set of non-zero entries
    s = RandStream('mt19937ar','Seed',0);
    filterRowCol = sprintf('%d,',randperm(s,origN,sampledN));
else
    filterRowCol = '';
end


for NUMTAB = 2

alg04_triCount01_Graphulo
pause(3);
% if ~doClient && SCALEsampled <= 15
%      alg03_kTrussAdj02_D4M
%      pause(3);
% end

%TadjkTruss = DB(TNadjkTruss); TadjkTrussD4M = DB(TNadjkTrussD4M);

% Verification runs out of Java heap memory at SCALE 12 and larger
if SCALEsampled < 12
    %alg03_kTrussAdj03_Verify
end

end
end
end
end
