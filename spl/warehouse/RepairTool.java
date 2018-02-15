package spl.warehouse;
import java.util.concurrent.Semaphore;

import spl.management.RepairToolInformation;

/**
 * The Class RepairTool.
 */
public class RepairTool {

	/** The repair tool's name. */
	private String name;
	/** The repair tool's remaining quantity in the tool's pool. */
	public Semaphore quantity;
	
	/**
	 * Instantiates a new repair tool.
	 *
	 * @param name the name
	 * @param quantity the quantity
	 */
	public RepairTool(String name, int quantity)
	{
		this.name = name;
		this.quantity=new Semaphore(quantity, true);
	}
	
	/**
	 * Instantiates a new repair tool.
	 *
	 * @param copiedRepairTool the copied repair tool
	 */
	public RepairTool(RepairToolInformation copiedRepairTool){
		this.name = copiedRepairTool.getRepairToolName();
		this.quantity =new Semaphore(copiedRepairTool.getRepairToolQuantity(), true);
	}

	/**
	 * Gets the tool's name.
	 *
	 * @return the id
	 */
	public String getId(){
		return name;
	}

	/**
	 * Gets the remaining quantity in the tool's pool.
	 *
	 * @return the quantity
	 */
	public int getQuantity(){

		return quantity.getQueueLength();

	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return ("tool name: " + this.name + "quantity: " + this.quantity + System.getProperty("line.seperator"));
	}

}
