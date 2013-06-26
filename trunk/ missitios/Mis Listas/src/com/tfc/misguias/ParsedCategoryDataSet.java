package com.tfc.misguias;

public class ParsedCategoryDataSet {
	 
    private String id = null;
    private String name = null;
    private String description = null;
 
    public String getId() {
      return id;
    }
    public void setId(String extractedString) {
     this.id = extractedString;
    }    
 
    public String getName() {
     return name;
    }
    public void setName(String extractedString) {
     this.name = extractedString;
    }
    
    public String getDescription() {
        return description;
       }
       public void setDescription(String extractedString) {
        this.description = extractedString;
       }
 
    public String toString(){
         return "name = " + this.name;
    }	
}