%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Script for reading in sequence data and comparing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('on'); more('off')                  % Turn on echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

DNAwordsize = 10;                        % Set DNA sequence word size.

[A1n A1p] = SplitSequenceGram2CSV('data/bacteria.csv',DNAwordsize);   % Read reference data (takes ~1 minute).
[A2n A2p] = SplitSequenceGram2CSV('data/palm.csv',DNAwordsize);       % Read sample data (takes ~1 minute).

% Plot distribution of word sequences..
figure; loglog(full(OutDegree(Adj(A1n).')),'o'); xlabel('Wordcount');  ylabel('# Words w/Wordcount');
figure; loglog(full(OutDegree(Adj(A2n).')),'o'); xlabel('Wordcount');  ylabel('# Words w/Wordcount');

A1A2 = dblLogi(A1n) * dblLogi(A2n.');    % Convert to 0,1 and correlate.
A1A2key = CatKeyMul(A1n,A2n.');          % Same thing, but preserve word pedigree.

% Find sequences with a lot of word matches.
AbigMatch = A1A2key & putVal(dblLogi(A1A2 > 8),'~,')

echo('off');                             % Turn off echoing.

% Look at the word positions of these sequences.
for i=1:size(AbigMatch,1)
  [r c v] = find(AbigMatch(i,:));
  v = strrep(v,',','');
  displayFull(A1p(r,v).' + A2p(c,v).')
end


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%


