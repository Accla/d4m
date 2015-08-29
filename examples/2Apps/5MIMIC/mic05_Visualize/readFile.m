function contents = readFile(fileName)
%% readFile accepts the name of a file
%% and returns a string with the contents
%% of that file
fileID = fopen(fileName,'r');
formatSpec = '%s';
contents = fscanf(fileID,formatSpec);
fclose(fileID);
end
