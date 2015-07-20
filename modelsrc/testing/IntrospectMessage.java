package testing;

public class IntrospectMessage implements IMessage {
	private IntrospectMessage() {	
	}
	
	public static IntrospectMessage instance = new IntrospectMessage();
}
