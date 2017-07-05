package exception;

import java.io.IOException;

public class ServerConnectionException extends IOException {
	private static final long serialVersionUID = -5786095424591408014L;

	public ServerConnectionException(IOException e) {
		super(e);
	}
}
