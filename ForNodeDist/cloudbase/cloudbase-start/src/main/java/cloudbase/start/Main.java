package cloudbase.start;


import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Main {

	/**
	 * @param args
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws MasterNotRunningException 
	 * @throws TableNotFoundException 
	 */

	private static URLClassLoader ecl;
	private static final String SITE_CONF = "/conf/cloudbase-site.xml";
	private static final String DEFAULT_CONF = "/conf/cloudbase-default.xml";

	public static void main(String[] args) throws Exception {

		try {
			if(args.length == 0){
				printUsage();
				System.exit(-1);
			}

			URLClassLoader cl = Main.getExtensionClassLoader();
			final String argsToPass[] = new String[args.length - 1];
			System.arraycopy(args, 1, argsToPass, 0, args.length - 1);
			final Class<?> toRun;

			if(args[0].equals("master")){
				toRun = Class.forName("cloudbase.core.master.Master", true, cl);
			}else if(args[0].equals("tserver")){
				toRun = Class.forName("cloudbase.core.tabletserver.TabletServer", true, cl);
			}else if(args[0].equals("shell")){
				toRun = Class.forName("cloudbase.core.util.Shell", true, cl);
			}else if(args[0].equals("init")){
				toRun = Class.forName("cloudbase.core.util.Initialize", true, cl);
			}else if(args[0].equals("admin")){
				toRun = Class.forName("cloudbase.core.util.Admin", true, cl);
			}else if(args[0].equals("gc")){
				toRun = Class.forName("cloudbase.core.gc.GarbageCollector", true, cl);
			}else if(args[0].equals("monitor")){
				toRun = Class.forName("cloudbase.core.monitor.Monitor", true, cl);
			}else if(args[0].equals("classpath")) {
				System.out.println("List of classpath items are:");
				for (URL url : cl.getURLs())
					System.out.println(url);
				return;
			}else {
				toRun = Class.forName(args[0], true, cl);
			}
			Runnable r = new Runnable() {
				public void run() {
					Method main;
					try {
						main = toRun.getMethod("main", String[].class);
						main.invoke(null, new Object[] {argsToPass});
					} catch (Throwable t) {
						System.err.println("Uncaught exception: " + t.getMessage());
						t.printStackTrace(System.err);
						System.exit(-1);
					}
				}
			} ;
			Thread t = new Thread(r, args[0]);
			t.setContextClassLoader(cl);
			t.start();
		} catch (Throwable t) {
			System.err.println("Uncaught exception: " + t.getMessage());
			t.printStackTrace(System.err);
		}
	}

	private static void printUsage() {
		System.out.println("cloudbase init | master | tserver | shell | admin | gc | classpath | <cloudbase class> args");		
	}

	public synchronized static URLClassLoader getExtensionClassLoader() throws Exception{
		if(ecl == null){
			//Get classpaths
			String cp = getClasspathStrings();
			if (cp==null)
				return null;
			String[] cps = cp.split(",");
			ArrayList<URL> urls = new ArrayList<URL>();
			for (String classpath : cps)
				addUrl(classpath, urls);
			ecl = new URLClassLoader(urls.toArray(new URL[urls.size()]), Main.class.getClassLoader());
		}
		return ecl;
	}

	private static void addUrl(String classpath, ArrayList<URL> urls) throws Exception {
		classpath = classpath.trim();
		if (classpath.length()==0)
			return;
		String[] cpSplit = classpath.split(",");
		if (cpSplit.length>1) {
			for (String cp : cpSplit)
				addUrl(cp, urls);
			return;
		}
		classpath = replaceEnvVars(classpath, System.getenv());
		final File extDir = new File(classpath);
		if (extDir.isDirectory())
			urls.add(extDir.toURI().toURL());
		else {
		    if (extDir.getParentFile() != null) {
		        File[] extJars = extDir.getParentFile().listFiles(new FilenameFilter(){
		            public boolean accept(File dir, String name) {
		                return name.matches("^"+extDir.getName());
		            }}
		        );

		        if (extJars != null && extJars.length > 0)
		            for (File jar : extJars)
		                urls.add(jar.toURI().toURL());
		    }
		}
	}

    static String replaceEnvVars(String classpath, Map<String, String> env) {
        Pattern envPat = Pattern.compile("\\$[A-Za-z][a-zA-Z0-9_]*");
		Matcher envMatcher = envPat.matcher(classpath);
		while (envMatcher.find(0)) {
		    // name comes after the '$'
		    String varName = envMatcher.group().substring(1);
		    String varValue = env.get(varName);
		    if (varValue == null) {
		        varValue = "";
		    }
		    classpath = (
		            classpath.substring(0, envMatcher.start()) + 
		            varValue + 
		            classpath.substring(envMatcher.end())
		            );
		    envMatcher.reset(classpath);
		}
        return classpath;
    }

	private static String getClasspathStrings() throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

		String result = getCPfromPath(System.getenv("CLOUDBASE_HOME")+SITE_CONF, db);
		if (result==null)
			result = getCPfromPath(System.getenv("CLOUDBASE_HOME")+DEFAULT_CONF, db);
		return result;
	}

	private static String getCPfromPath(String path, DocumentBuilder db) throws Exception {
		File file = new File(path);
		if (file==null || !file.exists())
			return null;
		Document doc = db.parse(file);
		Node props = findTag(doc, "configuration");
		return findClasspath(props.getChildNodes());
	}

	private static String findClasspath(NodeList props) {
		for (int i = 0; i < props.getLength(); i++) {
			
			if (props.item(i)==null || props.item(i).getNodeType()!=Node.ELEMENT_NODE || !"property".equals(props.item(i).getNodeName()))
				continue;
			Node aProp = findTag(props.item(i), "name");
			if (aProp==null || aProp.getNodeType()!=Node.ELEMENT_NODE || !aProp.getTextContent().equals("cloudbase.classpaths")) {
				continue;
			}
			aProp = findTag(props.item(i), "value");
			return aProp.getTextContent();
		}
		return null;
	}

	private static Node findTag(Node n, String tag) {
		NodeList nl = n.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			if (tag.equals(nl.item(i).getNodeName())) {
				return nl.item(i);
			}
		}
		return null;
	}
}
