package graphs;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.TypeDeclaration;

import metrics.CouplingMetric;
import utility.Utility;

public class CouplingGraph {

	Map<String, Map<String, Double>> couplings;
	CallGraph2 graph;
	CouplingMetric couplingMetric;

	public CouplingGraph(CallGraph2 graph, CouplingMetric couplingMetric) {
		super();
		this.graph = graph;
		this.couplingMetric = couplingMetric;
		couplings = new HashMap<>();

	}

	public void generateCouplingGraph() {
		Double couplingValue;
		String fullyQNclassNameA;
		String fullyQNclassNameB;
		for (TypeDeclaration cls : graph.getClasses()) {
			fullyQNclassNameA = Utility.getClassFullyQualifiedName(cls);
			for (TypeDeclaration cls2 : graph.getClasses()) {
				fullyQNclassNameB = Utility.getClassFullyQualifiedName(cls2);
				if (!fullyQNclassNameA.equals(fullyQNclassNameB)) {
					couplingValue = couplingMetric.getCouplingMetricBetweenAB(fullyQNclassNameA, fullyQNclassNameB);
					if (couplingValue > 0) {
						if (!couplings.containsKey(fullyQNclassNameA)) {
							couplings.put(fullyQNclassNameA, new HashMap<String, Double>());
						}
						couplings.get(fullyQNclassNameA).put(fullyQNclassNameB, couplingValue);
					}
				}
			}
		}
	}

	public void displayCouplings() {
		for (String classNameA : couplings.keySet()) {
			for (String classNameB : couplings.get(classNameA).keySet()) {
				System.out.println("Classe : " + classNameA + " et classe : " + classNameB + " valeur : "
						+ couplings.get(classNameA).get(classNameB));
			}
		}

	}

	public Map<String, Map<String, Double>> getCouplings() {
		return couplings;
	}

	public void setCouplings(Map<String, Map<String, Double>> couplings) {
		this.couplings = couplings;
	}

}
