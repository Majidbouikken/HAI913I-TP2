package spoon;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtType;

public class SpoonCouplingGraph {

	private CtModel model;
	private SpoonCouplingMetric spoonCouplingMetric;
	private Map<String, Map<String, Double>> unidiractionalWeightedCouplingGraph = new HashMap<>();
	private Map<String, Map<String, Double>> bidiractionalWeightedCouplingGraph = new HashMap<>();

	public SpoonCouplingGraph(CtModel model) {
		super();
		this.model = model;
		this.spoonCouplingMetric = new SpoonCouplingMetric(model);
	}

	public void createUnidiractionalWeightedCouplingGraph() throws IOException {
		String classNameA, classNameB;
		Double couplingValue;
		for (CtType<?> type : model.getAllTypes()) {
			classNameA = type.getQualifiedName();
			for (CtType<?> type2 : model.getAllTypes()) {
				classNameB = type2.getQualifiedName();
				if (!classNameA.equals(classNameB)) {
					couplingValue = spoonCouplingMetric.getCouplingMetricBetweenAB(classNameA, classNameB);
					if (couplingValue > 0) {
						if (!unidiractionalWeightedCouplingGraph.containsKey(classNameA)) {
							unidiractionalWeightedCouplingGraph.put(classNameA, new HashMap<String, Double>());
						}
						unidiractionalWeightedCouplingGraph.get(classNameA).put(classNameB, couplingValue);
					}
				}

			}

		}
	}

	public void createBidirectionalWeightedCouplingGraph() throws IOException {
		String classNameA, classNameB;
		Double couplingValue, couplingValue2, sommeCouplingValue;
		boolean cond1, cond2;
		for (CtType<?> type : model.getAllTypes()) {
			classNameA = type.getQualifiedName();
			for (CtType<?> type2 : model.getAllTypes()) {
				classNameB = type2.getQualifiedName();
				if (!classNameA.equals(classNameB)) {
					if (isClassesNotTreated(classNameA, classNameB) && isClassesNotTreated(classNameB, classNameA)) {
						couplingValue = spoonCouplingMetric.getCouplingMetricBetweenAB(classNameA, classNameB);
						couplingValue2 = spoonCouplingMetric.getCouplingMetricBetweenAB(classNameB, classNameA);
						sommeCouplingValue = couplingValue + couplingValue2;
						if (sommeCouplingValue > 0) {
							if (!bidiractionalWeightedCouplingGraph.containsKey(classNameA)) {
								bidiractionalWeightedCouplingGraph.put(classNameA, new HashMap<String, Double>());
							}
							bidiractionalWeightedCouplingGraph.get(classNameA).put(classNameB, sommeCouplingValue);
						}
					}
				}

			}

		}
	}

	private boolean isClassesNotTreated(String classNameA, String classNameB) {
		if (!bidiractionalWeightedCouplingGraph.containsKey(classNameA)
				|| (bidiractionalWeightedCouplingGraph.containsKey(classNameA)
						&& !bidiractionalWeightedCouplingGraph.get(classNameA).containsKey(classNameB))) {
			return true;
		}
		return false;

	}
	// Getter && Setter

	public Map<String, Map<String, Double>> getUnidirectionalWeightedCouplingGraph() {
		return unidiractionalWeightedCouplingGraph;
	}

	public void setUnidiractionalWeightedCouplingGraph(
			Map<String, Map<String, Double>> unidiractionalWeightedCouplingGraph) {
		this.unidiractionalWeightedCouplingGraph = unidiractionalWeightedCouplingGraph;
	}

	public Map<String, Map<String, Double>> getBidirectionalWeightedCouplingGraph() {
		return bidiractionalWeightedCouplingGraph;
	}

	public void setBidiractionalWeightedCouplingGraph(
			Map<String, Map<String, Double>> bidiractionalWeightedCouplingGraph) {
		this.bidiractionalWeightedCouplingGraph = bidiractionalWeightedCouplingGraph;
	}

}
