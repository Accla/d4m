if not(exist('DB', 'var'))
    global DB;
    
    % Create a DB.  
    %DB = DBserver('classdb01.cloud.llgrid.ll.mit.edu:2181','Accumulo','accumulo',user,password);
    DB = DBsetupLLGrid('classdb06');
%classdb06pword='3@9JE3gt8OuDR-PxcqPucz2Eb'

%DB = DBserver('classdb06.cloud.llgrid.ll.mit.edu:2181','Accumulo','classdb06','AccumuloUser',classdb06pword)
%DB = DBserver('d2ddb03.cloud.llgrid.ll.mit.edu:2181','Accumulo','d2ddb03','AccumuloUser','89YJ@g2CvnLdJ%KNMixHCV1n1')
% DB = DBserver('d2ddb03.cloud.llgrid.ll.mit.edu:2181','Accumulo','d2ddb03','AccumuloUser', 'qP4me4g4fr1kebwpDNd1oRa3t');
end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
% FOUO
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

