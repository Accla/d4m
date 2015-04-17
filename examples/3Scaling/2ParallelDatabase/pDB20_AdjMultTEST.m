%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Benchmark Accumulo TableMult on Adjacency matrix with itself.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
DBsetup;                                    % Create binding to database.
echo('off'); more('off')                     % Turn off echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Use entire Adjacency table.
%T = DB('Tadj')

Tadj = DB('DH_TgraphAdj'); %,'DH_TgraphAdjT');
tname = getName(Tadj);
Tadj2 = DB('DH2_TgraphAdj'); %,'DH2_TgraphAdjT');
tname2 = getName(Tadj2);
numAdj = nnz(Tadj);
numAdj2 = nnz(Tadj2);
fprintf('Tadj  %s #entries: %d\n',tname,numAdj);
fprintf('Tadj2 %s #entries: %d\n',tname2,numAdj2);
rname = [tname 'X' tname2 '_t'];
% test existence: strfind(ls(DB),[rname ' '])
Tres = DB(rname);
deleteForce(Tres);
% Removes overhead of creating table to hold results from timing.
Tres = DB(rname);
% Bugfix ACCUMULO-3645
%deleteTriple(Tres,'z,','z,');

% tic;
% G = DBaddJavaOps('edu.mit.ll.graphulo.MatlabGraphulo','instance','localhost:2181','root','secret');
% G.TableMultTest(rname,tname,tname);
% multTimeDB = toc; fprintf('DB TableMult Time, P=R compaction: %f\n',multTimeDB);
% fprintf('Result Table %s #entries: %d\n',rname,nnz(Tres));
multTimeDB = 0;

rowFilterGraphulo = '';%'1,:,';
rowFilterMat = '';%'1,:,zzz,'; % Assoc does not parse the same as Graphulo
colFilterAT = '';%'4,54,58,59,';
colFilterB = '';%'1025,1026,';

%putSplits(Tadj,'256,'); % determined empirically to divide roughly equally.


% deleteForce(Tres);
% Tres = DB(rname);
tic;
G = DBaddJavaOps('edu.mit.ll.graphulo.MatlabGraphulo','instance','localhost:2181','root','secret');
G.TableMult(tname,tname2,rname,rowFilterGraphulo,colFilterAT,colFilterB,500000,true);
multTimeDBBW = toc; fprintf('DB TableMult Time, BatchWrite to R: %f\n',multTimeDBBW);
fprintf('Result Table %s #entries: %d\n',rname,nnz(Tres));


tic;
if isempty(rowFilterMat)
    A = str2num(Tadj(:,:)); % All data
else
    A = str2num(Tadj(rowFilterMat,:)); 
end
if ~isempty(colFilterAT)
    A = A(:,colFilterAT);
end

if isempty(rowFilterMat)
    A2 = str2num(Tadj2(:,:)); % All data
else
    A2 = str2num(Tadj2(rowFilterMat,:));
end
if ~isempty(colFilterB)
    A2 = A2(:,colFilterB);
end
getTime = toc; fprintf('Time to scan & str2num A and B: %f\n', getTime);
%if (nnz(A) ~= 52403)
%    fprintf('WARNING: nnz(A) = %d\n',nnz(A));
%end

tic;
AAt = A.'*A2;
multTimeLocal = toc; fprintf('Local Assoc Time for %s * %s: %f\n',tname,tname2,multTimeLocal);

rnameman = [rname '_mat'];
TresMat = DB(rnameman);
deleteForce(TresMat);
TresMat = DB(rnameman);
tic;
put(TresMat, num2str(AAt));
putResultTime = toc; fprintf('Write result from Matlab to %s: %f\n',rnameman,putResultTime);


% Check correctness
AAtDB = str2num(Tres(:,:));
if ~isequal(AAt,AAtDB)
    fprintf('NOT EQUAL RESULTS LOCAL AND DB VERSION\n');
end


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dylan Hutchison (dhutchis@mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2015> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%



