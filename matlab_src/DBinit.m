
%DBinit: Set up path for D4M binding to databases.
%Database user function.
%  Usage:
%    DBinit
%  Inputs:
%    
%  Outputs:
%    modifies java path

% Get directory that this file is in.
d4m_home = fileparts(fileparts(mfilename('fullpath')));


if 1

fd = filesep;

% USER: Add external files *NOT* included in LLONLY distribution.
% Find the files and put them in lib or change these
% entries to point to these files.

% ****  IMPORTANT NOTE ****
% If you will use accumulo, check that libthrift-0.6.1.jar lists before thrift-0.3.jar.
% Vice versa, if you will use cloudbase, list thrift-0.3.jar before libthrift-0.6.1.jar.
if exist('OCTAVE_VERSION','builtin')
% Add files included in LLONLY distribution.
javaaddpath([d4m_home fd 'lib' fd 'graphulo-3.0.0.jar']);

javaaddpath([d4m_home fd 'libext' fd 'accumulo-core-1.8.0.jar']);
javaaddpath([d4m_home fd 'libext' fd 'accumulo-fate-1.8.0.jar']);
javaaddpath([d4m_home fd 'libext' fd 'commons-codec-1.7.jar']);
javaaddpath([d4m_home fd 'libext' fd 'commons-collections-3.2.2.jar']);
javaaddpath([d4m_home fd 'libext' fd 'commons-collections4-4.1.jar']);
javaaddpath([d4m_home fd 'libext' fd 'commons-configuration-1.6.jar']);
javaaddpath([d4m_home fd 'libext' fd 'commons-lang-2.4.jar']);
javaaddpath([d4m_home fd 'libext' fd 'commons-lang3-3.1.jar']);
javaaddpath([d4m_home fd 'libext' fd 'commons-logging-1.1.1.jar']);
javaaddpath([d4m_home fd 'libext' fd 'commons-math3-3.6.1.jar']);
javaaddpath([d4m_home fd 'libext' fd 'guava-14.0.1.jar']);
javaaddpath([d4m_home fd 'libext' fd 'hadoop-common-2.7.2.jar']);
javaaddpath([d4m_home fd 'libext' fd 'htrace-core-3.1.0-incubating.jar']);
javaaddpath([d4m_home fd 'libext' fd 'htrace-zipkin-3.1.0-incubating.jar']);
javaaddpath([d4m_home fd 'libext' fd 'libthrift-0.9.3.jar']);
javaaddpath([d4m_home fd 'libext' fd 'log4j-1.2.17.jar']);
javaaddpath([d4m_home fd 'libext' fd 'mtj-1.0.4.jar']);
javaaddpath([d4m_home fd 'libext' fd 'org.json-chargebee-1.0.jar']);
javaaddpath([d4m_home fd 'libext' fd 'slf4j-api-1.7.10.jar']);
javaaddpath([d4m_home fd 'libext' fd 'slf4j-log4j12-1.7.10.jar']);
javaaddpath([d4m_home fd 'libext' fd 'zookeeper-3.4.6.jar']);

else
% MATLAB one line version (faster than adding individually) 
%Common jars 
javaaddpath({ ...
[d4m_home fd 'lib' fd 'graphulo-3.0.0.jar'] ...
, [d4m_home fd 'libext' fd 'accumulo-core-1.8.0.jar'] ...
, [d4m_home fd 'libext' fd 'accumulo-fate-1.8.0.jar'] ...
, [d4m_home fd 'libext' fd 'commons-codec-1.7.jar'] ...
, [d4m_home fd 'libext' fd 'commons-collections-3.2.2.jar'] ...
, [d4m_home fd 'libext' fd 'commons-collections4-4.1.jar'] ...
, [d4m_home fd 'libext' fd 'commons-configuration-1.6.jar'] ...
, [d4m_home fd 'libext' fd 'commons-lang-2.4.jar'] ...
, [d4m_home fd 'libext' fd 'commons-lang3-3.1.jar'] ...
, [d4m_home fd 'libext' fd 'commons-logging-1.1.1.jar'] ...
, [d4m_home fd 'libext' fd 'commons-math3-3.6.1.jar'] ...
, [d4m_home fd 'libext' fd 'guava-14.0.1.jar'] ...
, [d4m_home fd 'libext' fd 'hadoop-common-2.7.2.jar'] ...
, [d4m_home fd 'libext' fd 'htrace-core-3.1.0-incubating.jar'] ...
, [d4m_home fd 'libext' fd 'htrace-zipkin-3.1.0-incubating.jar'] ...
, [d4m_home fd 'libext' fd 'libthrift-0.9.3.jar'] ...
, [d4m_home fd 'libext' fd 'log4j-1.2.17.jar'] ...
, [d4m_home fd 'libext' fd 'mtj-1.0.4.jar'] ...
, [d4m_home fd 'libext' fd 'org.json-chargebee-1.0.jar'] ...
, [d4m_home fd 'libext' fd 'slf4j-api-1.7.10.jar'] ...
, [d4m_home fd 'libext' fd 'slf4j-log4j12-1.7.10.jar'] ...
, [d4m_home fd 'libext' fd 'zookeeper-3.4.6.jar'] ...
});
end

clear d4m_home fd

end
