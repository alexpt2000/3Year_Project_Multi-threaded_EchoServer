package gmit;

import java.util.ArrayList;
import java.util.Hashtable;

public class ServicesBean {

	ClientBean clientBean = new ClientBean();

	private ArrayList<ClientBean> clientsBank = new ArrayList<ClientBean>();
	private Hashtable<String, ArrayList<BankBean>> clientsStatements = new Hashtable<String, ArrayList<BankBean>>();

	public ServicesBean() {

	}

	public ServicesBean(ClientBean clientBean, ArrayList<ClientBean> clientsBank,
			Hashtable<String, ArrayList<BankBean>> clientsStatements) {
		this.clientBean = clientBean;
		this.clientsBank = clientsBank;
		this.clientsStatements = clientsStatements;
	}

	/*
	 * Get and Sets
	 * */
	public ArrayList<ClientBean> getClientsBank() {
		return clientsBank;
	}

	public void setClientsBank(ArrayList<ClientBean> clientsBank) {
		this.clientsBank = clientsBank;
	}

	public Hashtable<String, ArrayList<BankBean>> getClientsStatements() {
		return clientsStatements;
	}

	public void setClientsStatements(Hashtable<String, ArrayList<BankBean>> clientsStatements) {
		this.clientsStatements = clientsStatements;
	}

	
	
	/*
	 * Add clients into a list
	 * */
	
	public void addClients(String name, String address, String banAC, String username, String password) {
		clientsBank.add(new ClientBean(name, address, banAC, username, password));
	}

	
	/*
	 * Add Statements into a list
	 * */
	
	public void addStatements(String acNumber, double trans, double bal) {
		
		if (clientsStatements.get(acNumber) == null){
			clientsStatements.put(acNumber, new ArrayList<BankBean>());
		}
		
		BankBean bank = new BankBean(trans, bal);
		clientsStatements.get(acNumber).add(bank);
		System.out.println(acNumber + " " + trans + " " + bal);
	}
}
