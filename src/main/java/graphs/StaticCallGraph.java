package graphs;

import java.io.IOException;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import utility.Utility;
import visitors.ClassDeclarationsCollector;
import visitors.MethodDeclarationsCollector;
import visitors.MethodInvocationsCollector;

public class StaticCallGraph extends CallGraph {

	/* CONSTRUCTOR */
	private StaticCallGraph(String projectPath) {
		super(projectPath);
	}

	/* METHODS */

	// ajouter dans graph toutes les méthodes et leur invocations de toute les
	// classe
	// du compilation unit fournit en paramètre
	public static StaticCallGraph createCallGraph(String projectPath, CompilationUnit cUnit) {
		StaticCallGraph graph = new StaticCallGraph(projectPath);
		ClassDeclarationsCollector classCollector = new ClassDeclarationsCollector();
		cUnit.accept(classCollector);

		for (TypeDeclaration cls : classCollector.getClasses()) {
			MethodDeclarationsCollector methodCollector = new MethodDeclarationsCollector();
			cls.accept(methodCollector);

			for (MethodDeclaration method : methodCollector.getMethods())
				graph.addMethodAndInvocations(cls, method);
		}

		return graph;
	}

	// récupération du graph complet du projet
	// cad toutes les méthodes et leurs invocations
	public static StaticCallGraph createCallGraph(String projectPath) throws IOException {
		StaticCallGraph graph = new StaticCallGraph(projectPath);

		for (CompilationUnit cUnit : graph.parser.parseProject()) {
			StaticCallGraph partial = StaticCallGraph.createCallGraph(projectPath, cUnit);
			graph.addMethods(partial.getMethods());
			graph.addInvocations(partial.getInvocations());
		}

		return graph;
	}

	private boolean addMethodAndInvocations(TypeDeclaration cls, MethodDeclaration method) {
		if (method.getBody() != null) {
			String methodName = Utility.getMethodFullyQualifiedName(cls, method);
			this.addMethod(methodName);
			MethodInvocationsCollector invocationCollector = new MethodInvocationsCollector();
			this.addInvocations(cls, method, methodName, invocationCollector);
			this.addSuperInvocations(methodName, invocationCollector);
		}

		return method.getBody() != null;
	}

	private void addInvocations(TypeDeclaration cls, MethodDeclaration method, String methodName,
			MethodInvocationsCollector invocationCollector) {
		method.accept(invocationCollector);
		// System.out.println("type methode declaration = " + t.getName().toString());
		for (MethodInvocation invocation : invocationCollector.getMethodInvocations()) {
			String invocationName = getMethodInvocationName(cls, invocation);
			this.addMethod(invocationName);
			this.addInvocation(methodName, invocationName);
		}
	}

	private String getMethodInvocationName(TypeDeclaration cls, MethodInvocation invocation) {
		Expression expr = invocation.getExpression();

		String invocationName = "";
		if (expr != null) {
			ITypeBinding type = expr.resolveTypeBinding();

			if (type != null)
				invocationName = type.getQualifiedName() + "::" + invocation.getName().toString();
			else
				invocationName = expr + "::" + invocation.getName().toString();

		}

		else
			invocationName = Utility.getClassFullyQualifiedName(cls) + "::" + invocation.getName().toString();

		// System.out.println("type methode invocation = " + invocationName);
		return invocationName;
	}

	private void addSuperInvocations(String methodName, MethodInvocationsCollector invocationCollector) {
		for (SuperMethodInvocation superInvocation : invocationCollector.getSuperMethodInvocations()) {
			String superInvocationName = superInvocation.getName().getFullyQualifiedName();
			this.addMethod(superInvocationName);
			this.addInvocation(methodName, superInvocationName);
		}
	}
}
