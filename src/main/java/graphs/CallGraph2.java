package graphs;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.LongStream;

import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import processors.ASTProcessor;

public class CallGraph2 extends ASTProcessor {

	/* ATTRIBUTES */
	/*
	 * Ajout d'un attribut <code>classes</code> stockant l'ensemble des classes
	 * parsés d'une instance.
	 */
	private Set<TypeDeclaration> classes = new HashSet<>();
	private Set<MethodDeclaration> methods = new HashSet<>();
	private Map<MethodDeclaration, Map<MethodInvocation, Integer>> invocations = new HashMap<>();

	/* CONSTRUCTOR */
	public CallGraph2(String projectPath) {
		super(projectPath);
	}

	/* GETTERS AND SETTERS */
	public Set<TypeDeclaration> getClasses() {
		return classes;
	}

	public Set<MethodDeclaration> getMethods() {
		return methods;
	}

	public long getNbMethods() {
		return methods.size();
	}

	public long getNbInvocations() {
		return invocations.keySet().stream().map(source -> invocations.get(source))
				.map(destination -> destination.values()).flatMap(Collection::stream)
				.flatMapToLong(value -> LongStream.of((long) value)).sum();
	}

	public Map<MethodDeclaration, Map<MethodInvocation, Integer>> getInvocations() {
		return invocations;
	}

	/*
	 * Ajout d'une classe <code>aClass</code>.
	 */
	public boolean addClass(TypeDeclaration aClass) {
		return classes.add(aClass);
	}

	/*
	 * Ajout d'un ensemble de classes <code>classes</code>.
	 */
	public boolean addClasses(Set<TypeDeclaration> classes) {
		return this.classes.addAll(classes);
	}

	public boolean addMethod(MethodDeclaration method) {
		return methods.add(method);
	}

	public boolean addMethods(Set<MethodDeclaration> methods) {
		return methods.addAll(methods);
	}

	public void addInvocation(MethodDeclaration source, MethodInvocation destination) {

		if (invocations.containsKey(source)) {

			if (invocations.get(source).containsKey(destination)) {
				int numberOfArrows = invocations.get(source).get(destination);
				invocations.get(source).put(destination, numberOfArrows + 1);
			}

			else {
				invocations.get(source).put(destination, 1);
			}
		}

		else {
			methods.add(source);
			invocations.put(source, new HashMap<MethodInvocation, Integer>());
			invocations.get(source).put(destination, 1);
		}
	}

	public void addInvocation(MethodDeclaration source, MethodInvocation destination, int occurrences) {
		methods.add(source);
		if (!invocations.containsKey(source))
			invocations.put(source, new HashMap<MethodInvocation, Integer>());
		invocations.get(source).put(destination, occurrences);
	}

	public void addInvocations(Map<MethodDeclaration, Map<MethodInvocation, Integer>> map) {
		for (MethodDeclaration source : map.keySet())
			for (MethodInvocation destination : map.get(source).keySet())
				this.addInvocation(source, destination, map.get(source).get(destination));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Static Call Graph");
		builder.append("\nMethods: " + methods.size() + ".");
		builder.append("\nInvocations: " + getNbInvocations() + ".");
		builder.append("\n");

		for (MethodDeclaration source : invocations.keySet()) {
			builder.append(source.getName().toString() + ":\n");

			for (MethodInvocation destination : invocations.get(source).keySet())
				builder.append("\t---> " + destination.getName().toString() + " ("
						+ invocations.get(source).get(destination) + " time(s))\n");
			builder.append("\n");
		}

		return builder.toString();
	}
}
