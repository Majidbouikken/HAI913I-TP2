package spoon;

import java.io.IOException;

import spoon.reflect.CtModel;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.Query;
import spoon.reflect.visitor.filter.TypeFilter;
import utility.Utility;

public class SpoonCouplingMetric {
	private CtModel model;

	public SpoonCouplingMetric(CtModel model) {
		super();
		this.model = model;
	}


	// calcule du nombre total total d'appels

	public long getTotalNumberOfCalls() {
		long result = 0;
		for (CtType<?> type : model.getAllTypes()) {
			for (CtMethod<?> method : type.getAllMethods()) {
				for (CtInvocation<?> methodInvocation : Query.getElements(method,
						new TypeFilter<CtInvocation<?>>(CtInvocation.class))) {
					if (methodInvocation.getTarget().getType() != null) {
						if (!type.getQualifiedName().equals(
								methodInvocation.getTarget().getType().getTypeDeclaration().getQualifiedName())) {
							result++;
						}
					}
				}
			}

		}
		return result;

	}

	/**
	 * calcule du nombre d'appel de la classe A vers la classe B
	 */
	public long getNbRelationsBetweenAB(String classA, String classB) throws IOException {
		long result = 0;
		if (classA.equals(classB)) {
			return 0;
		}
		for (CtType<?> type : model.getAllTypes()) {
			if (classA.equals(type.getQualifiedName())) {
				for (CtMethod<?> method : type.getAllMethods()) {
					for (CtInvocation<?> methodInvocation : Query.getElements(method,
							new TypeFilter<CtInvocation<?>>(CtInvocation.class))) {
						if (methodInvocation.getTarget().getType() != null) {
							if (classB.equals(
									methodInvocation.getTarget().getType().getTypeDeclaration().getQualifiedName())) {
								result++;
							}
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * Le <code>result</code> à été arrondi a 3 décimal après la virugle.
	 */
	public double getCouplingMetricBetweenAB(String fullyQNclassNameA, String fullyQNclassNameB) throws IOException {
		long nbTotalRelations = this.getTotalNumberOfCalls();
		long nbRelationsAB = this.getNbRelationsBetweenAB(fullyQNclassNameA, fullyQNclassNameB);
		double result = (nbRelationsAB + 0.0) / (nbTotalRelations + 0.0);
		return Utility.round(result, 3);
	}

}
