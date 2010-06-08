

d4m_home=getenv('D4M_HOME');
addpath([d4m_home, '/matlab_src'])
addpath([d4m_home, '/examples'])
addpath([d4m_home, '/matlab_src/html'])


% Jar set for the CB-1.1.0 Stack
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
}    

javaaddpath(entries)
