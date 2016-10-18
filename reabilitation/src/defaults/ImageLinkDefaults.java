package defaults;

import java.util.HashMap;
import java.util.Map;

import defaults.TextLinkDefaults.Key;

public class ImageLinkDefaults {
	private static ImageLinkDefaults instance;

	public static ImageLinkDefaults getInstance() {
		if (instance == null) {
			instance = new ImageLinkDefaults();
		}
		return instance;
	}

	public enum Key {
		BACKGROUND, 
		FIRST_SCREEN, 
		MAIN_MENU_EXIT, 
		MAIN_MENU_ABOUT, 
		MAIN_MENU_HELP, 
		MAIN_MENU_TASKS, 
		MAIN_MENU_EXIT_ROLLOVER, 
		MAIN_MENU_ABOUT_ROLLOVER, 
		MAIN_MENU_HELP_ROLLOVER, 
		MAIN_MENU_TASKS_ROLLOVER, 
		SMALL_MENU_ABOUT, 
		SMALL_MENU_BEGINING, 
		SMALL_MENU_END, 
		SMALL_MENU_RESULTS, 
		SMALL_MENU_ABOUT_ROLLOVER, 
		SMALL_MENU_BEGINING_ROLLOVER, 
		SMALL_MENU_END_ROLLOVER, 
		SMALL_MENU_RESULTS_ROLLOVER, 
		ARROW, 
		CIRCLE_ARROW, 
		START, 
		PAUSE, 
		BEGINING, 
		REPEAT, 
		ALL_RESULTS, 
		START_ROLLOVER, 
		PAUSE_ROLLOVER, 
		BEGINING_ROLLOVER, 
		REPEAT_ROLLOVER, 
		ALL_RESULTS_ROLLOVER, 
		COMPARISON,
		LISTEN,
		PAIRS,
		EDIT,
		DELETE,
		MENU,
		USER,
		ARROW_BUTTON,
		INCREASE,
		DECREASE_HORIZONTAL,
		DECREASE,
		INCREASE_HORIZONTAL,
		HEADER_ARROW,
		HEADER_ARROW_SORT,
		ARROW_RIGHT,
		MATRIX,
		CLOSE,
		RESTORE,
		HIDE,
		CLOSE_ROLLOVER,
		RESTORE_ROLLOVER,
		HIDE_ROLLOVER,
		RADIO,
		RADIO_SELECTED,
		REPEAT_TASK,
		REPEAT_TASK_ROLLOVER,
		LOGO,
		KOMPLIMED,
		COPYRIGHT;
	}

	private Map<Key, String> links;

