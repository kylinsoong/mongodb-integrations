package org.mongodb.sample;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.bson.Document;
import org.json.JSONObject;
import org.json.XML;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * If RuntimeException throw, please execute GenerateLargeXML first.
 * 
 * @author ksoong
 *
 */
public class XMLToJSONloader {
	
	private static final String username = "root";
	
	private static final String password = "mongo";
	
	private static final String url = "localhost:27000,localhost:27001,localhost:27002";
	
	private static final String authDB = "admin";
	
	private static MongoClient MDB_CLIENT = null;
	
	private static MongoDatabase MDB = null;
	
	private static MongoCollection<Document> COLL = null;
	
	public static MongoCollection<Document> getCollection() {
		
		if(null == MDB_CLIENT) {
			String uri = "mongodb://" + username + ":" + password + "@" + url + "/" + authDB;
			ConnectionString connectionString = new ConnectionString(uri);
			MDB_CLIENT = MongoClients.create(connectionString);
		}
		
		if(null == MDB) {
			MDB = MDB_CLIENT.getDatabase("bos");
		}
		
		if(null == COLL) {
			COLL = MDB.getCollection("xmltojson");
		}
				
		return COLL;
	}
	
	public static void destory() {
		if(null != MDB_CLIENT) {
			MDB_CLIENT.close();
		}
	}

	public static void main(String[] args) throws Exception {
		
		String tmpDir = getUsersHomeDir() + File.separator + "tmp" ;
		Path tmpPath = Paths.get(tmpDir);
		Path path = Paths.get(tmpPath.toString(), "rows.xml");
    	
    	if(!Files.exists(path)) {
    		throw new RuntimeException(path + " does not exist");
    	}
    	
    	long start = System.currentTimeMillis();

    	XMLEventReader xmlEventReader = XMLInputFactory.newFactory().createXMLEventReader(Files.newInputStream(path));
		
		while (xmlEventReader.hasNext()) {
			
			XMLEvent event = xmlEventReader.nextEvent();
			
			if(event.isStartElement()) {
				StartElement startElement = event.asStartElement();
				if (startElement.getName().getLocalPart().equals("row")) {
					StringBuffer sb = new StringBuffer(startElement.toString());
					extractSubXml(sb, xmlEventReader);
					xmltojson(sb.toString());
				}
			}
		}
		
		System.out.println("XmltoJSON 1m rows spend " + (System.currentTimeMillis() - start) + " milliseconds");
		
		destory();
	}
	
	private static void xmltojson(String xml) {
		
		JSONObject xmlJSONObj = XML.toJSONObject(xml);
		
		MongoCollection<Document> collection = getCollection();
		
		collection.insertOne(org.bson.Document.parse(xmlJSONObj.getJSONObject("row").toString()));
		
	}

	private static void extractSubXml(StringBuffer sb, XMLEventReader xmlEventReader) throws XMLStreamException {
		
		while (xmlEventReader.hasNext()){
			
			XMLEvent event = xmlEventReader.nextEvent();
			
			if(!event.isStartElement() && !event.isEndElement()) {
				//TODO this need more fine-grained logic, use html style symbols
				String tmp = event.toString();
				tmp = tmp.replace("&", "&amp;");
				tmp = tmp.replace(">", "&gt;");
				sb.append(tmp);
			} else {
				sb.append(event.toString());
			}
			
			
			if(event.isEndElement()) {
				EndElement endElement = event.asEndElement();
				if(endElement.getName().getLocalPart().equals("row")) {
					break;
				}
			}
		}
	}

	static String getUsersHomeDir() {
	    String users_home = System.getProperty("user.home");
	    return users_home.replace("\\", "/"); // to support all platforms.
	}

}
