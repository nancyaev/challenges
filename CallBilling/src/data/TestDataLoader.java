package data;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import persistence.CsvHandler;
import persistence.PropertyFileHandler;
/**
 * Class used to load the test data
 * @author nestrada
 *
 */
public class TestDataLoader {
	
	MongoClient mongoClient;
	DB db;
	
	private  final String PROPERTIES_FILE = "CallBilling.properties";
	
	private  final String PROPERTY_MONGODB_HOST = "MONGODB_HOST";
	private  final String PROPERTY_MONGODB_PORT = "MONGODB_PORT";
	private  final String PROPERTY_MONGODB_DATABASE_NAME = "MONGODB_DATABASE_NAME";
	private  final String PROPERTY_MONGODB_PROFIT_MARGING_KEY = "MONGODB_PROFIT_MARGING_KEY";
	private  final String PROPERTY_MONGODB_PROFIT_MARGING_VALUE = "MONGODB_PROFIT_MARGING_VALUE";
	private  final String PROPERTY_MONGODB_CURRENT_PROFIT_MARGING_KEY = "MONGODB_CURRENT_PROFIT_MARGING_KEY";
	private  final String PROPERTY_MONGODB_PROFIT_MARGING_COLLECTION_NAME = "MONGODB_PROFIT_MARGING_COLLECTION_NAME";
	private  final String PROPERTY_TEST_PROFIT_MARGING = "TEST_PROFIT_MARGING";
	private  final String PROPERTY_MONGODB_CLIENT_ACCOUNT_BALANCE_COLLECTION_NAME = "MONGODB_CLIENT_ACCOUNT_BALANCE_COLLECTION_NAME";
	private  final String PROPERTY_MONGODB_CLIENT_ACCOUNT_ID_KEY = "MONGODB_CLIENT_ACCOUNT_ID_KEY";
	private  final String PROPERTY_TEST_CLIENT_ID = "TEST_CLIENT_ID";
	private  final String PROPERTY_MONGODB_CLIENT_BALANCE_KEY = "MONGODB_CLIENT_BALANCE_KEY";
	private  final String PROPERTY_TEST_CLIENT_BALANCE = "TEST_CLIENT_BALANCE";
	private  final String PROPERTY_MONGODB_BROWSER_CALL_COST_KEY = "MONGODB_BROWSER_CALL_COST_KEY";
	private  final String PROPERTY_MONGODB_BROWSER_CALL_COST_VALUE = "MONGODB_BROWSER_CALL_COST_VALUE";
	private  final String PROPERTY_MONGODB_BROWSER_CALL_COST_CURRENT = "MONGODB_BROWSER_CALL_COST_CURRENT";
	private  final String PROPERTY_MONGODB_BROWSER_CALL_COLLECTION = "MONGODB_BROWSER_CALL_COLLECTION";
	private  final String PROPERTY_TEST_BROWSER_COST = "TEST_BROWSER_COST";
	private  final String PROPERTY_MONGODB_CLIENT_BILLING_COLLECTION = "MONGODB_CLIENT_BILLING_COLLECTION";
	
