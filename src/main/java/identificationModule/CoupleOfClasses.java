package identificationModule;

public class CoupleOfClasses {

	private String classNameA;
	private String classNameB;
	private double couplageMetricValue;

	public CoupleOfClasses() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CoupleOfClasses(String classNameA, String classNameB, double couplageMetricValue) {
		super();
		this.classNameA = classNameA;
		this.classNameB = classNameB;
		this.couplageMetricValue = couplageMetricValue;
	}

	public String getClassNameA() {
		return classNameA;
	}

	public String getClassNameB() {
		return classNameB;
	}

	public double getCouplageMetricValue() {
		return couplageMetricValue;
	}
}
