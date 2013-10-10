package com.tfc.misguias;

public class Place {
	private Long id = null;
	private String name = null;
    private Double latitude = null;
    private Double longitude = null;
    private String description = null;
    private String address = null;
    private Float puntuation = null;
    private String comment = null;
    private String web = null;
    private String price= null;
    private Long type_id= null;
    private String image= null;
        
    
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Float getPuntuacion() {
		return puntuation;
	}
	public void setPuntuation(Float puntuacion) {
		this.puntuation = puntuacion;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getWeb() {
		return web;
	}
	public void setWeb(String web) {
		this.web = web;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public Long getType_id() {
		return type_id;
	}
	public void setType_id(Long type_id) {
		this.type_id = type_id;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
}