	public TestDataLoader(){
		try {
			int port;
			String databaseName;
			String host;
			CsvHandler csvHandler;
			
			databaseName = PropertyFileHandler.readProperty(PROPERTY_MONGODB_DATABASE_NAME, PROPERTIES_FILE);
			host = PropertyFileHandler.readProperty(PROPERTY_MONGODB_HOST, PROPERTIES_FILE);
			port = Integer.parseInt(PropertyFileHandler.readProperty(PROPERTY_MONGODB_PORT, PROPERTIES_FILE));

			mongoClient = new MongoClient(host, port);
			db = mongoClient.getDB(databaseName);
			
			csvHandler = new CsvHandler(databaseName, host, port,PROPERTIES_FILE);
			csvHandler.saveVoicePricesTwilio();
			csvHandler.saveTalkdeskCost();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Load talkdesk cost values, TwilioRates, account balance, browser call price, client billing, profit marging
	 * @param args
	 */
	public static void main(String[] args) {
		TestDataLoader test = new TestDataLoader();
		test.saveAccountBalance();
		test.saveBrowserCall();
		test.saveClientBilling();
		test.saveProfitMarging();
	}
	
	public void saveProfitMarging(){
		String PROFIT_MARGING_COLLECTION = PropertyFileHandler.readProperty(PROPERTY_MONGODB_PROFIT_MARGING_COLLECTION_NAME, PROPERTIES_FILE);
		String PROFIT_MARGING_KEY = PropertyFileHandler.readProperty(PROPERTY_MONGODB_PROFIT_MARGING_KEY, PROPERTIES_FILE);
		String PROFIT_MARGING_VALUE = PropertyFileHandler.readProperty(PROPERTY_MONGODB_PROFIT_MARGING_VALUE, PROPERTIES_FILE);
		String CURRENT_PROFIT_MARGING_KEY = PropertyFileHandler.readProperty(PROPERTY_MONGODB_CURRENT_PROFIT_MARGING_KEY, PROPERTIES_FILE);
		String CURRENT_PROFIT_MARGING_VALUE = PropertyFileHandler.readProperty(PROPERTY_TEST_PROFIT_MARGING, PROPERTIES_FILE);
		
		db.getCollection(PROFIT_MARGING_COLLECTION).drop();
		setData(PROFIT_MARGING_COLLECTION, PROFIT_MARGING_KEY, PROFIT_MARGING_VALUE, CURRENT_PROFIT_MARGING_KEY,CURRENT_PROFIT_MARGING_VALUE);
		
	}
	public void saveAccountBalance(){
		String CLIENT_ID;
		String CLIENT_BALANCE;
		String CURRENT_TEST_BALANCE;
		String CLIENT_ACCOUNT_COLLECTION;
		String TEST_CLIENT;
		Double testBalance;
		
		CLIENT_ACCOUNT_COLLECTION = PropertyFileHandler.readProperty(PROPERTY_MONGODB_CLIENT_ACCOUNT_BALANCE_COLLECTION_NAME, PROPERTIES_FILE);
		TEST_CLIENT = PropertyFileHandler.readProperty(PROPERTY_TEST_CLIENT_ID, PROPERTIES_FILE);
		CLIENT_ID = PropertyFileHandler.readProperty(PROPERTY_MONGODB_CLIENT_ACCOUNT_ID_KEY, PROPERTIES_FILE);
		CLIENT_BALANCE = PropertyFileHandler.readProperty(PROPERTY_MONGODB_CLIENT_BALANCE_KEY, PROPERTIES_FILE);
		CURRENT_TEST_BALANCE = PropertyFileHandler.readProperty(PROPERTY_TEST_CLIENT_BALANCE, PROPERTIES_FILE);
		testBalance = Double.parseDouble(CURRENT_TEST_BALANCE);
		
		db.getCollection(CLIENT_ACCOUNT_COLLECTION).drop();
		setData(CLIENT_ACCOUNT_COLLECTION, CLIENT_ID, TEST_CLIENT, CLIENT_BALANCE, testBalance);
	}
	public void saveBrowserCall(){
		String BROWSER_CALL_KEY;
		String BROWSER_CALL_VALUE;
		String BROWSER_CALL_CURRENT;
		String TEST_BROWSER_COST;
		String BROWSER_CALL_COLLECTION;
		
		BROWSER_CALL_KEY = PropertyFileHandler.readProperty(PROPERTY_MONGODB_BROWSER_CALL_COST_KEY, PROPERTIES_FILE);
		BROWSER_CALL_VALUE = PropertyFileHandler.readProperty(PROPERTY_MONGODB_BROWSER_CALL_COST_VALUE, PROPERTIES_FILE);
		BROWSER_CALL_CURRENT = PropertyFileHandler.readProperty(PROPERTY_MONGODB_BROWSER_CALL_COST_CURRENT, PROPERTIES_FILE);
		BROWSER_CALL_COLLECTION = PropertyFileHandler.readProperty(PROPERTY_MONGODB_BROWSER_CALL_COLLECTION, PROPERTIES_FILE);
		TEST_BROWSER_COST = PropertyFileHandler.readProperty(PROPERTY_TEST_BROWSER_COST, PROPERTIES_FILE);
		
		db.getCollection(BROWSER_CALL_COLLECTION).drop();
		setData(BROWSER_CALL_COLLECTION, BROWSER_CALL_KEY, BROWSER_CALL_VALUE, BROWSER_CALL_CURRENT, TEST_BROWSER_COST);		
	}
	
	public void saveClientBilling(){
		String CLIENT_BILLING_COLLECTION;
		String CURRENT_TEST_BALANCE;
		String ACCOUNT_ID_KEY;
		String TEST_CLIENT;
		DateFormat dateFormat;//NOTE: this date field is used to save the date and time as key of charge
		Date date;
		
		dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		date = new Date();
		
		CURRENT_TEST_BALANCE = PropertyFileHandler.readProperty(PROPERTY_TEST_CLIENT_BALANCE, PROPERTIES_FILE);;
		CLIENT_BILLING_COLLECTION = PropertyFileHandler.readProperty(PROPERTY_MONGODB_CLIENT_BILLING_COLLECTION, PROPERTIES_FILE);
		ACCOUNT_ID_KEY = PropertyFileHandler.readProperty(PROPERTY_MONGODB_CLIENT_ACCOUNT_ID_KEY, PROPERTIES_FILE);
		TEST_CLIENT = PropertyFileHandler.readProperty(PROPERTY_TEST_CLIENT_ID, PROPERTIES_FILE);
		
		db.getCollection(CLIENT_BILLING_COLLECTION).drop();
		setData(CLIENT_BILLING_COLLECTION, ACCOUNT_ID_KEY, TEST_CLIENT, dateFormat.format(date), CURRENT_TEST_BALANCE);		
	}
	
	public void setData(String collectionName, String keyOne, String valueOne, String keyTwo, String valueTwo)  {
		BasicDBObject document;
		DBCollection dbCollection;
		
		db.getCollection(collectionName).drop();		
		dbCollection = db.getCollection(collectionName);
		document = new BasicDBObject();
		
		document.put(keyOne, valueOne);
		document.put(keyTwo, valueTwo);
		dbCollection.insert(document);
	}
	
	public void setData(String collectionName, String keyOne, String valueOne, String keyTwo, Double valueTwo)  {
		BasicDBObject document;
		DBCollection dbCollection;
		
		db.getCollection(collectionName).drop();
		dbCollection = db.getCollection(collectionName);
		document = new BasicDBObject();
		
		document.put(keyOne, valueOne);
		document.put(keyTwo, valueTwo);
		dbCollection.insert(document);
	}
}
