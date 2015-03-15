%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Test all of the examples.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

Atest = runTESTdir('./1Intro/1AssocIntro');
Atest = Atest + runTESTdir('./1Intro/2EdgeArt');
Atest = Atest + runTESTdir('./1Intro/3GroupTheory');
Atest = Atest + runTESTdir('./2Apps/1EntityAnalysis');
Atest = Atest + runTESTdir('./2Apps/2TrackAnalysis');
Atest = Atest + runTESTdir('./2Apps/3PerfectPowerLaw');
Atest = Atest + runTESTdir('./2Apps/4BioBlast');
Atest = Atest + runTESTdir('./3Scaling/1KroneckerGraph');
Atest = Atest + runTESTdir('./3Scaling/2ParallelDatabase');
Atest = Atest + runTESTdir('./3Scaling/3MatrixPerformance');

displayFull(Atest);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%