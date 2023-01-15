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

	// Setters && Setters
	public String getClassNameA() {
		return classNameA;
	}

	public void setClassNameA(String classNameA) {
		this.classNameA = classNameA;
	}

	public String getClassNameB() {
		return classNameB;
	}

	public void setClassNameB(String classNameB) {
		this.classNameB = classNameB;
	}

	public double getCouplageMetricValue() {
		return couplageMetricValue;
	}

	public void setCouplageMetricValue(double couplageMetricValue) {
		this.couplageMetricValue = couplageMetricValue;
	}

}
