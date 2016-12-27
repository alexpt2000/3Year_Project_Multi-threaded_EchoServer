package gmit;

public class ClientBean {

	private String clientName;
	private String clientAddress;
	private String clientACnumber;
	private String clientUserName;
	private String clientPassword;

	public ClientBean() {

	}

	public ClientBean(String clientName, String clientAddress, String clientACnumber, String clientUserName,
			String clientPassword) {
		this.clientName = clientName;
		this.clientAddress = clientAddress;
		this.clientACnumber = clientACnumber;
		this.clientUserName = clientUserName;
		this.clientPassword = clientPassword;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getClientACnumber() {
		return clientACnumber;
	}

	public void setClientACnumber(String clientACnumber) {
		this.clientACnumber = clientACnumber;
	}

	public String getClientUserName() {
		return clientUserName;
	}

	public void setClientUserName(String clientUserName) {
		this.clientUserName = clientUserName;
	}

	public String getClientPassword() {
		return clientPassword;
	}

	public void setClientPassword(String clientPassword) {
		this.clientPassword = clientPassword;
	}

}
