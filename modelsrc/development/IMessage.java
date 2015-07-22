package development;

public interface IMessage {
	static public interface IReceiver {
		boolean receive(IMessage message);
	}
}
