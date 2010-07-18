

d4m_home = fileparts(fileparts(mfilename('fullpath')));

%d4m_home=getenv('D4M_HOME');
%addpath([d4m_home, '/matlab_src'])
%addpath([d4m_home, '/examples'])
%addpath([d4m_home, '/matlab_src/html'])

javaaddpath([d4m_home '/conf']);
javaaddpath([d4m_home '/lib/commons-logging-1.0.4.jar']);
javaaddpath([d4m_home '/lib/log4j-1.2.15.jar']);
javaaddpath([d4m_home '/lib/hadoop-0.20.2-core.jar']);
javaaddpath([d4m_home '/lib/hadoop-0.20.2-tools.jar']);
javaaddpath([d4m_home '/lib/cloudbase-core-1.1.0-LL1.jar']);
javaaddpath([d4m_home '/lib/cloudbase-start-1.1.0.jar']);
javaaddpath([d4m_home '/lib/cloudbase-server-1.1.0.jar']);
javaaddpath([d4m_home '/lib/thrift-20080411p1.jar']);
javaaddpath([d4m_home '/lib/zookeeper-3.2.2.jar']);
javaaddpath([d4m_home '/lib/d4m_api-0.01.jar']);

%%% Add MySQL jar's as necessary.


% Jar set for the CB-1.1.0 Stack
% Faster to do in Matlab.
if 0
entries = { 
    [d4m_home '/conf'], 
    [d4m_home '/lib/commons-logging-1.0.4.jar'],
    [d4m_home '/lib/log4j-1.2.15.jar'],
    [d4m_home '/lib/hadoop-0.20.2-core.jar'],
    [d4m_home '/lib/hadoop-0.20.2-tools.jar'],
    [d4m_home '/lib/cloudbase-core-1.1.0-LL1.jar'],
    [d4m_home '/lib/cloudbase-start-1.1.0.jar'],
    [d4m_home '/lib/cloudbase-server-1.1.0.jar'],
    [d4m_home '/lib/thrift-20080411p1.jar'],
    [d4m_home '/lib/zookeeper-3.2.2.jar'],
    [d4m_home '/lib/d4m_api-0.01.jar']
};  
javaaddpath(entries)
end


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer:  Mr. Craig McNally (cmcnally@ll.mit.edu),
%   Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

