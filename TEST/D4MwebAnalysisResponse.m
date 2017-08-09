function queryJSONCSV = D4MwebAnalysisResponse(queryJSONCSV)
% D4MwebAnalysisResponse: Framework for interacting with multiple analysis techniques.
% TODO: How to handle different Time/Seed combos (may need Tables with row+col query).
% TODO: Debug Semantic/Seed/Graph/

  J = parseJSON(queryJSONCSV);
  Aq = JSONCSV2assoc(queryJSONCSV);

  Ar = D4ManalysisResponse(Aq);

  queryJSONCSV = Assoc2JSONCSV(Ar,J.rowSeparator,J.columnSeparator,'QueryResponse');
  %queryJSONCSV = Ar;

end

