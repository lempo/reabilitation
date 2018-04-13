package defaults;

import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import reabilitation.utils.Utils;

public class InterfaceTextDefaults {
	private static InterfaceTextDefaults instance;
	private static String fileName;

	public static InterfaceTextDefaults getInstance() {
		if (instance == null) {
			instance = new InterfaceTextDefaults();
		}
		return instance;
	}

	private HashMap<String, String> map;

	private InterfaceTextDefaults() {
		fileName = TextLinkDefaults.getInstance().getLink(TextLinkDefaults.Key.INTERFACE);
		map = new HashMap<String, String>();
		Document doc = Utils.openXML(fileName);
		NodeList n = doc.getElementsByTagName("string");
		for (int i = 0; i < n.getLength(); i++) {
			String s = n.item(i).getAttributes().getNamedItem("value").getNodeValue();
			s = s.replaceAll("_", "&nbsp;");
			map.put(n.item(i).getAttributes().getNamedItem("key").getNodeValue(), s);
		}
	}

	public String getDefault(String key) {
		return map.get(key);
	}
}
