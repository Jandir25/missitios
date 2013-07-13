package com.tfc.misguias;

public class Guide {

		private Long id = null;
		private String origin = null;
	    private String title = null;
	    private String description = null;
	    private Long creator = null;
	    private String date = null;
	    private Long category_id = null;
	    private String image = null;
	    private Boolean publico = null;
	 
	    public Long getId() {
	      return id;
	    }
	    public void setId(Long extractedString) {
	     this.id = extractedString;
	    }    
	 
	    public String getTitle() {
	     return title;
	    }
	    public void setTitle(String extractedString) {
	     this.title = extractedString;
	    }
	    
	    public String getDescription() {
	        return description;
	    }
	    
	    public void setDescription(String extractedString) {
	        this.description = extractedString;
	    }
	 
	    public String toString(){
	         return "title = " + this.title;
	    }
		public String getIcon() {
			return image;
		}
		public void setIcon(String icon) {
			this.image = icon;
		}
		public String getOrigin() {
			return origin;
		}
		public void setOrigin(String origin) {
			this.origin = origin;
		}
		public Long getCreator() {
			return creator;
		}
		public void setCreator(Long creator) {
			this.creator = creator;
		}
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public Long getCategory_id() {
			return category_id;
		}
		public void setCategory_id(Long category_id) {
			this.category_id = category_id;
		}
		public Boolean getPublico() {
			return publico;
		}
		public void setPublico(Boolean publico) {
			this.publico = publico;
		}	
		
		
		
	}
