import control.CallBillingControl;
import persistence.PropertyFileHandler;
/**
 * Command line app that can 
 * *Remove credits from a given account
 * *List the charges for the given account
 * @author nestrada
 *
 */
public class CallBilling {
	
	private  final String PROPERTIES_FILE = "CallBilling.properties";
	private  final String PROPERTY_MONGODB_HOST = "MONGODB_HOST";
	private  final String PROPERTY_MONGODB_PORT = "MONGODB_PORT";
	private  final String PROPERTY_MONGODB_DATABASE_NAME = "MONGODB_DATABASE_NAME";
	private  final String PROPERTY_CHARGE = "PROPERTY_CHARGE";
	private  final String PROPERTY_LIST = "PROPERTY_LIST";
	
	CallBillingControl callBillingControl;
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String forwardedPhoneNumber = null;
		String operation = args [0];
		String callDuration = args [1];
		String accountName = args [2];
		String talkdeskPhoneNumber = args [3];
		String customerPhoneNumber = args [4];
		if (args.length>5){
			forwardedPhoneNumber = args [5];
		}
		
		CallBilling callbilling = new CallBilling(operation, callDuration, accountName, talkdeskPhoneNumber, customerPhoneNumber, forwardedPhoneNumber);
		
	}
	
	public CallBilling(String operation, String callDuration, String accountName,String talkdeskPhoneNumber, String customerPhoneNumber,String forwardedPhoneNumber){
		int port;
		String databaseName;
		String host;
		String charge;
		String list;
		
		databaseName = PropertyFileHandler.readProperty(PROPERTY_MONGODB_DATABASE_NAME, PROPERTIES_FILE);
		host = PropertyFileHandler.readProperty(PROPERTY_MONGODB_HOST, PROPERTIES_FILE);
		port = Integer.parseInt(PropertyFileHandler.readProperty(PROPERTY_MONGODB_PORT, PROPERTIES_FILE));
		charge = PropertyFileHandler.readProperty(PROPERTY_CHARGE, PROPERTIES_FILE);
		list = PropertyFileHandler.readProperty(PROPERTY_LIST, PROPERTIES_FILE);
		callBillingControl = new CallBillingControl(databaseName, host, port, PROPERTIES_FILE);
		
		if (operation.equals(charge)){
			charge(accountName, talkdeskPhoneNumber, customerPhoneNumber, forwardedPhoneNumber);
		}
		if (operation.equals(list)){
			list(accountName);
		}
	}
	
	 /**
	 * Remove credits from a given account
	 * @param accountName
	 * @param talkdeskPhoneNumber
	 * @param customerPhoneNumber
	 * @param forwardedPhoneNumber
	 */
	private void charge(String accountName, String talkdeskPhoneNumber, String customerPhoneNumber, String forwardedPhoneNumber){
		callBillingControl.charge(accountName, talkdeskPhoneNumber, customerPhoneNumber, forwardedPhoneNumber);
		System.out.println("Current credit "+ callBillingControl.getAccountBalance(accountName));
	}
	/**
	 *  List the charges for the given account
	 * @param accountName
	 */
	private void list(String accountName){
		System.out.println("List of charges "+ callBillingControl.getListOfCharges(accountName));
	}
	
}
