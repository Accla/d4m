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
  -Matlab (or Octave)

  D4M (w/database)
    -CLOUDBASE
      -Zookeeper
      -Hadoop File system
      -Cloudbase database
    -HBASE (TBD)
    -OTHERS (TBD)


INSTALLING AND RUNNING:

  - Add the following to your ~/matlab/startup.m file (or ~/.octaverc file).

      addpath('<ParentDir>/d4m_api/matlab_src');  % Replace <ParentDir> with location of d4m_api.
      DBinit;    % Initalizes java path.  NOTE: Octave requires Java package be installed.

  - Edit d4m_api/matlab_src/TEST/DBsetup.m so it points to your Cloudbase server.

  - Start Matlab (or octave --traditional).

  - cd to d4m_api/matlab_src/TEST, and run any script ending in TEST.m

  - To run all tests type:

       Atest = runTESTdir('./')


RUNNING ON MacOSX

Same as above.

OTHER SETTINGS
    NA

FILES  *NEEDS UPDATING*

  Description of files/directories:

  README            This file.
  examples/         Directory containinDBsubsrefFindg example programs.
  src/              D4M_API source files.

  doc/
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

  src/
    DBinit.m               Function to init env variables to use the database.
    DBinsert.m             Function to insert data into a specified table.
    DBsubsrefFind.m        Function to retrieve data from a specified table.
    DBLS.m                 Function to retrieve a list of tables from a specified host.



