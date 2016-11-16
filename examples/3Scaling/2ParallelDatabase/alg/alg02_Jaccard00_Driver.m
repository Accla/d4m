
MyDBsetup;
myPrefix = 'DH_';
infoFunc = @util_UpdateInfoAndDB;

%DELETE_TABLE_TRIGGER = true;

for doClient = false
for SCALE = 14
for SEED = 20160331
for NUMTAB = 1:2

tname = [myPrefix 'pg' num2str(SCALE,'%02d') '_' num2str(SEED)];
TNadjUU = [tname '_TgraphAdjUU'];
TNadjUUDeg = [tname '_TgraphAdjUUDeg'];
TNadjJaccard = [tname '_TgraphAdjJaccard'];
TNadjJaccardD4M = [tname '_TgraphAdjJaccardD4M'];

alg02_Jaccard01_Graphulo;
pause(3);

% if SCALE <= 15 && ~doClient
%     Hybrid = false;
% 	alg02_Jaccard02_D4M;
% 	pause(3);
% %    Hybrid = true;
% %    alg02_Jaccard02_D4M;
% end


% Verification runs out of Java heap memory at SCALE 12 and larger
% if SCALE < 12
%     TadjJaccard = DB(TNadjJaccard); TadjJaccardD4M = DB(TNadjJaccardD4M);
%     alg02_Jaccard03_Verify;
% end

end
end
end
end
