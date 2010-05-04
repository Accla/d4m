function MHtrack = D4MqueryMHtrack(p)
% Returns multiple hypothesis track for p.
  GLOBAL D4MqueryGlobal

  % Specify track keys.
  nl = char(13);
  t=['TIME/*' nl];    l=['NE_LOCATION/*' nl];

  MHtrack = Reuters3MHtrack(D4MqueryGlobal.DbTr,p,t,l);

end

