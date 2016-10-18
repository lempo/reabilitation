package reabilitation;

public class TaskGroup {
	private String name;
	private String text;
	private String toolTipText;
	private String image;
	private String bigImage;
	private String rolloverImage;

	public TaskGroup(String name, String text, String image, String bigImage, String rolloverImage,
			String toolTipText) {
		super();
		this.name = name;
		this.text = text.replace("!linebreak!", "<br/>");
		this.image = image;
		this.bigImage = bigImage;
		this.rolloverImage = rolloverImage;
		this.toolTipText = toolTipText.replace("!linebreak!", "<br/>");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getBigImage() {
		return bigImage;
	}

	public void setBigImage(String bigImage) {
		this.bigImage = bigImage;
	}

	public String getRolloverImage() {
		return rolloverImage;
	}

	public void setRolloverImage(String rolloverImage) {
		this.rolloverImage = rolloverImage;
	}

	public String getToolTipText() {
		return toolTipText;
	}

	public void setToolTipText(String toolTipText) {
		this.toolTipText = toolTipText;
	}
}
