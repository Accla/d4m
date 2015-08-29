function [docID,nTopTopics,nTopWords] = parseQuery(query)
%% This function accepts a query (string)
%% and returns the three expected fields
%% as a string (docID) and doubles (nTopTopics, nTopWords)
jsonQuery = json2struct(query);
docID = [jsonQuery.docID ','];
nTopTopics = str2num(jsonQuery.numTopics);
nTopWords = str2num(jsonQuery.numWords);
end
