%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Read entity data and organize into sparse associative array.
%
% Entity data are derived summaries obtained by from automated
% entity extraction algorithms applied to <1% of the NIST Rueters Corpus.
% See: http://trec.nist.gov/data/reuters/reuters.html
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('on');  more('off');                      % Turn on echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

E = ReadCSV('Entity.csv');                     % Read CSV file (takes ~ 1 minute).
displayFull(E(1:5,:));                         % Show first rows in tabular form.

[row col doc]      = E(:,'doc,');              % Get doc column.
[row col entity]   = E(:,'entity,');           % Get entity column.
[row col position] = E(:,'position,');         % Get position column.
[row col type]     = E(:,'type,');             % Get type column.

typeEntity = CatStr(type,'/',entity);          % Interleave type and entity strings.
E = Assoc(doc,typeEntity,position);            % Create sparse edge matrix.

save('Entity.mat','E');                        % Save to binary file.

displayFull(E(1,:));                           % Show first row in tabular form.

disp(E);                                       % Show structure.

spy(transpose(E));                             % Plot the transpose.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
