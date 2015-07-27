package development;

public class Message {
	static public class Introspect implements IMessage {private Introspect() {}}
	static public class Die implements IBroadcastMessage {private Die() {}}
	static public class EndOfContract implements IMessage {private EndOfContract() {}}

	public static Die die = new Die();
	public static Introspect introspect = new Introspect();
	public static EndOfContract endOfContract = new EndOfContract();
}
