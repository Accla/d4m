function A = Mat2Assoc(mat,rowPrefix,colPrefix)
% Convert (sparse or full) matrix to Assoc with row and col labels '1,2,3,...,'
% Optional argument prefix for node and column labels.
% Ex. Mat2Assoc([0,7;15,2]) == Assoc('1,2,2,','2,1,2,',[7,15,2])
% Ex. Mat2Assoc([0,7;15,2],'n') == Assoc('n1,n2,n2,','n2,n1,n2,',[7,15,2])
% Ex. Mat2Assoc([0,7;15,2],'e','n') == Assoc('e1,e2,e2,','n2,n1,n2,',[7,15,2])
switch nargin
    case 1
        rowPrefix = []; colPrefix=[];
    case 2
        colPrefix = rowPrefix;
end
[row, col, val] = find(mat);
row = strrep(num2str(row.',[rowPrefix '%d,']), ' ', '');
col = strrep(num2str(col.',[colPrefix '%d,']), ' ', '');
% disp(row); disp(col);
A = Assoc(row,col,val);
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
