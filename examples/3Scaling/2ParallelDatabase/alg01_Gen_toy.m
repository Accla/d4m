% toy data for testing

Amat = [0 1 1 1 0; % Input adjacency matrix
     1 0 1 0 1;
     1 1 0 1 0; 
     1 0 1 0 0;
     0 1 0 0 0];
A = Mat2Assoc(Amat,'v');
Ttoy = DB('toy_TgraphAdj','toy_TgraphAdjT');
As = num2str(A);
put(Ttoy,As)

TtoyDeg = DB('toy_TgraphAdjDeg');
Asout = putCol(num2str(sum(A,2)),'OutDeg,');
Asin = putCol(num2str(sum(A,1)).','InDeg,');
put(TtoyDeg, Asout);
put(TtoyDeg, Asin);

