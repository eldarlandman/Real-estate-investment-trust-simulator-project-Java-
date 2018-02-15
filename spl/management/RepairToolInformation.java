package spl.management;
import java.util.logging.Logger;


/**
 * The Class RepairToolInformation.
 * 
 * "This object will hold information of a single tool type."
 */
public class RepairToolInformation {

	/** The tool's name. */
	private String name;
	
	/** The tool's quantity. */
	private int quantity;
	
	/**
	 * Instantiates a new repair tool information.
	 *
	 * @param name the name
	 * @param quantity the quantity
	 * @param LOGGER the logger
	 */
	public RepairToolInformation(String name, int quantity, Logger LOGGER) {
		this.name=name;
		this.quantity=quantity;
	}
	
	/**
	 * Instantiates a new repair tool information.
	 *
	 * @param name the name
	 * @param quantity the quantity
	 */
	public RepairToolInformation(String name, int quantity) {
		this.name=name;
		this.quantity=quantity;
	}
	
	/**
	 * Gets the repair tool's name.
	 *
	 * @return the repair tool name
	 */
	public String getRepairToolName(){
		return name;
	}
	
	/**
	 * Gets the repair tool's quantity.
	 *
	 * @return the repair tool quantity
	 */
	public int getRepairToolQuantity(){
		return quantity;
	}
	
	/**
	 * Increase tool's quantity.
	 *
	 * @param quantity the added quantity
	 */
	public void setRepairToolInformationQuantity(int quantity)
	{
		this.quantity = quantity;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "name: " + this.name + " quantity: " + this.quantity;
	}
	
}
