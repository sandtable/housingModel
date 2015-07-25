package development;

import ec.util.MersenneTwisterFast;
import sim.engine.Schedule;
import sim.engine.SimState;
import utilities.ModelTime;

public class ModelRoot extends ModelNode {
	public Schedule	schedule;
	public MersenneTwisterFast random;
	
	public ModelRoot(SimState parent, IModelNode...agents) {
		super(agents);
		schedule = parent.schedule;
		random = parent.random;
	}
	
	@Override
	public <T extends IModelNode> T find(Class<T> type) {
		return(get(type));
	}
	
	public ModelTime timeNow() {
		double scheduleTime = schedule.getTime();
		if(scheduleTime < 0.0) scheduleTime = 0.0;
		return(new ModelTime(scheduleTime,ModelTime.Units.RAW));
	}

}
