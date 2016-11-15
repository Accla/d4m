
DoGenData = true;
EdgesPerVertex = 16;
DoDeleteDB = true;
DoPutDB = true;
DONUM = 1;
if DONUM == 1
rand('seed',20150507); % used for DHB tables
elseif DONUM == 2
rand('seed',20140507); % used for DH tables
end
%%
getTime = 0; multTimeDB = 0;

% TOPgetTime = [];
% TOPmultTimeDB = [];
% TOPmultTimeDBBW = [];
% TOPmultTimeLocal = [];
% TOPputResultTime = [];
% TOPSCALE = [];% figure;


DBsetup;
Tinfo = DB('DH_info','DH_infoT');
nl = char(10);

for SCALE=12%10:18
if DONUM == 1
    myName = ['DHB_' num2str(SCALE,'%02d') '_'];
elseif DONUM == 2
    myName = ['DH_' num2str(SCALE,'%02d') '_'];
end
DBsetup;
if DoGenData
    t1s = tic;
    pDB02_FileTEST
    t1d = toc(t1s);
    t2s = tic;
    pDB03_AssocTEST
    t2d = toc(t2s);
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
    t3s = tic;
    pDB06_AdjInsertTEST
    t3d = toc(t3s);
    
    t4s = tic;
    putSplits(Tadj, '', '');
    %G = DBaddJavaOps('edu.mit.ll.graphulo.MatlabGraphulo','classdb54','classdb54.cloud.llgrid.txe1.mit.edu:2181','AccumuloUser','G@6K7fgwS46f^^1bWlDW-Lz@G');
    G.Compact(getName(Tadj));
    t4d = toc(t4s);
        
    if exist('Tinfo','var')
        t5s = tic;
        Ainfo = Assoc('','','');
        Ainfo = Ainfo + Assoc([getName(Tadj) nl],['SCALE' nl],[num2str(SCALE) nl]);
        Ainfo = Ainfo + Assoc([getName(Tadj) nl],['nodes' nl],[num2str( NumStr(Row(TadjDeg(:,:))) ) nl]);
        Ainfo = Ainfo + Assoc([getName(Tadj) nl],['EdgesPerVertex' nl],[num2str(EdgesPerVertex) nl]);
        Ainfo = Ainfo + Assoc([getName(Tadj) nl],['edges' nl],[num2str(nnz(Tadj)) nl]);
        t5d = toc(t5s);
        Ainfo = Ainfo + Assoc([getName(Tadj) nl],['tFile' nl],[num2str(t1d) nl]);
        Ainfo = Ainfo + Assoc([getName(Tadj) nl],['tAssoc' nl],[num2str(t2d) nl]);
        Ainfo = Ainfo + Assoc([getName(Tadj) nl],['tInsert' nl],[num2str(t3d) nl]);
        Ainfo = Ainfo + Assoc([getName(Tadj) nl],['tMerge' nl],[num2str(t4d) nl]);
        Ainfo = Ainfo + Assoc([getName(Tadj) nl],['tStat' nl],[num2str(t5d) nl]);
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
