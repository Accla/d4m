function [W,H] = NMF(A,k)
% Non-negative Matrix Factorization of Assoc A into W and H using k topics.
% A = W*H. A is nxm, W is nxk, H is kxm.

AA= Adj(Abs0(A));
[n,m]=size(AA);

W=abs(randn(n,k));
fprintf('%d: W is NxK:\n',0);
disp(W)
H=abs(zeros(k,m));

curriter=1;
err=0;
newerr = norm(AA-W*H,'fro');

%[W,H,iter,HIS]=nmf(Adj(Abs0(A)),k);
maxiter=20;
while (abs(newerr-err)>.01 && curriter<maxiter )
    err=newerr;
    fprintf('NMF Error iteration %2d: %.2f\n',curriter,err);
    
    %Solve Solve: W'WH = W'A for H
    %H=pinv(W)*pinv(W')*W'*AA;
    fprintf('%d: tmp1 WTW is KxK:\n',curriter);
    disp(W'*W)
    tmp = matrixInverse(W'*W);
    fprintf('%d: tmp1 INVERSE WTW is KxK:\n',curriter);
    disp(tmp)
    H=tmp*(W'*AA);
    
    %Set H to non-negative elements
    H=H.*(H>0);
    fprintf('%d: H is KxM:\n',curriter);
    disp(H)
    
    %Solve HHTWT=HAT for W
    %Wt=pinv(H')*pinv(H)*H*(AA');
    fprintf('%d: tmp1 HHT is KxK:\n',curriter);
    disp(H*H')
    tmp = matrixInverse(H*H');
    fprintf('%d: tmp1 INVERSE HHT is KxK:\n',curriter);
    disp(tmp)
    Wt=tmp*(H*AA'); % Use matrix inverse below.
    
    %Set W to nonnegative elements
    W=(Wt.*(Wt>0))';
    fprintf('%d: W is NxK:\n',curriter);
    disp(W)
    
    newerr = norm(AA-W*H,'fro');
    fprintf('%d: WH is NxM with ERROR %f:\n',curriter,newerr);
    disp(W*H)
    fprintf('%d: A is NxM with ERROR %f:\n',curriter,newerr);
    disp(full(AA))
    
    curriter=curriter+1;
end

%Put labels on W and H
kStr = sprintf('%0.2d,',1:k);
W = putCol(noVal(putAdj(A,sparse(W))),kStr);  % Put labels on WW.
% figure; spy(WW > 0.01);                        % Show documents that are strongly tied to topics.
% xlabel('Topic ID');  ylabel('Document ID');

H = putRow(noVal(putAdj(A,sparse(H))),kStr);  % Put labels on H.
% figure; spy(HH.' > 0.01);                       % Show entities that are strongly tied to topics.
% xlabel('Topic ID');  ylabel('Entity');
end

function X = matrixInverse(A)
%Uses Newton's iterative method to solve AX = I

%invA = inv(A);
maxiter = 100;
X= A.' ./ (norm(A,1)*norm(A,Inf));
for i=1:maxiter
 X=X*(2*eye(size(A,1))-A*X);
 %disp(['Norm: ' num2str(norm((inv(A)-X),'fro'))]);
end

end
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%