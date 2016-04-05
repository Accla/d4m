function alg02_Jaccard_Verify(DB, G, TNadjJaccard, TNadjJaccardD4M)

LSDB = ls(DB);
if StrSearch(LSDB,[TNadjJaccard ' ']) < 1 || StrSearch(LSDB,[TNadjJaccardD4M ' ']) < 1
    error(['Please do experiments for D4M and Graphulo first: ' TNadjJaccardD4M ' and ' TNadjJaccard]);
end
TadjJaccard = DB(TNadjJaccard);
TadjJaccardD4M = DB(TNadjJaccardD4M);

JG = str2num(TadjJaccard(:,:));
JD = str2num(TadjJaccardD4M(:,:));
correct = abs(JG-JD) > 1e-6; % TOLERANCE

if ~correct
    error('NOT EQUAL RESULTS LOCAL AND DB VERSION');
end
