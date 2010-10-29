%
%Example code to illustrate how to use java in Octave
% In this example, I connect with the cloud and get a list 
% of tables.
% To run this example,
% 1. Change directory to the D4M_HOME/matlab_src and start octave.
% 2.  Run DBinit script at the octave prompt to setup the javaaddpath
%     octave>   DBinit
% 3.  Run the example at the octave prompt, ie,
%     octave>  DbInfoOctaveExample
%
instance='cloudbase';
host='f-2-10.llgrid.ll.mit.edu';
user='root';
password='secret';
%% Instantiate the D4mDbInfo object using the java_new
dbinfo=java_new('edu.mit.ll.d4m.db.cloud.D4mDbInfo',instance,host,user,password);

% You can invoke a method in an object either using java_invoke(OBJECT,'name of method to invoke', 'option arguments') or via the object.
% Here I call the getTableList member method by using java_invoke.
tableList = java_invoke(dbinfo,'getTableList');

%% Similarly, i can get a list of table names by invoking the method by the usual java syntax - dbinfo.getTableList()
%    tableList = dbinfo.getTableList();  

disp(['List of tables [ ',host, ' ] -  ',tableList]) 
