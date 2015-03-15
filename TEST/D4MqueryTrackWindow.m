function trackList = D4MqueryTrackWindow(t,l);
% Returns best tracks for mulitple persons.
  global D4MqueryGlobal

  trackList = (D4MqueryGlobal.DbTr(t,:) == l);

end

