%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Tests the JSON Parser

D4MqueryJSONformat;

tic
queryA1 = parseJSON(QueryRequestGetTrackNamesJSON);
queryA2 = parseJSON(QueryRequestMHtrackJSON);
toc

tic
%queryB1 = parseJSON_3rdParty(QueryRequestGetTrackNamesJSON);
queryB2 = parseJSON(QueryRequestMHtrackJSON);
toc




%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
