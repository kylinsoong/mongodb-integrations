package org.mongodb.sample;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bson.conversions.Bson;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Pipelines {
	
	List<String> original = new ArrayList<>();
	List<List<? extends Bson>> pipelines = new ArrayList<>();
	List<String> optimized = new ArrayList<>();
	
	public List<List<? extends Bson>> pipelines() {
		return pipelines;
	}
	
	public String original(int i) {
		return original.get(i);
	}
	
	public List<? extends Bson> pipeline(int i) {
		return pipelines.get(i);
	}
	
	public String optimized(int i) {
		return optimized.get(i);
	}

	public Pipelines(String fileName) {
		try {
			init(fileName);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void init(String fileName) throws ParserConfigurationException, SAXException, IOException {

		File inputFile = new File(fileName);
		if(!inputFile.exists()) {
			throw new FileNotFoundException(fileName);
		}
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();
        
        NodeList nList = doc.getElementsByTagName("pipeline");
        
        for (int temp = 0; temp < nList.getLength(); temp++) {
        	Node nNode = nList.item(temp);
        	if (nNode.getNodeType() == Node.ELEMENT_NODE) {
        		Element eElement = (Element) nNode;
        		List<org.bson.Document> pipeline = new ArrayList<>();
        		StringBuffer sb = new StringBuffer();
        		sb.append("[");
        		String value = eElement.getAttribute("value");
        		original.add(value);
        		NodeList slist = eElement.getElementsByTagName("stage");
        		
        		for(int i = 0 ; i < slist.getLength() ; i ++) {
        			Node sNode = slist.item(i);
        			String json = sNode.getTextContent();
        			sb.append(json).append(",");
        			org.bson.Document stage = org.bson.Document.parse(json);
        			pipeline.add(stage);
        		}
        		if(pipeline.size() > 0) {
        			optimized.add(sb.toString().substring(0, sb.toString().length() - 1) + "]");
        			pipelines.add(pipeline);
        		}
        	}
        }	
	}

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		
		
		Pipelines test = new Pipelines("pipelines.xml");
		        
		List<List<? extends Bson>> pipelines = test.pipelines();
		
		for(int i = 0 ; i < pipelines.size() ; i ++) {
			System.out.println("优化前：");
			System.out.println(test.original(i));
			System.out.println("优化后：");
			System.out.println(test.optimized(i));
			System.out.println(test.pipeline(i));
		}
				
	}

}
