package spl.assets;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.util.logging.Logger;


/**
 * The Class Assets.
 * 
 * "This object will hold a collection of Asset. And contain methods that are related to assets."
 */
public class Assets {

	/** The inner assets collection. */
	private Vector<Asset> innerAssetCollection;

	/** The logger. */
	private Logger LOGGER;

	/**
	 * Instantiates a new assets.
	 *
	 * @param LOGGER the logger
	 */
	public Assets(Logger LOGGER)
	{
		this.innerAssetCollection = new Vector<Asset>();
		this.LOGGER = LOGGER;
		LOGGER.info("new Assets");
	}

	/**
	 * Adds the asset.
	 * was created for populating the assets class with assets while reading the input information files
	 *
	 * @param addedAsset the added asset
	 */
	public void addAsset(Asset addedAsset)
	{
		this.innerAssetCollection.addElement(addedAsset);
	}


	/**
	 * Gets a list of damaged assets.
	 *
	 * @return the damaged assets
	 */
	public Vector<Asset> getDamagedAssets()
	{
		Vector<Asset> damagedAssets=new Vector<Asset>();
		for (int i=0; i<this.innerAssetCollection.size(); i++){
			if (this.innerAssetCollection.elementAt(i).checkIfDamaged()){
				damagedAssets.add(this.innerAssetCollection.elementAt(i));
			}
		}
		return damagedAssets;
	}



	/**
	 * Find's the smallest fit asset for stay that meets the sent parameters
	 *
	 * @param type the requested type
	 * @param size the requested size
	 * @return the asset that was found fit to meet the customer's demands. return null if no fit asset was found.
	 */
	public synchronized Asset findFitAssets(String type, int size)
	{
		LOGGER.fine(Thread.currentThread().getName() + " searching for fit asset");
		sortAssetsBySize();
		Asset returnedValue = null;
		boolean stopForLoop = false;
		for (int i = 0; i < this.innerAssetCollection.size() & !stopForLoop; i++)
		{
			if ((this.innerAssetCollection.elementAt(i).getSize() >= size) &&
					(this.innerAssetCollection.elementAt(i).getType().equals(type)) &&
					(this.innerAssetCollection.elementAt(i).shiftStatusAvailableToBooked()))//get to the third condition after checked suitable size and type -then return true if available and change field to "booked"
			{//a proper (available, smallest possible)asset was found 
				returnedValue = this.innerAssetCollection.elementAt(i);
				stopForLoop = true;
			}
		}
		return returnedValue;
	}

	/**
	 * Sort assets by their size.
	 */
	private synchronized void sortAssetsBySize()
	{
		Collections.sort(this.innerAssetCollection, new Comparator<Asset>(){

			@Override
			public int compare(Asset asset1, Asset asset2) {
				return asset1.getSize() - asset2.getSize();
			}

		});
	}
	
	/**
	 * verifies that all of the asset's are available(not occupied/booked)
	 *
	 * @return true, if successful
	 */
	public boolean checkAllAssetsAvailable()
	{
		for (Asset asset : this.innerAssetCollection)
		{
			asset.checkNotOccupied();
		}
		return true;
	}

}
