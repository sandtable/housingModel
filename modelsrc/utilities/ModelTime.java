package utilities;

public class ModelTime {
	public enum Units {
		DAY(1.0),
		DAYS(1.0),
		WEEK(7.0),
		WEEKS(7.0),
		MONTH(30.0),
		MONTHS(30.0),
		YEAR(360.0),
		YEARS(360.0);
		
		Units(double d) {
			multiplier = d;
		}
		double multiplier;
	}
	
	public ModelTime(double t) {
		time = t;
	}

	public ModelTime(ModelTime mt) {
		time = mt.raw();
	}

	public ModelTime(double t, Units units) {
		time = t*units.multiplier;
	}
	
	public ModelTime minus(ModelTime other) {
		return(new ModelTime(raw() - other.raw()));
	}

	public ModelTime plus(ModelTime other) {
		return(new ModelTime(raw() + other.raw()));
	}

	public double inDays() {return(time/Units.DAYS.multiplier);}
	public double inWeeks() {return(time/Units.WEEKS.multiplier);}
	public double inMonths() {return(time/Units.MONTHS.multiplier);}
	public double inYears() {return(time/Units.YEARS.multiplier);}
	public double raw() {return(time);} // as used by schedule
	
	static public ModelTime years(double t) {
		return(new ModelTime(t,Units.YEAR));
	}
	static public ModelTime months(double t) {
		return(new ModelTime(t,Units.MONTH));
	}
	static public ModelTime weeks(double t) {
		return(new ModelTime(t,Units.WEEK));
	}
	static public ModelTime days(double t) {
		return(new ModelTime(t,Units.DAY));
	}
	static public ModelTime year() {
		return(ModelTime.years(1.0));
	}
	static public ModelTime month() {
		return(ModelTime.months(1.0));
	}
	static public ModelTime week() {
		return(ModelTime.weeks(1.0));
	}
	static public ModelTime day() {
		return(ModelTime.days(1.0));
	}
	
	double time;
}
