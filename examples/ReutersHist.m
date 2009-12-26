% Computes histograms of Reuters Entity data.

% Read in entities.
%ReutersEntityRead;

% Sum in each diretion. 
EntDeg = sum(A,1);
DocDeg = sum(A,2);

EntHist = sparse(Adj(EntDeg),1,1);
DocHist = sparse(Adj(DocDeg),1,1);

figure;
loglog(DocHist,'o');
xlabel('Entities in Document');
ylabel('Number of Documents');


figure;
loglog(EntHist,'o');
xlabel('Documents per Entity');
ylabel('Number of Entities');


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

