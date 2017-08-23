

%%%%%%%%%%%%
% Sets up and runs D4M query service.

% Setup data.
Reuters3parse;     % Parse reuters data.
Reuters3trackParse;  % Parse tracks from Reuters data.

DBsetup;
Reuters3insert;      % Insert doc/entity into DB.
Reuters3trackInsert;  % Insert tracks into DB.

if 1

% Create globals for query functions.
global D4MqueryGlobal
D4MqueryGlobal.DB = DB;
D4MqueryGlobal.DbA = D4MqueryGlobal.DB('ReutersDataTEST','ReutersDataTESTt');
D4MqueryGlobal.DbTr = D4MqueryGlobal.DB('ReutersTracksTEST','ReutersTracksTESTt');


% Start Java service for handling queries.
% which will call D4MwebQueryResponse.

%minLength = 100;
%trackNames = D4MqueryGetTrackNames(minLength);
%
% nl = char(10);
% p = ['NE_PERSON_GENERIC/john howard' nl];
% p = ['NE_PERSON_GENERIC/james sanders' nl];
% p = ['NE_PERSON_GENERIC/paula gutierrez' nl];
% MHtrack = D4MqueryMHtrack(p);
%
% p = ['NE_PERSON_GENERIC/john howard' nl];
% t = ['TIME/199606300000' nl];
% l = ['NE_LOCATION/asia' nl];
% docList = D4MqueryTrackPointDocList(p,t,l);
%
% p = ['NE_PERSON_GENERIC/john howard' nl];
% pp = ['NE_PERSON_GENERIC/irene patricia cunningham' nl];
% multiTr = D4MqueryMultiTrack([p pp]);
%
%
% t = ['TIME/199606300000' nl ':' nl 'TIME/199705140000' nl];
% l = ['NE_LOCATION/asia' nl];
% trackList = D4MqueryTrackWindow(t,l);


% Load in JSON format of queries.

D4MqueryJSONformat;

QueryResponseGetTrackNamesJSON = D4MwebQueryResponse(QueryRequestGetTrackNamesJSON)
QueryResponseMHtrackJSON = D4MwebQueryResponse(QueryRequestMHtrackJSON)

deleteForce(D4MqueryGlobal.DbA);
deleteForce(D4MqueryGlobal.DbTr);

save([mfilename '.mat'],'-v6','QueryResponseGetTrackNamesJSON','QueryResponseMHtrackJSON');

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

