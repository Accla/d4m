     **********************************************************
     ***              D4M Database Api                   ***
     ***               Dr. Albert Reuther                   ***
     ***             MIT Lincoln Laboratory                 ***
     ***               reuther@ll.mit.edu                   ***
     **********************************************************


INTRODUCTION

  D4M_API is set of D4M scripts that enable the storage
  and retrieval of D4M data into the cloud.


REQUIREMENTS

  -D4M application
  -Hadoop File system
  -Cloudbase database


INSTALLING AND RUNNING:

  - Add the D4M_HOME env variable to your bash profile.
    Example; D4M_HOME=/path/to/d4m_api-2.0

  - Type edit classpath.txt and add the following to
    your classpath.txt file.

    $D4M_HOME/conf
    $D4M_HOME/lib/commons-logging-1.0.4.jar
    $D4M_HOME/lib/log4j-1.2.15.jar
    $D4M_HOME/lib/hadoop-0.19.0-core.jar
    $D4M_HOME/lib/hadoop-0.19.0-tools.jar
    $D4M_HOME/lib/cloudbase-core-1.0.0-RC2.jar
    $D4M_HOME/lib/thrift-20080411p1.jar
    $D4M_HOME/lib/zookeeper-3.2.0.jar
    $D4M_HOME/d4m_api-2.0.jar

  - Log out and log back in.
  - Restart Matlab.

  - Navigate to D4M_HOME/matlab_src and type "DBinit"

  - Type "help DBinsert" or "help DBsubsrefFind" to get info on using Insert or Find.

  - Type "ExampleInsert" and then "ExampleFind" to run the examples.


RUNNING ON MacOSX

Same as above.

OTHER SETTINGS
    NA

FILES

  Description of files/directories:

  README            This file.
  examples/         Directory containinDBsubsrefFindg example programs.
  src/              D4M_API source files.

  doc/
    README.vX.X     What's new in this version.


  examples/
    ExampleInsert.m Inserts data into an example table.
    ExampleFind.m   Retrieves data from an example table.

  src/
    DBinit.m               Function to init env variables to use the database.
    DBinsert.m             Function to insert data into a specified table.
    DBsubsrefFind.m        Function to retrieve data from a specified table.


