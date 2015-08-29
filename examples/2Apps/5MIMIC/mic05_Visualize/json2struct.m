function [vars,varNames] = json2struct(json)
%json2struct takes json string of key-value pairs and outputs struct with
%variables defined in json
%   

newVars=strsplit(json,',');
varNames=cell(length(newVars),1);

for i=1:length(newVars)
    thisPair=strsplit(newVars{i},':');
    if length(thisPair) > 2 % Correct for any colons in 'value'
        thisPair{2} = strjoin(thisPair(2:end),':');
    end
    thisVar=strrep(strrep(strrep(thisPair{1},'"',''),'{',''),' ','');
    thisVal=strrep(strrep(strrep(thisPair{2},'"',''),'}',''),' ','');
    
    eval(['vars.' thisVar ' = thisVal;'])
    varNames{i}=thisVar;
end

end

