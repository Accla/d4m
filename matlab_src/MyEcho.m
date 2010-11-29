function command = MyEcho(command)
  Q = '''';
  command(command == '$') = Q;
  disp(command);
end
