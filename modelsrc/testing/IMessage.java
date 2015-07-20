package testing;

public interface IMessage {
	static public interface IReceiver {
		boolean receive(IMessage message);
	}
}
