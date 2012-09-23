function [di_ex ni_ex] = PowerLawFit(alpha,Ngoal,Mgoal);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Compute perfect power law and theoretical estimates of N and M.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%echo('on'); more('off')              % Turn on echoing.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%alpha = 1;  Ngoal = 10000;   Mgoal = 8.*Ngoal;

% Find M limits.
Mrange_dmax(1) = round((Ngoal - 1).^(1./alpha));
Mrange(1) = round(Mrange_dmax(1).^alpha + Mrange_dmax(1));


disp(['N: ' num2str(Ngoal) '  Mmin: ' num2str(Mrange(1)) '  M/N: ' num2str(Mrange(1)./Ngoal) '  Nd: ' num2str(1) '  dmax: ' num2str(Mrange_dmax(1))]);
%disp(['N: ' num2str(Ngoal) '  Mmax: ' num2str(Mrange(1)) '  Nd: ' num2str(1) '  dmax: ' num2str(Mrange_dmax(1))]);

if (Mgoal < Mrange(1))
  disp('WARNING: Mgoal is below minimum.');
end


disp('%%%%%%%%%%% Sampled Search  %%%%%%%%%%%');

% Exhaustive search.
% Compute vector of dmax grid points.
%dmaxVec = 1:10:Mrange_dmax(1);
dmaxVec = 1:(Mrange_dmax(1)-1)./99:Mrange_dmax(1);

%NdVec = 1:round(sqrt(Mrange_dmax(1).^alpha));
NdVec = 1:100;


