
% Delete All Data Tables for Scaling Tests

allTables=strsplit(ls(DB),' ');

tablesToDelete={TadjName, [TadjName 'T'], [TadjName 'Deg'],...
    TedgeName, [TedgeName 'T'], [TedgeName 'Deg'], TsingleName,...
    [TadjName 'BFS'], [TadjName 'BFST'], [TedgeName 'BFS'], [TedgeName 'BFST'], [TsingleName 'BFS'],...
    [TadjName 'Jaccard'],[TedgeName 'kTruss'],[TedgeName 'kTrussT'],[TadjName 'kTruss'],...
    [TedgeName 'NMF_H'],[TedgeName 'NMF_HT'],[TedgeName 'NMF_W'],[TedgeName 'NMF_WT']};

for i=1:length(tablesToDelete)
    if sum(~cellfun(@isempty,strfind(allTables,tablesToDelete{i})))
        deleteForce(DB(tablesToDelete{i}));
    end
end
