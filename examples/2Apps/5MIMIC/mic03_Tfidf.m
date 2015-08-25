%% This script performs a TF-IDF on the filtered edge table and writes the result to a new table.
%% TF-IDF is Term frequency - inverse document frequency

DBsetup;
existingTables = ls(DB);
disp('DBsetup complete.');

%% This section is TF-IDF
if isempty(strfind(existingTables,[tnTfidf ' ']))
    ne = G.doTfidf(tnFilteredT,tnFilteredRowDeg,-1,...
                   tnTfidf,tnTfidfT);
    fprintf('number of entries in filtered table TF-IDF is %d\n',ne);
else
    fprintf('%s already exists; skipping\n',tnTfidf);
end

