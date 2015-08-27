function outputJSON = makeJson(topic,topWords)
%% makeJson accepts a topic and the topWords in that topic
%% and outputs a json object with tuples where each tuple
%% consists of (topicName, topicWeight, wordName, wordWeight)
    nTopWords = length(topWords);
    topicName = topic.topicName;
    topicWeight = topic.topicWeight;
    tuples = '';
    for iWord = 1:nTopWords
        word = topWords{iWord};
        wordName = word.wordName;
        wordWeight = word.wordWeight;
        tuple = ['["' topicName '",' num2str(topicWeight) ',"' wordName '",' num2str(wordWeight) ']'];
        tuples = [tuples tuple ','];
    end
    outputJSON = ['{"schema":["topicName","topicWeight","wordName","wordWeight"],"tuples":[' tuples(1:end-1) ']}'];
    % Example: outputJSON = '{"tuples":[["topic|1",0.365,"blue",.781],["topic|2",0.415,"red",.221]],"schema":["topicID","topicWeight","wordString","wordWeight"]}
end
