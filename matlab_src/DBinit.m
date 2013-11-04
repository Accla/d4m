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

if ispc
 fd = '\';
else
 fd = '/';
end

% USER: Add external files *NOT* included in LLONLY distribution.
% Find the files and put them in lib or change these
% entries to point to these files.

ACC_VERSION='1.5.0';
%HD_VERSION='0.20.205.0';
HD_VERSION='1.1.2';

% ****  IMPORTANT NOTE ****
% If you will use accumulo, check that libthrift-0.6.1.jar is list before thrift-0.3.jar
% Vice versa, if you will use cloudbase, list thrift-0.3.jar before libthrift-0.6.1.jar.
if exist('OCTAVE_VERSION','builtin')
   % Add files included in LLONLY distribution.
   javaaddpath([d4m_home fd 'lib' fd 'D4M_API_JAVA.jar']);
   javaaddpath([d4m_home fd 'lib' fd 'D4M_API_JAVA_AC.jar']);

   javaaddpath([d4m_home fd 'libext' fd 'commons-logging-1.0.4.jar']);
   javaaddpath([d4m_home fd 'libext' fd 'commons-collections-3.2.jar']);
   javaaddpath([d4m_home fd 'libext' fd 'commons-configuration-1.5.jar']);
   javaaddpath([d4m_home fd 'libext' fd 'commons-io-1.4.jar']);
   javaaddpath([d4m_home fd 'libext' fd 'commons-lang-2.4.jar']);
   javaaddpath([d4m_home fd 'libext' fd 'log4j-1.2.15.jar']);

   javaaddpath([d4m_home fd 'libext' fd 'hadoop-core-' HD_VERSION '.jar']);
   javaaddpath([d4m_home fd 'libext' fd 'hadoop-tools-' HD_VERSION '.jar']);

   javaaddpath([d4m_home fd 'libext' fd 'slf4j-api-1.6.1.jar']);
   javaaddpath([d4m_home fd 'libext' fd 'slf4j-log4j12-1.6.1.jar']);
   javaaddpath([d4m_home fd 'libext' fd 'zookeeper-3.3.5.jar']);
   javaaddpath([d4m_home fd 'libext' fd 'json.jar']);

   javaaddpath([d4m_home fd 'libext' fd 'accumulo-core-' ACC_VERSION '.jar']);
   javaaddpath([d4m_home fd 'libext' fd 'accumulo-server-' ACC_VERSION '.jar']);
%   javaaddpath([d4m_home fd 'libext' fd 'cloudtrace-' ACC_VERSION '.jar']);
   javaaddpath([d4m_home fd 'libext' fd 'accumulo-fate-' ACC_VERSION '.jar']);
   javaaddpath([d4m_home fd 'libext' fd 'accumulo-trace-' ACC_VERSION '.jar']);
   javaaddpath([d4m_home fd 'libext' fd 'libthrift-0.9.1.jar']);

   javaaddpath([d4m_home fd 'libext' fd 'commons-jci-core-1.0.jar']);
   javaaddpath([d4m_home fd 'libext' fd 'commons-jci-fam-1.0.jar']);


   %For SQLserver and Sybase.
   javaaddpath([d4m_home fd 'libext' fd 'jtds-1.2.5.jar']);

else
   % MATLAB one line version (faster than adding individually) 
   %Common jars 
  javaaddpath({
      [d4m_home fd 'lib' fd 'D4M_API_JAVA.jar'], ...
      [d4m_home fd 'lib' fd 'D4M_API_JAVA_AC.jar'], ...
      [d4m_home fd 'libext' fd 'accumulo-core-' ACC_VERSION '.jar'], ...
      [d4m_home fd 'libext' fd 'accumulo-server-' ACC_VERSION '.jar'], ...
      [d4m_home fd 'libext' fd 'accumulo-trace-' ACC_VERSION '.jar'], ...
      [d4m_home fd 'libext' fd 'accumulo-fate-' ACC_VERSION '.jar'], ...
      [d4m_home fd 'libext' fd 'libthrift-0.9.1.jar'], ...
      [d4m_home fd 'libext' fd 'commons-logging-1.0.4.jar'], ...
      [d4m_home fd 'libext' fd 'commons-collections-3.2.jar'], ...
      [d4m_home fd 'libext' fd 'commons-configuration-1.5.jar'], ...
      [d4m_home fd 'libext' fd 'commons-io-1.4.jar'], ...
      [d4m_home fd 'libext' fd 'commons-lang-2.4.jar'], ...
      [d4m_home fd 'libext' fd 'log4j-1.2.15.jar'], ...
      [d4m_home fd 'libext' fd 'hadoop-core-' HD_VERSION '.jar'], ...
      [d4m_home fd 'libext' fd 'hadoop-tools-' HD_VERSION '.jar'], ...
      [d4m_home fd 'libext' fd 'slf4j-api-1.6.1.jar'], ...
      [d4m_home fd 'libext' fd 'slf4j-log4j12-1.6.1.jar'], ...
      [d4m_home fd 'libext' fd 'zookeeper-3.3.5.jar'], ...
      [d4m_home fd 'libext' fd 'json.jar'], ...
      [d4m_home fd 'libext' fd 'commons-jci-core-1.0.jar'], ...
      [d4m_home fd 'libext' fd 'commons-jci-fam-1.0.jar'], ...
      [d4m_home fd 'libext' fd 'jtds-1.2.5.jar'] });

end

clear d4m_home  ACC_VERSION HD_VERSION fd

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

