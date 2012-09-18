%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Compare multiply performance of different array types.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
echo('off'); more('off')                    % Turn off echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
load('data/MP1_Dense.mat');        Ad_size = size(AdenseResults);
load('data/MP2_Sparse.mat');       As_size = size(AsparseResults);
load('data/MP3_Assoc.mat');        Aa_size = size(AassocResults);
load('data/MP4_AssocCatKey.mat');  Ack_size = size(AcatKeyResults);
load('data/MP5_AssocCatVal.mat');  Acv_size = size(AcatValResults);
load('data/MP6_AssocPlus.mat');    Ap_size = size(AassocPlusResults);

loglog(Adj(AdenseResults(2:Ad_size(1),'MemFrac,')),Adj(AdenseResults(2:Ad_size(1),'ProcFrac,')),'o-');
xlabel('Fraction of Memory Used');  ylabel('Fraction of Peak Performance');
hold('on');
loglog(Adj(AsparseResults(2:As_size(1),'MemFrac,')),Adj(AsparseResults(2:As_size(1),'ProcFrac,')),'^-');
loglog(Adj(AassocResults(2:Aa_size(1),'MemFrac,')),Adj(AassocResults(2:Aa_size(1),'ProcFrac,')),'s-');
loglog(Adj(AcatKeyResults(2:Ack_size(1),'MemFrac,')),Adj(AcatKeyResults(2:Ack_size(1),'ProcFrac,')),'+-');
loglog(Adj(AcatValResults(2:Acv_size(1),'MemFrac,')),Adj(AcatValResults(2:Acv_size(1),'ProcFrac,')),'x-');
loglog(Adj(AassocPlusResults(2:Ap_size(1),'MemFrac,')),Adj(AassocPlusResults(2:Ap_size(1),'ProcFrac,')),'v-');
hold('off');
legend({'dense' 'sparse' 'assoc' 'catkey' 'catval' 'assoc+'})

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%