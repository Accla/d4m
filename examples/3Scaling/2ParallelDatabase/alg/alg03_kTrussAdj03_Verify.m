%function alg02_Jaccard_Verify(DB, G, TNadjkTruss, TNadjkTrussD4M)
util_Require('DB, G, TNadjkTruss, TNadjkTrussD4M');

LSDB = ls(DB);
if StrSearch(LSDB,[TNadjkTruss ' ']) < 1 || StrSearch(LSDB,[TNadjkTrussD4M ' ']) < 1
    error(['Please do experiments for D4M and Graphulo first: ' TNadjkTrussD4M ' and ' TNadjkTruss]);
end
TadjkTruss = DB(TNadjkTruss);
TadjkTrussD4M = DB(TNadjkTrussD4M);

JG = str2num(TadjkTruss(:,:));
JD = str2num(TadjkTrussD4M(:,:));
incorrect = abs(JG-JD) > 1e-6; % TOLERANCE

if ~isempty(incorrect)
    error('NOT EQUAL RESULTS LOCAL AND DB VERSION');
end
clear JD JG
