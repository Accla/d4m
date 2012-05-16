% Set up path for D4M binding to databases.

% Get directory that this file is in.
d4m_home = fileparts(fileparts(mfilename('fullpath')));

if 1
% Add files included in LLONLY distribution.
javaaddpath([d4m_home '/lib/D4M_API_JAVA.jar']);


% USER: Add external files *NOT* included in LLONLY distribution.
% Find the files and put them in lib or change these
% entries to point to these files.
CB_VERSION='1.3.4';
ACC_VERSION='1.4.0';
HD_VERSION='0.20.205.0';

javaaddpath([d4m_home '/lib/commons-logging-1.0.4.jar']);
javaaddpath([d4m_home '/lib/commons-collections-3.2.jar']);
javaaddpath([d4m_home '/lib/commons-configuration-1.5.jar']);
javaaddpath([d4m_home '/lib/commons-io-1.4.jar']);
javaaddpath([d4m_home '/lib/commons-lang-2.4.jar']);
javaaddpath([d4m_home '/lib/log4j-1.2.15.jar']);

%javaaddpath([d4m_home '/lib/hadoop-0.20.2-core.jar']);
%javaaddpath([d4m_home '/lib/hadoop-0.20.2-tools.jar']);

javaaddpath([d4m_home '/lib/hadoop-core-' HD_VERSION '.jar']);
javaaddpath([d4m_home '/lib/hadoop-tools-' HD_VERSION '.jar']);

javaaddpath([d4m_home '/lib/cloudbase-core-' CB_VERSION '.jar']);
javaaddpath([d4m_home '/lib/cloudbase-start-' CB_VERSION '.jar']);
javaaddpath([d4m_home '/lib/cloudbase-server-' CB_VERSION '.jar']);
javaaddpath([d4m_home '/lib/thrift-0.3.jar']);
javaaddpath([d4m_home '/lib/slf4j-api-1.6.1.jar']);
javaaddpath([d4m_home '/lib/slf4j-log4j12-1.6.1.jar']);
javaaddpath([d4m_home '/lib/zookeeper-3.3.5.jar']);
javaaddpath([d4m_home '/lib/json.jar']);

javaaddpath([d4m_home '/lib/accumulo-core-' ACC_VERSION '.jar']);
javaaddpath([d4m_home '/lib/accumulo-server-' ACC_VERSION '.jar']);
javaaddpath([d4m_home '/lib/cloudtrace-' ACC_VERSION '.jar']);
javaaddpath([d4m_home '/lib/libthrift-0.6.1.jar']);
%New in cb-1.3.2
javaaddpath([d4m_home '/lib/commons-jci-core-1.0.jar']);
javaaddpath([d4m_home '/lib/commons-jci-fam-1.0.jar']);
javaaddpath([d4m_home '/lib/cloudtrace-0.1.3.jar']);

%For SQLserver and Sybase.
javaaddpath([d4m_home '/lib/jtds-1.2.5.jar']);

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

