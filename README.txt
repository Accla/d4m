NOTE: This is the Accumulo 1.6.0+ version.
*This build will not work against Accumulo 1.5 and previous.*

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

1. INTRODUCTION

  D4M is a library that allows unstructured data to be represented
as triples in sparse matrices (Associative Arrays) and can be
manipulated using standard linear algebraic operations.
Using D4M it is possible to construct advanced analytics
with just a few lines of code.
  D4M also supports parallel computing and connections to
high performance databases (e.g., Accumulo).

2. DOCUMENTATION

  For installation, please read this short (~5 page) document.
  For usage please see the eight lecture course in d4m_api/docs directory.
  For examples please see the numerous examples (ending in TEST.m) in the d4m_api/examples directory.
  When citing D4M in publications please use:

    [Kepner et al, ICASSP 2012] Dynamic Distributed Dimensional Data Model (D4M) 
    Database and Computation System, J. Kepner, W. Arcand, W. Bergeron, N. Bliss, 
    R. Bond, C. Byun, G. Condon, K. Gregson, M. Hubbell, J. Kurz, A. McCabe, P. Michaleas,
    A. Prout, A. Reuther, A. Rosa & C. Yee, ICASSP (International Conference on Accoustics,
    Speech, and Signal Processing), Special session on Signal and Information Processing
    for "Big Data" (organizers: Bliss & Wolfe), March 25-30, 2012, Kyoto, Japan 

3. REQUIREMENTS

  D4M (standalone)
  -Requires Matlab (www.mathworks.com/matlab) or GNU Octave 3.2+ (www.octave.org)

  D4M Parallel
  -Requires pMatlab (www.ll.mit.edu/pMatlab)

  D4M Database
  -Requires D4M database connector jar (see d4m_api/lib)
  -Requires various 3rd party jars (see d4m_api/libext)
  -Requires a running database
    -D4M provides full support to Accumulo (accumulo.apache.org)
    -D4M provides query support to SQL databases via JTDS (jtds.sourceforge.net)
  -GNU octave < 3.6 requires the Java package

4. LICENSE

  D4M follows the highly successful FFTW MIT licensing model (see fftw.org) and
is avalable via a number of licenses: Free (GPL), U.S. Gov't Agency,
U.S. Gov't Contractor, and Commercial.  See additional documentation in the distribution.

5. INSTALLATION

  Extract d4m_api.X.X.X.zip in your local directory.
If you want to connect to a database, then also download and extract the external libraries
libext.X.X.X.zip file and place it in the d4m_api/ directroy.  This should result
in a distribution containing:

          d4m_api-X.X.X
            docs/
            examples/
            lib/
            libext/
            matlab_src/
            TEST/

 From here on we will refer to the full path to d4m_api-X.X.X as D4M_HOME
and ">>" denotes the Matlab (or GNU Octave) prompt.

6. QUICKSTART

  (1) Start Matlab (or GNU Octave)
  (2) Add the D4M library to your path by typing

  >> addpath('D4M_HOME/matlab_src')

  (3)  Done.

  Display the function refernce by typing:

  >> help D4M

  Run the first example by typing:

  >> cd D4M_HOME/examples/1Intro/1AssocIntro
  >> AI1_SetupTEST

7. ADDING PARALLEL AND DATABASE CAPABILITIES

  It is recommended that the D4M setup be placed in the Matlab ~/matlab/startup.m or GNU Octave ~/.ocatverc file.
[Note: Windows users should consult their Matlab/Octave documentation to determine where this should exist.]
Below is a fully commented example of what this file might look like:

  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
  D4M_HOME = '/Users/kepner/SVN/d4m_api';       % SET TO LOCATION OF D4M.

  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
  addpath([D4M_HOME '/matlab_src']);            % Add the D4M library.

  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
  Assoc('','','');                              % Initialize library.

  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
  % Uncomment the following line to enable the D4M database connector.
  %DBinit;    % This requires that the libext/ directory is in place.

  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
  % Uncomment and modify the following four lines for parallel D4M.
  %PMATLAB_HOME = '/Users/kepner/SVN/pMatlab';   % SET location of pMatlab.
  %addpath([PMATLAB_HOME '/MatlabMPI/src']);    % Add MatlabMPI.
  %addpath([PMATLAB_HOME '/src']);              % Add pMatlab.
  %pMatlabGlobalsInit;
  %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

8. TESTING

  To run all the examples, cd to the examples/ directory, start matlab (or GNU Octave) and type:

  >>  cd D4M_HOME/examples
  >>  d4mTestAllExamples             

  NOTE: Some of the programs in examples/3Scaling/2ParallelDatabase require a valid database connection.
  To run in parallel these programs also require pMatlab (www.ll.mit.edu/pMatlab/).

  To configure the Database, you will need to uncomment and modify the DB = DBsetup(...) command in 
  examples/3Scaling/2ParallelDatabase/DBsetup.m


9. RUNNING IN PARALLEL

  Several parallel examples can be found in examples/3Scaling/2ParallelDatabase.
To run in parallel edit an example (e.g., pDB02_FileTEST.m) by uncommenting the lines
marked "% PARALLEL."  To run on 4 processors on your local machine type:

  >> cd D4M_HOME/examples/3Scaling/2ParallelDatabase
  >> eval(pRUN('pDB02_FileTEST',4,{}))


10. DATABASE CONNECTION

10.1 Seting up an Accumulo connection
 To establish an Accumulo connection in D4M, use the DBserver object.

  >> DB = DBserver(host, db_type, instance_name, [username],[password])

DBserver needs the following parameters
  host name :   zookeeper host name
  database type:  always use 'Accumulo' as the parameter value
  instance name:  Accumulo instance name
  user name:   user name on database. 
  password:    password for user  


  You will be prompted for a username and password if you don't include them.
As you type the password you will not see anything displayed, so type carefully and hit return.

    >> hostname='localhost'
    >> cb_type = 'Accumulo'
    >> instance_name='Accumulo'
    >> DB = DBserver(hostname,cb_type,instance_name);

      Enter a username:
          JoeUser <return>
      Enter a password.
              <return>

10.2 Create a table or get an existing table in Accumulo
  D4M has 2 flavors of database table objects - DBtable and DBtablePair.
With these table objects, you have access to the data.
Once you have the DBserver object, you can create a single table or get an existing table
by instantiating a DBtable object by passing a name of the table to the DBserver object.

  >> T = DB('MyTableName');

To create DBtablePair object,

  >> TT = DB('MyTable','MyTableTranspose');

10.3 Querying for data
  You can query for data via  the DBtable or DBtablePair.
The syntax is

  >> A =  T(row_key,column_key)

The results from the query are contained in an associative array object Assoc.

  >> A = T(:,:)

This query will give you back all the data from T in a Assoc object.

The row_key and column_key have a particular format to follow:

 ":"  colon indicate all results.

 'cat,fat,hat,'  queries for cat, fat, and hat
  Note, the ending comma is a necessary delimiter to include in the query string.

 'cat,:,pat,' will query for a range, from cat through pat


10.4 Examples:

  This will search the rows for cat, hat , and sat and any columns.

  >> A = T('cat,hat,sat,',:)

  This query will give me back the range between cat and sat, and all columns.

  >> A= T('cat,:,sat,', :);

  This query will give me back all rows with columns of 'cat', 'fat', and 'what'.

  >> A = T(:,'cat,fat,what,');

  The above query will be much faster if a table pair is used:

  >> A = TT(:,'cat,fat,what,');

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%




  
 


 




 

