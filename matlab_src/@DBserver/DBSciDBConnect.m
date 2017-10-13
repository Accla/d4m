%DBsciDBConnect: Connects to a SciDB instance
%Database internal function used by ls.
%  Usage:
%    conn = DBSciDBConnect(DB)
%  Inputs:
%    DB = database object with a binding to a specific database
% Outputs:
%    conn = SQL connector object

function [stat, sessionID] = DBSciDBConnect(DB)

cmd = ['wget -q -O - "' DB.host 'new_session" --http-user=' DB.user ' --http-password=' DB.pass];

[stat, sessionID] = system(cmd);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu),
%                    Dr. Siddharth Samsi (sid@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2015> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

