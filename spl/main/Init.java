package spl.main;

import java.util.Vector;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import spl.assets.Asset;
import spl.assets.AssetContent;
import spl.management.ClerkDetails;
import spl.management.Customer;
import spl.management.CustomerGroupDetails;
import spl.management.Location;
import spl.management.Management;
import spl.management.RentalRequest;
import spl.management.RepairMaterialInformation;
import spl.management.RepairToolInformation;
import spl.warehouse.RepairMaterial;
import spl.warehouse.RepairTool;


/**
 * The Class Init which used for parsing the input files and populating the programs data structures with the parsed date.
 */
public class Init {

	/** The logger. */
	private static Logger LOGGER;
	
	/**
	 * Parses all of the data.
	 *
	 * @param management the management
	 * @param LOGGER the logger
	 */
	public static void parseAll(String[] args, Management management, Logger LOGGER){
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try{
			Init.LOGGER = LOGGER;
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(args[2]);
			parseAssets(doc, management, LOGGER);
			doc=dBuilder.parse(args[1]);
			parseAssetContentsRepairDetails(doc, management, LOGGER);
			doc = dBuilder.parse(args[0]);
			readInitialData(doc, management, LOGGER);
			doc = dBuilder.parse(args[3]);
			readCustomerGroups(doc, management, LOGGER);
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}


	/**
	 * Parses the assets information from "Assets.xml".
	 *
	 * @param doc the Document object which the xml input files were parsed into
	 * @param m the management
	 * @param LOGGER the logger
	 */
	public static void parseAssets(Document doc, Management m, Logger LOGGER){

		NodeList listOfAssets=doc.getElementsByTagName("Asset");
		for(int i=0; i<listOfAssets.getLength(); i++){
			Element cur_asset=(Element)listOfAssets.item(i);
			
			NodeList cur_Node=cur_asset.getElementsByTagName("Name");
			String name=(cur_Node.item(0)).getTextContent(); //name of ASSET i
			
			cur_Node=cur_asset.getElementsByTagName("Type");
			String type=(cur_Node.item(0)).getTextContent(); //type of ASSET i
			
			cur_Node=cur_asset.getElementsByTagName("Size");
			int size=Integer.parseInt( (cur_Node.item(0)).getTextContent() );//size of ASSET i
			
			cur_Node=cur_asset.getElementsByTagName("Location");
			Element location=(Element)cur_Node.item(0);
			double X=Double.parseDouble(location.getAttribute("x"));
			double Y=Double.parseDouble(location.getAttribute("y"));
			Location loc=new Location(X,Y); //location of ASSET i
			
			cur_Node=cur_asset.getElementsByTagName("CostPerNight");
			int cost=Integer.parseInt( (cur_Node.item(0)).getTextContent() ); //cost of ASSET i
			
			Asset newAsset=new Asset(name, type, size, loc, cost, Init.LOGGER);
			
			//set vector of asset content for ASSET i
			NodeList listOfAssetsContent=cur_asset.getElementsByTagName("AssetContent");
			for (int j=0; j<listOfAssetsContent.getLength();j++){
				Element cur_assets_content=(Element)listOfAssetsContent.item(j);
				cur_Node=cur_assets_content.getElementsByTagName("Name");
				String contentName=(cur_Node.item(0)).getTextContent();
				cur_Node=cur_assets_content.getElementsByTagName("RepairMultiplier");
				double contentMultiplier=Double.parseDouble( (cur_Node.item(0)).getTextContent() );
				
				AssetContent contentToInput=new AssetContent(contentName, contentMultiplier, Init.LOGGER);
				newAsset.addAssetContent(contentToInput);
			}

			m.addAsset(newAsset);
			
			
		}
	}

	/**
	 * parses the customer groups information "AssetContentsRepairDetails.xml".
	 *
	 * @param doc the Document object which the xml input files were parsed into
	 * @param management the management
	 * @param LOGGER the logger
	 */
	public static void readCustomerGroups(Document doc, Management management, Logger LOGGER){
		
		NodeList customerGroupsList = doc.getElementsByTagName("CustomerGroupDetails");
		for (int i = 0; i < customerGroupsList.getLength(); i++)
		{
			Element customerGroupsElement = (Element)customerGroupsList.item(i);
			
			NodeList groupManagerNameNodeList = customerGroupsElement.getElementsByTagName("GroupManagerName");
			String groupManagerName = (groupManagerNameNodeList.item(0)).getTextContent();
			CustomerGroupDetails newCustomerGroup = new CustomerGroupDetails(groupManagerName, LOGGER);
			
			NodeList CustomersNodeList = customerGroupsElement.getElementsByTagName("Customer");
			for (int j = 0; j < CustomersNodeList.getLength(); j++)
			{
				Element CustomerElement = (Element)CustomersNodeList.item(j);
				
				NodeList nameNodeList = CustomerElement.getElementsByTagName("Name");
				String name = (nameNodeList.item(0)).getTextContent();
				
				NodeList vandalismTypeNodeList = CustomerElement.getElementsByTagName("Vandalism");
				String vandalismType = (vandalismTypeNodeList.item(0)).getTextContent();
				
				NodeList minimumDamageNodeList = CustomerElement.getElementsByTagName("MinimumDamage");
				String stringMinimumDamage = (minimumDamageNodeList.item(0)).getTextContent();
				int minimumDamage = Integer.parseInt(stringMinimumDamage);
				
				NodeList maximumDamageNodeList = CustomerElement.getElementsByTagName("MaximumDamage");
				String stringMaximumDamage = (maximumDamageNodeList.item(0)).getTextContent();
				int maximumDamage = Integer.parseInt(stringMaximumDamage);
				
				Customer newCustomer = new Customer(name, vandalismType, minimumDamage, maximumDamage, LOGGER);
				
				newCustomerGroup.addCustomer(newCustomer);
			}
			
			NodeList groupRequestsNodeLists = customerGroupsElement.getElementsByTagName("Request");
			for (int j = 0; j < groupRequestsNodeLists.getLength(); j++)
			{
				Element requestElement = (Element)groupRequestsNodeLists.item(j);
				
				String stringRequestId = requestElement.getAttribute("id");
				int requestId = Integer.parseInt(stringRequestId);
				
				NodeList assetTypeNodeList = requestElement.getElementsByTagName("Type");
				String assetType = (assetTypeNodeList.item(0)).getTextContent();
				
				NodeList assetSizeNodeList = requestElement.getElementsByTagName("Size");
				String stringAssetSize = (assetSizeNodeList.item(0)).getTextContent();
				int assetSize = Integer.parseInt(stringAssetSize);
				
				NodeList durationNodeList = requestElement.getElementsByTagName("Duration");
				String stringDuration = (durationNodeList.item(0)).getTextContent();
				int duration = Integer.parseInt(stringDuration);
				
				RentalRequest newRentalRequest = new RentalRequest(requestId, assetType, assetSize, duration, groupManagerName, LOGGER);
				newCustomerGroup.addRentalRequest(newRentalRequest);
				
			}
		
			management.addCustomerGroup(newCustomerGroup);
		}
		

	}

	/**
	 * parses warehouse contents and clerks information from "InitialData.xml"
	 *
	 * @param doc the Document object which the xml input files were parsed into
	 * @param management the management
	 * @param LOGGER the logger
	 */
	public static void readInitialData(Document doc, Management management, Logger LOGGER){
		
		NodeList toolsList = doc.getElementsByTagName("Tool");
		for(int i=0; i<toolsList.getLength(); i++)
		{
			Element currentToolElement = (Element)toolsList.item(i);
			
			NodeList toolNameNodeList = currentToolElement.getElementsByTagName("Name");
			String name = (toolNameNodeList.item(0)).getTextContent();
			
			NodeList toolQuantityNodeList = currentToolElement.getElementsByTagName("Quantity");
			int quantity = Integer.parseInt((toolQuantityNodeList.item(0)).getTextContent());
			RepairTool newRepairTool = new RepairTool(name, quantity);
			management.addItemRepairToolToWarehouse(newRepairTool);	
		}
		
		NodeList materialsList = doc.getElementsByTagName("Material");
		for(int i=0; i<materialsList.getLength(); i++)
		{
			Element currentMaterialElement = (Element)materialsList.item(i);
			
			NodeList materialNameNodeList = currentMaterialElement.getElementsByTagName("Name");
			String name = (materialNameNodeList.item(0)).getTextContent();
			
			NodeList materialQuantityNodeList = currentMaterialElement.getElementsByTagName("Quantity");
			int quantity = Integer.parseInt((materialQuantityNodeList.item(0)).getTextContent());
			RepairMaterial newRepairMaterial = new RepairMaterial(name, quantity);
			management.addItemRepairMaterialToWarehouse(newRepairMaterial);
		}
		
		NodeList clerksList = doc.getElementsByTagName("Clerk");
		for (int i = 0; i < clerksList.getLength(); i++)
		{
			Element currentClerkElement = (Element)clerksList.item(i);
			
			NodeList clerkNameNodeList = (NodeList)currentClerkElement.getElementsByTagName("Name");
			String name = (clerkNameNodeList.item(0)).getTextContent();
			
			NodeList clerkLocationNodeList = (NodeList)currentClerkElement.getElementsByTagName("Location");
			Element clerkLocationElement = (Element)clerkLocationNodeList.item(0);
			String stringXLocation = clerkLocationElement.getAttribute("x");
			int x = Integer.parseInt(stringXLocation);
			String stringYLocation = clerkLocationElement.getAttribute("y");
			int y = Integer.parseInt(stringYLocation);
			Location clerkLocation = new Location(x, y);
			ClerkDetails newClerk = new ClerkDetails(name, clerkLocation, Init.LOGGER);
			management.addClerk(newClerk);
		}
		
		NodeList numberOfMaintenancePersonsNodeList = doc.getElementsByTagName("NumberOfMaintenancePersons");
		String NumberOfMaintenancePersons = (numberOfMaintenancePersonsNodeList.item(0)).getTextContent();
		management.setNumberOfMaintenancePersons(Integer.parseInt(NumberOfMaintenancePersons));
		
		NodeList totalNumberOfRentalRequestsNodeList = doc.getElementsByTagName("TotalNumberOfRentalRequests");
		String totalNumberOfRentalRequests = (totalNumberOfRentalRequestsNodeList.item(0)).getTextContent();
		management.setRentalRequestsAmount(Integer.parseInt(totalNumberOfRentalRequests));
		
		
			
	}

	/**
	 * Parses asset contents and their repair details from "CustomersGroups.xml" 
	 *
	 * @param doc the Document object which the xml input files were parsed into
	 * @param m the management
	 * @param LOGGER the logger
	 */
	public static void parseAssetContentsRepairDetails(Document doc, Management m, Logger LOGGER){
		NodeList listOfAssetContent=doc.getElementsByTagName("AssetContent");
		for(int i=0; i<listOfAssetContent.getLength(); i++){//iterate all contents
			
			Element cur_assetContent=(Element)listOfAssetContent.item(i);
			NodeList cur_Node=cur_assetContent.getElementsByTagName("Name");
			String name=(cur_Node.item(0)).getTextContent(); //name of AssetContent
			
			
			NodeList listOfRepairTools=cur_assetContent.getElementsByTagName("Tool");
			Vector<RepairToolInformation> vecOfRepairTools=new Vector<RepairToolInformation>();//contain all the repairTools of the specific content
			
			for (int j=0; j<listOfRepairTools.getLength(); j++){//iterate all repairTools for specific content
				Element cur_RepairTool=(Element)listOfRepairTools.item(j);
				
				cur_Node=cur_RepairTool.getElementsByTagName("Name");
				String toolName=(cur_Node.item(0)).getTextContent();//name of tool
			
				
				cur_Node=cur_RepairTool.getElementsByTagName("Quantity");
				int quantity=Integer.parseInt((cur_Node.item(0)).getTextContent() );//quantity if tool
				
				
				RepairToolInformation repairTool=new RepairToolInformation(toolName, quantity, Init.LOGGER);
				vecOfRepairTools.add(repairTool); 
				
			}
			m.addItemRepairToolInformation(name, vecOfRepairTools);
			
			
			NodeList listOfRepairMaterials=cur_assetContent.getElementsByTagName("Material");
			Vector<RepairMaterialInformation> vecOfRepairMaterials=new Vector<RepairMaterialInformation>();//contain all the repairMaterilas of the specific content
			
			for (int j=0; j<listOfRepairMaterials.getLength(); j++){//iterate all repairMaterials for specific content
				Element cur_RepairMat=(Element)listOfRepairMaterials.item(j);
				
				cur_Node=cur_RepairMat.getElementsByTagName("Name");
				String MaterialName=(cur_Node.item(0)).getTextContent();//name of Material
				
				cur_Node=cur_RepairMat.getElementsByTagName("Quantity");
				int quantity=Integer.parseInt((cur_Node.item(0)).getTextContent() );//quantity of Material
				
				
				RepairMaterialInformation repairMaterial=new RepairMaterialInformation(MaterialName, quantity, Init.LOGGER);
				vecOfRepairMaterials.add(repairMaterial); 
				
			}
			m.addItemRepairMaterialInformation(name, vecOfRepairMaterials);
		}
		
		
	}


}
