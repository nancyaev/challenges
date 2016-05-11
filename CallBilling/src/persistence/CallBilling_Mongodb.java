package persistence;

import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
/**
 * This class manages the Mongodb connections for the billing process
 * @author nestrada
 *
 */
public class CallBilling_Mongodb {
	
	private  final String PROPERTY_MONGODB_TALKDESK_NUMBER_COST_COLLECTION_NAME = "MONGODB_TALKDESK_NUMBER_COST_COLLECTION_NAME";
	private  final String PROPERTY_MONGODB_TWILIO_RATES_COLLECTION_NAME = "MONGODB_TWILIO_RATES_COLLECTION_NAME";
	private  final String PROPERTY_MONGODB_PROFIT_MARGING_COLLECTION_NAME = "MONGODB_PROFIT_MARGING_COLLECTION_NAME";
	private  final String PROPERTY_MONGODB_CLIENT_ACCOUNT_BALANCE_COLLECTION_NAME = "MONGODB_CLIENT_ACCOUNT_BALANCE_COLLECTION_NAME";
	private  final String PROPERTY_MONGODB_CLIENT_BILLING_COLLECTION = "MONGODB_CLIENT_BILLING_COLLECTION";
	private  final String PROPERTY_MONGODB_CLIENT_ACCOUNT_ID_KEY = "MONGODB_CLIENT_ACCOUNT_ID_KEY";
	private  final String PROPERTY_MONGODB_PREFIX_KEY = "MONGODB_PREFIX_KEY";
	private  final String PROPERTY_MONGODB_RATE_KEY = "MONGODB_RATE_KEY";
	private  final String PROPERTY_MONGODB_PROFIT_MARGING_KEY = "MONGODB_PROFIT_MARGING_KEY";
	private  final String PROPERTY_MONGODB_PROFIT_MARGING_VALUE = "MONGODB_PROFIT_MARGING_VALUE";
	private  final String PROPERTY_MONGODB_CURRENT_PROFIT_MARGING_KEY = "MONGODB_CURRENT_PROFIT_MARGING_KEY";
	private  final String PROPERTY_MONGODB_CLIENT_BALANCE_KEY = "MONGODB_CLIENT_BALANCE_KEY";
	private  final String PROPERTY_MONGODB_BROWSER_CALL_COST_KEY = "MONGODB_BROWSER_CALL_COST_KEY";
	private  final String PROPERTY_MONGODB_BROWSER_CALL_COST_VALUE = "MONGODB_BROWSER_CALL_COST_VALUE";
	private  final String PROPERTY_MONGODB_BROWSER_CALL_COST_CURRENT = "MONGODB_BROWSER_CALL_COST_CURRENT";
	private  final String PROPERTY_MONGODB_BROWSER_CALL_COLLECTION = "MONGODB_BROWSER_CALL_COLLECTION";
	private  final String PROPERTY_MONGODB_MAX_PREFIX_NUMBER_SIZE = "MONGODB_MAX_PREFIX_NUMBER_SIZE";
	
	private String TWILIO_RATE_COLLECTION;
	private String TALKDESK_NUMBER_COLLECTION;
	private String PROFIT_MARGING_COLLECTION;
	private String CLIENT_ACCOUNT_COLLECTION;
	private String CLIENT_BILLING_COLLECTION;
	private String BROWSER_CALL_COLLECTION;
	private String BROWSER_CALL_KEY;
	private String BROWSER_CALL_VALUE;
	private String BROWSER_CALL_CURRENT;
	private String PREFIX_KEY;
	private String RATE_KEY;
	private String PROFIT_MARGING_KEY;
	private String PROFIT_MARGING_VALUE;
	private String CURRENT_PROFIT_MARGING_KEY;
	private String CLIENT_BALANCE;
	private String CLIENT_ID;
	private int MAX_PREFIX_NUMBER_SIZE;
	
	MongoClient mongoClient;
	DB db;
	DBCollection COLLECTION;
		
