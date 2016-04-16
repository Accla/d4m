%function alg01_Gen_ComputeAdjUUDeg(G, tname, TNadjUU, TNadjUUDeg, infoFunc);
% Generate degree table for adjacency table. Works for any symmetric table.
% alg01_Gen_ComputeAdjUUDeg(G, 'DH_pg10_20160331_TadjUU', 'DH_pg10_20160331_TadjUUDeg', @util_UpdateInfo)
util_Require('G tname TNadjUU TNadjUUDeg infoFunc')
% tname = 'DH_pg10_20160331'; TadjUU = 'DH_pg10_20160331_TadjUU'; TadjUUDeg = 'DH_pg10_20160331_TadjUUDeg'; infoFunc = @disp;

tic;
G.generateDegreeTable(TNadjUU, TNadjUUDeg, true, 'Degree');
genTime = toc; disp(['Time to generate UU Degree Table: ' num2str(genTime) ]);

tic;
G.Compact(TNadjUU);
compactTime = toc;


nl = char(10);
Ainfo = Assoc('','','');
Ainfo = Ainfo + Assoc([tname nl],['tComputeAdjUUDeg' nl],[num2str(genTime) nl]);
Ainfo = Ainfo + Assoc([tname nl],['tCompactAdjUUDeg' nl],[num2str(compactTime) nl]);
infoFunc(Ainfo);
        
