function run_d4m_analytics(dbHost)
  listener=edu.mit.ll.sks.matlab.D4mAnalyticsJsonListener(java.lang.String('localhost'), java.lang.String(dbHost))

  while true,
    pause(1)    
  end
end