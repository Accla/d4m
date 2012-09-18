function AtrackGraph = TrackGraph(Atrack)
%TRACKGRAPH forms graph of locations from Track Associative array.

  % Find 1 hop and >1 hop tracks.
  AtrackHop = sum(Atrack,1);
  Hop1 = Col(AtrackHop == 1);
  Hop2 = Col(AtrackHop > 1); 

  % Get track list.  Naturally comes out sorted by p.
  [t1 p1 x1] = find(Atrack(:,Hop1));
  AtrackGraph1 = Assoc(x1,x1,1,@sum);

  [t2 p2 x2] = find(Atrack(:,Hop2));

  % Create matrices and shifted matrices.
  p21mat = Str2mat(p2);
  x21mat = Str2mat(x2);
  p22mat = circshift(p21mat,[-1 0]);
  x22mat = circshift(x21mat,[-1 0]);

  % Find where p21 and p22 are the same.
  test = sum(abs(p21mat - p22mat),2);   
  x21 = Mat2str(x21mat(test == 0,:));   
  x22 = Mat2str(x22mat(test == 0,:));   

  AtrackGraph2 = Assoc(x21,x22,1,@sum);

  AtrackGraph = AtrackGraph1 + AtrackGraph2;

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
