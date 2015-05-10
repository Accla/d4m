%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Generate a Kronecker graph and save to data files.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('off'); more('off')                   % Turn off echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Nfile = 8;                                       % Set the number of files to save to.

myFiles = 1:Nfile;                               % Set list of files.
%myFiles = global_ind(zeros(Nfile,1,map([Np 1],{},0:Np-1)));   % PARALLEL

for i = myFiles
  tic;
    fname = ['data/' num2str(i)];  disp(fname);  % Create filename.

    % Open files, read data, and close files.
    fidRow=fopen([fname 'r.txt'],'r+'); fidCol=fopen([fname 'c.txt'],'r+'); fidVal =fopen([fname 'v.txt'],'r+');
      rowStr = fread(fidRow,inf,'uint8=>char').';
      colStr = fread(fidCol,inf,'uint8=>char').';
      %valStr = fread(fidVal,inf,'uint8=>char').'; not used
    fclose(fidRow);                     fclose(fidCol);                     fclose(fidVal);

    A = Assoc(rowStr,colStr,1,@sum);             % Construct Adjacency Assoc and sum duplicates.
    save([fname '.A.mat'],'A');                  % Save associative array to file.
    
    % Incidence Assoc construction
    [rowStr, colStr, valStr] = find(num2str(A)); % Get versions with duplicates summed together.
    Nedge = NumStr(rowStr);                      % # of edges.
    edgeBit = ceil(log10(i.*Nedge));             % # of decimal places

    edgeStr = sprintf(['%0.' num2str(edgeBit) 'd,'],((i-1).*Nedge)+(1:Nedge));    % Create identifier for each edge.
    edgeStrMat = Str2mat(edgeStr);
    edgeStrMat(:,1:end-1) = fliplr(edgeStrMat(:,1:end-1));    % Make big endian.
    edgeStr = Mat2str(edgeStrMat);

    outStr = CatStr('Out,','|',rowStr);          % Format out edge string list.
    inStr = CatStr('In,','|',colStr);            % Format in edge string list.

    E = Assoc([edgeStr edgeStr],[outStr inStr],[valStr valStr]); % Create directed incidence Assoc.
    save([fname '.E.mat'],'E');                  % Save incidence Assoc to file.
    
  assocTime = toc;  disp(['Time: ' num2str(assocTime) ', Edges/sec: ' num2str(NumStr(rowStr)./assocTime)]);
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

