%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Insert edge data into an incidence matrix.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
DBsetup;                                         % Create binding to database and tables.
echo('off'); more('off')                         % Turn off echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Nfile = 8;                                       % Set the number of files to save to.

myFiles = 1:Nfile;                               % Set list of files.
%myFiles = global_ind(zeros(Nfile,1,map([Np 1],{},0:Np-1)));   % PARALLEL.

for i = myFiles
  tic;
    fname = ['data/' num2str(i)];  disp(fname);  % Create filename.

    % Open files, read data, and close files.
    fidRow=fopen([fname 'r.txt'],'r+'); fidCol=fopen([fname 'c.txt'],'r+'); fidVal =fopen([fname 'v.txt'],'r+');
      rowStr = fread(fidRow,inf,'uint8=>char').';
      colStr = fread(fidCol,inf,'uint8=>char').';
      valStr = fread(fidVal,inf,'uint8=>char').';
    fclose(fidRow);                     fclose(fidCol);                     fclose(fidVal);

    Nedge = NumStr(rowStr);                                    % Compute the number of edges.
    edgeBit = ceil(log10(i.*Nedge));

    edgeStr = sprintf(['%0.' num2str(edgeBit) 'd,'],((i-1).*Nedge)+(1:Nedge));    % Create identifier for each edge.
    edgeStrMat = Str2mat(edgeStr);
    edgeStrMat(:,1:end-1) = fliplr(edgeStrMat(:,1:end-1));     % Make big endian.
    edgeStr = Mat2str(edgeStrMat);

    outStr = CatStr('Out,','/',rowStr);                        % Format out edge string list.
    inStr = CatStr('In,','/',colStr);                          % Format in edge string list.

    put(Tedge,[edgeStr edgeStr],[outStr inStr],[valStr valStr]);        % Insert edges.

    Edeg = Assoc([outStr inStr],'Degree,',1,@sum);             % Compute degree.

    put(TedgeDeg,num2str(Edeg));                               % Accumulate degrees.

  insertTime = toc;  disp(['Time: ' num2str(insertTime) ', Edges/sec: ' num2str((4.*Nedge+nnz(Edeg))./insertTime)]);
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

