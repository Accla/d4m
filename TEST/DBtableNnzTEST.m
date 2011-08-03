% Test nnz for DBtable

rowStr = 'cat rat bat sat chat fat pat bad baa cap car pat cat bee buzz bee pats pab ';
colStr = 'a aa aaa a b bb bbb abc aab pac abc bong fat peek zing zee bang paa ';
valStr = 'a-a a-aa a-aaa a-b a-bb a-bbb a-a aa-a b-a-a c-a-p c-a-r pat-bong catfat bee-pee buzz-zing bee-zee pats-bang flute ';

% Create assoc array.
A = Assoc(rowStr,colStr,valStr);

DBsetup

tablename='DbtableNnzTEST';
T = DB(tablename);
deleteForce(T);
T = DB(tablename);
% Insert some data
    put(T,A);

num = nnz(T);
%disp(num);
assert(num == 18,['BAD!!! Result is not 18.']); 



