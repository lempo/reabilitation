package reabilitation;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.io.*;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import defaults.TextLinkDefaults;
import exception.DiskPermissionsException;
import exception.HddSerialScriptException;
import exception.ProgramFilesBrokenException;

public class Utills {
	public static Document openXML(String path) {
		DocumentBuilderFactory f = null;
		DocumentBuilder builder = null;
		Document doc = null;

		try {
			f = DocumentBuilderFactory.newInstance();
			f.setValidating(false);
			builder = f.newDocumentBuilder();
		}

		catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}

		try {
			doc = builder.parse(Utills.class.getResource(path).openStream());
		} catch (SAXException | IOException e1) {
			e1.printStackTrace();
		}

		return doc;
	}

	public static Document openXMLAbsolutePath(String path) throws ProgramFilesBrokenException {
		DocumentBuilderFactory f = null;
		DocumentBuilder builder = null;
		Document doc = null;

		try {
			f = DocumentBuilderFactory.newInstance();
			f.setValidating(false);
			builder = f.newDocumentBuilder();
		}

		catch (ParserConfigurationException e1) {
			e1.printStackTrace();
			throw new ProgramFilesBrokenException();
		}

		try {
			doc = builder.parse(new File(path));
		} catch (SAXException | IOException e1) {
			e1.printStackTrace();
			throw new ProgramFilesBrokenException();
		}

		return doc;
	}

	public static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = Utills.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.out.println("Couldn't find file: " + path);
			return null;
		}
	}

	public static Set<Component> getAllComponents(Component component) {
		Set<Component> children = new HashSet<Component>();
		children.add(component);
		if (component instanceof Container) {
			Container container = (Container) component;
			Component[] components = container.getComponents();
			for (int i = 0; i < components.length; i++) {
				children.addAll(getAllComponents(components[i]));
			}
		}
		return children;
	}

	public static Object createObject(Constructor constructor, Object[] arguments) {
		Object object = null;

		try {
			object = constructor.newInstance(arguments);
			return object;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return object;
	}

	/**
	 * ������������ ������ ����� ������ ��� �������� ������ (� ��������)
	 * ������������, ��� ����� �������� html, ������� ������ ��� �������� ������
	 * ���, ��� ������ � ���� � ��������� style. �� ���������� �� �����������
	 * �����! ��������� ������ ����� �� �������� style, ���� �� ��� ��� -
	 * ���������� �� ��������� 14pt ArialNarrow.
	 * 
	 * @param text
	 *            �����
	 * @param width
	 *            ������
	 * @param c
	 *            ���������, �� ������� �������������� �����
	 * @return ������
	 */
	public static int calculateTextHeight(String text, int width, JComponent c) {
		int height = 0;

		DocumentBuilder db = null;
		Document doc = null;
		try {
			db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(text));

		try {
			doc = db.parse(is);
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}

		NodeList nodeList = doc.getElementsByTagName("*");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			NamedNodeMap k = node.getAttributes();
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				int s = Font.PLAIN;
				int size = 12;
				String name = "ArialNarrow";
				if (k.getNamedItem("style") != null) {
					String style = k.getNamedItem("style").getNodeValue();
					if (style.indexOf("font:") != -1) {
						int j = style.indexOf("font:") + "font: ".length();
						int p = style.indexOf(";");
						style = style.substring(j, p);

						String[] splited = style.split(" ");

						for (int t = 0; t < splited.length; t++) {
							if (splited[t].equals("bold")) {
								s += Font.BOLD;
								continue;
							}
							if (splited[t].equals("italic")) {
								s += Font.ITALIC;
								continue;
							}
							if (Character.isDigit(splited[t].charAt(0))) {
								splited[t] = splited[t].substring(0, splited[t].length() - "pt".length());
								size = Integer.parseInt(splited[t]);
								continue;
							}
							name = splited[t];
						}
					}
					Font f = new Font(name, s, size);
					FontMetrics fm = c.getFontMetrics(f);
					int w = fm.stringWidth(node.getTextContent());
					int h = fm.getHeight();
					height += Math.ceil((double) w / width) * h;
				}
			}
		}
		return height;
	}

	public static Document openXMLFromString(String xml) {
		DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document doc = null;
		try {
			builder = f.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));
			try {
				doc = builder.parse(is);
				String message = doc.getDocumentElement().getTextContent();
				System.out.println(message);
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}

		return doc;
	}

	public static String getHDDSerialNumber() throws DiskPermissionsException, ProgramFilesBrokenException, HddSerialScriptException {
		String result = "";
		File file;
		FileWriter fw;
		String vbs = "";
		Scanner in;
		Process p;

		try {
			file = File.createTempFile("realhowto", ".vbs");
			file.deleteOnExit();
			fw = new java.io.FileWriter(file);
		} catch (IOException e) {
			e.printStackTrace();
			throw new DiskPermissionsException(e);
		}

		try {
			in = new Scanner(Utills.class.getResource("resources/text/script.txt").openStream());
		} catch (IOException e) {
			e.printStackTrace();
			throw new ProgramFilesBrokenException(e);
		}
		while (in.hasNext())
			vbs += in.nextLine() + "\r\n";
		in.close();

		try {
			fw.write(vbs);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new DiskPermissionsException(e);
		}

		try {
			p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = input.readLine()) != null)
				result += line;
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new HddSerialScriptException(e);
		}

		return result.trim();
	}

	public static String getFilePath() {
		return new File("").getAbsolutePath();
	}

	public static String getLicenceKey() throws ProgramFilesBrokenException {
		Document doc = Utills.openXMLAbsolutePath(Utills.getFilePath() + "/config");
		NodeList n = doc.getElementsByTagName("key");
		if (n.item(0) == null)
			throw new ProgramFilesBrokenException();
		return n.item(0).getTextContent();
	}

	public static String getLicenceUserName() throws ProgramFilesBrokenException {
		Document doc = Utills.openXMLAbsolutePath(Utills.getFilePath() + "/config");
		NodeList n = doc.getElementsByTagName("username");
		if (n.item(0) == null)
			throw new ProgramFilesBrokenException();
		return n.item(0).getTextContent();
	}

	public static String getVersion() throws ProgramFilesBrokenException {
		Document doc = Utills.openXML(TextLinkDefaults.getInstance().getLink(TextLinkDefaults.Key.VERSION));
		NodeList n = doc.getElementsByTagName("version");
		if (n.item(0) == null)
			throw new ProgramFilesBrokenException();
		return n.item(0).getTextContent();
	}

	public static String getVersionDate() throws ProgramFilesBrokenException {
		Document doc = Utills.openXML(TextLinkDefaults.getInstance().getLink(TextLinkDefaults.Key.VERSION));
		NodeList n = doc.getElementsByTagName("date");
		if (n.item(0) == null)
			throw new ProgramFilesBrokenException();
		return n.item(0).getTextContent();
	}

	public static String getAppServer() throws ProgramFilesBrokenException {
		Document doc = Utills.openXML(TextLinkDefaults.getInstance().getLink(TextLinkDefaults.Key.SERVER));
		NodeList n = doc.getElementsByTagName("app");
		if (n.item(0) == null)
			throw new ProgramFilesBrokenException();
		return n.item(0).getTextContent();
	}

	public static Boolean getCheckUpdatesAuto() {
		Document doc = null;
		try {
			doc = Utills.openXMLAbsolutePath(Utills.getFilePath() + "/config");
			NodeList n = doc.getElementsByTagName("checkUpdatesAuto");
			if (n.item(0) == null)
				throw new ProgramFilesBrokenException();
			if (n.item(0).getTextContent().equals("true"))
				return true;
			else
				return false;
		} catch (ProgramFilesBrokenException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String readFile(String path) {
		byte[] encoded = null;
		String s = null;
		try {
			encoded = Files.readAllBytes(Paths.get(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			s = new String(encoded, "utf8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return s;
	}

	public static void writeFile(String contents, String path) {
//		File file = new File(path);
//		FileWriter fileWriter;
//		try {
//			fileWriter = new FileWriter(file);
//			fileWriter.write(contents);
//			fileWriter.flush();
//			fileWriter.close();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
		
		File f = new File(path);
		OutputStreamWriter bufferedWriter;
		try {
			bufferedWriter = new OutputStreamWriter(new FileOutputStream(f), "UTF8");
			bufferedWriter.append(contents);
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	
	public static void createConfigFile(File f, String name, String key) throws DiskPermissionsException {
		DateFormat format = new SimpleDateFormat("dd.MM.yy");
		try {
			f.createNewFile();
			OutputStreamWriter bufferedWriter = new OutputStreamWriter(new FileOutputStream(f), "UTF8");
			bufferedWriter.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
			bufferedWriter.append("<config>\n");
			bufferedWriter.append("<username>" + name + "</username>\n");
			bufferedWriter.append("<key>" + key + "</key>\n");
			bufferedWriter.append("<checkUpdatesAuto>false</checkUpdatesAuto>");
			bufferedWriter.append("</config>");
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new DiskPermissionsException(e);
		}
	}
}
