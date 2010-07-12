
package edu.mit.ll.d4m.db.cloud;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author wi20909
 */
public class D4mDbRow {
    public D4mDbRow() {}


  public String row = "";
  public String title = "";
  public String column = "";
  public String value = "";
  public String description = "";
  public String location = "";
  public String modified = "";
  public ArrayList termsList = new ArrayList();
  public String termsFound = "";
  public Double ranking = 0.00;
  public HashMap searchTermsMap = new HashMap();

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }



  


    public ArrayList getTermsList() {
        return termsList;
    }

    public void addToTermsList(String term) {
        this.termsList.add(term);
    }

    public Double getRanking() {
        return ranking;
    }

    public void setRanking(Double ranking) {
        this.ranking = ranking;
    }

    public String getTermsFound() {
        return termsFound;
    }

    public void setTermsFound(String termsFound) {
        this.termsFound = termsFound;
    }

    public HashMap getSearchTermsMap() {
        return searchTermsMap;
    }

    public void addSearchTermsMap(String key, String value) {
        this.searchTermsMap.put(key, value);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    

}
