package defaults;

import java.util.HashMap;
import java.util.Map;

public class TextLinkDefaults {
	private static TextLinkDefaults instance;

	public static TextLinkDefaults getInstance() {
		if (instance == null) {
			instance = new TextLinkDefaults();
		}
		return instance;
	}

	public enum Key {
		FAQ, 
		ABOUT, 
		GROUPS, 
		WELCOME, 
		CATEGORIES,
		DICTIONARY,
		LISTEN,
		TRAVEL,
		WORDS,
		INTERFACE,
		FIGURES,
		VERSION,
		SERVER;
	}

	private Map<Key, String> links;

	private TextLinkDefaults() {
		links = new HashMap<Key, String>();

		links.put(Key.FAQ, "resources/text/faq.html");
		links.put(Key.ABOUT, "resources/text/about.html");
		links.put(Key.GROUPS, "resources/text/groups.xml");
		links.put(Key.WELCOME, "resources/text/welcome.html");
		links.put(Key.CATEGORIES, "resources/text/words/categories.xml");
		links.put(Key.DICTIONARY, "resources/text/dictionary/");
		links.put(Key.LISTEN, "resources/text/listen/listen.xml");
		links.put(Key.TRAVEL, "resources/text/travel/regions.xml");
		links.put(Key.WORDS, "resources/text/words/categories.xml");
		links.put(Key.INTERFACE, "resources/text/interface.xml");
		links.put(Key.FIGURES, "resources/text/figures/");
		links.put(Key.VERSION, "resources/text/version.xml");
		links.put(Key.SERVER, "resources/text/server.xml");
	}

	public String getLink(Key key) {
		return links.get(key);
	}

	public void setLink(Key key, String link) {
		links.put(key, link);
	}
}
