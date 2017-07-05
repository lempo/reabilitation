package exception;

import java.io.IOException;

public class DiskPermissionsException extends IOException {
	private static final long serialVersionUID = -3897262063199420652L;
	
	public DiskPermissionsException(IOException e) {
		super(e);
	}
}
