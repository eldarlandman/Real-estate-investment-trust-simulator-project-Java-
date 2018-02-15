package spl.management;
import java.util.logging.Logger;

import spl.assets.Asset;

/**
 * The Class DamageReport.
 * 
 * "A damage report will be generated once a CustomerGroup fishes simulating its CallableSimulateStayInAsset,
 *  and the damage percentage returned, then the RunnableCustomerGroupManager will create the DamageReport object"
 */
public class DamageReport {
	
	/** The reported asset. */
	private Asset reportedAsset;
	
	/** The damage percentage. */
	private double damagePercentage;
	
	/**
	 * Instantiates a new damage report.
	 *
	 * @param reportedAsset the reported asset
	 * @param damagePrecentage the damage percentage
	 * @param LOGGER the logger
	 */
	public DamageReport(Asset reportedAsset, double damagePrecentage, Logger LOGGER)
	{
		this.reportedAsset = reportedAsset;
		this.damagePercentage = damagePrecentage;
		LOGGER.fine("new DamageReport: " + this.reportedAsset.getName() + " " + this.damagePercentage);
	}
	
	/**
	 * Gets the damage percentage.
	 *
	 * @return the damage percentage
	 */
	public double getDamagePercentage()
	{
		return this.damagePercentage;
	}

	/**
	 * Gets the reported asset.
	 *
	 * @return the reported asset
	 */
	public Asset getReportedAsset() {
		return reportedAsset;
	}

	/**
	 * Check if the asset was damaged as a result of the customer stay.
	 *
	 * @return true, if damaged
	 */
	public boolean checkAssetContentsHealthBelow65() {
		return (this.reportedAsset.checkIfDamaged());
	}

}
