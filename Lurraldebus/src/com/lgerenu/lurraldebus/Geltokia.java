package com.lgerenu.lurraldebus;

public class Geltokia {
	
	private int id; // Geltokiaren zenbakia
	private String name; // Izena
	private String desc; // Deskribapena
	private double lat; // Latitudea
	private double lon; // Longitudea
	private int distantzia; // Gauden puntutik geltokiraino dagoen distantzia metrotan
	
	public Geltokia (){
	}
	
	public Geltokia (int id, String name, String desc, double lat, double lon, int distantzia){
		this.id = id;
		this.name = name;
		this.desc = desc;
		this.lat = lat;
		this.lon = lon;
		this.distantzia = distantzia;
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

	public int getDistantzia() {
		return distantzia;
	}

	public void setDistantzia(int distantzia) {
		this.distantzia = distantzia;
	}
}
