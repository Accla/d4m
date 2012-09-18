%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Show different ways to index an associative array.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('on'); more('off');          % Turn on echoing and paging.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

A = ReadCSV('A.csv');            % Read CSV file into associative array.

A1r = A('a,b,',:);               % Get rows a and b.
A2r = A('a *,',1:3);             % Get rows containing a and columns 1 thru 3.
A3r = A('a,:,b,',:);             % Get rows a to b.
A4r = A(StartsWith('a,c,'),:);   % Get rows starting with a or c.
A1c = A(:,'a,b,');               % Get cols a and b.
A2c = A(1:3,'a *,');             % Get rows 1 thru 3 and cols containing a.
A3c = A(:,'a,:,b,');             % Get cols a to b.
A4c = A(:,StartsWith('a,c,'));   % Get cols starting with a or b.
A1v = (A < 'b,');                % Get all values less than b.

displayFull(A1v);                % Show in tabular form.

% save([mfilename '.mat'],'-v6','A1r','A2r','A3r','A1c','A2c','A3c','A1v')

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
