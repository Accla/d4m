function A = Struct2Assoc(S, splitSep)
% Function Struct2Assoc converts a struct to associative array
% Associative array utility funciton
%  Usage:
%    A = StrFileWrite(S,varargin)
%  Inputs:
%    S = struct to convert
%    splitSep = single character separator
%  Outputs:
%    A = Associative Array output. Row key corresponds with struct id (for
%    structs greater than 1 dimension)
%

structfields=fields(S);
A=Assoc('','','');

for i=1:size(S,2)
    Stmp=S(i);
    for j=1:numel(structfields)
        A = A+Assoc(['StructIdx|' num2str(i) ','], [structfields{j} splitSep num2str(eval(['Stmp.' structfields{j}])) ','], '1,');
    end
end

