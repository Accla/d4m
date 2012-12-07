%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Generate a Kronecker graph and save to data files.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('off'); more('off')                        % Turn off echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Nfile = 8;                                       % Set the number of files to save to.

myFiles = 1:Nfile;                               % Set list of files.
%myFiles = global_ind(zeros(Nfile,1,map([Np 1],{},0:Np-1)));   % PARALLEL.
%agg(zeros(Nfile,1,map([Np 1],{},0:Np-1)));       % PARALLEL.

Aout = Assoc('','','');  Ain = Assoc('','','');  % Initialize arrays for tallying.

for i = myFiles
  tic;
    fname = ['data/' num2str(i)];  disp(fname);  % Create filename.
    load([fname '.mat']);                        % Load associative arrays.
    Aout = Aout + sum(A,2);                      % Compute out degree.
    Ain  = Ain  + sum(A,1);                      % Compute in degree.
  sumTime = toc;  disp(['Sum Time: ' num2str(sumTime) ', Edges/sec: ' num2str(0.5.*(nnz(Adj(Aout))+nnz(Adj(Ain)))./sumTime)]);
end

tic;                                             % Globally aggregate from different processsors.
%  Aout = gagg(Aout);   Ain = gagg(Ain);          % PARALLEL.
aggTime = toc;  disp(['Agg Time: ' num2str(aggTime) ', Edges/sec: ' num2str(0.5.*(sum(Adj(Aout))+sum(Adj(Ain)))./aggTime)]);

figure;  loglog(full(sparse(Adj(Aout),1,1)),'o');  xlabel('out degree');   % Plot out degree.
figure;  loglog(full(sparse(Adj(Ain),1,1)),'o');   xlabel('in degree');    % Plot in degree.


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

