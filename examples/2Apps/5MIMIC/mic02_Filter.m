%% The goal of this script is to filter the large dataset down to a size
%% that is manageable in NMF.  We also eliminate columns we are not interested in.
%% We do this via medical dictionary filtering and degree filtering.

DBsetup;
existingTables = ls(DB);
disp('DBsetup complete.');

% Params
fileMedDict = 'medDict.txt';
minDegree = 2;
maxDegree = 1000000;


if ~isempty(strfind(existingTables,[tnFiltered ' ']))
    fprintf('Materialized table subset %s already exists. Skipping.\n',tnFiltered);
else
    %% Get words from the medical dictionary to use as a rowFilter
    medWords = readFile(fileMedDict);
    
    %% Among only the words in the medical dictionary, filter away the ones 
    %% that apear in less than minDegree documents or greater than maxDegree documents.
    degMap = java.util.HashMap();
    scalarTypeLONG = JavaInnerEnum(edu.mit.ll.graphulo.simplemult.MathTwoScalar(), 'ScalarType', 'LONG');
    minMaxFilter = edu.mit.ll.graphulo.skvi.MinMaxFilter.iteratorSetting(1,scalarTypeLONG,...
                                                      java.lang.Integer(minDegree),java.lang.Integer(maxDegree));
    itList = java.util.Collections.singletonList(minMaxFilter);
    numWords = G.OneTable(tnEdgeDeg,[],[],degMap,50,[],[],[],medWords,[],itList,[],[]);
    fprintf('Number of words after filtering is %d\n',numWords);
    
    %% Words that pass the filtering step are in rows of entries in degMap
    %% The following code extracts the words that passed filtering
    wordsAfterFilter = '';
    setRowsIter = degMap.keySet().iterator();
    nl = ''; 
    while (setRowsIter.hasNext())
        wordsAfterFilter = [wordsAfterFilter nl char(setRowsIter.next().getRow().toString())];
        nl = char(10);
    end
    wordsAfterFilter = [wordsAfterFilter nl]; % D4M string with the words we want to keep, after filtering both medDict and degree

    %% The following code materializes a subset of the original edge table that only includes columns 
    %% with the words that passed the filtering step above.
    noDupsIter = java.util.Collections.singletonList(...
        edu.mit.ll.graphulo.skvi.NoConsecutiveDuplicateRowsIterator.combinerSetting(1));
    numEntriesFiltered = G.OneTable(tnEdgeT,tnFilteredT,tnFiltered,...
                                    [],50,[],[],[],wordsAfterFilter,[],noDupsIter,[],[]);
    fprintf('Materialized table subset in %s and %s. NumEntries in each is %d\n',tnFiltered, tnFilteredT, numEntriesFiltered);

    %% Now save the patientIDs for which we have at least one document containing at least one word that passed filtering.
    %% This is used in the visualization step: displaying a list of patients in our filtered data set.
    
    % This is what the code would look like if we used only D4M (and not Graphulo)
    %Tfilter = DB(tnFiltered);
    %rowIDs = Row(Tfilter(:,:));
    %tmp = Tedge(rowIDs,:);
    %patientIDs = Col(tmp(:,StartsWith('patientID|,')));
    
    gatherColQ = edu.mit.ll.graphulo.reducer.GatherReducer();
    keyPartCOLQ = JavaInnerEnum(gatherColQ, 'KeyPart', 'COLQ');
    gatherColQOpts = edu.mit.ll.graphulo.reducer.GatherReducer.reducerOptions(keyPartCOLQ);
    gatherColQ.init(gatherColQOpts,[]);
    G.TwoTableROWSelector(tnFiltered,tnEdge,[],[],50,[],[],...
                          StartsWith('patientID|,'),true,...
                          [],[],[],gatherColQ,gatherColQOpts,-1,[],[]);
    
    % The patientIDs are in the gatherColQ object. This code extracts them into the variable patientIDs.
    setCols = gatherColQ.getSerializableForClient();
    setColsIter = setCols.iterator();
    patientIDs = ''; nl = char(10);
    while (setColsIter.hasNext())
        patientIDs = [patientIDs nl char(setColsIter.next())];
    end
    patientIDs = [patientIDs nl];
    numPatients = NumStr(patientIDs);
    fprintf('# of patients is %d\n',numPatients);

    % Save the patientIDs to a file
    [patientIDs,~] = SplitStr(patientIDs,'|');
    patientIDs = strrep(patientIDs, char(10), ',');
    iname = struct(DB).instanceName;
    fileName = ['patientIDs_' iname '.csv'];
    writeFile(patientIDs,fileName);
    disp(['Patient IDs have been written to ' fileName]);
end

%% This section creates the appropriate degree table for the records (used in TF-IDF step)
if isempty(strfind(existingTables,[tnFilteredRowDeg ' ']))
    numRecords = G.generateDegreeTable(tnFiltered,tnFilteredRowDeg,false);
    fprintf('Number of records in degree table is %d\n',numRecords);
else
    fprintf('%s already exists; skipping\n',tnFilteredRowDeg);
end




