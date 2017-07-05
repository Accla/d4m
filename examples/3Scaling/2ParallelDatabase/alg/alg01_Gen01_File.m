%function tname = alg01_Gen01_File(SCALE, SEED, EdgesPerVertex, Nfile, myPrefix, infoFunc)
% Generate row, col, val files from Graph500 generator.
% alg01_Gen_File(10, 20160331, 16, 8, 'DH_', @util_UpdateInfo)
% or @(Ainfo) put(Tinfo, Ainfo)
util_Require('SCALE SEED EdgesPerVertex Nfile tname infoFunc')

% SCALE = 10; myPrefix = 'DH_'; SEED = 20160331; EdgesPerVertex = 16; Nfile = 8; infoFunc = @disp;

rand('seed',SEED)
%tname = [myPrefix 'pg' num2str(SCALE,'%02d') '_' num2str(SEED)];
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
skipped = false;

for i = myFiles
    fname = [dname filesep num2str(i)];  % Create filename.
    if exist([fname 'r.txt'],'file') && exist([fname 'c.txt'],'file') && exist([fname 'v.txt'],'file')
        % disp(['Skipping text ' fname]);
        skipped = true;
    else
        disp(fname);  
  tic;

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
end

if ~skipped
nl = char(10);
Ainfo = Assoc('','','');
Ainfo = Ainfo + Assoc([tname nl],['SCALE' nl],[num2str(SCALE) nl]);
Ainfo = Ainfo + Assoc([tname nl],['EdgesPerVertex' nl],[num2str(EdgesPerVertex) nl]);
Ainfo = Ainfo + Assoc([tname nl],['tFile' nl],[num2str(fileTime) nl]);
infoFunc(Ainfo);
end        
