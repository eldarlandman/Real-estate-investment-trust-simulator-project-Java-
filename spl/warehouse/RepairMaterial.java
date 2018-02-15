package spl.warehouse;
import spl.management.RepairMaterialInformation;

/**
 * The Class RepairMaterial.
 */
public class RepairMaterial {

	/** the material's name */
	
	private String name;
	
	/** The quantity of this material. */
	private int quantity;
	
	/**
	 * Instantiates a new repair material.
	 *
	 * @param name the name
	 * @param quantity the quantity
	 */
	public RepairMaterial(String name, int quantity)
	{
		this.name = name;
		this.quantity = quantity;
	}
	
	/**
	 * Instantiates a new repair material.
	 *
	 * @param other the other
	 */
	public RepairMaterial(RepairMaterialInformation other)
	{
		this.name = other.getRepairMaterialName();
		this.quantity = other.getMaterialQuantity();
	}


	/**
	 * Gets the material's name.
	 *
	 * @return id of the material
	 */
	public String getId(){

		return name;

	}

	/**
	 * Gets the remaining quantity of this material.
	 *
	 * @return quantity of the material
	 */
	public int getQuantity(){

		return quantity;

	}
	
	/**
	 * Sets the remaining quantity of this material.
	 *
	 * @param requiredQuantity the new quantity
	 */
	public void setQuantity(int requiredQuantity){
		this.quantity=quantity+requiredQuantity;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return ("material name: " + this.name + "quantity: " + this.quantity + System.getProperty("line.seperator"));
	}
	
	
}
