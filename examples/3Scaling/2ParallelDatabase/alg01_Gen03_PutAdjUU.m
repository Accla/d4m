%function alg01_Gen_PutAdjUU(DB, G, tname, TNadjUU, infoFunc)
% Insert undirected, unweighted adjacency table and compact it.
% alg01_Gen_PutAdj('DH_pg10_20160331', DB('DH_pg10_20160331_TadjUU', @util_UpdateInfo)
util_Require('DB G TNadjUU tname infoFunc')
% tname = 'DH_pg10_20160331'; TadjUU = 'DH_pg10_20160331_TadjUU'; infoFunc = @disp;

dname = [pwd filesep tname];

LSDB = ls(DB);
if StrSearch(LSDB,[TNadjUU ' ']) >= 1
    TadjUU = DB(TNadjUU); 
    if exist('DELETE_TABLE_TRIGGER','var') && DELETE_TABLE_TRIGGER
        deleteForce(TadjUU);
    else
        delete(TadjUU);
    end
end

TadjUU = DB(TNadjUU);

% DBsetup;
% Tinfo = DB('DH_info','DH_infoT');

Nfile = size(dir([dname filesep '*.A.mat']),1);
if (Nfile == 0)
    error('No data files; please run alg01_Gen_Assoc first');
end


myFiles = 1:Nfile;                               % Set list of files.
%myFiles = global_ind(zeros(Nfile,1,map([Np 1],{},0:Np-1)));      % PARALLEL.

for i = myFiles
  tic;
    fname = [dname filesep num2str(i)];  disp(fname);  % Create filename.

    load([fname '.A.mat']);                        % Load associative array.
    A = Abs0(A);
    put(TadjUU,num2str(A));                        % Insert associative array.
    put(TadjUU,num2str(A.'));                        % Insert associative array.

    % Aout_i = putCol(num2str(sum(A,2)),'OutDeg,');   % Compute out degree.
    % Ain_i = putCol(num2str(sum(A,1)).','InDeg,');     % Compute in degree.

    % put(TadjDeg,Aout_i);                         % Accumulate out degrees.
    % put(TadjDeg,Ain_i);                          % Accumulate in degrees.
  insertTime = toc;  disp(['Time: ' num2str(insertTime) ', Edges/sec: ' num2str(2.*(nnz(A))./insertTime)]); 
end
disp(['Table entries: ' num2str(nnz(TadjUU))]);

tic;
G.Compact(TNadjUU);
compactTime = toc;

nl = char(10);
Ainfo = Assoc('','','');
Ainfo = Ainfo + Assoc([tname nl],['tInsertAdjUU' nl],[num2str(insertTime) nl]);
Ainfo = Ainfo + Assoc([tname nl],['tCompactAdjUU' nl],[num2str(compactTime) nl]);
infoFunc(Ainfo);
        
