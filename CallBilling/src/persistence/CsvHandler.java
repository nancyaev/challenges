package persistence;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

/**
 * This class handles the Twilio Csv file
 * @author nestrada
 *
 */
public class CsvHandler {
	private  final String PROPERTIES_FILE;
	private  final String PROPERTY_CSV_TWILIO_VOICE_PRICES = "CSV_TWILIO_VOICE_PRICES";
	private  final String PROPERTY_CSV_TWILIO_VOICE_RATE_INDEX = "CSV_TWILIO_VOICE_RATE_INDEX";
	private  final String PROPERTY_CSV_TWILIO_VOICE_PHONE_INDEX = "CSV_TWILIO_VOICE_PHONE_INDEX";
	private  final String PROPERTY_CSV_TWILIO_VOICE_SEPARATOR = "CSV_TWILIO_VOICE_SEPARATOR";
	
	private  final String PROPERTY_CSV_TALKDESK_COSTS = "CSV_TALKDESK_COSTS";
	private  final String PROPERTY_CSV_TALKDESK_RATE_INDEX = "CSV_TALKDESK_RATE_INDEX";
	private  final String PROPERTY_CSV_TALKDESK_PHONE_INDEX = "CSV_TALKDESK_PHONE_INDEX";
	private  final String PROPERTY_CSV_TALKDESK_SEPARATOR = "CSV_TALKDESK_SEPARATOR";
	
	private  final int FIRST_ROW = 1;
	private  CallBilling_Mongodb db;
	
	public CsvHandler(String databaseName, String host, int port, String propertiesFile){
		PROPERTIES_FILE = propertiesFile;
		db = new CallBilling_Mongodb(databaseName,host,port,propertiesFile);
	}
	
	/**
	 * This method reads the voice prices of Twilio
	 * CSV FORMAT: Name, Rate, Phone Numbers that Start With
	 * Stores the data in db
	 * 	Key = Phone Numbers that Start With
	 *  Value = Rate
	 * @param fileName
	 * @throws  
	 */
	public void saveVoicePricesTwilio()  {
		int  rateIndex, phonePrefixIndex;
		String CsvFileName, phonePrefixSeparator, listPrefixPhone;
		String[] arrayPrefixPhone;
		Double rate;
		Reader in;
		Iterable<CSVRecord> CsvRecords;
		HashMap<String, Double> twilioPricesMap;
		
		try {
			CsvFileName = PropertyFileHandler.readProperty(PROPERTY_CSV_TWILIO_VOICE_PRICES,PROPERTIES_FILE);
			rateIndex = Integer.parseInt(PropertyFileHandler.readProperty(PROPERTY_CSV_TWILIO_VOICE_RATE_INDEX,PROPERTIES_FILE));
			phonePrefixIndex = Integer.parseInt(PropertyFileHandler.readProperty(PROPERTY_CSV_TWILIO_VOICE_PHONE_INDEX,PROPERTIES_FILE));
			phonePrefixSeparator = PropertyFileHandler.readProperty(PROPERTY_CSV_TWILIO_VOICE_SEPARATOR,PROPERTIES_FILE);
			
			in = new FileReader(CsvFileName);
			CsvRecords = CSVFormat.EXCEL.parse(in);
			twilioPricesMap = new HashMap<String, Double>();

			for (CSVRecord record : CsvRecords) {
				if (record.getRecordNumber() != FIRST_ROW){
					rate = Double.parseDouble(record.get(rateIndex));
					listPrefixPhone  = record.get(phonePrefixIndex).replaceAll("\\s","");
					arrayPrefixPhone = listPrefixPhone.split(phonePrefixSeparator);
					
					for (String currentPrefix : arrayPrefixPhone){
						twilioPricesMap.put(currentPrefix, rate);
					}
				}
			}
			db.setVoicePricesTwilio(twilioPricesMap);
		} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * This method reads the voice prices of Talkdesk Csv file
	 * CSV FORMAT: Name, Rate, Phone Numbers that Start With
	 * Stores the data in db
	 * 	Key = Phone Numbers that Start With
	 *  Value = Rate
	 * @param fileName
	 * @throws  
	 */
	public void saveTalkdeskCost()  {
		int rateIndex, phonePrefixIndex;
		String CsvFileName, phonePrefixSeparator, listPrefixPhone;
		String[] arrayPrefixPhone;
		Double rate;
		Reader in;
		Iterable<CSVRecord> CsvRecords;
		HashMap<String, Double> talkdeskMap;
		
		try {
			CsvFileName = PropertyFileHandler.readProperty(PROPERTY_CSV_TALKDESK_COSTS,PROPERTIES_FILE);
			rateIndex = Integer.parseInt(PropertyFileHandler.readProperty(PROPERTY_CSV_TALKDESK_RATE_INDEX,PROPERTIES_FILE));
			phonePrefixIndex = Integer.parseInt(PropertyFileHandler.readProperty(PROPERTY_CSV_TALKDESK_PHONE_INDEX,PROPERTIES_FILE));
			phonePrefixSeparator = PropertyFileHandler.readProperty(PROPERTY_CSV_TALKDESK_SEPARATOR,PROPERTIES_FILE);
			
			in = new FileReader(CsvFileName);
			CsvRecords = CSVFormat.EXCEL.parse(in);
			talkdeskMap = new HashMap<String, Double>();

			for (CSVRecord record : CsvRecords) {
				if (record.getRecordNumber() != FIRST_ROW){
					rate = Double.parseDouble(record.get(rateIndex));
					listPrefixPhone  = record.get(phonePrefixIndex).replaceAll("\\s","");
					arrayPrefixPhone = listPrefixPhone.split(phonePrefixSeparator);
					
					for (String currentPrefix : arrayPrefixPhone){
						talkdeskMap.put(currentPrefix, rate);
					}
				}
			}
			db.setTalkdeskNumberCost(talkdeskMap);
		} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
