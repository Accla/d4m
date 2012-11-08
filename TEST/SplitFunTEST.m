%testSplitFun tests split functions
%DB = DBsetup;
DBsetup;
DBstruct = struct(DB);

if  strcmp(DBstruct.type,'Accumulo')

  T = DB('testSplitDemo');
  try
     disp('---adding splits aa;p; ...');
     addSplits(T, 'aa;p;');
     disp('Here are the crurrent splits:');
     disp(getSplits(T));

     disp('---adding splits b;cde;w2; ...');
     addSplits(T, 'b;cde;w2;');
     disp('Here are the crurrent splits:');
     disp(getSplits(T));

     disp('---putting splits (replacing old splits with) b;g;w;y;');
     putSplits(T,'b;g;w;y;');
     disp('Here are the crurrent splits:');
     disp(getSplits(T));

     disp('---putting no splits');
     putSplits(T,'');
     %same effect as mergeSplits(T,[],[]);
     disp('Here are the crurrent splits:');
     disp(getSplits(T));
    
     deleteForce(T);
     clear T;
  catch E
     deleteForce(T);
     clear T;
     throw(E);
  end

end
