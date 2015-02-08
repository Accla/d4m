% Use to load Nfiles of the saved adjacency and incidence Assocs
% from pDB03_AssocTEST.  Useful for testing in-memory algorithm scaling.

Nfile = 8;

Eall = Assoc('','','');
for fi = 1:Nfile
    fname = ['data/' num2str(fi)]; disp(fname);  % Filename.
    load([fname '.E.mat']);                      % Load Incidence Assoc.
    Eall = E + Eall;
end
E = Eall;
Edeg = sum(E,1).';

Aall = Assoc('','','');
for fi = 1:Nfile
    fname = ['data/' num2str(fi)]; disp(fname);  % Filename.
    load([fname '.A.mat']);
    Aall = Aall + A;
end
A = Aall;
Adeg = sum(A,2);
