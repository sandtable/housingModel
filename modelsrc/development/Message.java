package development;

import contracts.Contract;

public class Message {
	static public class Introspect implements IMessage {private Introspect() {}}
	static public class Die implements IBroadcastMessage {private Die() {}}
	static public class EndOfContract implements IMessage {
		public EndOfContract(Contract c) {contract = c;}
		Contract contract;
	}

	public static Die die = new Die();
	public static Introspect introspect = new Introspect();
}
