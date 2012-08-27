%AssocCatStrFunc: Concatenates strings inside a matrix multiply.
%Associative array internal function for CatKeyMul and CatValMul.
%  Usage:
%    j = AssocCatStrFunc(i)
%  Inputs:
%    i = set of indices into AssocOldValStrMatGlobal
%  Outputs:
%    j = an index into AssocOldValStrGlobal
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%ASSOCIATIVE ARRAY AND DATABASE TABLE
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  User Functions:
%    StartsWith: Converts a list of strings into a list of ranges that can be used in an query.
%
%  Utility Functions:
%    IsClass: Tests if an object is a specific classname.
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
%    CatKeyMul: Perform matrix multiply and concatenate colliding row/col keys into the value.
%    CatValMul: Perform matrix multiply and concatenate colliding values.
%
%    col2type: Splits column keys of associative array and stores first part as column key and second part as value; inverse of val2col. 
%    val2col: Append associative array values to column keys; inverse of col2type. 
%    col2val: Splits column keys of associative array and stores second part as column key and first part as value. 
%
%  Utility Functions:
%    putAssoc: Constructs all elements of an associative array from its components.
%
%    MyEcho: Echo back the given command. Use $ for quoting strings. Useful for demos.
%
%  Internal Functions (not user functions):
%    AssocCatStrFunc: Concatenates strings inside a matrix multiply.
%
%    randiTmp: DEPRECATED. Stub for randi if it isn't available.
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%STRING LIST
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%  User Functions:
%    NumStr: Counts the number of strings in a string list.
%
%    CatStr: Concatenates two string lists with a separator; inverse of StrSplit.
%    SplitStr: Uncats list of strings into two separate lists of strings; inverse of CatStr.
%    KronCatStr: Concatenates the pairwise permutations of two string lists with a separator.
%
%    Str2mat: Converts list of strings to char matrix; inverse of Mat2str.
%    Mat2str: Converts char matrix to a list of strings; inverse of Str2mat.
%
%    CatTriple: Appends r, c, v and rr, cc, vv. Assumes each pair has same type.
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
%    put: Inserts data into a database table.
%    Iterator: Creates a new iterator inside of a table and sets its limits.
%
%  Utility Functions:
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
%    DBaddJavaOps: Adds a java database operation.
%    DBcreate: Create a table on a specific database.
%    DBcreate: Delete a table on a specific database.
%    DBinsert: Inserts triples into a database table.
%    DBsetupLLGrid: Create database binding on LLGrid.
%    putNumLimit: Set the maximum number of items to return from a single query.
%    putNumRow: STUB. Set the maximum number of rows to return from a single query.
%
%    DBsubsrefFind: DEPRECATED. Queries triples from a database table using row and column keys.
%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
