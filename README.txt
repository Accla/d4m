%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineers: 
%   Mr. Craig McNally (cmcnally@ll.mit.edu)
%   Dr. Jeremy Kepner (kepner@ll.mit.edu)
%   Mr. Will Smith (will.smith@ll.mit.edu)
%   Mr. Chuck Yee (yee@ll.mit.edu)
%
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


INTRODUCTION

  D4M_API is set of D4M scripts that enable the storage
  and retrieval of associative array data into the cloud.

REQUIREMENTS

  D4M (standalone)
  -Matlab (or Octave 3.2.2+)

  D4M (w/database)
    -CLOUDBASE
      -Zookeeper
      -Hadoop File system
      -Cloudbase database
    -HBASE (TBD)
    -OTHERS (TBD)

CREATING A DIFF DISTRIBUTION:
  1)  ant diff_package zip -DRevA=<Revision number you want to work from>

CREATING A FULL DISTRIBUTION:
  1)  ant package zip

CREATING A STUB DISTRIBUTION:
  1)  ant ll_package zip

INSTALLING AND RUNNING FULL DISTRIBUTION:

  - Add the following to your ~/matlab/startup.m file (or ~/.octaverc file).

      addpath('<ParentDir>/d4m_api/matlab_src');  % Replace <ParentDir> with location of d4m_api.
      DBinit;    % Initalizes java path.  NOTE: Octave requires Java package be installed.
      % DBinit1p1; % Same as DBinit but for version 1.1.

  - Edit d4m_api/matlab_src/TEST/DBsetup.m so it points to your Cloudbase server.

  - Start Matlab (or octave --traditional).

  - cd to d4m_api/matlab_src/TEST, and run any script ending in TEST.m

  - To run all tests type:

       Atest = runTESTdir('./')

INSTALLING AND RUNNING STUB DISTRIBUTION:

  - Same as above, but also ...

  - Find the following Jars and put them in d4m_api/lib
      -
      -
      ...

  - If Octave 3.2.4-
      - Rename matlab_src/randiTmp.m to randi.m

RUNNING ON MacOSX

Same as above.

OTHER SETTINGS
    NA

FILES  *NEEDS UPDATING*

  Description of files/directories:

    D4M_HOME
         |____ bin
         |____ conf
         |____ docs
         |____ examples
         |____ lib
         |____ matlab_src
         |____ src



  README            This file.
  bin/              Shell scripts

  conf/             configuration files for cloudbase, hadoop, zookeeper, log4j

  examples/         Directory containinDBsubsrefFindg example programs.
  src/              D4M_API Java source files.

  docs/
    README.vX.X     What's new in this version.
    java.opts       Configuration file for specifying MATLAB - Java max memory.


  examples/
    ExampleInsert.m     Inserts data into an example table.
    ExampleFind.m       Retrieves data from an example table.
    ExampleFindAll      Retrieves all data from an example table.
    ExampleFindColumns  Retrieves data using a column query
                        from an example table.
    ExampleFindRows     Retrieves data using a row query
                        from an example table.

  matlab_src/
    DBinit.m               Function to init env variables to use the database.
    DBinsert.m             Function to insert data into a specified table.
    DBsubsrefFind.m        Function to retrieve data from a specified table.
    DBLS.m                 Function to retrieve a list of tables from a specified host.
    TEST/                  Test suite for Matlab code

  lib/               location for all jars. See ExternalContrib for explanation of jar dependencies
    cloudbase-core-1.2.0.jar
    cloudbase-server-1.2.0.jar
    commons-collections-3.2.jar
    commons-configuration-1.5.jar
    commons-io-1.4.jar
    commons-lang-2.4.jar
    commons-logging-1.0.4.jar
    hadoop-0.20.2-core.jar
    hadoop-0.20.2-tools.jar
    log4j-1.2.15.jar
    slf4j-api-1.6.1.jar
    slf4j-log4j12-1.6.1.jar
    thrift-0.2.jar
    zookeeper-3.2.2.jar  





