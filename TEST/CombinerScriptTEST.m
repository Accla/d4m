%DB = DBsetup;
DBsetup;
T = DB('combineDemoTable');
try
    disp('About to add:');
    A = Assoc('r1;r1;','maxc1;sumc1;','2;5;')
    put(T,A);
    disp('Result:');
    T(:,:)
    
    if ~exist('N','var')
        N = 1000;  % Set the number of entries to insert if not yet set outside script.
    end
    
    fprintf('\n------------------------------\n');
    fprintf('Designating columns with combiners:\n');
%    designateCombiningColumns(T,'maxc1;','max');
%    designateCombiningColumns(T,'sumc1;','sum');
    T = addColCombiner(T,'maxc1;','max');
    T = addColCombiner(T,'sumc1;','sum');

%    disp(listCombiningColumns(T));
    disp(ColCombiner(T));
    fprintf(1, 'About to add %d of:\n', N);
    A = Assoc('r1;r1;','maxc1;sumc1;','1;4;') %for display purposes
    r = repmat('r1;r1;',1,N);
    c = repmat('maxc1;sumc1;',1,N);
    v = repmat('1;4;',1,N);
    
    %put(T,A);
    tic;
    putTriple(T,r,c,v);
    putTime = toc;
    
    insertRate = 2 .* N ./ putTime;
    
    disp('Result:');
    T(:,:)
    fprintf(1, 'INSERT RATE: %g\n', insertRate);
    expectMax = 2;
    expectSum = 5+N*4;
    actualMax = str2num(Val(T(:,'maxc1;')));
    actualSum = str2num(Val(T(:,'sumc1;')));
    if expectMax ~= actualMax
        disp('WARNING: maxc1 has unexpected output');
    end
    if expectSum ~= actualSum
        disp('WARNING: sumc1 has unexpected output');
    end
    
    fprintf('\n------------------------------\n');
    disp('Revoking combining columns:');
%    revokeCombiningColumns(T,'maxc1;sumc1;');
%    disp(listCombiningColumns(T));
    T = deleteColCombiner(T,'maxc1;sumc1;');
    disp(ColCombiner(T));
    disp('About to add:');
    A = Assoc('r1;r1;','maxc1;sumc1;','1;4;')
    put(T,A);
    disp('Result:');
    T(:,:)
    
    deleteForce(T);
    clear T;
catch E
    deleteForce(T);
    clear T;
    throw(E);
end
