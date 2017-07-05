package exception;

import java.io.IOException;

public class HddSerialScriptException extends IOException {
	private static final long serialVersionUID = 5861667835683738374L;

	public HddSerialScriptException(IOException e) {
		super(e);
	}
}
