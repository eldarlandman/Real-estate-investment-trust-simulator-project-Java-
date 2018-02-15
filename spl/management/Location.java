package spl.management;

public class Location {
	
	/**fields
	 *  @param x coordinate of the location
	 *  @param y coordinate of the location 
	 *  */
	private double x;
	private double y;
	
	/**
	 * Instantiates a new location.
	 *
	 * @param x the x
	 * @param y the y
	 */
	public Location(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * 
	 * @param location other location which is deep copied to the new Location object
	 */
	public Location(Location location) {
		this.x = location.x;
		this.y = location.y;
	}

	/**
	 * calculate the Euclidean distance
	 * @param location
	 * @return the Euclidean distance from this to the other location parameter
	 */
	public long calculatDistance(Location location)
	{		
		double doubleResult = Math.sqrt((location.x - this.x) * (location.x - this.x) +  
									(location.y - this.y) * (location.y - this.y));
		
		long longResult = Math.round(doubleResult);
		return longResult;
	}

}
