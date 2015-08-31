%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%D4M: DYNAMIC DISTRIBUTED DIMENSIONAL DATA MODEL
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% 
% D4M is a library of functions that allow row, column and value triples of strings and/or numbers
% to be formed into associative arrays and manipulated using linear algebra.  These triples
% can be inserted into and queried from database tables.
% The functions summarized below consist of User, Utility and Internal functions.
% User functions are intended to be used by users.  Utility functions are less commonly used by users.
% Internal functions are not intended to be used by users.
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%QUICK INSTALL
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  (1) Unpack d4m software.
%  (2) Add the d4m_api/matlab_src to your path, e.g.
%	 addpath /home/kepner/d4m_api/matlab_src
%  (3) Start matlab and type "help D4M" to see this page
%  (4) Type "help function_name" to see more detailed documentation
%  (5) Look at the material in d4m_api/docs and d4m_api/examples
%  (6) If you are going to be using D4M with a database type
%        DBinit
%  (7) Put the addpath and DBinit commands in your startup.m file which is typically found in:
%       /home/kepner/matlab/startup.m
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%ASSOCIATIVE ARRAY AND DATABASE TABLE
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  User Functions:
%    (),subsref: Selects rows and columns from an associative array or database table.
%    StartsWith: Converts a list of strings into a list of ranges that can be used in a query.
%    nnz: Returns number of non-zeros in an associative array or database table.
%
%  Utility Functions:
%    IsClass: Tests if an object is a specific classname.
%    randRow: Randomly selects rows from an associative array or database table.
%
%    runTESTdir: Runs every *TEST.m script in a directory.
%
%    columnNeighbors: DEPRECATED. Using a starting set of column keys find graph neighbors to a specified depth. 
%
%  Internal Functions (not user functions):
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%ASSOCIATIVE ARRAY
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  User Functions:
%    Assoc: Constructs an associative array from row, column, and value triples.
%    Row: Returns row keys of an associative array.
%    putRow: Replaces the row keys in an associative array; does no consistency checking.
%    Col: Returns column keys of an associative array.
%    putCol: Replaces the column keys in an associative array; does no consistency checking.
%    Val: Returns the unique column values of an associative array in sorted order.
%    putVal: Replaces the string values in an associative array; does no consistency checking.
%    Adj: Returns associative array adjacency matrix that connect row, columns, and values.
%    putAdj: Replaces the adjacency matrix in an associative array; does no consistency checking.
%    reAssoc: Rebuilds an associative array so that all keys are in their correct sorted order.
%
%    size: Returns the dimensions of an associative array.
%    numel: Returns the number of row times the number of columns in an associative array.
%    isempty: Checks if an associative array is empty.
%    find: Converts an associative array in to triples.
%    sum: Performs sum along the dimension of an associative array with numeric values.
%
%    disp: Display the internal structure of an associative array.
%    display: Display an associative array as a list of triples.
%    displayFull: Display an associative array as a formatted table.
%    plot: Creates a plot of an associative array vector of values.
%    spy: Creates a 2D plot of non-empty entries in an associative array; clicking on a dot shows its value.
%
%    double: Converts associative array adjacency matrix to a double.
%    logical: Converts associative array adjacency matrix to a logical of ones and zeros.
%    dblLogi: Converts associative array adjacency matrix to a double of ones and zeros.
%    Abs0: Converts associative array adjacency matrix to a double of ones and zeros. (same as dblLogi)
%    abs: Absolute value of matrix of the adjacency matrix of an associative array.
%    num2str: Converts numeric values in an associative array to string values.
%    str2num: Converts string values in an associative array to numeric values.
%    NoDiag: Remove diagonal (i.e., A(k,k) = 0) from associative array or matrix.
%    diag: DEPRECATED, Returns diagonal of an associative array.
%
%    *,mtimes: Performs matrix multiply of two associative arrays.
%    mtimesCustom: Matrix multiply of two associative arrays over an arbitrary ring.
%    sqIn: Computes A.' * A efficiently.
%    sqOut: Computes A * A.' efficiently.
%    +,plus: Add two associative arrays.
%    -,minus: Subtracts one associative array from another.
%    .*,times: Multiplies the numeric values of one associative array by the values of another.
%    ./,rdvide: Divides the numeric values of one associative array by the values of another.
%    .',transpose: Performs a matrix transpose on an associative array.
%    &,and: Performs logical and of two associative arrays; output value is the min of the two input values.
%    |,or: Performs logical or of two associative arrays; output value is the min of the two input values.
%    xor: Performs logical xor of two associative arrays; output value is the min of the two input values.
%    ==,eq: Compares the values of an associative array with a scalar.
%    ~=,ne: Compares the values of an associative array with a scalar.
%    >,gt: Compares the values of an associative array with a scalar.
%    >=,ge: Compares the values of an associative array with a scalar.
%    <,lt: Compares the values of an associative array with a scalar.
%    <=,le: Compares the values of an associative array with a scalar.
%    strcmp: Compares the values of an associative array with a scalar.
%    max: DEPRECATED. Compares an associative array with a scalar along a specified dimension.
%    min: DEPRECATED. Compares an associative array with a scalar along a specified dimension.
%
%    TopColPerRow: Picks the top valued column for each row.
%    TopRowPerCol: Picks the top valued rows for each column.
%    CatKeyMul: Perform matrix multiply and concatenate colliding row/col keys into the value.
%    CatValMul: Perform matrix multiply and concatenate colliding values.
%
%    col2type: Splits column keys of associative array and stores first part as column key and second part as value; inverse of val2col. 
%    val2col: Append associative array values to column keys; inverse of col2type. 
%    col2val: Splits column keys of associative array and stores second part as column key and first part as value. 
%
%  Utility Functions:
%    putAssoc: Constructs all elements of an associative array from its components.
%    noCol: Eliminates the column keys of an associative array.
%    noRow: Eliminates the row keys of an associative array.
%    noVal: Eliminates the values strings of an associative array.
%    NewSep: Replaces string separator in all string lists..
%    Key: Returns concatenated row and column keys of an associative array.
%    conv: Convolves associative array vector with window vector (using 'same' syntax).
%    MyEcho: Echo back the given command. Use $ for quoting strings. Useful for demos.
%    randCol: Randomly selects Msub cols from an associative array.
%
%  Internal Functions (not user functions):
%    AssocCatStrFunc: Concatenates strings inside a matrix multiply.
%    Equallike: Compares the values of an associative array with a scalar.
%    Pluslike: Performs element wise binary functions on two associative arrays.
%    loadobj: Used to load associative array from a file.
%    spyTicks: Used to load associative array from a file.
%
%    randiTmp: DEPRECATED. Stub for randi if it isn't available.
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%STRING LIST
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  User Functions:
%    NumStr: Counts the number of strings in a string list.
%
%    CatStr: Concatenates two string lists with a separator; inverse of SplitStr.
%    SplitStr: Uncats list of strings into two separate lists of strings; inverse of CatStr.
%    KronCatStr: Concatenates the pairwise permutations of two string lists with a separator.
%
%    Str2mat: Converts list of strings to char matrix; inverse of Mat2str.
%    Mat2str: Converts char matrix to a list of strings; inverse of Str2mat.
%    Text2word: Converts list of text strings to list of words. 
%
%    CatTriple: Appends r, c, v and rr, cc, vv; assumes each pair has same type.
%    catFind: Appends triples from an associative array to another set of triples; assumes each pair has same type.
%
%  Utility Functions:
%    StrSearch: Finds index location of one string inside another.
%    StrSepsame: Makes separators in two lists of strings the same.
%    StrSubind: Returns sub-strings i found in string list s.
%    StrSubsref: Returns index locations of one list of strings inside another.
%    StrUnique: Finds unique strings in string array and index mapping between input and output.
%
%    Mertonize: DEPRECATED. Interleave the values of two decimal strings.
%    MertonizeLatLon: DEPRECATED. Interleave the values of two decimal strings.
%
%  Internal Functions (not user functions):
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%IO
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  User Functions:
%    StrLS: Returns list of files in a directory formatted as a string list.
%
%    Assoc2CSV: Writes an associative array to a CSV file.
%    Assoc2CSVstr: Converts an associative array to a CSV string.
%    Assoc2JSONCSV: Converts an associative array to a JSON formatted CSV string.
%    FindCSVsimple: Fast read of a CSV (or TSV) file into simple triples.
%    FindCSV: Reads a CSV (or TSV) file into triples.
%    ReadCSV: Reads a CSV (or TSV) file into an associative array.
%    ReadCSVfiles: Reads multiple CSV (or TSV) files into an associative array.
%    CSVstr2assoc: Converts a CSV formatted string to an associative array.
%    JSONCSV2assoc: Converts a CSV string stored inside a JSON data structure to an associative array.
%    parseJSON: Converts a JSON formatted string into a data structure.
%
%    genRmatData: Generates a graph using the R-MAT generator.
%
%  Utility Functions:
%    ParseFileCols: DEPRECATED. Parse a file into a sequence of cols. Can select subcolumns..
%
%  Internal Functions (not user functions):
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%DATABASE
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  User Functions:
%    DBinit: Set up path for D4M binding to databases. 
%    DBserver: Create DBserver object with a binding to a specific database.
%    display: Show the contents of a DBserver object and list its tables
%    ls: List tables in a DBserver object
%    put: Inserts data into a database table.
%    (),subsref: Create DBtable object binding to a specific database table; creates tables if they don't exist.
%    delete: Deletes table from a database; prompts user before proceeding.
%    deleteForce: Deletes a table from a database; does *not* prompt user before proceeding.
%
%  Utility Functions:
%
%  Internal Functions (not user functions):
%    DBtable: Create DBtable object binding to a specific database table.
%    DBtablePair: Create DBtable object binding to a specific database table pair.
%    DBaddJavaOps: Adds a java database operation.
%    DBcreate: Create a table on a specific database.
%    DBcreate: Delete a table on a specific database.
%    DBsqlConnect: Constructs a connector to an SQL database.
%    DBsetupLLGrid: Create database binding on LLGrid.
%    new: Creates a new table object based on an old table.
%
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%DATABASE TABLE
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  User Functions:
%    Iterator: Creates a new iterator inside of a table and sets its limits.
%    addColCombiner: Sets which column names should have combiners.
%    ColCombiner: List the columns in the table with combiners.
%    deleteColCombiner: Deletes the combiners for specific column names.
%
%  Utility Functions:
%    close: Reset iterator in a table object.
%    putColumnFamily: Set the column family currently be used by a table.
%    ColumnFamily: Retrieve the column family currently be used by a table.
%    getName: Retrieve the name of the table.
%    putSecurity: Set the security label currently used by a table.
%    Security: Get the security label currently used by a table.
%    addSplits: Adds row splits to a table for better load balancing across multiple servers.
%    putSplits: Replaces all row splits in a table; used for better load balancing across multiple servers.
%    mergeSplits: 
%    Splits: 
%
%    InsertAssocFiles: Inserts Assoc array files into a database table.
%
%    TsqlCol: Returns column names in an SQL table.
%    TsqlSize: Returns size of an SQL table.
%
%    DBtableIndexRow: DEPRECATED. Create a numeric row index for a table and puts it into another table.
%    DBtableRandRow: DEPRECATED. Randomly selects rows from a table using a numeric index table.
%    WriteDBtableIndex: DEPRECATED. Uses and index table to write out a table.
%    IndexAssocFiles: DEPRECATED. Inserts Assoc array files into a table and creates a row index at the same time.
%
%    ExtendPair: DEPRECATED. Complex analytic for extending pairs of keys. 
%    PairCheck: DEPRECATED. Checks if a set of string pairs is in a database table. 
%
%  Internal Functions (not user functions):
%    DBinsert: Inserts triples into a database table.
%    NumLimit: Gets the maximum number of items to return from a single query.
%    putNumLimit: Set the maximum number of items to return from a single query.
%    putNumRow: STUB. Set the maximum number of rows to return from a single query.
%    PutBytes: Get the chunk size for table inserts used by the put function.
%    putPutBytes: Set the chunk size for table inserts used by the put function.
%
%    DBsubsrefFind: DEPRECATED. Queries triples from a database table using row and column keys.
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%GRAPHULO
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  User functions:
%    AdjBFS: Out-degree-filtered Breadth First Search on Adjacency Assoc/DB.
%    EdgeBFS: Out-degree-filtered Breadth First Search on Incidence Assoc/DB.
%    Jaccard: Compute Jaccard coefficients in strict upper triangle of unweighted, undirected adjacency matrix.
%    kTrussAdj:  Compute k-Truss subgraph of Undirected, Unweighted Adjacency Assoc E.
%    kTrussEdge: Compute k-Truss subgraph of Undirected, Unweighted Incidence Assoc E.
%
%  Utility Functions:
%    Mat2Assoc: Convert matrix to Assoc with generated row and col labels.
%    Adj2Edge: Convert weighted, directed Adjacency Assoc to Incidence Assoc
%    EdgeList2Mat: Convert start and end vertex arrays into sparse incidence matrix. Eliminate self-edges and duplicate edges.
%    OutInRow2Adj: Convert Single-Table format Assoc to Adjacency Assoc.
%    Adj2OutInRow: Convert Adjacency Assoc to Single-Table format Assoc.
% 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
