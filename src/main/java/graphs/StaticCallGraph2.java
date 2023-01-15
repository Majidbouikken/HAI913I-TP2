package graphs;

import java.io.IOException;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import utility.Utility;
import visitors.ClassDeclarationsCollector;
import visitors.MethodDeclarationsCollector;
import visitors.MethodInvocationsCollector;

public class StaticCallGraph2 extends CallGraph2 {

	/* CONSTRUCTOR */
	public StaticCallGraph2(String projectPath) {
		super(projectPath);
	}

	/* METHODS */

	/*
	 * + Ajout de chaque classes parsés <code>cl</code> a l'instance courante.
	 */
	// ajouter dans graph toutes les méthodes et leur invocations de toute les
	// classe
	// du compilation unit fournit en paramètre
	public static StaticCallGraph2 createCallGraph(String projectPath, CompilationUnit cUnit) {
		StaticCallGraph2 graph = new StaticCallGraph2(projectPath);
		ClassDeclarationsCollector classCollector = new ClassDeclarationsCollector();
		cUnit.accept(classCollector);

		for (TypeDeclaration cls : classCollector.getClasses()) {
			graph.addClass(cls);
			MethodDeclarationsCollector methodCollector = new MethodDeclarationsCollector();
			cls.accept(methodCollector);

			for (MethodDeclaration method : methodCollector.getMethods())
				graph.addMethodAndInvocations(cls, method);
		}

		return graph;
	}

	/*
	 * Ajout des classes de l'instance <code>partial</code> a l'instance courante
	 * <code>graph</code>.
	 */
	// récupération du graph complet du projet
	// cad toute les méthods et leurs invocation
	public static StaticCallGraph2 createCallGraph(String projectPath) throws IOException {
		StaticCallGraph2 graph = new StaticCallGraph2(projectPath);

		for (CompilationUnit cUnit : graph.parser.parseProject()) {
			StaticCallGraph2 partial = StaticCallGraph2.createCallGraph(projectPath, cUnit);
			graph.addClasses(partial.getClasses());
			graph.addMethods(partial.getMethods());
			graph.addInvocations(partial.getInvocations());
		}

		return graph;
	}

	private boolean addMethodAndInvocations(TypeDeclaration cls, MethodDeclaration method) {
		if (method.getBody() != null) {
			String methodName = Utility.getMethodFullyQualifiedName(cls, method);
			this.addMethod(method);
			MethodInvocationsCollector invocationCollector = new MethodInvocationsCollector();
			this.addInvocations(cls, method, methodName, invocationCollector);
		}

		return method.getBody() != null;
	}

	private void addInvocations(TypeDeclaration cls, MethodDeclaration method, String methodName,
			MethodInvocationsCollector invocationCollector) {
		method.accept(invocationCollector);
		for (MethodInvocation invocation : invocationCollector.getMethodInvocations()) {
			this.addInvocation(method, invocation);
		}
	}

}
