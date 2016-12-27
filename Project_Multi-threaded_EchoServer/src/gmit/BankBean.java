package gmit;

public class BankBean {
	
	double transactions = 0;
	double balance = 0;
	
	public BankBean() {

	}

	public BankBean(double transactions, double balance) {
		this.transactions = transactions;
		this.balance = balance;
	}

	public double getTransactions() {
		return transactions;
	}

	public void setTransactions(double transactions) {
		this.transactions = transactions;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	


}


