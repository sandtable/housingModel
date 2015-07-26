package development;

import sim.engine.SimState;

public class Model extends SimState {

	public Model(long seed, IModelNode...agents) {
		super(seed);
		root = new ModelRoot(this, agents);
	}
	
	public void start() {
		root.start(null);
	}

	static public ModelRoot root;
	private static final long serialVersionUID = 1714518191380607106L;
}
