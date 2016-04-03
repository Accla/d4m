function alg01_Gen_PutAdj(DB, G, tname, TNadj, TNadjT, TNadjDeg, infoFunc)
% Put adjacency matrix from Assoc files into Accumulo.
% Sum colliding entries.

% tname = 'DH_pg10_20160331'; Tadj = 'DH_pg10_20160331_Tadj'; TadjDeg = 'DH_pg10_20160331_Tadj'; infoFunc = @disp;

dname = [pwd filesep tname];

LSDB = ls(DB);
if StrSearch(LSDB,[TNadj ' ']) >= 1
    Tadj = DB(TNadj, TNadjT); 
    delete(Tadj);
end
if StrSearch(LSDB,[TNadjDeg ' ']) >= 1
    TadjDeg = DB(TNadjDeg); 
    delete(TadjDeg);
end

Tadj = DB(TNadj, TNadjT); 
TadjDeg = DB([tname '_TgraphAdjDeg']);
TadjDeg = addColCombiner(TadjDeg,'OutDeg,InDeg,','sum');  % Set accumulator columns.

% For summing up values
Tadj1 = DB(TNadj);
Tadj2 = DB(TNadjT);
addColCombiner(Tadj1,':,','sum');
addColCombiner(Tadj2,':,','sum');


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

    put(Tadj,num2str(A));                        % Insert associative array.

    Aout_i = putCol(num2str(sum(A,2)),'OutDeg,');   % Compute out degree.
    Ain_i = putCol(num2str(sum(A,1)).','InDeg,');     % Compute in degree.

    put(TadjDeg,Aout_i);                         % Accumulate out degrees.
    put(TadjDeg,Ain_i);                          % Accumulate in degrees.
  insertTime = toc;  disp(['Time: ' num2str(insertTime) ', Edges/sec: ' num2str((2.*nnz(A)+nnz(Aout_i)+nnz(Ain_i))./insertTime)]); 
end
disp(['Table entries: ' num2str(nnz(Tadj))]);

tic;
G.Compact(TNadj);
G.Compact(TNadjT);
compactTime = toc;

% can safely remove combiner now that we compacted
revokeCombiningColumns(Tadj1,':,')
revokeCombiningColumns(Tadj2,':,')


nl = char(10);
Ainfo = Assoc('','','');
Ainfo = Ainfo + Assoc([tname nl],['tInsertAdjAndDeg' nl],[num2str(insertTime) nl]);
Ainfo = Ainfo + Assoc([tname nl],['tCompactAdj' nl],[num2str(compactTime) nl]);
infoFunc(Ainfo);
        
end