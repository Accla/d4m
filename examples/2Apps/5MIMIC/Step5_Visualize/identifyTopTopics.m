function topTopics = identifyTopTopics(w,nTopTopics)
%% This function accepts w (D4M associative array) and
%% nTopTopics (double), and returns as many topics (struct)
%% as nTopTopics. Each topic has a name (string) and weight (double).
    topTopics = cell(1,nTopTopics);
    topTopicsAssoc = TopColPerRow(w,nTopTopics);
    [r,c,v] = find(topTopicsAssoc);
    delimiter = c(end);
    topTopicNames = strsplit(c,delimiter);
    for iTopic = 1:nTopTopics
        topicName = topTopicNames{iTopic};
        topicWeight = v(iTopic);
        topTopics{iTopic} = struct('topicName',topicName,'topicWeight',topicWeight);
    end
end
