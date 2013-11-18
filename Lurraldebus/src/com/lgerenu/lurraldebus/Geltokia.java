package com.lgerenu.lurraldebus;

public class Geltokia {
	
	private int id; // Geltokiaren zenbakia
	private String name; // Izena
	private String desc; // Deskribapena
	private double lat; // Latitudea
	private double lon; // Longitudea
	
	public Geltokia (int id, String name, String desc, double lat, double lon){
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.lat = lat;
		this.lon = lon;
	}
	
	/**
	 * Zenbakia lortu
	 * @return Geltokiaren zenbakia
	 */
	public int getId() {
		return id;
	}

	/**
	 * Identifikatzailea aldatu
	 * @param id Identifikatzailea
	 */
	public void setId(int id){
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public double getLat() {
		return lat;
	}
	
	public void setLat(double lat) {
		this.lat = lat;
	}
	
	public double getLon() {
		return lon;
	}
	
	public void setLon(double lon) {
		this.lon = lon;
	}
}
