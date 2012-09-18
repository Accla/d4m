% Test nnz for DBtable

rowStr = 'cat1 rat2 bat3 sat4 chat5 fat6 pat7 bad8 baa9 cap10 car11 pat12 cat13 bee14 buzz15 bee16 pats17 pab18 ';
colStr = 'a aa aaa a b bb bbb abc aab pac abc bong fat peek zing zee bang paa ';
valStr = 'a-a a-aa a-aaa a-b a-bb a-bbb a-a aa-a b-a-a c-a-p c-a-r pat-bong catfat bee-pee buzz-zing bee-zee pats-bang flute ';

% Create assoc array.
A = Assoc(rowStr,colStr,valStr);

DBsetup

tablename='DbtableNnzTEST';
T = DB(tablename);
deleteForce(T);
pause(0.5);
T = DB(tablename);
% Insert some data
    put(T,A);
pause(1);
num = nnz(T);
T=close(T);
disp([' Num entries = ' num2str(num)]);
assert(num == 18,['BAD!!! Result is not 18, result= ' num2str(num)]); 
deleteForce(T);


