if 1
  DBsetup;  % Setup database DB.
  %DBsetupSKSdev1;  % Setup database DB.
 
  % Delete and create table.
  T1 = DB('iTest1');  deleteForce(T1); T1 = DB('iTest1');
  T2 = DB('iTest2','iTest2T');  deleteForce(T2); T2 = DB('iTest2','iTest2T');

  AssocSetup;  % Create assoc array A.
  put(T1,A);
  put(T2,A);
  pause(2);
end
%DBsetupSKSdev1; T1 = DB('iTest1'); T2 = DB('iTest2','iTest2T');

AssocSetup;  % Create assoc array A.

Tlist = {A T1 T2};

for Tcell = Tlist
  T = Tcell{1};

  AT = T(:,:);

  % Check to see if there are any global differences.
  A - AT

  qclass = {':' 'a ' 'b ' StartsWith('a ')  StartsWith('b ')  'a b '};

  Qfail = 0;
  disp([class(T) ' NUMLIMIT = 0']);
  for i = 1:numel(qclass)
    for j = 1:numel(qclass)
      AT = T(qclass{i},qclass{j});
      Qdiff = double(A(qclass{i},qclass{j})) - double(AT);
      disp(['Query: T(' qclass{i} ',' qclass{j} '): num incorrect = ' num2str(nnz(Qdiff))]);
      if (nnz(Qdiff))
%keyboard  
      end
      Qfail = Qfail + double(logical(nnz(Qdiff)));
    end
  end

  disp([class(T) ' NUMLIMIT = 1']);
  Ti = Iterator(T,'elements',1);
  for i = 1:numel(qclass)
    for j = 1:numel(qclass)
      AT = Assoc('','','');
      ATi = Ti(qclass{i},qclass{j});
      while numel(ATi)
        AT = AT + ATi;
        ATi = Ti();
      end
      Qdiff = double(A(qclass{i},qclass{j})) - double(AT);
      disp(['Query: T(' qclass{i} ',' qclass{j} '): num incorrect = ' num2str(nnz(Qdiff))]);
      if (nnz(Qdiff))
%keyboard  
      end
      Qfail = Qfail + double(logical(nnz(Qdiff)));
    end
  end
end

disp([num2str((numel(Tlist) * 2 * numel(qclass).^2) - Qfail) ' / ' num2str((numel(Tlist) * 2 * numel(qclass).^2)) ' PASSED']);
T1 = close(T1);
T2 = close(T2);
deleteForce(T1);
deleteForce(T2);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
