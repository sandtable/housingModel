package development;

import sim.engine.SimState;

public class ModelBase extends SimState {

	public ModelBase(long seed, IModelNode...agents) {
		super(seed);
		root = new ModelRoot(this, agents);
	}
	
	public void start() {
		root.start(null);
	}
	
	public void stop() {
		kill();
	}

	public static ModelRoot root;
	private static final long serialVersionUID = 1714518191380607106L;
}
