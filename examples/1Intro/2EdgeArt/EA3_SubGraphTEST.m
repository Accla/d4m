%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Show different ways to index an associative array.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('on'); more('off');          % Turn on echoing and paging.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

E = ReadCSV('Edge.csv');                       % Read CSV file into associative array.
Ev = dblLogi( E(:, StartsWith('V,')) );        % Get vertices and convert to numbers.

EvO = Ev(StartsWith('O,'),:);                  % Get orange edges.
EvG = Ev(StartsWith('G,'),:);                  % Get green edges.

AvOG = transpose(EvO) * EvG;                   % Compute vertex adjacency graph.
       disp(AvOG);                             % Show empty structure.

AeOG = EvO * transpose(EvG);                   % Compute edge adjacency graph.
displayFull(AeOG);                             % Show in tabular form.

AeOG = CatKeyMul(EvO,transpose(EvG));          % Compute edge adjacency graph preserving pedigree.
displayFull(AeOG);                             % Show in tabular form.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
