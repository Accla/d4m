%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Benchmark Accumulo TableMult on Adjacency matrix with itself.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
DBsetup;                                    % Create binding to database.
echo('off'); more('off')                     % Turn off echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Use entire Adjacency table.
%T = DB('Tadj')

tname = getName(Tadj);
numAdj = nnz(Tadj);
fprintf('Tadj %s #entries: %d\n',tname,numAdj);
rname = [tname 'X' tname '_t'];
% test existence: strfind(ls(DB),[rname ' '])
Tres = DB(rname);
deleteForce(Tres);
% Removes overhead of creating table to hold results from timing.
Tres = DB(rname);
% Bugfix ACCUMULO-3645
deleteTriple(Tres,'z,','z,');

tic;
G = DBaddJavaOps('edu.mit.ll.graphulo.MatlabGraphulo','instance','localhost:2181','root','secret');
G.TableMultTest(rname,tname,tname);
multTimeDB = toc; fprintf('DB TableMult Time, P=R compaction: %f\n',multTimeDB);
fprintf('Result Table %s #entries: %d\n',rname,nnz(Tres));


deleteForce(Tres);
Tres = DB(rname);
ptable='spt'; % Stored procedure table
Tp = DB(ptable);
deleteForce(Tp);
Tp = DB(ptable);
deleteTriple(Tp,'y,','y,');
tic;
G = DBaddJavaOps('edu.mit.ll.graphulo.MatlabGraphulo','instance','localhost:2181','root','secret');
G.TableMultTest(ptable,tname,tname,'',rname,true);
multTimeDBBW = toc; fprintf('DB TableMult Time, BatchWrite to R: %f\n',multTimeDBBW);
fprintf('Result Table %s #entries: %d\n',rname,nnz(Tres));


tic;
A = str2num(Tadj(:,:)); % All data
getTime = toc; fprintf('Time to scan & str2num entire adj table: %f\n', getTime);

tic;
AAt = A*A.';
multTimeLocal = toc; fprintf('Local Assoc Time to multiply adj table with itself tranposed: %f\n',multTimeLocal);


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



