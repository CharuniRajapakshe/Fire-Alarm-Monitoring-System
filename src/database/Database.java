package database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sensor.SensorMessage;

public class Database {

	// write sensor updates to SensorHistory.xml file
	public synchronized void addSensorsData(SensorMessage sensor)
			throws ParserConfigurationException, SAXException, IOException, TransformerException {

		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.parse("SensorHistory.xml");
			Element rootElement = doc.getDocumentElement();

			// Sensor elements
			Element sens = doc.createElement("Sensor");
			rootElement.appendChild(sens);

			Element id = doc.createElement("id");
			id.appendChild(doc.createTextNode(sensor.getId()));
			sens.appendChild(id);

			Element loc = doc.createElement("loc");
			loc.appendChild(doc.createTextNode(sensor.location));
			sens.appendChild(loc);

			Element temp = doc.createElement("temp");
			temp.appendChild(doc.createTextNode(Double.toString(sensor.getTemp())));
			sens.appendChild(temp);

			Element battery = doc.createElement("battery");
			battery.appendChild(doc.createTextNode(Integer.toString(sensor.getBattery())));
			sens.appendChild(battery);

			Element smoke = doc.createElement("smoke");
			smoke.appendChild(doc.createTextNode(Integer.toString(sensor.getSmoke())));
			sens.appendChild(smoke);

			Element co2 = doc.createElement("co2");
			co2.appendChild(doc.createTextNode(Integer.toString(sensor.getCo2())));
			sens.appendChild(co2);

			Element resFlag = doc.createElement("resFlag");
			resFlag.appendChild(doc.createTextNode(Boolean.toString(sensor.isResponsFlag())));
			sens.appendChild(resFlag);

			Element emeFlag = doc.createElement("emeFlag");
			emeFlag.appendChild(doc.createTextNode(Boolean.toString(sensor.isEmergencyFlag())));
			sens.appendChild(emeFlag);

			Element date = doc.createElement("date");
			date.appendChild(doc.createTextNode(sensor.date));
			sens.appendChild(date);

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult("SensorHistory.xml");

			transformer.transform(source, result);

			System.out.println("File saved!");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

	// modify the SensorHistory.xml file
	// add or delete sensor data
	public synchronized void addDeleteSensorData(SensorMessage sensor, String s) {

		try {
			String path = "SensorHistory.xml";
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(path);

			// input id
			String id = sensor.getId();
			Node delNode = searchById(id, document);
			if (delNode != null) {
				document.getDocumentElement().removeChild(delNode);
				saveData(document, path);
			}
			if (s.equalsIgnoreCase("add")) {
				addSensorsData(sensor);
			}

		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// search sensors by sensor id
	public static Node searchById(String id, Document doc) {

		NodeList lst = doc.getElementsByTagName("id");

		for (int i = 0; i < lst.getLength(); i++) {
			String content = lst.item(i).getTextContent();
			if (content.equalsIgnoreCase(id)) {
				Node p = lst.item(i).getParentNode();
				return p;
			}
		}
		return null;

	}

	// save the modified sensor data
	public synchronized void saveData(Document doc, String path) {

		try {
			System.out.println(path);
			DOMSource source = new DOMSource(doc);
			File f = new File(path);
			StreamResult result = new StreamResult(f);
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(source, result);
			System.out.println("save");

		} catch (TransformerFactoryConfigurationError | TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// read SensorHistory.xml file and get all sensor data
	public List<SensorMessage> readSensorData(String filePath) {
		List<SensorMessage> senList = new ArrayList<SensorMessage>();
		System.out.println(filePath);
		File xmlFile = new File(filePath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			NodeList nodeList = doc.getElementsByTagName("Sensor");

			// XML is loaded as Document in memory
			// convert it to Object List
			for (int i = 0; i < nodeList.getLength(); i++) {
				senList.add(getSensor(nodeList.item(i)));
			}

		} catch (SAXException | ParserConfigurationException | IOException e1) {
			e1.printStackTrace();
		}

		return senList;

	}

	// set xml sensor node values to SensorMessage objects
	private static SensorMessage getSensor(Node node) {

		SensorMessage msg = new SensorMessage();

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Element element = (Element) node;
			msg.setId(getTagValue("id", element));
			msg.location = getTagValue("loc", element);
			msg.setTemp(Double.parseDouble(getTagValue("temp", element)));
			msg.setBattery(Integer.parseInt(getTagValue("battery", element)));
			msg.setSmoke(Integer.parseInt(getTagValue("smoke", element)));
			msg.setCo2(Integer.parseInt(getTagValue("co2", element)));
			msg.setResponsFlag(Boolean.parseBoolean(getTagValue("resFlag", element)));
			msg.setEmergencyFlag(Boolean.parseBoolean(getTagValue("emeFlag", element)));
			msg.date = getTagValue("date", element);
		}
		System.out.println(msg.getId() + " " + msg.location);

		return msg;
	}

	private static String getTagValue(String tag, Element element) {
		NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
		Node node = (Node) nodeList.item(0);
		return node.getNodeValue();
	}

	// first check for the sensor existence and then return the sensor values 
	public SensorMessage readCurrentData(String id, String path) {
		
		SensorMessage sm = new SensorMessage();

		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(path);
			

			// input id
			Node node = searchById(id, document);
			if (node != null) {
				return getSensor(node);
			}

		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sm;
	}

	// Check whether the user exists in the login.xml
	public boolean userExists(String username, String filePath) {

		try {
			File fXmlFile = new File(filePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("user");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					if (getTagValue("username", eElement).equals(username)) {
						return true;
					}
				}
			}
			return false;
		} catch (Exception ex) {
			System.out.println("Database exception : userExists()");
			return false;
		}
	}

	//First check user existence and then compare username with the password value
	public boolean checkLogin(String username, String password, String filePath) {

		if (!userExists(username, filePath)) {
			return false;
		}

		try {
			File fXmlFile = new File(filePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("user");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					if (getTagValue("username", eElement).equals(username)
							&& getTagValue("password", eElement).equals(password)) {
						return true;
					}
				}
			}
			System.out.println("Hippie");
			return false;
		} catch (Exception ex) {
			System.out.println("Database exception : userExists()");
			return false;
		}
	}
}
