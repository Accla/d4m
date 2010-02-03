package cloudbase.core.client.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.hadoop.io.Text;

import cloudbase.core.client.ScannerBase;
import cloudbase.core.data.Column;
import cloudbase.core.data.IterInfo;
import cloudbase.core.filter.RegExFilter;
import cloudbase.core.security.LabelConversions;
import cloudbase.core.tabletserver.iterators.RegExIterator;
import cloudbase.core.tabletserver.iterators.SortedKeyValueIterator;
import cloudbase.core.util.TextUtil;

public class ScannerOptions implements ScannerBase
{
    
    List<IterInfo> serverSideIteratorList = Collections.emptyList();
    Map<String, Map<String, String>> serverSideIteratorOptions = Collections.emptyMap();
    
    TreeSet<Column> fetchedColumns = new TreeSet<Column>();
    
    byte[] authorizations = LabelConversions.formatAuthorizations(new short[0]);
    
    private String regexIterName = null;
    
    ScannerOptions(){}
    
    ScannerOptions(ScannerOptions so)
    {
    	this.authorizations = so.authorizations;
    	
        this.regexIterName = so.regexIterName;
        this.fetchedColumns = new TreeSet<Column>(so.fetchedColumns);
        this.serverSideIteratorList = new ArrayList<IterInfo>(so.serverSideIteratorList);
        
        this.serverSideIteratorOptions = new HashMap<String, Map<String,String>>();
        Set<Entry<String, Map<String, String>>> es = so.serverSideIteratorOptions.entrySet();
        for (Entry<String, Map<String, String>> entry : es)
            this.serverSideIteratorOptions.put(entry.getKey(), new HashMap<String, String>(entry.getValue()));
    }
    
    /**
     * Sets server side scan iterators.
     * 
     */

    public synchronized void setScanIterators(int priority, String iteratorClass, String iteratorName) throws IOException, ClassNotFoundException {
        if (serverSideIteratorList.size() == 0) {
            serverSideIteratorList = new ArrayList<IterInfo>();
        }
        
        for(IterInfo ii : serverSideIteratorList){
            if(ii.iterName.equals(iteratorName)){
                throw new RuntimeException("Iterator name is already in use "+iteratorName);
            }
        }
        
        if (!Class.forName(iteratorClass).isInstance(SortedKeyValueIterator.class)) {
        //  throw new IOException(iteratorClass+" is not an instance of "+SortedKeyValueIterator.class);
        }
        serverSideIteratorList.add(new IterInfo(priority, iteratorClass, iteratorName));
    }
    
    /**
     * Sets options for server side scan iterators.
     * 
     */
    
    public synchronized void setScanIteratorOption(String iteratorName, String key, String value){
        if(serverSideIteratorOptions.size() == 0){
            serverSideIteratorOptions = new HashMap<String, Map<String,String>>();
        }
        
        Map<String, String> opts = serverSideIteratorOptions.get(iteratorName);
        
        if(opts == null){
            opts = new HashMap<String, String>();
            serverSideIteratorOptions.put(iteratorName, opts);
        }
        
        opts.put(key, value);
    }
    
    /**
     * Must call this method to initialize regular expresions
     * on a scanner.
     * 
     */
    
    public synchronized void setupRegex(String iteratorName, int iteratorPriority) throws IOException, ClassNotFoundException{
        if(regexIterName != null){
            throw new RuntimeException("regex already setup");
        }
        
        setScanIterators(iteratorPriority, RegExIterator.class.getName(), iteratorName);
        regexIterName = iteratorName;
    }
    
    private synchronized void setupDefaultRegex(){
        try {
            setupRegex("regExAuto", Integer.MAX_VALUE);
        } catch (Exception e) {
            throw new RuntimeException("Failed to setup default regex");
        } 
    }
    
    /**
     * Set a row regular expression that filters non matching entries server side.
     * 
     */
    
    public synchronized void setRowRegex(String regex){
        if(regexIterName == null){
            setupDefaultRegex();
        }
        setScanIteratorOption(regexIterName, RegExFilter.ROW_REGEX, regex);
    }
    
    /**
     * Set a column family regular expression that filters non matching entries server side.
     * 
     */
    
    public synchronized void setColumnFamilyRegex(String regex){
        if(regexIterName == null){
            setupDefaultRegex();
        }
        setScanIteratorOption(regexIterName, RegExFilter.COLF_REGEX, regex);
    }
    
    /**
     * Set a column qualifier regular expression that filters non matching entries server side.
     * 
     */
    
    public synchronized void setColumnQualifierRegex(String regex){
        if(regexIterName == null){
            setupDefaultRegex();
        }
        setScanIteratorOption(regexIterName, RegExFilter.COLQ_REGEX, regex);
    }
    
    /**
     * Set a value regular expression that filters non matching entries server side.
     * 
     */
    
    public synchronized void setValueRegex(String regex){
        if(regexIterName == null){
            setupDefaultRegex();
        }
        setScanIteratorOption(regexIterName, RegExFilter.VALUE_REGEX, regex);
    }
    
    public synchronized void fetchColumnFamily(Text col) {
        Column c = new Column(TextUtil.getBytes(col), 
                              null,
                              null);
        fetchedColumns.add(c);
    }
    
    public synchronized void fetchColumn(Text colFam, Text colQual) {
        Column c = new Column(TextUtil.getBytes(colFam), 
                              TextUtil.getBytes(colQual),
                              null);
        fetchedColumns.add(c);
    }
    
    public synchronized void fetchColumn(Column column) {
        fetchedColumns.add(column);
    }
    
    public synchronized void clearColumns() {
        fetchedColumns.clear();
    }
    
    public synchronized TreeSet<Column> getFetchedColumns() {
        return fetchedColumns;
    }
}