	public CallBilling_Mongodb(String databaseName, String host, int port, String propertiesFile){
		try {
			
			TWILIO_RATE_COLLECTION = PropertyFileHandler.readProperty(PROPERTY_MONGODB_TWILIO_RATES_COLLECTION_NAME, propertiesFile);
			CLIENT_ACCOUNT_COLLECTION = PropertyFileHandler.readProperty(PROPERTY_MONGODB_CLIENT_ACCOUNT_BALANCE_COLLECTION_NAME, propertiesFile);
			TALKDESK_NUMBER_COLLECTION = PropertyFileHandler.readProperty(PROPERTY_MONGODB_TALKDESK_NUMBER_COST_COLLECTION_NAME, propertiesFile);
			CLIENT_BILLING_COLLECTION = PropertyFileHandler.readProperty(PROPERTY_MONGODB_CLIENT_BILLING_COLLECTION, propertiesFile);
			PREFIX_KEY = PropertyFileHandler.readProperty(PROPERTY_MONGODB_PREFIX_KEY, propertiesFile);
			RATE_KEY = PropertyFileHandler.readProperty(PROPERTY_MONGODB_RATE_KEY, propertiesFile);
			PROFIT_MARGING_COLLECTION = PropertyFileHandler.readProperty(PROPERTY_MONGODB_PROFIT_MARGING_COLLECTION_NAME, propertiesFile);
			PROFIT_MARGING_KEY = PropertyFileHandler.readProperty(PROPERTY_MONGODB_PROFIT_MARGING_KEY, propertiesFile);
			PROFIT_MARGING_VALUE = PropertyFileHandler.readProperty(PROPERTY_MONGODB_PROFIT_MARGING_VALUE, propertiesFile);
			CURRENT_PROFIT_MARGING_KEY = PropertyFileHandler.readProperty(PROPERTY_MONGODB_CURRENT_PROFIT_MARGING_KEY, propertiesFile);
			CLIENT_BALANCE = PropertyFileHandler.readProperty(PROPERTY_MONGODB_CLIENT_BALANCE_KEY, propertiesFile);
			CLIENT_ID = PropertyFileHandler.readProperty(PROPERTY_MONGODB_CLIENT_ACCOUNT_ID_KEY, propertiesFile);
			BROWSER_CALL_COLLECTION = PropertyFileHandler.readProperty(PROPERTY_MONGODB_BROWSER_CALL_COLLECTION, propertiesFile);
			BROWSER_CALL_KEY = PropertyFileHandler.readProperty(PROPERTY_MONGODB_BROWSER_CALL_COST_KEY, propertiesFile);
			BROWSER_CALL_VALUE = PropertyFileHandler.readProperty(PROPERTY_MONGODB_BROWSER_CALL_COST_VALUE, propertiesFile);
			BROWSER_CALL_CURRENT = PropertyFileHandler.readProperty(PROPERTY_MONGODB_BROWSER_CALL_COST_CURRENT, propertiesFile);
			MAX_PREFIX_NUMBER_SIZE = Integer.parseInt(PropertyFileHandler.readProperty(PROPERTY_MONGODB_MAX_PREFIX_NUMBER_SIZE, propertiesFile));
			
			mongoClient = new MongoClient(host, port);
			db = mongoClient.getDB(databaseName);
		
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * The DB format
	 * 	Key = prefix
	 * 	Value = cost
	 * Returns the complete list of TwilioPrices
	 */
	public HashMap<String, Double> getVoicePricesTwilio(){
		HashMap<String, Double> ratesMap;
		DBCollection twilioPricesCollection;
		DBCursor cursor;
		
		twilioPricesCollection = db.getCollection(TWILIO_RATE_COLLECTION);
		cursor = twilioPricesCollection.find();
		ratesMap = new HashMap<>();
		
        while (cursor.hasNext()) { 
        	DBObject current = cursor.next();
        	ratesMap.put((String) current.get(PREFIX_KEY), (Double)current.get(RATE_KEY));
        }
		
		return ratesMap;
	}
	
	/**
	 * The DB format
	 * 	Key = prefix
	 * 	Value = cost
	 * @param fileName
	 */
	public HashMap<String, Double> getTalkDeskRates(){
		HashMap<String, Double> ratesMap;
		DBCollection twilioPricesCollection;
		DBCursor cursor;
		
		twilioPricesCollection = db.getCollection(TALKDESK_NUMBER_COLLECTION);
		cursor = twilioPricesCollection.find();
		ratesMap = new HashMap<>();
		
        while (cursor.hasNext()) { 
        	DBObject current = cursor.next();
        	ratesMap.put((String) current.get(PREFIX_KEY), (Double)current.get(RATE_KEY));
        }
		
		return ratesMap;
	}

	/**
	 * Returns the number of digits contained in the largest "Phone Numbers that Start With" value 
	 * @return
	 */
	public int getMaxSizeDigitsInNumber(){
		return MAX_PREFIX_NUMBER_SIZE;
	}
	
	/**
	 * Gets the cost of the talkdesk number based on the given prefix
	 * if the prefix is not on the list then the prefix has a generic cost
	 * @param prefix 
	 * @return
	 */
	public double getTalkDeskNumberCost(String prefix){
		double cost;
		
		cost = readDoubleValue(TALKDESK_NUMBER_COLLECTION,PREFIX_KEY,prefix,RATE_KEY);
		
		return cost;
	}
	
	/**
	 * Gets the Twilio rate based on the given prefix
	 * @param prefix 
	 * @return
	 */
	public double getTwilioRate(String prefix){
		double cost;
		
		cost = readDoubleValue(TWILIO_RATE_COLLECTION,PREFIX_KEY,prefix,RATE_KEY);
		
		return cost;
	}
	
	/**
	 * Gets the  current profit margin
	 * 	Key
	 * @param prefix 
	 * @return
	 */
	public double getProfitMargin(){
		double cost;
		
		cost = readDoubleValue(PROFIT_MARGING_COLLECTION, PROFIT_MARGING_KEY, PROFIT_MARGING_VALUE,CURRENT_PROFIT_MARGING_KEY);
		
		return cost;
	}
	/**
	 * Returns the current account balance of the given client
	 * @return
	 */
	public double getAccountBalance(String client){
		double cost;
		
		cost = readDoubleValue(CLIENT_ACCOUNT_COLLECTION, CLIENT_ID, client,CLIENT_BALANCE);
		
		return cost;
	}
	/**
	 * Reads a double value from the given Key in the given collection
	 * @param collectionName
	 * @param queryKey
	 * @param queryValue
	 * @param keyToRead
	 * @return
	 */
	private Double readDoubleValue(String collectionName, String queryKey, String queryValue, String keyToRead){
		Double value = 0.0;
		Object valueFromDB;
		DBCollection collection;
		DBCursor cursor;
		BasicDBObject whereQuery;
		DBObject currentDbObject;
		
		collection = db.getCollection(collectionName);
		whereQuery = new BasicDBObject();
		whereQuery.put(queryKey, queryValue);
		cursor = collection.find(whereQuery);
		
		while(cursor.hasNext()) {
			currentDbObject = cursor.next();
			valueFromDB = currentDbObject.get(keyToRead);
			if (valueFromDB.getClass() == String.class){
				value = Double.parseDouble( (String)currentDbObject.get(keyToRead));
			}else{
				value = (Double) currentDbObject.get(keyToRead);
			}
		}
		
		return value;
	}
	
	/**
	 * Returns a document that matches with the given query on the given collection
	 * @param collectionName
	 * @param queryKey
	 * @param queryValue
	 * @return
	 */
	private Map readDocument(String collectionName, String queryKey, String queryValue){
		DBCollection collection;
		DBCursor cursor;
		BasicDBObject whereQuery;
		DBObject currentDbObject;
		Map document = null;
		
		collection = db.getCollection(collectionName);
		whereQuery = new BasicDBObject();
		whereQuery.put(queryKey, queryValue);
		cursor = collection.find(whereQuery);
		
		while(cursor.hasNext()) {
			currentDbObject = cursor.next();
			document = currentDbObject.toMap();
		}
		
		return document;
	}
	
	/**
	 * Charge the client with the callCost
	 * @param clientID
	 * @param valueToCharge
	 */
	public void chargeClient(String clientID, double callCost){
		double negativeCallCost = -callCost; //to decrement the value
		
		incrementValueInDocument(CLIENT_ACCOUNT_COLLECTION, CLIENT_ID,  clientID, CLIENT_BALANCE, negativeCallCost);
		updateBillingHistory(clientID, callCost);
	}
	
	/**
	 * Updates the list the charges for the given account
	 * @param clientID
	 * @param callCost
	 */
	public void updateBillingHistory(String clientID, double callCost){
		double negativeCallCost; 
		Date date;
		BasicDBObject document;
		DBCollection dbCollection;
		DateFormat dateFormat;//NOTE: this date field is used to save the date and time as key of charge
		
		dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		date = new Date();
		negativeCallCost = -callCost; //to decrement the value
		dbCollection = db.getCollection(CLIENT_BILLING_COLLECTION);
		
		document = new BasicDBObject().append("$set", new BasicDBObject().append(dateFormat.format(date), negativeCallCost));
		dbCollection.update(new BasicDBObject().append(CLIENT_ID, clientID), document);
	}
	
	/**
	 * Returns the list the charges for the given account
	 * @param clientID
	 * @return
	 */
	public Map getBillinHistory(String clientID){
		return readDocument(CLIENT_BILLING_COLLECTION, CLIENT_ID, clientID);
	}
	
	/**
	 * Increments the value of a given key in a document
	 * @param collectionName
	 * @param queryKey
	 * @param queryValue
	 * @param keyToUpdate
	 * @param value
	 */
	private void incrementValueInDocument(String collectionName, String queryKey, String queryValue, String keyToUpdate, double value){
		BasicDBObject document;
		DBCollection dbCollection;
		
		dbCollection = db.getCollection(collectionName);
		document = new BasicDBObject().append("$inc", new BasicDBObject().append(keyToUpdate, value));
		dbCollection.update(new BasicDBObject().append(queryKey, queryValue), document);
	}
	/**
	 * The DB format
	 * 	Key = prefix
	 * 	Value = cost
	 * @param fileName
	 * @throws UnknownHostException 
	 */
	public void setVoicePricesTwilio(HashMap<String, Double> twilioPrices) throws UnknownHostException{
		int maxPrefixNumbersize;
		BasicDBObject document;
		DBCollection twilioPricesCollection;
		
		db.getCollection(TWILIO_RATE_COLLECTION).drop();
		twilioPricesCollection = db.getCollection(TWILIO_RATE_COLLECTION);
		document = new BasicDBObject();
		maxPrefixNumbersize = 0;
		
		for (String phonePrefix: twilioPrices.keySet()){
			document.put(PREFIX_KEY, phonePrefix);
			document.put(RATE_KEY, twilioPrices.get(phonePrefix));
			twilioPricesCollection.insert(document);
			document.clear();
			if (phonePrefix.length() > maxPrefixNumbersize)
				maxPrefixNumbersize = phonePrefix.length();
		}
	}
	
	/**
	 * The DB format
	 * 	Key = prefix
	 * 	Value = cost
	 * @param talkdeskNumberCosts
	 * @throws UnknownHostException
	 */
	public void setTalkdeskNumberCost(HashMap<String, Double> talkdeskNumberCosts) throws UnknownHostException{
		BasicDBObject document;
		DBCollection talkdeskNumberCollection;
		
		db.getCollection(TALKDESK_NUMBER_COLLECTION).drop();
		talkdeskNumberCollection = db.getCollection(TALKDESK_NUMBER_COLLECTION);
		document = new BasicDBObject();
		
		for (String phonePrefix: talkdeskNumberCosts.keySet()){
			document.put(PREFIX_KEY, phonePrefix);
			document.put(RATE_KEY, talkdeskNumberCosts.get(phonePrefix));
			talkdeskNumberCollection.insert(document);
			document.clear();
		}
			
	}
	/**
	 * CHALLENGE NOTE: 1c if the call is answered in the web browser, 
	 * otherwise the price to charge should be the same as Twilio charges for calls to that number.
	 * For stong consistent access
	 * @param forwardedNumber
	 * @return
	 */
	public double getExternalNumberCost(String prefix){
		double cost;
		
		if (prefix == null){
			cost = getBrowserCallCostFromDB();
		}else{
			cost = getTwilioRate(prefix);
		}
		return cost;
	}
	/**
	 * Returns the browsers call cost
	 * @return
	 */
	public double getBrowserCallCostFromDB(){
		double cost;
		
		cost = readDoubleValue(BROWSER_CALL_COLLECTION, BROWSER_CALL_KEY, BROWSER_CALL_VALUE,  BROWSER_CALL_CURRENT);
		
		return cost;
	}
}
