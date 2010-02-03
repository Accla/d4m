package cloudbase.core.util;

import java.io.IOException;
import java.io.PrintStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;


public class ConvertMapFiles  extends Configured implements Tool {
	
	  public static class MapClass extends MapReduceBase
	    implements Mapper<LongWritable, Text, Text, Text> {
	 
		  Configuration conf = null;
		  
	    public void map(LongWritable key, Text value, 
	                    OutputCollector<Text, Text> output, 
	                    Reporter reporter) throws IOException {
	    	
	    	//split on tab
	    	String args[] = value.toString().split("\t");
	    	boolean deleteExisting = Boolean.parseBoolean(args[0]);
	    	
	    	String mapFiles[] = new String[]{args[1], args[2]};
	    	
	    	if(conf == null){
    			conf = new Configuration();
    		}
	    	
	    	FileSystem fs = FileSystem.get(conf);
	    	
	    	//if the destination file exists, delete... this could be the result
	    	//of a previously failed map task
	    	if(fs.exists(new Path(mapFiles[1]))){
	    		fs.delete(new Path(mapFiles[1]), true);
	    	}
	    	
	    	ConvertMapFile.main(mapFiles);
	    	
	    	if(deleteExisting){
	    		fs.delete(new Path(args[1]), true);
	    	}
	    }
	  }
	  public int run(String[] args) throws Exception {
		  
		  	if(args.length != 5){
				System.err.println("usage <0.5.X cloudbase dir> <1.0 cloudbase dir> <scratch dir> <num mappers> <delete existing, true or false>");
				return -1;
			}
			
			String inDir = args[0];
			String outDir = args[1];
			String scratch = args[2];
			int numMappers = Integer.parseInt(args[3]);
			boolean deleteExsiting = Boolean.parseBoolean(args[4]);
			
			createConvertFiles(inDir, outDir, numMappers, scratch, deleteExsiting);
		  
			JobConf conf = new JobConf(getConf(), ConvertMapFiles.class);
		    conf.setJobName("convert map files");

		    conf.setMapperClass(MapClass.class);   
		    
		    FileInputFormat.setInputPaths(conf,new Path(scratch));
		    FileOutputFormat.setOutputPath(conf,new Path(scratch+"/out"));
		    
		    conf.setNumReduceTasks(0);
		    
		    conf.setMapSpeculativeExecution(false);
		    conf.setSpeculativeExecution(false);
		    
		    JobClient.runJob(conf);
		    
		    FileSystem fs = FileSystem.get(new Configuration());
		    fs.delete(new Path(scratch), true);
		    
		    return 0;
	}
	
	public static void main(String[] args) throws Exception {
		 int res = ToolRunner.run(new Configuration(), new ConvertMapFiles(), args);
		 System.exit(res);
	 }

	private static void createConvertFiles(String inDir, String outDir, int numMappers, String scratch, boolean deleteExisting) throws IOException {
		
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);
		
		if(fs.exists(new Path(scratch))){
			throw new IOException("Scratch dir "+scratch+" exist ");
		}
		
		int numMapFiles = 0;
		
		FileStatus[] tables = fs.listStatus(new Path(inDir+ "/tables/"));
        for (FileStatus table : tables) {
        	
        	if(table.getPath().getName().equals("!USERS")){
        		continue;
        	}
        	
            FileStatus[] tablets = fs.listStatus(table.getPath());
            for (FileStatus tablet : tablets) {
            	FileStatus[] mapFiles = fs.listStatus(tablet.getPath());
            	for (FileStatus mapFile : mapFiles) {
            		if(mapFile.getPath().getName().startsWith("map_")){
            			numMapFiles++;
            		}
				}
            }
        }
        
        int mapNumber = 0;
        int count = 0;
        int mapFilesPerMapper = numMapFiles / numMappers;  
        
        FSDataOutputStream fsout = fs.create(new Path(scratch+"/convert-"+String.format("%06d", mapNumber)));
        PrintStream out = new PrintStream(fsout);
        
        
        tables = fs.listStatus(new Path(inDir+ "/tables/"));
        for (FileStatus table : tables) {
        	
        	if(table.getPath().getName().equals("!USERS")){
        		continue;
        	}
        	
            FileStatus[] tablets = fs.listStatus(table.getPath());
            for (FileStatus tablet : tablets) {
            	FileStatus[] mapFiles = fs.listStatus(tablet.getPath());
            	for (FileStatus mapFile : mapFiles) {
            		if(mapFile.getPath().getName().startsWith("map_")){
            			String relativePath = "/tables/"+table.getPath().getName()+"/"+tablet.getPath().getName()+"/"+mapFile.getPath().getName();
            			out.println(deleteExisting+"\t"+inDir+relativePath+"\t"+outDir+relativePath);
            			
            			count++;
            			if(count > mapFilesPerMapper){
            				count = 0;
            				mapNumber++;
            				out.close();
            				fsout = fs.create(new Path(scratch+"/convert-"+String.format("%06d", mapNumber)));
            		        out = new PrintStream(fsout);
            			}
            		}
				}
            }
        }
        
        out.close();
	}
}
