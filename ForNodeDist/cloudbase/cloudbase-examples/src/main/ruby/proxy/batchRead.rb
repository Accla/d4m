$: << "#{ENV['CLOUDBASE_HOME']}/examples/client/proxy/ruby"
require "client"
require "ClientProxy"
require "data_types"

CP = Cloudbase::Proxy

def main
   table = "atable"
   cr = CP::Column.new(:columnFamily => "af", :columnQualifier => "acolumn")

   c = client
   begin
       c.deleteTable('atable')
       sleep 2
   rescue
   end
   c.createTable('atable', [])
   mutations = []
   for i in 0..100
      cu = CP::ColumnUpdate.new(:column => cr, :value => "column#{i}")
      mutations << CP::Mutation.new(:row => "%02drow" % i, :updates => [cu])
   end
   errs = c.update("atable", mutations)

   arange = CP::Range.new(:start => CP::Key.new(:row => ' '), 
			  :stop => CP::Key.new(:row => '1'))

   count = 0
   batch = c.lookup(table, [arange], [cr], [])
   while TRUE
      data = c.fetch(batch)
      for item in data.data
         puts item.key.row
         raise "Oops... row out of batch range" if not item.key.row =~ /^0/
         count += 1
      end
      break if not data.more
   end
   raise "Got the wrong number of entries" if count != 10
end

main
