function [] = writeFile(data,fileName)
%% writeFile accepts data (string)
%% and writes it to a file with a
%% name provided in fileName (string)
fileID = fopen(fileName,'w');
formatSpec = '%s';
fprintf(fileID,formatSpec,data);
fclose(fileID);
end
