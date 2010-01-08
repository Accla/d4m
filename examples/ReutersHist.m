% Computes histograms of Reuters Entity data.

% Read in entities.
%ReutersEntityRead;

Aper = A(:,'NE_PERSON/*,');
Aloc = A(:,'NE_LOCATION/*,');
Aorg = A(:,'NE_ORGANIZATION/*,');

% Sum in each diretion. 
EntDeg = sum(A,1);
EntDegPer = sum(Aper,1);
EntDegLoc = sum(Aloc,1);
EntDegOrg = sum(Aorg,1);

DocDeg = sum(A,2);
DocDegPer = sum(Aper,2);
DocDegLoc = sum(Aloc,2);
DocDegOrg = sum(Aorg,2);



EntHist = sparse(Adj(EntDeg),1,1);
EntHistPer = sparse(Adj(EntDegPer),1,1);
EntHistLoc = sparse(Adj(EntDegLoc),1,1);
EntHistOrg = sparse(Adj(EntDegOrg),1,1);
DocHist = sparse(Adj(DocDeg),1,1);
DocHistPer = sparse(Adj(DocDegPer),1,1);
DocHistLoc = sparse(Adj(DocDegLoc),1,1);
DocHistOrg = sparse(Adj(DocDegOrg),1,1);

figure;
loglog(EntHist,'o');
xlabel('Documents per Entity');
ylabel('Number of Entities');
hold on
loglog(EntHistPer,'+');
loglog(EntHistLoc,'x');
loglog(EntHistOrg,'.');
legend('all','person','place','org');
hold off

figure;
loglog(DocHist,'o');
xlabel('Entities in Document');
ylabel('Number of Documents');
hold on
loglog(DocHistPer,'+');
loglog(DocHistLoc,'x');
loglog(DocHistOrg,'.');
legend('all','person','place','org');
hold off



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

