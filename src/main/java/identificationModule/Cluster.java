package identificationModule;

import java.util.HashSet;
import java.util.Set;

public class Cluster {

	private Set<String> classes;
	private double metricCouplingValue;

	public Cluster() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Cluster(Set<String> classes, double metricCouplingValue) {
		super();
		this.classes = classes;
		this.metricCouplingValue = metricCouplingValue;
	}

	public Cluster(Cluster other) {
		super();
		this.classes = new HashSet();
		for (String csl : other.getClasses()) {
			this.classes.add(csl);
		}

		this.metricCouplingValue = other.getMetricCouplingValue();
	}

	public void addClasses(Set<String> classesToAdd) {
		for (String classToAdd : classesToAdd) {
			if (!this.getClasses().contains(classToAdd)) {
				this.classes.add(classToAdd);
			}
		}
	}

	public boolean hasSameClasses(Cluster other) {
		for (String clsName : this.classes) {
			for (String otherClassName : other.getClasses()) {
				if (clsName.equals(otherClassName)) {
					return true;
				}
			}
		}
		return false;
	}

	public void display() {
		for (String className : classes) {
			System.out.println("Classe : " + className);
		}
		System.out.println("valeur de la métrique de couplage de ce cluster : " + metricCouplingValue + "\n");
	}

	public void displayP() {
		for (String className : classes) {
			System.out.println("Classe : " + className);
		}
	}
	// Getters && Setters

	public Set<String> getClasses() {
		return classes;
	}

	public void setClasses(Set<String> classes) {
		this.classes = classes;
	}

	public double getMetricCouplingValue() {
		return metricCouplingValue;
	}

	public void setMetricCouplingValue(double metricCouplingValue) {
		this.metricCouplingValue = metricCouplingValue;
	}

}
