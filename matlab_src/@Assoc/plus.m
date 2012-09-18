function AB = plus(A,B)
%+,plus: Add two associative arrays.
%Associative array user function.
%  Usage:
%    AB = A + B
%    AB = plus(A,B)
%  Inputs:
%    A = associative array
%    B = associative array
%  Outputs:
%    AB = associative array that is the union of the of the keys of A and B

  % Short circuit if either is empty.
  if (nnz(A) == 0) && (nnz(B) == 0)
     AB = Assoc('','','');
     return;
  end
  if (nnz(A) == 0) && (nnz(B) > 0)
     AB = B;
     return;
  end
  if (nnz(A) > 0) && (nnz(B) == 0)
     AB = A;
     return;
  end


  % deal with DB table.
  if IsClass(A,'DBtable')
     AB = put(A,B);
     return;
  end
  if IsClass(B,'DBtable')
     AB = put(B,A);
     return;
  end

  % Deal with value type mismatches.
  if ( not(isempty(A.val)) && not(isempty(B.val)) )
%  if ( not(isempty(A.val)) || not(isempty(B.val)) )
    AB = or(A,B);
  else
    if (not(isempty(A.val)) && nnz(B))
       A = double(logical(A));
    end
    if (nnz(A) && not(isempty(B.val)))
       B = double(logical(B));
    end

    %AB = Pluslike(A,B,@sum);

    if 1
      [NA MA] = size(A.A);
      [NB MB] = size(B.A);

      AB = A;

      AB.col = B.col;

      AB.val = '';

      AdjA = A.A;
      AdjB = B.A;

      if (isempty(A.row) || isempty(B.row))
        AB.row = '';
        NAB = max(NA,NB);
        if (NA < NAB)
          AdjA(NAB,end) = 0;
        end
        if (NB < NAB)
          AdjB(NAB,end) = 0;
        end
      else
        [Arow Brow] = StrSepsame(A.row,B.row);
        [AB.row in2out out2in] = StrUnique([Arow Brow]);
        NAB = NumStr(AB.row);
        if (NA < NAB)
%          AdjA1 = sparse(NAB,size(AdjA,2),nnz(AdjA));
%          AdjA1(out2in(1:NA),:) = AdjA;
%          AdjA = AdjA1;
          AdjA1 = spalloc(size(AdjA,2),NAB,nnz(AdjA));
          AdjA1(:,out2in(1:NA)) = AdjA.';
          AdjA = AdjA1.';
        end
        if (NB < NAB)
%          AdjB1 = sparse(NAB,size(AdjB,2),nnz(AdjB));
%          AdjB1(out2in(NA+1:end),:) = AdjB;
%          AdjB = AdjB1;
          AdjB1 = spalloc(size(AdjB,2),NAB,nnz(AdjB));
          AdjB1(:,out2in(NA+1:end)) = AdjB.';
          AdjB = AdjB1.';
        end
      end

      if (isempty(A.col) || isempty(B.col))
        AB.col = '';
        MAB = max(MA,MB);      
        if (MA < MAB)
          AdjA(end,MAB) = 0;
        end
        if (MB < MAB)
          AdjB(end,MAB) = 0;
        end
      else
        [Acol Bcol] = StrSepsame(A.col,B.col);
        [AB.col in2out out2in] = StrUnique([Acol Bcol]);
        MAB = NumStr(AB.col);
        if (MA < MAB)
          AdjA1 = spalloc(size(AdjA,1),MAB,nnz(AdjA));
          AdjA1(:,out2in(1:MA)) = AdjA;
          AdjA = AdjA1;
        end
        if (MB < MAB)
          AdjB1 = spalloc(size(AdjB,1),MAB,nnz(AdjB));
          AdjB1(:,out2in(MA+1:end)) = AdjB;
          AdjB = AdjB1;
        end
      end

      AB.A = AdjA + AdjB;

      % Check for empty rows or columns and correct.
%      rowSum = sum(double(logical(AB.A)),2);
%      colSum = sum(double(logical(AB.A)),1);
      rowSum = sum(double(AB.A ~= 0),2);
      colSum = sum(double(AB.A ~= 0),1);
      if  nnz(rowSum == 0)
        rowOK = find(rowSum > 0);
        AB.A = AB.A(rowOK,:);
        if not(isempty(AB.row))
          rowMat = Str2mat(AB.row);
          AB.row = Mat2str(rowMat(rowOK,:));
        end
      end
      if  nnz(colSum == 0)
        colOK = find(colSum > 0);
        AB.A = AB.A(:,colOK);
        if not(isempty(AB.col))
          colMat = Str2mat(AB.col);
          AB.col = Mat2str(rowMat(colOK,:));
        end
      end

    end
 
  end

%keyboard
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