	private ImageLinkDefaults() {
		links = new HashMap<Key, String>();

		links.put(Key.BACKGROUND, "resources/image/background.png");
		links.put(Key.FIRST_SCREEN, "resources/image/background1.png");
		links.put(Key.MAIN_MENU_EXIT, "resources/image/exit.png");
		links.put(Key.MAIN_MENU_ABOUT, "resources/image/about.png");
		links.put(Key.MAIN_MENU_HELP, "resources/image/help.png");
		links.put(Key.MAIN_MENU_TASKS, "resources/image/tasks.png");
		links.put(Key.MAIN_MENU_EXIT_ROLLOVER, "resources/image/exit_rollover.png");
		links.put(Key.MAIN_MENU_ABOUT_ROLLOVER, "resources/image/about_rollover.png");
		links.put(Key.MAIN_MENU_HELP_ROLLOVER, "resources/image/help_rollover.png");
		links.put(Key.MAIN_MENU_TASKS_ROLLOVER, "resources/image/tasks_rollover.png");
		links.put(Key.SMALL_MENU_ABOUT, "resources/image/small_menu_about.png");
		links.put(Key.SMALL_MENU_BEGINING, "resources/image/small_menu_begining.png");
		links.put(Key.SMALL_MENU_END, "resources/image/small_menu_end.png");
		links.put(Key.SMALL_MENU_RESULTS, "resources/image/small_menu_results.png");
		links.put(Key.SMALL_MENU_ABOUT_ROLLOVER, "resources/image/small_menu_about_rollover.png");
		links.put(Key.SMALL_MENU_BEGINING_ROLLOVER, "resources/image/small_menu_begining_rollover.png");
		links.put(Key.SMALL_MENU_END_ROLLOVER, "resources/image/small_menu_end_rollover.png");
		links.put(Key.SMALL_MENU_RESULTS_ROLLOVER, "resources/image/small_menu_results_rollover.png");
		links.put(Key.ARROW, "resources/image/arrow.png");
		links.put(Key.ARROW_RIGHT, "resources/image/arrow_right.png");
		links.put(Key.CIRCLE_ARROW, "resources/image/circleArrow.png");
		links.put(Key.START, "resources/image/start.png");
		links.put(Key.PAUSE, "resources/image/pause.png");
		links.put(Key.BEGINING, "resources/image/begining.png");
		links.put(Key.REPEAT, "resources/image/begining.png");
		links.put(Key.ALL_RESULTS, "resources/image/allresults.png");
		links.put(Key.START_ROLLOVER, "resources/image/start_rollover.png");
		links.put(Key.PAUSE_ROLLOVER, "resources/image/pause_rollover.png");
		links.put(Key.BEGINING_ROLLOVER, "resources/image/begining_rollover.png");
		links.put(Key.REPEAT_ROLLOVER, "resources/image/begining_rollover.png");
		links.put(Key.ALL_RESULTS_ROLLOVER, "resources/image/allresults_rollover.png");
		links.put(Key.COMPARISON, "resources/image/comparison/");
		links.put(Key.LISTEN, "resources/image/listen/");
		links.put(Key.PAIRS, "resources/image/pairs/");
		links.put(Key.MATRIX, "resources/image/matrix/");
		links.put(Key.EDIT, "resources/image/edit.png");
		links.put(Key.DELETE, "resources/image/delete.png");
		links.put(Key.MENU, "resources/image/menu");
		links.put(Key.USER, "resources/image/user.png");
		links.put(Key.ARROW_BUTTON, "resources/image/arrow_button.png");
		links.put(Key.INCREASE, "resources/image/increase.png");
		links.put(Key.DECREASE_HORIZONTAL, "resources/image/decrease_horizontal.png");
		links.put(Key.DECREASE, "resources/image/decrease.png");
		links.put(Key.INCREASE_HORIZONTAL, "resources/image/increase_horizontal.png");
		links.put(Key.HEADER_ARROW, "resources/image/header_arrow.png");
		links.put(Key.HEADER_ARROW_SORT, "resources/image/header_arrow_sort.png");
		links.put(Key.CLOSE, "resources/image/close.png");
		links.put(Key.RESTORE, "resources/image/restore.png");
		links.put(Key.HIDE, "resources/image/hide.png");
		links.put(Key.CLOSE_ROLLOVER, "resources/image/close_rollover.png");
		links.put(Key.RESTORE_ROLLOVER, "resources/image/restore_rollover.png");
		links.put(Key.HIDE_ROLLOVER, "resources/image/hide_rollover.png");
		links.put(Key.RADIO, "resources/image/radio.png");
		links.put(Key.RADIO_SELECTED, "resources/image/radio_selected.png");
		links.put(Key.REPEAT_TASK, "resources/image/repeat_task.png");
		links.put(Key.REPEAT_TASK_ROLLOVER, "resources/image/repeat_task_rollover.png");
		links.put(Key.LOGO, "resources/image/logo.png");
		links.put(Key.KOMPLIMED, "resources/image/komplimed.png");
		links.put(Key.COPYRIGHT, "resources/image/copyright.png");
	}

	public String getLink(Key key) {
		return links.get(key);
	}

	public void setLink(Key key, String link) {
		links.put(key, link);
	}
}
