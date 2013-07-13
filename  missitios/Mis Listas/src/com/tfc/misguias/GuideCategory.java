package com.tfc.misguias;

public class GuideCategory {

		private Long id = null;
	    private String name = null;
	    private String description = null;
	    private String icon = null;
	 
	    public Long getId() {
	      return id;
	    }
	    public void setId(Long extractedString) {
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
		public String getIcon() {
			return icon;
		}
		public void setIcon(String icon) {
			this.icon = icon;
		}	
	}
