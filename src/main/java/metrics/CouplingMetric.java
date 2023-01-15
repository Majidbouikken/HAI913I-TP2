package metrics;

import java.util.Map;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import graphs.CallGraph2;
import utility.Utility;

public class CouplingMetric {

	private CallGraph2 graph;

	public CouplingMetric(CallGraph2 graph) {
		super();
		this.graph = graph;
	}

	public long getTotalNbRelations() {
		long total = 0;
		Map<MethodInvocation, Integer> mapInvocations;
		String classNameM2 = "";
		IMethodBinding binding;
		TypeDeclaration typeM1;
		ITypeBinding type;
		String classNameM1;
		for (MethodDeclaration m1 : graph.getInvocations().keySet()) {
			mapInvocations = graph.getInvocations().get(m1);
			for (MethodInvocation methodInvocation : mapInvocations.keySet()) {
				binding = methodInvocation.resolveMethodBinding();
				if (binding != null) {
					type = binding.getDeclaringClass();
					if (type != null) {
						classNameM2 = type.getQualifiedName();
						typeM1 = (TypeDeclaration) m1.getParent();
						classNameM1 = Utility.getClassFullyQualifiedName(typeM1);
						if (!classNameM2.equals(classNameM1)) {
							total += mapInvocations.get(methodInvocation);
						}
					}
				}
			}
		}
		return total;
	}

	public long getNbRelationsBetweenAB(String fullyQNclassNameA, String fullyQNclassNameB) {
		long total = 0;
		Map<MethodInvocation, Integer> mapInvocations;
		String classNameM2 = "";
		for (MethodDeclaration m1 : graph.getInvocations().keySet()) {
			TypeDeclaration t = (TypeDeclaration) m1.getParent();
			if (Utility.getClassFullyQualifiedName(t).toString().equals(fullyQNclassNameA)) {
				mapInvocations = graph.getInvocations().get(m1);
				for (MethodInvocation m2 : mapInvocations.keySet()) {
					Expression expression = m2.getExpression();
					if (expression != null) {
						ITypeBinding typeBinding = expression.resolveTypeBinding();
						if (typeBinding != null) {
							classNameM2 = typeBinding.getQualifiedName();
						}
					}
					IMethodBinding binding = m2.resolveMethodBinding();
					if (binding != null) {
						ITypeBinding type = binding.getDeclaringClass();
						if (type != null) {
							classNameM2 = type.getQualifiedName();
						}
					}
					if (classNameM2.equals(fullyQNclassNameB)) {
						total++;
					}
				}
			}
		}
		return total;
	}

	/*
	 * Le <code>result</code> à été arrondi a 3 décimal après la virugle.
	 */
	public double getCouplingMetricBetweenAB(String fullyQNclassNameA, String fullyQNclassNameB) {
		long nbTotalRelations = this.getTotalNbRelations();
		long nbRelationsAB = this.getNbRelationsBetweenAB(fullyQNclassNameA, fullyQNclassNameB);
		double result = (nbRelationsAB + 0.0) / (nbTotalRelations + 0.0);
		return Utility.round(result, 3);
	}
}
