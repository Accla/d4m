%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Generate a Kronecker graph and save to data files.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('off'); more('off')                        % Turn off echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Nfile = 8;                                       % Set the number of files to save to.

myFiles = 1:Nfile;                               % Set list of files.
%myFiles = global_ind(zeros(Nfile,1,map([Np 1],{},0:Np-1)));   % PARALLEL.

rout = '';  cin = '';  dout = zeros(0);  din = zeros(0);     % Initialize arrays for tallying.
for i = myFiles
  tic;
    fname = ['data/' num2str(i)];  disp(fname);  % Create filename.

    load([fname '.mat']);                        % Load associative array.

    Aout_i = sum(A,2);                           % Compute out degree.
    rout = [rout Row(Aout_i)];                   % Append row keys.
    dout = [dout ; full(Adj(Aout_i))];           % Append out degree counts.

    Ain_i = sum(A,1);                            % Compute in degree.
    cin = [cin Col(Ain_i)];                      % Append column keys.
    din = [din ; full(Adj(Ain_i)).'];            % Append in degree counts.
  toc
end

tic;
  Aout = Assoc(rout,1,dout,@sum);
  Ain = Assoc(1,cin,din,@sum);
degreeTime = toc;  disp(['Sum Time: ' num2str(degreeTime) ', Edges/sec: ' num2str((sum(dout)+sum(din))./degreeTime)]);

tic;                                             % Globally aggregate from different processsors.
%  Aout = gagg(Aout);   Ain = gagg(Ain);         % PARALLEL.
aggTime = toc;  disp(['Agg Time: ' num2str(aggTime) ', Edges/sec: ' num2str((sum(dout)+sum(din))./aggTime)]);

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

