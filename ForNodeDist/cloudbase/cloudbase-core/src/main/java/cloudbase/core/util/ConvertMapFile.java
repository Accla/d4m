package cloudbase.core.util;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.WritableName;
import org.apache.log4j.Logger;
import org.jets3t.service.model.CreateBucketConfiguration;

import cloudbase.core.CBConstants;
import cloudbase.core.data.Key;
import cloudbase.core.data.MyMapFile;
import cloudbase.core.data.MySequenceFile;
import cloudbase.core.data.Value;
import cloudbase.core.data.MyMapFile.Writer;
import cloudbase.core.data.deprecated.DeletableImmutableBytesWritable;
import cloudbase.core.data.deprecated.IKey;
import cloudbase.core.security.LabelExpression;

public class ConvertMapFile {
    
    private static Logger log = Logger.getLogger(ConvertMapFile.class);
    
    static {
        WritableName.setName(cloudbase.core.data.deprecated.IKey.class,
                             "cloudbase.core.data.IKey");
        WritableName.setName(cloudbase.core.data.deprecated.DeletableImmutableBytesWritable.class, 
                             "cloudbase.core.data.DeletableImmutableBytesWritable");
        
    }

    private static void convert(FileSystem fs, Configuration conf, String in, String out) throws IOException {
        MyMapFile.Reader reader = new MyMapFile.Reader(fs, in, conf);
        if (!reader.getKeyClass().equals(cloudbase.core.data.deprecated.IKey.class))
            throw new RuntimeException("Bad class mapping");
       
        conf.setInt("io.seqfile.compress.blocksize", CBConstants.DEFAULT_MAPFILE_COMPRESSION_BLOCK_SIZE);
        
        MyMapFile.Writer writer = new MyMapFile.Writer(conf, fs, out, Key.class, Value.class, MySequenceFile.CompressionType.BLOCK);
        
        Map<Key, Value> sort = new TreeMap<Key, Value>();
        
        Key last = null;

        while (true) {
            IKey iKey = new IKey();
            DeletableImmutableBytesWritable iValue = new DeletableImmutableBytesWritable();
            if (!reader.next(iKey, iValue)) break;

            //decode and encode the security label, because the way it is encoded in 1.0 is different
            //than in 0.5
            LabelExpression le = new LabelExpression(iKey.getKeyData(), iKey.getLabelOffset(), iKey.getLabelLen());
            
            byte[] labelBA = le.toByteArray();
            
            Key oKey = new Key(iKey.getKeyData(), iKey.getRowOffset(), iKey.getRowLen(),
            		iKey.getKeyData(), iKey.getColumnFamilyOffset(), iKey.getColumnFamilyLen(),
            		iKey.getKeyData(), iKey.getColumnQualifierOffset(), iKey.getColumnQualifierLen(),
            		labelBA, 0, labelBA.length,
            		iKey.getTimestamp());
            
            oKey.setDeleted(iValue.isDeleted());
            
            Value oValue = new Value(iValue.get(), false);
            if (last != null && last.compareTo(oKey, 4) != 0) {
                flush(writer, sort);
            }
            last = oKey;
            sort.put(oKey, oValue);
        }
        flush(writer, sort);
        writer.close();
    }


    private static void flush(Writer writer, Map<Key, Value> sort) throws IOException {
        for (Entry<Key, Value> e : sort.entrySet()) {
            writer.append(e.getKey(), e.getValue());
        }
        sort.clear();
    }
    
    public static void main(String[] args) throws IOException {
        //Cloudbase.init("//conf//gc_logger.ini");
        Configuration conf = new Configuration();
        FileSystem  fs = FileSystem.get(conf);
        
        if (args.length != 2) {
            log.error("usage: ConvertMapFile <input mapfile> <output mapfile>");
            return;
        }
        String filename = args[0];
        String outFilename = args[1];
        
        convert(fs, conf, filename, outFilename);
    }
    

}
