package graphs;

import java.util.HashMap;
import java.util.Map;

import metrics.CouplingMetric;

public class BidirectionalCouplingGraph extends CouplingGraph {

	private Map<String, Map<String, Double>> bidirectionalCouplings;

	public BidirectionalCouplingGraph(CallGraph2 graph, CouplingMetric couplingMetric) {
		super(graph, couplingMetric);
		bidirectionalCouplings = new HashMap<>();
	}

	/**
	 * It gives for each couple of class A and B, a coupling metric
	 */
	public void generateBidirectionalCouplingGraph() {
		Double couplingValue;
		Double couplingValue2;
		Double sommeCoupling;
		Map<String, Double> tempMap;
		boolean couplingB2ANotTreated;
		generateCouplingGraph();
		for (String classA : couplings.keySet()) {
			tempMap = couplings.get(classA);
			if (tempMap != null) {
				for (String classB : tempMap.keySet()) {
					couplingB2ANotTreated = !bidirectionalCouplings.containsKey(classB)
							|| (bidirectionalCouplings.containsKey(classB)
									&& !bidirectionalCouplings.get(classB).containsKey(classA));
					if (couplingB2ANotTreated) {
						couplingValue = couplingMetric.getCouplingMetricBetweenAB(classA, classB);
						couplingValue2 = couplingMetric.getCouplingMetricBetweenAB(classB, classA);
						sommeCoupling = couplingValue + couplingValue2;
						if (!bidirectionalCouplings.containsKey(classA)) {
							bidirectionalCouplings.put(classA, new HashMap<String, Double>());
						}
						bidirectionalCouplings.get(classA).put(classB, sommeCoupling);
					}
				}
			}

		}
	}

	// Getters && Setters
	public Map<String, Map<String, Double>> getBidirectionalCouplings() {
		return bidirectionalCouplings;
	}

	public void setBidirectionalCouplings(Map<String, Map<String, Double>> bidirectionalCouplings) {
		this.bidirectionalCouplings = bidirectionalCouplings;
	}

}
