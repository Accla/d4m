function tname = alg01_Gen_File(SCALE, SEED, EdgesPerVertex, Nfile, myPrefix, infoFunc)
% Generate row, col, val files from Graph500 generator.
% alg01_Gen_File(10, 20160331, 16, 8, 'DH_', @util_UpdateInfo)
% or @(Ainfo) put(Tinfo, Ainfo)


% SCALE = 10; myPrefix = 'DH_'; SEED = 20160331; EdgesPerVertex = 16; Nfile = 8; infoFunc = @disp;

rand('seed',SEED)
tname = [myPrefix 'pg' num2str(SCALE,'%02d') '_' num2str(SEED)];
dname = [pwd filesep tname];

% DBsetup;
% Tinfo = DB('DH_info','DH_infoT');

% SCALE = 12;   EdgesPerVertex = 16;               % Set algorithm inputs.
Nmax = 2.^SCALE;                                 % Max vertex ID.
M = EdgesPerVertex .* Nmax;                      % Total number of edges.

% Nfile = 8;                                       % Set the number of files to save to.
[~,~,~] = mkdir(dname);                         % Ensure directory exists

myFiles = 1:Nfile;                               % Set list of files.
%myFiles = global_ind(zeros(Nfile,1,map([Np 1],{},0:Np-1)));   % PARALLEL.

for i = myFiles
  tic;
    fname = [dname filesep num2str(i)];  disp(fname);  % Create filename.

    %rand('seed',i);                              % Set random seed to be unique for this file.
    [v1,v2] = KronGraph500NoPerm(SCALE,EdgesPerVertex./Nfile);       % Generate data.
 
    rowStr = sprintf('%d,',v1);                                      % Convert to strings.
    colStr = sprintf('%d,',v2);
    valStr = repmat('1,',1,numel(v1));

    % Open files, write data, and close files.
    fidRow=fopen([fname 'r.txt'],'w'); fidCol=fopen([fname 'c.txt'],'w'); fidVal =fopen([fname 'v.txt'],'w');
    fwrite(fidRow,rowStr);             fwrite(fidCol,colStr);             fwrite(fidVal,valStr);
    fclose(fidRow);                    fclose(fidCol);                    fclose(fidVal);
  fileTime = toc;  disp(['Time: ' num2str(fileTime) ', Edges/sec: ' num2str(numel(v1)./fileTime)]);
end


nl = char(10);
% t5s = tic;
Ainfo = Assoc('','','');
Ainfo = Ainfo + Assoc([tname nl],['SCALE' nl],[num2str(SCALE) nl]);
% Ainfo = Ainfo + Assoc([tname nl],['nodes' nl],[num2str( NumStr(Row(TadjDeg(:,:))) ) nl]);
Ainfo = Ainfo + Assoc([tname nl],['EdgesPerVertex' nl],[num2str(EdgesPerVertex) nl]);
% Ainfo = Ainfo + Assoc([tname nl],['edges' nl],[num2str(nnz(Tadj)) nl]);
% t5d = toc(t5s);
Ainfo = Ainfo + Assoc([tname nl],['tFile' nl],[num2str(fileTime) nl]);
% Ainfo = Ainfo + Assoc([getName(Tadj) nl],['tAssoc' nl],[num2str(t2d) nl]);
% Ainfo = Ainfo + Assoc([getName(Tadj) nl],['tInsert' nl],[num2str(t3d) nl]);
% Ainfo = Ainfo + Assoc([getName(Tadj) nl],['tMerge' nl],[num2str(t4d) nl]);
% Ainfo = Ainfo + Assoc([getName(Tadj) nl],['tStat' nl],[num2str(t5d) nl]);
% put(Tinfo,Ainfo);
infoFunc(Ainfo);
        
end