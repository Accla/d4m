
MyDBsetup;
myPrefix = 'DH_';
infoFunc = @util_UpdateInfoAndDB;

durability = 'sync'; % choices: none, log, flush, sync (default)
zSpecial = false; % controls special behavior for D4M -- inserts the intermediary tables into Accumulo -- used for experimentation
fused = true; % Use Graphulo fused kTruss or normal kTruss
%doClient = false; % Use client version of Graphulo kTruss - overrides fused
%doClientSparse = false; % use sparse or dense matrix; only matters if doClient == true
%maxiter = 99; % limit maximum number of iterations

%DELETE_TABLE_TRIGGER = true;

for doClient = false
for doClientSparse = false%[false,true]
if ~doClient && doClientSparse
    continue
end
for maxiter = 99
for k = 3
for SCALE = 11
if doClient && doClientSparse && SCALE >= 16
    continue
end
if doClient && ~doClientSparse && SCALE >= 15
    continue
end
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

TNadjkTruss = [tname '_sample' num2str(SCALEsampled) '_TgraphAdj' num2str(k) 'Truss'];
TNadjkTrussD4M = [tname '_sample' num2str(SCALEsampled) '_TgraphAdj' num2str(k) 'TrussD4M'];


for NUMTAB = 1

alg03_kTrussAdj01_Graphulo
pause(15);
if ~doClient && SCALEsampled <= 15
     alg03_kTrussAdj02_D4M
     pause(15);
end

%TadjkTruss = DB(TNadjkTruss); TadjkTrussD4M = DB(TNadjkTrussD4M);

% Verification runs out of Java heap memory at SCALE 12 and larger
if SCALEsampled < 12
    %alg03_kTrussAdj03_Verify
end

end
end
end
end
end
end
end
end
