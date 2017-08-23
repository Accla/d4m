% Benchmark Assoc against GraphAnalysis.org benchmark.

% Create data set.
SCALE = 18;
Nfiles = 1;
myFiles = 1:Nfiles;

TABLEDELETE=1;  % Delete table before and after.

PARALLEL=0;
Fmap = 0;
if PARALLEL==1
  Nfiles = Nfiles*Np;
  Fmap = map([Np 1],{},0:Np-1);
  myFiles = global_ind(zeros(Nfiles,1,Fmap))
end

%--------------------------------------------------------------------------
% Preamble.
%--------------------------------------------------------------------------

% Start overall run time for RUN_graphAnalysis.m (not performance timing).
OverallStartTime = clock;  

format long g % set the terminal dump (float or fixed) to print 15 digits. 
fprintf('\nHPCS SSCA #2 Graph Analysis Executable Specification Release 1.0,');
fprintf('\nbased on Written Spec. v2.2:  Running...\n\n');



%--------------------------------------------------------------------------
% Scalable Data Generator.
%--------------------------------------------------------------------------

fprintf('\nScalable Data Generator - genScalData() beginning execution...\n');

disp(['SCALE ' num2str(SCALE)]);
tic;
  % Generate the edge set.
  [E V] = genRmatData(SCALE);
genGraphTime = toc;  disp(['Graph gen time: ' num2str(genGraphTime) ]);

tic;
  % Save strings that don't change.
  startVertexStr = sprintf('%d ',E.StartVertex.');
  endVertexStr = sprintf('%d ',E.EndVertex.');
  weightStr = sprintf('%d ',E.Weight);
  qCol = sprintf('%d ',E.EndVertex(100).');
strGenTime = toc; disp(['String gen time: ' num2str(strGenTime)]);


clear('E');

tic;
  A = Assoc(startVertexStr,endVertexStr,weightStr);
  M = nnz(A);  s = size(A);  N = s(1);
assocConstructTime = toc; disp(['Assoc construct time: ' num2str(assocConstructTime)]);
assocPutRate = M / assocConstructTime; disp(['Assoc put rate: ' num2str(assocPutRate)]);

disp(['Loc Rows: ' num2str(s(1)) '  Loc Cols: ' num2str(s(2)) '  Loc Vals: ' num2str(M)]);
%disp(A)

row = str2num(Row(A));

if 1
  % Create a DB.
  DBsetup;
  T = DB('GraphAnalysisTEST');
%  T = DB('GraphAnalysisTEST','GraphAnalysisTESTt');
  if TABLEDELETE
    deleteForce(T);
    T = DB('GraphAnalysisTEST');
%    T = DB('GraphAnalysisTEST','GraphAnalysisTESTt');
  end
  DB

  % Make offset copies of starVertex to increase size of graph.
  %for i = 1:Nfiles
  totPutTime = 0;
  for i = myFiles
    tic;
      rowStr  = sprintf('%d ',(row + (i-1)*N));
%      rowStr  = sprintf('%d ',(row + Pid*N));
      A = putRow(A,rowStr);
    rowGenTime = toc; disp([num2str(i) ' File gen time: ' num2str(rowGenTime)]);
    tic;
      put(T,A);
    putTime = toc; disp(['DB put time: ' num2str(putTime)]);
    putRate = M / putTime; disp(['DB put rate: ' num2str(putRate)]);
    totPutTime = totPutTime + putTime;
  end
  disp(['DB total put time: ' num2str(totPutTime)]);
  totPutRate = Nfiles*M / totPutTime; disp(['DB total put rate: ' num2str(totPutRate)]);
  disp(['Tot Rows: ' num2str(Nfiles*s(1)) '  Tot Cols: ' num2str(s(2)) '  Tot Vals: ' num2str(Nfiles*M)]);

  tic;
%    ATc = T(:,qCol);
    ATc = T(qCol,:);
  getTime = toc; disp(['DB col get time: ' num2str(getTime)]);
  disp(['Values in col: ' num2str(nnz(ATc))]);
  tic;
    ATr = T(qCol,:);
  getTime = toc; disp(['DB row get time: ' num2str(getTime)]);
  disp(['Values in row: ' num2str(nnz(ATr))]);

  if TABLEDELETE
    deleteForce(T);
  end

end

tic;
  Ac = A(:,qCol);
getTime = toc; disp(['Assoc col get time: ' num2str(getTime)]);
disp(['Values in col: ' num2str(nnz(Ac))]);
tic;
  Ar = A(qCol,:);
getTime = toc; disp(['Assoc row get time: ' num2str(getTime)]);
disp(['Values in row: ' num2str(nnz(Ar))]);


save([mfilename '.mat'],'-v6','ATc','ATr','Ac','Ar')

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

