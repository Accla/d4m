% Example code to illustrate  Java usage in Octave
% This example will make  a LinkedList
% It will add 3 strings (BOO, FOO, ZOO) to the list
disp('Instantiate LinkedList called  ll ')
ll = java_new('java.util.LinkedList');
%  Call 'add' method to add 'BOO'.
% Java equivalent call is  ll.add("BOO")
disp('Add BOO')
java_invoke(ll,'add','BOO'); 
disp('Add FOO')
java_invoke(ll,'add','FOO');
disp('Add ZOO')
%java_invoke(ll,'add','ZOO');
ll.add('ZOO');
disp('Invoke method size....')
size=num2str(ll.size());
disp(['Size of linked list = ',size]);
