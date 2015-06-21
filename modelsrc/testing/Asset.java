package testing;

public class Asset<OWNER> {
	public Asset(OWNER iOwner) {
		owner = iOwner;
	}
	OWNER owner;
}
