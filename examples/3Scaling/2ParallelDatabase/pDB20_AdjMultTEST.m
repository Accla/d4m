%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Benchmark Accumulo TableMult on Adjacency matrix with itself.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
DBsetup;                                    % Create binding to database.
echo('off'); more('off')                     % Turn off echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Use entire Adjacency table.
%T = DB('Tadj')

tname = getName(Tadj);
Tadj2 = DB('DH2_TgraphAdj','DH2_TgraphAdjT');
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

% deleteForce(Tres);
% Tres = DB(rname);
ptable='spt'; % Stored procedure table
Tp = DB(ptable);
deleteForce(Tp);
Tp = DB(ptable);
deleteTriple(Tp,'y,','y,');
tic;
G = DBaddJavaOps('edu.mit.ll.graphulo.MatlabGraphulo','instance','localhost:2181','root','secret');
G.TableMultTest(ptable,tname,tname2,'',rname,true);
multTimeDBBW = toc; fprintf('DB TableMult Time, BatchWrite to R: %f\n',multTimeDBBW);
fprintf('Result Table %s #entries: %d\n',rname,nnz(Tres));


tic;
A = str2num(Tadj(:,:)); % All data
A2 = str2num(Tadj2(:,:));
getTime = toc; fprintf('Time to scan & str2num entire adj table: %f\n', getTime);

tic;
AAt = A.'*A2;
multTimeLocal = toc; fprintf('Local Assoc Time for %s * %s: %f\n',tname,tname2,multTimeLocal);


% Check correctness
AAtDB = str2num(Tres(:,:));
if ~isequal(AAt,AAtDB)
    fprintf('NOT EQUAL RESULTS LOCAL AND DB VERSION\n');
end


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%



