%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Setup database tables.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('on'); more('off')                                   % Turn off echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

DBsetup;                                                  % Create binding to database and tables.

TadjDeg = addColCombiner(TadjDeg,'OutDeg,InDeg,','sum');  % Set accumulator columns.

TedgeDeg = addColCombiner(TedgeDeg,'Degree,','sum');      % Set accumulator columns.

% Set splits.
%addSplits(Tedge1,'25,5,75,');
%addSplits(Tedge2,'Out/,');
%addSplits(Tedge2,'In/26,Out/,Out/26,');
%addSplits(Tedge2,'In/21,In/34,Out/,Out/21,Out/34,');
%addSplits(Tedge2,'In/164,In/262,In/45,Out/,Out/164,Out/262,Out/45,');
%addSplits(Tedge2,'In/133,In/2097,In/262,In/34,In/565,Out/,Out/133,Out/2097,Out/262,Out/34,Out/565,');

% Check splits.


% Delete all tables.
%delete(Tadj); delete(TadjDeg); delete(Tedge); delete (TedgeDeg);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

