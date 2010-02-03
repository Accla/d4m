require 'pathname'

HOME = ENV['CLOUDBASE_HOME']
path = "#{HOME}/gen-rb"
if Pathname.new(path).directory?
    $: << path
else
    $: << "#{HOME}/cloudbase-core/target/gen-rb"
end
$: << "#{ENV['HOME']}/local/usr/lib/ruby/site_ruby/1.8"

require 'socket'
require 'thrift/transport/tsocket.rb'
require 'thrift/protocol/tbinaryprotocol.rb'
require 'ClientProxy'

include Process

CB = Cloudbase::Proxy

public

def client()
   klass = 'cloudbase.core.client.proxy.ClientProxy'
   pid = fork { exec "#{HOME}/bin/cloudbase.sh", klass, "4567" }
   at_exit { kill "KILL", pid }
   puts "Sleeping"
   sleep 3
   puts "Connecting"
   transport = TBufferedTransport.new(TSocket.new('localhost', 4567))
   protocol = TBinaryProtocol.new(transport)
   client = CB::ClientProxy::Client.new(protocol)
   transport.open()
   return client
end