% Create 2D grid of Nd and dmax coordinates.
dmax = repmat(dmaxVec,numel(NdVec),1);
Nd = repmat(NdVec.',1,numel(dmaxVec));

[Ne Me] = PowerLawExactMulti(alpha,dmax,Nd);
dmax = reshape(dmax,numel(dmax),1);  Nd = reshape(Nd,numel(Nd),1);
Ne = reshape(Ne,numel(Ne),1);  Me = reshape(Me,numel(Me),1);
[penalty_e j] = min(sqrt((Ne - Ngoal).^2 + (Me - Mgoal).^2));
Nd_e = Nd(j);  dmax_e = dmax(j);
disp(['N: ' num2str(Ne(j)) '  M: ' num2str(Me(j)) '  M/N: '  num2str(Me(j)./Ne(j)) '  Nd: ' num2str(Nd(j)) '  dmax: ' num2str(dmax(j))]);


%%%%%%%%
% New algorithm:  start with Nd=1 and dmax set by above.
% Increase Nd until Mgoal is hit.
% Then decrease dmax until Ngoal is hit.
% Repeat until stopping criteria is met.

disp('%%%%%%%%%%% Heuristic Search  %%%%%%%%%%%');

dmax_i = Mrange_dmax(1);
%dmax_i = dmax(j);
Nd_i = 1;
%Nd_i = Nd(j);
Nd_i = 20;
%Nd_i = round((Mgoal./Ngoal).^2);
[N_i M_i] = PowerLawExact(alpha,dmax_i,Nd_i);
%penalty_i = sqrt((N_i./Ngoal - 1).^2 + (M_i./Mgoal - 1).^2);
penalty_i = sqrt((N_i - Ngoal).^2 + (M_i - Mgoal).^2);

C_dN = 0.1;

for i=1:100
  dN = max(round(C_dN.*abs(N_i - Ngoal)),1);
%  dmax_j = [dmax_i.*[1 1 1], (dmax_i - dN).*[1 1 1],  (dmax_i + dN).*[1 1 1]];
%  Nd_j = Nd_i + [-1 0 1, -1 0 1, -1 0 1];
  dmax_j = [dmax_i.*[1 1 1 1 1], (dmax_i - dN).*[1 1 1 1 1],  (dmax_i + dN).*[1 1 1 1 ], (dmax_i - 2.*dN).*[1 1 1 1 1],  (dmax_i + 2.*dN).*[1 1 1 1 ]];
  Nd_j = Nd_i + [-2 -1 0 1 2, -2 -1 0 1 2, -2 -1 0 1 2];

  Nd_j(Nd_j < 1) = 1;
  [N_j M_j] = PowerLawExactMulti(alpha,dmax_j,Nd_j);
%  [penalty_j j] = min(sqrt((N_j./Ngoal - 1).^2 + (M_j./Mgoal - 1).^2));
  [penalty_j j] = min(sqrt((N_j - Ngoal).^2 + (M_j - Mgoal).^2));
  if (penalty_j < penalty_i)
    dmax_i = dmax_j(j);  Nd_i = Nd_j(j);  N_i = N_j(j);  M_i = M_j(j);
    penalty_i = penalty_j;
    C_dN = 0.1;
%    disp(['N: ' num2str(N_i) '  M: ' num2str(M_i) '  M/N: '  num2str(M_i./N_i) '  Nd: ' num2str(Nd_i) '  dmax: ' num2str(dmax_i)]);
  else
%    C_dN = C_dN./2;
%    C_dN = C_dN./2;
    C_dN = 0.1.^(2.*(rand(1)-0.5));
%    C_dN = C_dN.^((rand(1)-0.5));
  end
end

    disp(['N: ' num2str(N_i) '  M: ' num2str(M_i) '  M/N: '  num2str(M_i./N_i) '  Nd: ' num2str(Nd_i) '  dmax: ' num2str(dmax_i)]);




disp('%%%%%%%%%%% Broyden Setup  %%%%%%%%%%%');



% Sample allowable N, M space to get initial guess at Nd and dmax.

N_dmaxVec = 50;   % Set size of dmax grid.
N_NdVec = 25;     % Set size of Nd grid.

% Compute vector of dmax grid points.
%dmaxVec = 10.^(1:0.25:10);
dmaxVec = 10.^(0.5:((log10(Mgoal.^(1/alpha))-0.5)./(N_dmaxVec-1)):log10(Mgoal.^(1/alpha)));


% Compute vector Nd grid points.
%NdVec = 10.^(1:0.25:9);
NdVec = 10.^(0.5:((log10(Mgoal.^(1/alpha))-0.5)./(N_NdVec-1)):log10(Mgoal.^(1/alpha)));
NdVec = 10.^(0.0:((log10(Ngoal.^(1/alpha))-0.0)./(N_NdVec-1)):log10(Ngoal.^(1/alpha)));


% Create 2D grid of Nd and dmax coordinates.
dmax = repmat(dmaxVec,numel(NdVec),1);
Nd = repmat(NdVec.',1,numel(dmaxVec));

% Compute analytic estimate for N and M from dmax and Nd.
[N M] = PowerLawEst(alpha,dmax,Nd);
%[N M] = PowerLawExactMulti(alpha,round(dmax),round(Nd));

% Find N and M that are feasible.
iOK = (N > 0) & (M > 0) & (dmax > Nd);
%iOK = (N > 0) & (M > 0);
N0 = N; N0(:) = NaN; N0(iOK) = N(iOK);
M0 = M; M0(:) = NaN; M0(iOK) = M(iOK);

% Find grid point that is closet to Ngoal, Mgoal.
dNgoal = log(N0) - log(Ngoal);   dMgoal = log(M0) - log(Mgoal);
dNMgoal = sqrt(dNgoal.^2 + dMgoal.^2);
[iMin jMin]  = find(dNMgoal == min(min(dNMgoal)));

% Plot grid and goal.
figure; loglog(N0,M0); hold('on'); loglog(N0.',M0.'); 
xlabel('N');  ylabel('M');
loglog(full(Ngoal),full(Mgoal),'o'); loglog(N0(iMin,jMin),M0(iMin,jMin),'*');
loglog((dmaxVec.^alpha)+1,(dmaxVec.^alpha)+ dmaxVec,'k-','LineWidth',2);

psi1 = -0.577215664901533;       % psi(1) = -(euler constant).

if (alpha==1)
  Hn = log(dmaxVec-1) - psi1 + 1./(2.*(dmaxVec-1));
  [Ntmp i] = min(abs((dmaxVec.^alpha).*Hn - Ngoal));
  Mrange_dmax(2) = round(dmaxVec(i));
  Mrange(2) = round(dmaxVec(i).^2);
  loglog((dmaxVec.^alpha).*Hn,dmaxVec.^2,'k-','LineWidth',2);
else
  Hn = ((dmaxVec-1).^(1-alpha) - 1)./(1 - alpha) - psi1 + 1./(2.*(dmaxVec-1));  % Closed form approximation.
  Hn2 = ((dmaxVec-1).^(2-alpha) - 1)./(2 - alpha) - psi1 + 1./(2.*(dmaxVec-1));  % Closed form approximation.
  [Ntmp i] = min(abs((dmaxVec.^alpha).*Hn - Ngoal));
  Mrange_dmax(2) = round(dmaxVec(i));
  Mrange(2) = round((dmaxVec(i).^alpha).*Hn2(i));
  loglog((dmaxVec.^alpha).*Hn,(dmaxVec.^alpha).*Hn2,'k-','LineWidth',2);
end

disp(['N: ' num2str(round(Ntmp + Ngoal)) '  Mmax: ' num2str(Mrange(2)) '  M/N: ' num2str(Mrange(2)./round(Ntmp + Ngoal)) '  Nd: ' num2str(Inf) '  dmax: ' num2str(Mrange_dmax(2))]);
if (Mgoal > Mrange(2))
  disp('WARNING: Mgoal is above maximum.');
end

hold('off');

disp('%%%%%%%%%%% Broyden Search  %%%%%%%%%%%');


% Use Broyden's method to get exact Nd and dmax.

% Compute starting point.
x = [NdVec(iMin); dmaxVec(jMin)];
F = [N0(iMin,jMin) - Ngoal; M0(iMin,jMin) - Mgoal];

% Compute Jacobian.
% J = [dN/dNd, dN/dNdmax; dM/dNd, dM/ddmax];
xeps = 0.001;  dxeps = 2.*x.*xeps;
[Neps Meps] = PowerLawEst(alpha,x(2).*(1 + xeps.*[0 0]),x(1).*(1 + xeps.*[-1 1]));
J(1,1) = (Neps(2) - Neps(1))./dxeps(1);    % dN/dNd
J(2,1) = (Meps(2) - Meps(1))./dxeps(1);    % dM/dNd
[Neps Meps] = PowerLawEst(alpha,x(2).*(1 + xeps.*[-1 1]),x(1).*(1 + xeps.*[0 0]));
J(1,2) = (Neps(2) - Neps(1))./dxeps(2);    % dN/ddmax
J(2,2) = (Meps(2) - Meps(1))./dxeps(2);    % dM/ddmax

Jinv = inv(J);   % Compute inverse of Jacobian.


% Iterate to find solution.
for i=1:6
  x1 = x - Jinv * F;    % Compute next starting point.
  [N1 M1] = PowerLawEst(alpha,x1(2),x1(1));

  F1 = [N1 - Ngoal; M1 - Mgoal];    % Compute next function.
  disp(['N: ' num2str(N1) '  M: ' num2str(M1) '  M/N: '  num2str(M1./N1) '  Nd: ' num2str(x1(1)) '  dmax: ' num2str(x1(2))]);

  % Update inverse of Jacobian.
  dx1 = x1 - x;    dF1 = F1 - F;
  Jinv1 = Jinv + ((dx1 - Jinv*dF1)./(dx1.' * Jinv * dF1))*(dx1.' * Jinv);

  x = x1; F = F1;  Jinv = Jinv1;    % Copy.
end

x = round(x);
Nd = x(1);    dmax = x(2);     % Set values of Nd and dmax.
% Nd = round(log(dmax)/log(2));

% Compute exact values.
[N M] = PowerLawExact(alpha,dmax,Nd);

disp(['N: ' num2str(N) '  M: ' num2str(M) '  M/N: '  num2str(M./N) '  Nd: ' num2str(Nd) '  dmax: ' num2str(dmax)]);



disp('Choice: Broyden');

penalty = sqrt((N - Ngoal).^2 + (M - Mgoal).^2);

if (penalty_i < penalty)
  disp('Choice: Heuristic');
  penalty = penalty_i;  Nd = Nd_i;  dmax = dmax_i;
end

if (penalty_e < penalty)
  disp('Choice: Sampled');
  penalty = penalty_e;  Nd = Nd_e;  dmax = dmax_e;
end


% Compute power law graph from these parameters.
n1 = dmax.^alpha;
logdi = (0:Nd).*log(dmax)./Nd;
di = unique(round(exp(logdi)));
logn1 = log(dmax).*alpha;
logni = logn1 - alpha*log(di);
ni = round(exp(logni));
%[di ni tmp] = find(sparse(di,ni,1));

N = sum(ni);
M = sum(ni.*di);
disp([num2str(N) ' ' num2str(M) ' ' num2str(M/N)]);

% Correct end points to make exact match.
ni_ex = ni;
di_ex = di;
ni_ex(1) = ni_ex(1) + (Ngoal - N);
%di_ex(end) = di_ex(end) + (Mgoal - M) - (Ngoal - N);
di_ex(ni_ex==1) = di_ex(ni_ex==1) + floor(((Mgoal - M) - (Ngoal - N))./nnz(ni_ex==1));
N_ex = sum(ni_ex);
M_ex = sum(ni_ex.*di_ex);
di_ex(end) = di_ex(end) + (Mgoal - M_ex);
M_ex = sum(ni_ex.*di_ex);


figure; loglog(di,ni,'o'); hold('on');
loglog([di_ex(1) di_ex(ni_ex==1)],[ni_ex(1) ni_ex(ni_ex==1)],'*');
hold('off');

disp([num2str(N_ex) ' ' num2str(M_ex) ' ' num2str(M_ex/N_ex)]);

ni_ex = ni;  di_ex = di;   % Undo correction.

return
end


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
