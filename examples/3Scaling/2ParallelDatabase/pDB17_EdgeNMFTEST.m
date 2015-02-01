%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Compute NMF E = W*H on Incidence Assoc from k-Truss file.
% Motivating Question: Do topics change before/after taking k-Truss?
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Prerequisite: pDB16_EdgeTrussTEST
echo('off'); more('off')                     % No echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% E is original incidence Assoc from pDB16_EdgeTrussTEST.
% Etruss is k-Truss of E from pDB16_EdgeTrussTEST (k given as ktruss).

ktraits = 5;                % # of traits

tic;                        % NMF on E
[W,H] = NMF(E,ktraits);
OrigNMFTime = toc; fprintf('NMF calc time on %d nodes, %d edges, %d traits: %f\n', ...
    NumStr(Row(E)),NumStr(Col(E)),ktraits,OrigNMFTime)

tic;                        % NMF on Etruss
[Wtruss,Htruss] = NMF(Etruss,ktraits);
TrussNMFTime = toc; fprintf('NMF calc time on %d-truss of %d nodes, %d edges, %d traits: %f\n', ...
    ktruss,NumStr(Row(Etruss)),NumStr(Col(Etruss)),ktraits,TrussNMFTime)

% Get trait membership for edges in both Etruss and E; see if they differ.
NumTop = 1;                             % # of top traits per edge/node.
Wsubset = W(Row(Wtruss),:);             % Only use edges in both W and Wtruss.

%edgeDiff = Wsubset - Wtruss;  % <-- Not a good way to compare; traits may be permuted. 

WTopTrait = TopColPerRow(Wsubset,NumTop);    % Compute top traits for W.
WtrussTopTrait = TopColPerRow(Wtruss,NumTop);% Compute top traits for Wtruss.
% If NumStr(Col(WTopTrait)) < ktraits || NumStr(Col(WtrussTopTrait)) < ktraits,
%   then ktraits may be too high; one of the k traits has no dominant edge.

% Concatenate each trait's top edges; display original and truss side-by-side.
helperFun = @(A,typ) reAssoc(putCol(CatKeyMul(A,A.'),repmat(typ,1,NumStr(Row(A)))));
Wcompare = helperFun(WTopTrait.','origEdge,') + helperFun(WtrussTopTrait.','trusEdge,');
fprintf('Top %d Edges per Trait:\n',NumTop); displayFull(Wcompare)

% Now do the same as above for nodes.
Hsubset = H(:,Col(Htruss));
HTopTrait = TopRowPerCol(Hsubset,NumTop);
HtrussTopTrait = TopRowPerCol(Htruss,NumTop);
Hcompare = helperFun(HTopTrait,'origNode,') + helperFun(HtrussTopTrait,'trusNode,');
fprintf('Top %d Nodes per Trait:\n',NumTop); displayFull(Hcompare)

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

