%%%%%%%%%%%%
% Sets ups and runs D4M query service.

% Setup data.
%Reuters3parse;     % Parse reuters data.
%Reuters3insert;      % Insert doc/entity into DB.
%Reuters3trackParse;  % Parse tracks from Reuters data.
%Reuters3trackInsert;  % Insert tracks into DB.

% Create globals for query functions.
global D4MqueryGlobal
D4MqueryGlobal.DB =  DBserver('f-2-9.llgrid.ll.mit.edu','cloudbase');
D4MqueryGlobal.DbA = D4MqueryGlobal.DB('ReutersData','ReutersDataT');
D4MqueryGlobal.DbTr = D4MqueryGlobal.DB('ReutersTracks','ReutersTracksT');


% Start Java service for handling queries.
% trackNames = D4MqueryGetTrackNames();
% MHtrack = D4MqueryMHtrack(p);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%



