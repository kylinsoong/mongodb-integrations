package org.mongodb.sample;

import java.io.FileReader;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;


import org.json.JSONObject;
import org.json.XML;

public class SimpleTransfer {

	public static void main(String[] args) throws Exception {

		Path path = Paths.get(new URI("file:///Users/ksoong/tmp/row.xml"));
//		XMLStreamReader sr = XMLInputFactory.newFactory().createXMLStreamReader(Files.newInputStream(path));
//		XmlMapper mapper = new XmlMapper();
		
		JSONObject xmlJSONObj = XML.toJSONObject(new FileReader(path.toFile()));
		
		
		System.out.println(xmlJSONObj.getJSONObject("row").toString());
	}

}
