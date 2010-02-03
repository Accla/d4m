package cloudbase.core.client.mapreduce;

import java.io.IOException;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.DefaultCodec;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordWriter;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.util.ReflectionUtils;

import cloudbase.core.CBConstants;
import cloudbase.core.data.Key;
import cloudbase.core.data.MyMapFile;
import cloudbase.core.data.Value;
import cloudbase.core.data.MySequenceFile.CompressionType;

/**
 * This class allows MapReduce jobs to use the Cloudbase data file format for output of data
 * 
 * The user must specify the output path that does not exist following via static method calls to this class:
 * 
 *   CBMapFileOutputFormat.setOutputPath(job, outputDirectory)
 *  
 *  Other methods from FileOutputFormat to configure options are ignored
 *  Compression is using the DefaultCodec and is always on
 */
public class CBMapFileOutputFormat extends FileOutputFormat<Key, Value>
{
    // Return the record writer
    @Override
    public RecordWriter<Key, Value> getRecordWriter(FileSystem ignored, JobConf job, String jobName, Progressable progress)
    throws IOException
    {
        // get the path of the temporary output file 
        Path file = FileOutputFormat.getTaskOutputPath(job, jobName);
        
        FileSystem fs = file.getFileSystem(job);
        
        // find the right codec
        Class<? extends CompressionCodec> codecClass = getOutputCompressorClass(job, DefaultCodec.class);
        CompressionCodec codec = ReflectionUtils.newInstance(codecClass, job);
        
        job.setInt("io.seqfile.compress.blocksize", CBConstants.DEFAULT_MAPFILE_COMPRESSION_BLOCK_SIZE);
        
        // ignore the progress parameter, since MapFile is local
        final MyMapFile.Writer out =
          new MyMapFile.Writer(job, fs, file.toString(), Key.class, Value.class, CompressionType.BLOCK,
        		  codec, progress);

        return new RecordWriter<Key, Value>()
        {
            public void write(Key key, Value value) throws IOException { out.append(key, value); }
            public void close(Reporter reporter) throws IOException { out.close(); }
        };
    }

}
