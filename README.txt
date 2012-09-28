%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

INTRODUCTION

  D4M is library that allows unstructured data to be represented
as triples in sparse matrices (Associative Arrays) and can be
manipulated using standard linear algebraic operations.
Using D4M it is possible to construct advanced analytics
with just a few lines of code.
  D4M also supports parallel computing and connections to
high performance databases.

DOCUMENTATION

  For installation, please read this short (~5 page) document.
  For usage please see the 8 lecture course in d4m_api/docs directory.
  For examples please see the numerous examples in the d4m_api/examples directory.

REQUIREMENTS

  D4M (standalone)
  -Requires Matlab (or GNU Octave 3.2.2+)

  D4M Parallel
  -Requires pMatlab (http://www.ll.mit.edu/pMatlab/)

  D4M Database
  -Requires D4M database connector jar (see d4m_api/lib)
  -Requires various 3rd party jars (see d4m_api/lib)
  -Requires a running database
    -D4M provides full support to Accumulo (accumulo.apache.org)
    -D4M provides partial support to SQL database via JTDS ()

LICENSE

  D4M follws the highly successfuly FFTW MIT licensing model (see fftw.org) and
is avalable via a number of licenses: Free (GPL), U.S. Gov't Agency,
U.S. Gov't Contractor, and Commercial.

  - cd to d4m_api/matlab_src/TEST, and run any script ending in TEST.m

  - To run all tests type:

       Atest = runTESTdir('./')


INSTALLATION

- Extract d4m_api.X.X.X.tar.gz in your local directory.
    - The distribution will contain:

          d4m_api-X.X.X
            docs/
            examples/
            lib/
            matlab_src/
            TEST/

- From here on we will refer to the full path to d4m_api-X.X.X as D4M_HOME.







- If you are using Matlab, it may be convenient to setup the startup.m with the path to d4m_api and its source directory
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




  
 


 




 

