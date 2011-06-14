function Ax12o_x12 = SemanticPairExtend(T,PairList,PairReplace,ColumnClutter)
      %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
      % Semantic extension of pairs using type data.
      %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
      % Reuters Column Types.
      % (1) TIME/,  (2) TIMELOCAL/, (3) NE_ORGANIZATION/, (4) NE_PERSON/,
      % (5) NE_PERSON_GENERIC/, (6) NE_PERSON_MILITARY/, (7) GEO/, (8) NE_LOCATION/,
      % (9) ProperName/,
      % = replace,  + replace append, ~ extend replace, * extend append
      % ProperName/|=NE_PERSON/|;
      % |ProperName/|+NE_PERSON_MILITARY/;
      % |ProperName/|=NE_LOCATION/;
      % |TIME/|~NE_PERSON/;
      % |NE_ORGANIZATION/|~NE_PERSON/;
      % |NE_PERSON/|~NE_PERSON_MILITARY/;
      % |NE_PERSON/|~NE_LOCATION/;
      % |NE_PERSON_MILITARY/|NE_LOCATION/|*NE_PERSON_GENERIC/|GEO/;
      % |TIME/|TIMELOCAL/|NE_ORGANIZATION/|NE_PERSON/|*TIME/|TIMELOCAL/|NE_ORGANIZATION/|NE_PERSON/;

      % PairCross.
      % TIME/|TIMELOCAL/|NE_ORGANIZATION/|NE_PERSON/|*NE_PERSON_GENERIC/|NE_PERSON_MILITARY/|;
      % |TIME/|TIMELOCAL/|NE_ORGANIZATION/|NE_PERSON/|*GEO/|NE_LOCATION/;

      PairSep = '|';  TypeSep = '/'; nl = char(10); cr = char(13); Operator = '=+~*';


      % Parse query arguments.
      x12o = PairList;
      PairReplaceMat = Str2mat(PairReplace);

      % Create starting pair mapping matrix.
      Ax12o_x12 = Assoc(x12o,x12o,1);

      % Append flips.
      [x1 x2] = SplitStr(Col(Ax12o_x12),PairSep);
      x12 = CatStr(x2,PairSep,x1);
      Ax12o_x12 = Ax12o_x12 + reAssoc(putCol(Ax12o_x12,x12));

      % Loop through each PairReplace syntax.
      for i=1:NumStr(PairReplace)

%disp(num2str(i));

        iPairReplace = Mat2str(PairReplaceMat(i,:));
        iPairReplace = iPairReplace(1:end-1);

        % Determine which side of pair this syntax is working on.   
        left = 1; right = 0;
        if (iPairReplace(1) == PairSep)
          left = 0; right = 1;
          iPairReplace = [iPairReplace(2:end) PairSep];
        end

        % Determine operation and split.
        for j=1:numel(Operator)
          if nnz(iPairReplace == Operator(j))
            iOp = Operator(j);
            [iPairReplace1 iPairReplace2] = SplitStr([iPairReplace nl],iOp);
            iPairReplace1 = iPairReplace1(1:end-1);
            iPairReplace1 = iPairReplace1(iPairReplace1 ~= TypeSep);
            iPairReplace2 = iPairReplace2(1:end-1);
            iPairReplace2 = iPairReplace2(iPairReplace2 ~= TypeSep);
          end
        end

       x12 = Col(Ax12o_x12);
       [x1 x2] = SplitStr(x12,PairSep);  % Split pair.
       AxTypeOld = Assoc(x12,x2,1);    % Make associative array of pair.
       if left 
         AxTypeOld = Assoc(x12,x1,1);;   % Flip.
       end
       AxTypeOld = col2type(AxTypeOld,TypeSep);    % Split types and put in columns.
       AxTypeOldSub = AxTypeOld(:,iPairReplace1);    % Select column type.

       % Check if there is anything that needs changing.
       if nnz(AxTypeOldSub)
         [rOld cOld tmp] = find(val2col(AxTypeOldSub,TypeSep));  % Get pairs.
         [rOld1 rOld2] = SplitStr(rOld,PairSep);  % Split pair.

         % Put old sides back together.
         if left
           x12old = CatStr(cOld,PairSep,rOld2);
         else
           x12old = CatStr(rOld1,PairSep,cOld);
         end
         Ax12o_x12old = Ax12o_x12(:,x12old);   % Get selected pairs.
         Ax12o_x12oldMinus = Ax12o_x12 - Ax12o_x12old;    % Get unselected pairs.
         % Simple type expension.
         if ((iOp == '=') | (iOp == '+'))
           AxTypeNewSub = reAssoc(putCol(AxTypeOldSub,iPairReplace2));   % Replace column type.
           [rNew cNew tmp] = find(val2col(AxTypeNewSub,TypeSep));   % Get pairs.
           [rNew1 rNew2] = SplitStr(rOld,PairSep);  % Split pair.

           % Put new sides back together.
           if left
             x12new = CatStr(cNew,PairSep,rNew2);
           else
             x12new = CatStr(rNew1,PairSep,cNew);
           end

           Ax12old_x12new = Assoc(x12old,x12new,1);   % Create transformation matrix.

           if (iOp == '=')
             Ax12o_x12 = Ax12o_x12oldMinus + Ax12o_x12old*Ax12old_x12new;  % Transform and replace.
           end
           if (iOp == '+')
             Ax12o_x12 = Ax12o_x12 + Ax12o_x12old*Ax12old_x12new;  % Transform and append.
           end
         end

         % Complex query based replacement.
         if ((iOp == '~') | (iOp == '*'))
           iPairReplace1search = CatStr(iPairReplace1,TypeSep,'_*,');
           iPairReplace2search = CatStr(iPairReplace2,TypeSep,'_*,');
           Ax12o_x12new = ExtendPair(Ax12o_x12,iPairReplace1search,PairSep,T,iPairReplace2search,left,ColumnClutter);
           if (iOp == '~')
             Ax12o_x12 = Ax12o_x12oldMinus + Ax12o_x12new;  % Transform and replace.
           end
           if (iOp == '*')
             Ax12o_x12 = Ax12o_x12 + Ax12o_x12new;  % Transform and append.
           end
         end

       end
%       Ax12o_x12
     end

% keyboard

end
