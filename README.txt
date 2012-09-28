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
% FOUO
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

INTRODUCTION

  D4M_API is set of D4M scripts that enable the storage
  and retrieval of associative arrays into a database.

REQUIREMENTS

  D4M (standalone)
  -Matlab (or GNU ctave 3.2.2+ with Java package 1.2.6+)

  D4M (w/database)
    -CLOUDBASE
      -Zookeeper
      -Hadoop File system
      -Cloudbase database
    -HBASE (TBD)
    -OTHERS (TBD)



INSTALLING AND RUNNING FULL OR LLONLY DISTRIBUTION:

  - Add the following to your ~/matlab/startup.m file (or ~/.octaverc file).

      addpath('<ParentDir>/d4m_api/matlab_src');  % Replace <ParentDir> with location of d4m_api.
      DBinit;    % Initalizes java path.  NOTE: Octave requires Java package be installed.

  - Edit matlab_src/DBinit.m file and find missing .jar files and put them in lib directory.

       The ExternalContrib file also contains brief description of the jars.

  - Edit d4m_api/matlab_src/TEST/DBsetup.m so it points to your Cloudbase server.

  - Start Matlab (or octave --traditional).

  - cd to d4m_api/matlab_src/TEST, and run any script ending in TEST.m

  - To run all tests type:

       Atest = runTESTdir('./')


INSTALLING LLONLY STUB DISTRIBUTION:

  - Assumes a LLONLY distribution has already been installed.

  - Unpack LLONLY STUB distribution on top of existing directory
    to get most recent changes to LLONLY distribution.


RUNNING ON MacOSX

  Same as above.

CREATING A FULL DISTRIBUTION:
  svn export <URL>
  ant package
  ant zip

CREATING A LLONLY DISTRIBUTION:
  ant ll_package
  ant zip


CREATING A LLONLY STUB DISTRIBUTION:

KNOWN ISSUES

  GNU Octave 3.2.2 / Java PKG 1.2.6
    matlab_src/runTESTdir only works if Octave is launched in TEST directory

Getting Started
--------------------------------------

- Extract d4m_api.X.X.X.tar.gz in your local directory.
    - The distribution will contain:


          d4m_api-X.X.X
            docs/
            examples/
            lib/
            matlab_src/
            TEST/

- Change directory to d4m_api-X.X.X directory, which will be referred to as D4M_HOME.

- If you are using matlab, it may be convenient to setup the startup.m with the path to d4m_api and its source directory
   - Do addpath command for the matlab_src directory
   - Have it run the DBinit script

        addpath(D4M_HOME/matlab_src);        % Add D4M commands to path.
        DBinit;                              % Only necessary if you want to connect to a database. 

   -  For octave, you would set the ".octaverc" file to do the initialization steps like matlab.
      In addition, you also need to call:

        Assoc('','','');                     % Init object paths.

- Run matlab (or octave) application from the d4m_api toplevel directory.
  To get a summary of all D4M commands type:

        help D4M

  To get help on a specific command type such as the Assoc function type:

        help Assoc

- To learn more about how to use D4M see the lectures in the docs/ directory
  and the exmple programs in the examples/ directory

- To run all the examples, cd to the examples/ directory, start matlab (or GNU Octave) and type:

        d4mTestAllExamples             

  NOTE: Some of the programs in examples/3Scaling/2ParallelDatabase require a valid database connection.
  To run in parallel these programs also require pMatlab (http://www.ll.mit.edu/pMatlab/).

Setup an Accumulo connection
--------------------------------------
 To establish an Accumulo connection in D4M, use the DBserver object.
   DBserver needs the following parameters
      - host name :   zookeeper host name
      - database type:  always use 'Accumulo' as the parameter value
      - instance name:  Accumulo instance name
      - user name:   user name on cloudbase. 
      - password:    password for user  

   DB = DBserver(host, db_type, instance_name, [username],[password])

   You will be prompted for a username and password if you don't include them.
   As you type the password you will not see anything displayed, so type carefully and hit return.

    >>  hostname='localhost'
    >>  cb_type = 'Accumulo'
    >>  instance_name='Accumulo'
    >> DB = DBserver(hostname,cb_type, instance_name);

      Enter a username:
          JoeUser <return>
      Enter a password.
              <return>


Create a table or get an existing table in Accumulo
-----------------------------------------------------
D4M has 2 flavors of database table objects - DBtable and DBtablePair.
With these table objects, you have access to the data.
 
Once you have the DBserver object, you can create a single table or get an existing table
by instantiating a DBtable object by passing a name of the table to the DBserver object.

      >>  db_table = DB('MyTableName');


To create DBtablePair object,

     >>  db_table_pair = DB('MyTable1','MyTable2');



Querying for data
-----------------------------------------------------
You can query for data via  the DBtable or DBtablePair.
The syntax is
    AssocObject =  DBtable(row_key , column_key)

The results from the query is contained in an associative array object Assoc.

    >> MyAssoc= db_table(:,:)

This query will give you back all the data from db_table in a Assoc object.

The row_key and column_key have a particular format to follow:

  -    ":"  colon indicate all results.

  -     'cat,fat,hat,'  queries for cat, fat, and hat
     Note, the ending comma is a necessary delimiter to include in the query string.

  -     'cat,:,pat,'   will query for a range, from cat through pat


Examples:

1.
     >>      assoc = db_table('cat,hat,sat,',:)

     This will search the rows for cat, hat , and sat and any columns.


2.
     >>    assoc = db_table('cat,:,sat,', :);

    This query will give me back the range between cat and sat, and all columns.


3.
    >>  assoc = db_table(:, 'cat, fat, what,');

    This query will give me back all rows with columns of 'cat', 'fat', and 'what'.





%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%




  
 


 




 

