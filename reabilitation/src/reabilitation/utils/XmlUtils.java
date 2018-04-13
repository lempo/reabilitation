package reabilitation.utils;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import defaults.TextLinkDefaults;
import reabilitation.Task;
import reabilitation.TaskGroup;

public class XmlUtils {
	public static TaskGroup[] getTaskGroups() {
		Document doc = Utils.openXML(TextLinkDefaults.getInstance().getLink(TextLinkDefaults.Key.GROUPS));

		NodeList n = doc.getElementsByTagName("group");
		NamedNodeMap k = null;
		
		TaskGroup[] taskGroups = new TaskGroup[n.getLength()];
		
		for (int i = 0; i < n.getLength(); i++) {
			k = n.item(i).getAttributes();
			taskGroups[i] = new TaskGroup(k.getNamedItem("name").getNodeValue(), k.getNamedItem("text").getNodeValue(),
					k.getNamedItem("image").getNodeValue(), k.getNamedItem("bigImage").getNodeValue(),
					k.getNamedItem("rolloverImage").getNodeValue(), k.getNamedItem("toolTipText").getNodeValue());
		}
		
		return taskGroups;
	}
	
	public static Task[] getTasks(int groupNumber) {
		Document doc = Utils.openXML(TextLinkDefaults.getInstance().getLink(TextLinkDefaults.Key.GROUPS));

		NodeList n = doc.getElementsByTagName("group");
		NamedNodeMap k = null;

		NodeList n1 = n.item(groupNumber).getChildNodes();
		Task[] tasks = new Task[n1.getLength()];

		for (int j = 0; j < n1.getLength(); j++) {
			k = n1.item(j).getAttributes();
			tasks[j] = new Task(k.getNamedItem("name").getNodeValue(), k.getNamedItem("image").getNodeValue(),
					k.getNamedItem("shortText").getNodeValue(), k.getNamedItem("longText").getNodeValue(),
					k.getNamedItem("longLongText").getNodeValue(), k.getNamedItem("bigImage").getNodeValue(),
					k.getNamedItem("className").getNodeValue(), k.getNamedItem("rolloverImage").getNodeValue());
		}
		
		return tasks;
	}
}
