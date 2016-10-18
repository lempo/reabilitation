package reabilitation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JOptionPane;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class HTTPClient {

	private final static String USER_AGENT = "Mozilla/5.0";
	private static String SERVER = "https://komplimed.herokuapp.com";
	// private final static String SERVER = "http://localhost:3000";

	public static String makeRequest(String url) {
		URL obj = null;
		StringBuffer response = null;
		try {
			obj = new URL(url);

			HttpURLConnection con = null;

			con = (HttpURLConnection) obj.openConnection();

			// optional default is GET

			con.setRequestMethod("GET");

			// add request header
			con.setRequestProperty("User-Agent", USER_AGENT);

			BufferedReader in = null;

			in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

			String inputLine;
			response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}

			in.close();
			con.disconnect();
		}

		catch (IOException e) {
			e.printStackTrace();
			Object[] options = { "OK" };
			JOptionPane.showOptionDialog(null, "Не удалось соединиться с сервером!", "Ошибка",
					JOptionPane.PLAIN_MESSAGE, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		}

		return response.toString();
	}

	public static boolean loginSpec(String name, String pass) {
		String url = null;
		try {
			url = SERVER + "/api/loginspec.xml?name=" + URLEncoder.encode(name, "UTF8") + "&pass="
					+ URLEncoder.encode(pass, "UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String response = makeRequest(url);

		if (response.equals("1"))
			return true;
		else
			return false;
	}

	public static boolean loginPatient(String name, String pass) {
		String url = null;
		try {
			url = SERVER + "/api/loginpatient.xml?name=" + URLEncoder.encode(name, "UTF8") + "&pass="
					+ URLEncoder.encode(pass, "UTF8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String response = makeRequest(url);

		if (response.equals("1"))
			return true;
		else
			return false;
	}

	public static String[][] listPatients() {
		String url = SERVER + "/api/listpatients.xml";
		String response = makeRequest(url);

		ArrayList<String[]> rows = new ArrayList<String[]>();

		Document doc = Utills.openXMLFromString(response);
		NodeList n = doc.getElementsByTagName("patient");
		for (int i = 0; i < n.getLength(); i++) {
			NodeList n1 = n.item(i).getChildNodes();
			String name = null;
			String number = null;
			for (int j = 0; j < n1.getLength(); j++) {
				if (n1.item(j).getNodeName().equals("name"))
					name = n1.item(j).getTextContent();
				if (n1.item(j).getNodeName().equals("pass"))
					number = n1.item(j).getTextContent();
			}

			String s[] = { number, name };
			rows.add(s);
		}

		String r[][];
		r = new String[rows.size()][];

		for (int i = 0; i < rows.size(); i++)
			r[i] = rows.get(i);

		return r;
	}

	public static void newPatient(String name, String pass) {
		String url = null;
		try {
			url = SERVER + "/api/newpatient.xml?name=" + URLEncoder.encode(name, "UTF8") + "&pass="
					+ URLEncoder.encode(pass, "UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		makeRequest(url);
	}

	public static void deletePatient(String name, String pass) {
		String url = null;
		try {
			url = SERVER + "/api/deletepatient.xml?name=" + URLEncoder.encode(name, "UTF8") + "&pass="
					+ URLEncoder.encode(pass, "UTF8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		makeRequest(url);
	}

	public static void saveResult(String name, String pass, String taskName, int result, String taskGroup) {
		String url = null;

		// do we have such group?
		try {
			url = SERVER + "/api/groupexists.xml?name=" + URLEncoder.encode(taskGroup, "UTF8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String response = makeRequest(url);
		// we don't, so need to add
		if (response.equals("0")) {
			try {
				url = SERVER + "/api/addgroup.xml?name=" + URLEncoder.encode(taskGroup, "UTF8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			makeRequest(url);
		}

		// do we have such task?
		try {
			url = SERVER + "/api/taskexists.xml?name=" + URLEncoder.encode(taskName, "UTF8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		response = makeRequest(url);
		// we don't, so need to add
		if (response.equals("0")) {
			try {
				url = SERVER + "/api/addtask.xml?name=" + URLEncoder.encode(taskName, "UTF8") + "&group_name="
						+ URLEncoder.encode(taskGroup, "UTF8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			makeRequest(url);
		}

		// now add result
		try {
			url = SERVER + "/api/addresult.xml?name=" + URLEncoder.encode(name, "UTF8") + "&pass="
					+ URLEncoder.encode(pass, "UTF8") + "&task_name=" + URLEncoder.encode(taskName, "UTF8") + "&result="
					+ result;
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		makeRequest(url);
	}

	public static String[][] findResults(String name, String pass) {
		String url = null;
		try {
			url = SERVER + "/api/findresults.xml?name=" + URLEncoder.encode(name, "UTF8") + "&pass="
					+ URLEncoder.encode(pass, "UTF8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String response = makeRequest(url);

		ArrayList<String[]> rows = new ArrayList<String[]>();

		Document doc = Utills.openXMLFromString(response);
		NodeList n = doc.getElementsByTagName("result");

		for (int i = 0; i < n.getLength(); i++) {
			NodeList n1 = n.item(i).getChildNodes();
			String taskId = null;
			String taskName = null;
			String taskGroup = null;
			String date = null;
			String result = null;
			for (int j = 0; j < n1.getLength(); j++) {
				if (n1.item(j).getNodeName().equals("task-id"))
					taskId = n1.item(j).getTextContent();
				if (n1.item(j).getNodeName().equals("created-at"))
					date = n1.item(j).getTextContent();
				if (n1.item(j).getNodeName().equals("result"))
					result = n1.item(j).getTextContent();
			}

			if (taskId == null)
				continue;

			try {
				url = SERVER + "/api/findtaskgroup.xml?id=" + URLEncoder.encode(taskId, "UTF8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			response = makeRequest(url);
			Document d = Utills.openXMLFromString(response);
			taskGroup = d.getElementsByTagName("name").item(0).getTextContent();

			try {
				url = SERVER + "/api/findtaskname.xml?id=" + URLEncoder.encode(taskId, "UTF8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			response = makeRequest(url);
			d = Utills.openXMLFromString(response);
			taskName = d.getElementsByTagName("name").item(0).getTextContent();

			String s[] = { date, taskGroup, taskName, result };
			rows.add(s);
		}

		String r[][];
		r = new String[rows.size()][];

		for (int i = 0; i < rows.size(); i++)
			r[i] = rows.get(i);

		return r;
	}

	public static void editPatient(String name, String pass, String nameNew, String passNew) {
		String url = null;
		try {
			url = SERVER + "/api/editpatient.xml?name=" + URLEncoder.encode(name, "UTF8") + "&pass="
					+ URLEncoder.encode(pass, "UTF8") + "&name_new=" + URLEncoder.encode(nameNew, "UTF8") + "&pass_new="
					+ URLEncoder.encode(passNew, "UTF8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		makeRequest(url);
	}

	public static boolean registerKey(String key, String userName) {
		String url = null;
		String hddSerial = Utills.getHDDSerialNumber();
		try {
			url = SERVER + "/api/registerkey.xml?key=" + URLEncoder.encode(key, "UTF8") + "&user_name="
					+ URLEncoder.encode(userName, "UTF8") + "&hddserial=" + URLEncoder.encode(hddSerial, "UTF8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String response = makeRequest(url);
		if (response.equals("1"))
			return true;
		else
			return false;
	}

	public static boolean checkKey(String key, String userName) {
		String url = null;
		String hddSerial = Utills.getHDDSerialNumber();
		try {
			url = SERVER + "/api/checkkey.xml?key=" + URLEncoder.encode(key, "UTF8") + "&user_name="
					+ URLEncoder.encode(userName, "UTF8") + "&hdd_serial=" + URLEncoder.encode(hddSerial, "UTF8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String response = makeRequest(url);

		if (response.equals("1"))
			return true;
		else
			return false;
	}

	public static int daysLeft(String key, String userName) {
		String url = null;
		String hddSerial = Utills.getHDDSerialNumber();
		try {
			url = SERVER + "/api/getdays.xml?key=" + URLEncoder.encode(key, "UTF8") + "&user_name="
					+ URLEncoder.encode(userName, "UTF8") + "&hdd_serial=" + URLEncoder.encode(hddSerial, "UTF8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String response = makeRequest(url);
		response = response.substring(0, response.indexOf('/'));

		return Integer.parseInt(response);
	}

	public static String getFrom(String key, String userName) {
		String url = null;
		String hddSerial = Utills.getHDDSerialNumber();
		try {
			url = SERVER + "/api/getfrom.xml?key=" + URLEncoder.encode(key, "UTF8") + "&user_name="
					+ URLEncoder.encode(userName, "UTF8") + "&hdd_serial=" + URLEncoder.encode(hddSerial, "UTF8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String response = makeRequest(url);

		SimpleDateFormat parserSDF = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date d = parserSDF.parse(response);
			parserSDF = new SimpleDateFormat("dd.MM.yyyy");
			response = parserSDF.format(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return response;
	}

	public static String getVersion(String date) {
		String url = null;
		try {
			url = SERVER + "/api/getversion.xml?date=" + URLEncoder.encode(date, "UTF8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String response = makeRequest(url);

		if (response.equals("0"))
			return null;

		Document d = Utills.openXMLFromString(response);
		String l = d.getElementsByTagName("location").item(0).getTextContent();
		return l;
	}

	public static void setSERVER(String sERVER) {
		SERVER = sERVER;
	}
}
