package control;
import java.util.HashMap;
import java.util.Map;
import persistence.CallBilling_Mongodb;
import persistence.PropertyFileHandler;

/**
 * This class manages the client billing process.
 * Right now all the operations are made under weak consistency guarantees
 * If data consistency is prefered over performance the price data can be cached on this class for faster access
 * 
 * @author nestrada
 */
public class CallBillingControl {
	private  final String PROPERTIES_FILE;
	private  final String PROPERTY_MONGODB_PREFIX_GENERIC_VALUE = "MONGODB_PREFIX_GENERIC_VALUE";
	
	private double browserCost;
	private String GENERIC_VALUE_KEY;
	private HashMap<String, Double> twilioRates;
	private HashMap<String, Double> talkDeskRates;
	private CallBilling_Mongodb db;
	
	public CallBillingControl(String databaseName, String host, int port, String propertiesFile){
		PROPERTIES_FILE = propertiesFile;
		GENERIC_VALUE_KEY = PropertyFileHandler.readProperty(PROPERTY_MONGODB_PREFIX_GENERIC_VALUE, PROPERTIES_FILE);
		
		db = new CallBilling_Mongodb(databaseName,host,port,PROPERTIES_FILE);
		twilioRates = db.getVoicePricesTwilio();
		browserCost = db.getBrowserCallCostFromDB();
		talkDeskRates = db.getTalkDeskRates();
	}
	
	/**
	 * Returns the list the charges for the given account
	 * @param clientID
	 */
	public String getListOfCharges(String clientID){
		return db.getBillinHistory(clientID).toString();
	}
	/**
	 * 
	 * @param clientID
	 * @return
	 */
	public double getAccountBalance(String clientID){
		return db.getAccountBalance(clientID);
	}
	/**
	 * This method removes credits from a given account.
	 * Formula inbound price 
	 * 		talkdeskNumberCost + externalNumberCost + profitMarging
	 * Cost values
	 * 		talkdesk_number_cost  1c, except US Toll free=3c and UK Toll free=6c 
	 * 		externalNumberCost 1c if answered in browser, otherwise read Twilio rate
	 * @param callDuration
	 * @param accountName
	 * @param talkdeskPhoneNumber
	 * @param customerPhoneNumber
	 * @param forwardedPhoneNumber
	 */
	public void charge(String accountName, String talkdeskPhoneNumber, String customerPhoneNumber, String forwardedPhoneNumber){
		double talkdeskNumberCost;
		double externalNumberCost;
		double profitMargin;
		double callCost;
		
		//Cost calculation
		talkdeskNumberCost = getTalkdeskNumberCost(talkdeskPhoneNumber);
		externalNumberCost = getExternalNumberCost(customerPhoneNumber);
		profitMargin = getProfitMargin();
		callCost = talkdeskNumberCost + externalNumberCost + profitMargin;

		//Perform the charge on clients account
		db.chargeClient(accountName, callCost);
	}
	/**
	 * Gets the talkdesk call cost based on DB values
	 * @param talkdeskPhoneNumber
	 * @return
	 */
	private double getTalkdeskNumberCost(String talkdeskPhoneNumber){
		double cost;
		String prefix;
		
		prefix = getNumberPrefix(talkdeskPhoneNumber);
		
		if (talkDeskRates.containsKey(prefix)){
			cost = talkDeskRates.get(prefix);
		}else{
			cost = talkDeskRates.get(GENERIC_VALUE_KEY);
		}
		
		return cost;
	}
	
	/**
	 * Returns the external number cost with weak consistency
	 * @param externalNumber
	 * @return
	 */
	private double getExternalNumberCost(String externalNumber){
		double cost;
		String prefix;
		
		if (externalNumber != null){
			prefix = getNumberPrefix(externalNumber);
			cost = twilioRates.get(prefix);
		}else{//browser
			cost = browserCost;
		}
		
		return cost;
	}
	
	/**
	 * Returns the profit margin based on DB values
	 * @param externalNumber
	 * @return
	 */
	private double getProfitMargin(){
		double profitMarging;
		
		profitMarging = db.getProfitMargin();
		
		return profitMarging;
	}
	
	/**
	 * Gets the prefix of a given number
	 */
	private String getNumberPrefix(String number){
		int prefixStartingIndex = 0;
		int minimumDigitsLenght = 1;
		int maxSizeDigits;
		String numberPrefix = null;
		
		maxSizeDigits= db.getMaxSizeDigitsInNumber();
		
		for (int iCurrentDigits = maxSizeDigits; iCurrentDigits >= minimumDigitsLenght; iCurrentDigits--){
			numberPrefix = number.substring(prefixStartingIndex, iCurrentDigits);
			if (twilioRates.containsKey(numberPrefix))
				break;
		}
		
		return numberPrefix;
	}
}