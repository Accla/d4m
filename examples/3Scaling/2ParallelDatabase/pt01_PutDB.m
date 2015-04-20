
DoGenData = true;
%SCALE = 12; 
EdgesPerVertex = 16;
DoDeleteDB = true;
DoPutDB = true;
%%
getTime = 0; multTimeDB = 0;

% TOPgetTime = [];
% TOPmultTimeDB = [];
% TOPmultTimeDBBW = [];
% TOPmultTimeLocal = [];
% TOPputResultTime = [];
% TOPSCALE = [];% figure;


DBsetup;
Tinfo = DB('DH_info');
nl = char(10);

for SCALE=20:1:20
myName = ['DH_' num2str('%02d',SCALE) '_'];
DBsetup;
if DoGenData
    pDB02_FileTEST
    pDB03_AssocTEST
end
if DoDeleteDB
    deleteForce(Tadj); 
    deleteForce(TadjDeg); 
    deleteForce(Tedge); 
    deleteForce(TedgeDeg);
end
if DoPutDB
    
    % Pre-Split here
    
    pDB05_SetupTEST
    pDB06_AdjInsertTEST
    
    G = DBaddJavaOps('edu.mit.ll.graphulo.MatlabGraphulo','instance','localhost:2181','root','secret');
    G.Compact(getName(Tadj);
    G.Compact(getName(TadjDeg));
    
    
    if exist('Tinfo','var')
        Ainfo = Assoc('','','');
        Ainfo = Ainfo + Assoc([getName(Tadj) nl],['SCALE' nl],[num2str(SCALE) nl]);
        Ainfo = Ainfo + Assoc([getName(Tadj) nl],['nodes' nl],[num2str( NumStr(Row(TadjDeg(:,:))) ) nl]);
        Ainfo = Ainfo + Assoc([getName(Tadj) nl],['EdgesPerVertex' nl],[num2str(EdgesPerVertex) nl]);
        Ainfo = Ainfo + Assoc([getName(Tadj) nl],['edges' nl],[num2str(nnz(Tadj)) nl]);
        put(Tinfo,Ainfo);
        
    end
end

% TOPgetTime = [TOPgetTime getTime];
% TOPmultTimeDB = [TOPmultTimeDB multTimeDB];
% TOPmultTimeDBBW = [TOPmultTimeDBBW multTimeDBBW];
% TOPmultTimeLocal = [TOPmultTimeLocal multTimeLocal];
% TOPputResultTime = [TOPputResultTime putResultTime];
% TOPSCALE = [TOPSCALE SCALE];
end

% experiment
% 1. Time Matlab in-memory
% 2. Time Accumulo TableMult compact
% 3. Time Accumulo TableMult BatchWrite
% 4. 
% Atime = Assoc('','','');
% for i=1:numel(TOPSCALE)
%     Atime = Atime + Assoc('getTime,multTimeDB,multTimeDBBW,multTimeLocal,',num2str(repmat(TOPSCALE(i),1,4),'%d,'), ...
%                           [TOPgetTime(i), TOPmultTimeDB(i), TOPmultTimeDBBW(i), TOPmultTimeLocal(i)]);
% end
%Assoc2CSV(Atime,char(10),',',['AtimeTableMult' datestr(datetime,'yy-mm-dd-HH-MM-SS') '.csv']);

% figure;
% plot(TOPSCALE,TOPmultTimeDB,'r*',TOPSCALE,TOPmultTimeDBBW,'g*',TOPSCALE,TOPmultTimeLocal,'b*', ...
%      TOPSCALE,TOPgetTime,'k*',TOPSCALE,TOPputResultTime,'c*');
% xlabel('SCALE'); ylabel('time'); 
% legend('multTimeDB','multTimeDBBW','multTimeLocal','getTime','putResultTime');



% Emergency code
%G.CancelCompact(rname);
