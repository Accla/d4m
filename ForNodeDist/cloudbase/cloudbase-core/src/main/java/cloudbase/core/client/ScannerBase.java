package cloudbase.core.client;

import java.io.IOException;

import org.apache.hadoop.io.Text;

/**
 * This class host methods that are shared between
 * all different types of scanners.
 * 
 */
public interface ScannerBase {
    
    /**
     * Sets server side scan iterators.
     * 
     */

    public void setScanIterators(int priority, String iteratorClass, String iteratorName) throws IOException, ClassNotFoundException;
    
    /**
     * Sets options for server side scan iterators.
     * 
     */
    
    public void setScanIteratorOption(String iteratorName, String key, String value);
    
    
    /**
     * Call this method to initialize regular expresions
     * on a scanner.  If it is not called, reasonable defaults
     * will be used.  
     * 
     */
    
    public void setupRegex(String iteratorName, int iteratorPriority) throws IOException, ClassNotFoundException;
    
    /**
     * Set a row regular expression that filters non matching entries server side.
     * 
     */
    
    public void setRowRegex(String regex);
    
    /**
     * Set a column family regular expression that filters non matching entries server side.
     * 
     */
    
    public void setColumnFamilyRegex(String regex);
    
    /**
     * Set a column qualifier regular expression that filters non matching entries server side.
     * 
     */
    
    public void setColumnQualifierRegex(String regex);
    
    /**
     * Set a value regular expression that filters non matching entries server side.
     * 
     */
    
    public void setValueRegex(String regex);
    
    public void fetchColumnFamily(Text col) ;
    
    public void fetchColumn(Text colFam, Text colQual);
    
    public void clearColumns();
}
