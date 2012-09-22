%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Script for reading in sequence data and comparing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('on'); more('off')                   % Turn on echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

DNAwordsize = 10;                  % Set DNA sequence word size.  

A1 = SplitSequenceCSV('data/bacteria.csv',DNAwordsize);    % Read in reference data.
A2 = SplitSequenceCSV('data/palm.csv',DNAwordsize);        % Read in sample data data.

A1A2 = A1 * A2.';                                          % Compute matches.
A1A2key = CatKeyMul(A1,A2.');                              % Compute pedigreed matches.

whos('A*');                                                % Show all the arrays.

figure; spy(A1);                                           % Display reference data.
figure; spy(A2);                                           % Display test data.
figure; spy(A1A2);                                         % Show matches.
figure; spy(A1A2key);                                      % Show matches with pedigree.


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
