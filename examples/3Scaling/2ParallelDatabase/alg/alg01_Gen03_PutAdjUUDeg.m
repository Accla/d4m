%function alg01_Gen_PutAdjUUDeg(DB, G, tname, TNadjUUDeg, infoFunc)
util_Require('DB G TNadjUUDeg tname infoFunc ND')
% tname = 'DH_pg10_20160331'; TadjUU = 'DH_pg10_20160331_TadjUU'; infoFunc = @disp;

dname = [pwd filesep tname];

LSDB = ls(DB);
if StrSearch(LSDB,[TNadjUUDeg ' ']) >= 1
    TadjUUDeg = DB(TNadjUUDeg); 
    if exist('DELETE_TABLE_TRIGGER','var') && DELETE_TABLE_TRIGGER
        deleteForce(TadjUUDeg);
    else
        delete(TadjUUDeg);
    end
end

% TadjUU = DB(TNadjUU);
TadjUUDeg = DB(TNadjUUDeg);
G.setUIntegerLexicoder(TNadjUUDeg);


Nfile = size(dir([dname filesep '*.A.mat']),1);
if (Nfile == 0)
    error('No data files; please run alg01_Gen_Assoc first');
end

specialTotal = 0;
myFiles = 1:Nfile;                               % Set list of files.
%myFiles = global_ind(zeros(Nfile,1,map([Np 1],{},0:Np-1)));      % PARALLEL.

for i = myFiles
  tic;
    fname = [dname filesep num2str(i)];  disp(fname);  % Create filename.

    load([fname '.A.mat']);                        % Load associative array.
    A = Abs0(A);
    if ND
        A = NoDiag(A);
    end
    % put(TadjUU,num2str(A));                        % Insert associative array.
    % put(TadjUU,num2str(A.'));                        % Insert associative array.

    A = sum(A + A.',2);
    put(TadjUUDeg, putCol(num2str(A), '1,'));

    thisTotal = Val(sum(A .* A - A,1)) ./ 2;
    specialTotal = specialTotal + thisTotal;



    % Aout_i = putCol(num2str(sum(A,2)),'OutDeg,');   % Compute out degree.
    % Ain_i = putCol(num2str(sum(A,1)).','InDeg,');     % Compute in degree.

    % put(TadjDeg,Aout_i);                         % Accumulate out degrees.
    % put(TadjDeg,Ain_i);                          % Accumulate in degrees.
  insertTime = toc;  disp(['Time: ' num2str(insertTime) ', Edges/sec: ' num2str((nnz(A))./insertTime)]); 
end

G.setDegreeTableTotal(TNadjUUDeg, specialTotal)

tic;
G.Compact(TNadjUUDeg);
compactTime = toc;

nl = char(10);
Ainfo = Assoc('','','');
Ainfo = Ainfo + Assoc([tname nl],['tInsertAdjUUDeg' nl],[num2str(insertTime) nl]);
Ainfo = Ainfo + Assoc([tname nl],['tCompactAdjUUDeg' nl],[num2str(compactTime) nl]);
Ainfo = Ainfo + Assoc([tname nl],['NoDiag' nl],[num2str(ND) nl]);
Ainfo = Ainfo + Assoc([tname nl],['specialTotal' nl],[num2str(specialTotal) nl]);
Ainfo
infoFunc(Ainfo);
        
