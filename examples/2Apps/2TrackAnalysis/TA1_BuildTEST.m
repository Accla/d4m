%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% General approach to computing tracks from entity edge data.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('on'); more('off')          % Turn on echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
load('Entity.mat');              % Load edge incidence matrix.
Es = E;                          % Copy original.
E = dblLogi(E);                  % Convert to numeric.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Show general purpose method for building tracks.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

p = StartsWith('PERSON/,');      % Set entity range.
t = StartsWith('TIME/,');        % Set time range.
x = StartsWith('LOCATION/,');    % Set spatial range.

% Limit to edges with all three.
E3 = E(Row( sum(E(:,p),2) & sum(E(:,t),2) & sum(E(:,x),2) ),[p t x]);

% Collapse to get unique time and space for each edge and get triples..
[edge time tmp]  = find(  val2col(col2type(E3(:,t),'/'),'/')  );
[edge space tmp] = find(  val2col(col2type(E3(:,x),'/'),'/')  );

Etx = Assoc(edge,time,space);     % Combine edge, time and space.
Ext = Assoc(edge,space,time);     % Combine edge, space and time.

% Construct time tracks with matrix multiply.
At = CatValMul(transpose(Etx),num2str(E3(:,p)));   
figure; spy(At.');                % Plot track.

% Construct space tracks with matrix multiply.
Ax = CatValMul(transpose(Ext),num2str(E3(:,p)));   
figure; spy(Ax.');                % Plot track.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
