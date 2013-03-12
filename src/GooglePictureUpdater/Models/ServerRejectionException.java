package GooglePictureUpdater.Models;

public class ServerRejectionException extends Exception {

	private String serverResponse;
	private int serverResponseCode;
	
	public ServerRejectionException(int serverResponseCode, String serverResponse, String message) {
		super(message);
		this.serverResponse = serverResponse;
		this.serverResponseCode = serverResponseCode;
	}

	public String getServerResponse() {
		return serverResponse;
	}

	public void setServerResponse(String serverResponse) {
		this.serverResponse = serverResponse;
	}

	public int getServerResponseCode() {
		return serverResponseCode;
	}

	public void setServerResponseCode(int serverResponseCode) {
		this.serverResponseCode = serverResponseCode;
	}
	
	
	
}
