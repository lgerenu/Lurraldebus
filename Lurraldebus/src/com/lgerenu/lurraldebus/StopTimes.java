package com.lgerenu.lurraldebus;

public class StopTimes {
	private int id;
	private String arrivalTime;
	private String departureTime;
	private int stopId;
	private int stopSequence;
	private int arrivalTimeSeconds;
	private int departureTimeSeconds;
	private int route_id;
	private String routeIzena;
	
	public StopTimes(int id, String arrivalTime, String departureTime, int stopId, int stopSequence, int arrivalTimeSeconds, int departureTimeSeconds, int route_id, String routeIzena){
		this.setId(id);
		this.setArrivalTime(arrivalTime); 
		this.setDepartureTime(departureTime);
		this.setStopId(stopId);
		this.setStopSequence(stopSequence);
		this.setArrivalTimeSeconds(arrivalTimeSeconds);
		this.setDepartureTimeSeconds(departureTimeSeconds);
		this.setRoute_id(route_id);
		this.setRouteIzena(routeIzena);
	}

	/**
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the arrivalTime
	 */
	public String getArrivalTime() {
		return arrivalTime;
	}

	/**
	 * @param arrivalTime the arrivalTime to set
	 */
	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	/**
	 * @return the departureTime
	 */
	public String getDepartureTime() {
		return departureTime;
	}

	/**
	 * @param departureTime the departureTime to set
	 */
	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}

	/**
	 * @return the stopId
	 */
	public int getStopId() {
		return stopId;
	}

	/**
	 * @param stopId the stopId to set
	 */
	public void setStopId(int stopId) {
		this.stopId = stopId;
	}

	/**
	 * @return the stopSequence
	 */
	public int getStopSequence() {
		return stopSequence;
	}

	/**
	 * @param stopSequence the stopSequence to set
	 */
	public void setStopSequence(int stopSequence) {
		this.stopSequence = stopSequence;
	}

	/**
	 * @return the arrivalTimeSeconds
	 */
	public int getArrivalTimeSeconds() {
		return arrivalTimeSeconds;
	}

	/**
	 * @param arrivalTimeSeconds the arrivalTimeSeconds to set
	 */
	public void setArrivalTimeSeconds(int arrivalTimeSeconds) {
		this.arrivalTimeSeconds = arrivalTimeSeconds;
	}

	/**
	 * @return the departureTimeSeconds
	 */
	public int getDepartureTimeSeconds() {
		return departureTimeSeconds;
	}

	/**
	 * @param departureTimeSeconds the departureTimeSeconds to set
	 */
	public void setDepartureTimeSeconds(int departureTimeSeconds) {
		this.departureTimeSeconds = departureTimeSeconds;
	}

	/**
	 * @return the route_id
	 */
	public int getRoute_id() {
		return route_id;
	}

	/**
	 * @param route_id the route_id to set
	 */
	public void setRoute_id(int route_id) {
		this.route_id = route_id;
	}

	/**
	 * @return the routeIzena
	 */
	public String getRouteIzena() {
		return routeIzena;
	}

	/**
	 * @param routeIzena the routeIzena to set
	 */
	public void setRouteIzena(String routeIzena) {
		this.routeIzena = routeIzena;
	}

}
