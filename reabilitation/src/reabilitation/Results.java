package reabilitation;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class Results {
	private String taskName;

	private TreeMap<Date, Integer> map;

	private int minValue = Integer.MAX_VALUE;
	private int maxValue = -1;

	Map.Entry<Date, Integer> maxEntry = null;
	Map.Entry<Date, Integer> minEntry = null;

	public Results(String taskName, int[] values, Date[] times) {
		super();
		this.taskName = taskName;
		map = new TreeMap<Date, Integer>();
		for (int i = 0; i < times.length; i++) {
			map.put(times[i], values[i]);
			if (values[i] > maxValue)
				maxValue = values[i];
			if (values[i] < minValue)
				minValue = values[i];
		}
	}

	public Results(TreeMap<Date, Integer> map, String taskName) {
		this.map = map;
		this.taskName = taskName;
		for (Map.Entry<Date, Integer> entry : map.entrySet()) {
			if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
				maxEntry = entry;
			}
			if (minEntry == null || entry.getValue().compareTo(minEntry.getValue()) < 0) {
				minEntry = entry;
			}
		}
		maxValue = maxEntry.getValue();
		minValue = minEntry.getValue();
	}

	public String getTaskName() {
		return taskName;
	}

	public TreeMap<Date, Integer> getMap() {
		return map;
	}

	public int getMinValue() {
		return minValue;
	}

	public int getMaxValue() {
		return maxValue;
	}
}
