%% This script exploits the smaller matrices W and H
%% from a non-negative matrix factorization routine
%% to identify the top topics for a given document
%% and the top words for each topic

% params    
inputFile = 'input.json';
outputFile = 'output.json';

% Sample input.json: {"docID": "00021_recordTime_3138-11-10-00:00:00.0_recordNum_5_recordType_RADIOLOGY_REPORT.txt", "numTopics": "3", "numWords": "10"} 

[outputFilePath,outputFileName,outputFileExt] = fileparts(outputFile);

% Process input file
query = readFile(inputFile);
[docID,nTopTopics,nTopWords] = parseQuery(query);

% Load tables
DBsetup;
DBs = struct(DB);
dbName = DBs.instanceName;

% Extract w (topic distribution for given doc)
tW = DB(tnW);
w = tW(docID,:);

% Identify top topics
topTopics = identifyTopTopics(num2str(w),nTopTopics);

for iTopTopic = 1:nTopTopics
        % Extract h (word distribution for given topic)
        topic = topTopics{iTopTopic};
        topicName = [topic.topicName ','];  % Note: hardcoded delimiter
        tH = DB(tnH);
        h = tH(topicName,:);

        % identify top words per topic
        topWords = identifyTopWords(num2str(h),nTopWords);

        % construct output JSON
        outputJSON = makeJson(topic,topWords);

        % write output file
        outputFileI = [outputFileName num2str(iTopTopic) outputFileExt];
        writeFile(outputJSON,outputFileI);
        fprintf('output written to %s\n',outputFileI)
end
 
