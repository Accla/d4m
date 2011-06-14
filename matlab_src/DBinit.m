% Set up path for D4M binding to databases.

% Get directory that this file is in.
d4m_home = fileparts(fileparts(mfilename('fullpath')));

if 1
% Add files included in LLONLY distribution.
javaaddpath([d4m_home '/conf']);
javaaddpath([d4m_home '/lib/d4m_api-1.1.1.jar']);


% USER: Add external files *NOT* included in LLONLY distribution.
% Find the files and put them in lib or change these
% entries to point to these files.

javaaddpath([d4m_home '/lib/commons-logging-1.0.4.jar']);
javaaddpath([d4m_home '/lib/commons-collections-3.2.jar']);
javaaddpath([d4m_home '/lib/commons-configuration-1.5.jar']);
javaaddpath([d4m_home '/lib/commons-io-1.4.jar']);
javaaddpath([d4m_home '/lib/commons-lang-2.4.jar']);
javaaddpath([d4m_home '/lib/log4j-1.2.15.jar']);
javaaddpath([d4m_home '/lib/hadoop-0.20.2-core.jar']);
javaaddpath([d4m_home '/lib/hadoop-0.20.2-tools.jar']);
javaaddpath([d4m_home '/lib/cloudbase-core-1.2.0.jar']);
javaaddpath([d4m_home '/lib/cloudbase-start-1.2.0.jar']);
javaaddpath([d4m_home '/lib/cloudbase-server-1.2.0.jar']);
javaaddpath([d4m_home '/lib/thrift-0.2.jar']);
javaaddpath([d4m_home '/lib/slf4j-api-1.6.1.jar']);
javaaddpath([d4m_home '/lib/slf4j-log4j12-1.6.1.jar']);
javaaddpath([d4m_home '/lib/zookeeper-3.2.2.jar']);
javaaddpath([d4m_home '/lib/json.jar']);

end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer:  Mr. Craig McNally (cmcnally@ll.mit.edu),
%   Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
% FOUO
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

