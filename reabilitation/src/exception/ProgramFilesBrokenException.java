package exception;

import java.io.IOException;

public class ProgramFilesBrokenException extends IOException {
	private static final long serialVersionUID = -2664714729100902072L;
	
	public ProgramFilesBrokenException(IOException e) {
		super(e);
	}

	public ProgramFilesBrokenException() {
		super();
	}
}
