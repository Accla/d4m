%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Show abstract algebra properties of associative arrays.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('on'); more('off');          % Turn on echoing and paging.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
nl = char(10);

AfuncRange = noRow(ReadCSV('./Range/FuncRange.csv'));   % Read in function ranges.
Afunc = PermuteRange(AfuncRange);                 % Create function permutations.
Assoc2CSV(Afunc,nl,',','./Func/AllF.csv');        % Write results.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% Semigroup                           %%%

AfuncAssociate = AssociateTest(Afunc);            % Associative.
Aa = Afunc(Row(AfuncAssociate),:);                % Select functions.
Assoc2CSV(Aa,nl,',','./Func/SemigroupF.csv');     % Write results.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% Commutative Semigroup               %%%

AfuncCommute = CommuteTest(Afunc);                % Commutative.
Ac = Afunc(Row(AfuncCommute),:);                  % Select functions.

% Associative and commutative.
Aca = Afunc(Row(noCol(AfuncCommute) & noCol(AfuncAssociate)),:);
Assoc2CSV(Aca,nl,',','./Func/CommSemigroupF.csv');  % Write results.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% Commutative Semiring                %%%

AfuncDist = DistributeTest(Aca,Aca);              % Distributive.
Assoc2CSV(AfuncDist,nl,',','./Func/CommSemiringF.csv');    % Write results.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% Commutative Monoid                 %%%

AfuncI = IdentityTest(Aca);                       % Identity (Commutative Monoid).
AfuncO = AnnihilatorTest(Aca);                    % Annihilator.
Acaio = Aca + AfuncI + AfuncO;
Assoc2CSV(Acaio,nl,',','./Func/CommMonoidF.csv');    % Write results.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%% Field wo/Inverse (Feld)             %%%

AfuncIO = IOTest(AfuncI,AfuncO);                  % Identity/Annihilator test.
AfuncFeld = AfuncIO(logical(AfuncDist & AfuncIO));   % Combine.
%AfuncFeld = putVal(dblLogi(AfuncDist),'-,') + AfuncSemiringWiki;
Assoc2CSV(AfuncFeld,nl,',','./Func/FeldF.csv');    % Write results.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%