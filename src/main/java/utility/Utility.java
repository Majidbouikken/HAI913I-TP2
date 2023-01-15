package utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import guru.nidi.graphviz.attribute.Color;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.parse.Parser;

public class Utility {

	// afficher le nom complet d'une classe
	public static String getClassFullyQualifiedName(TypeDeclaration typeDeclaration) {
		String name = typeDeclaration.getName().getIdentifier();

		if (typeDeclaration.getRoot().getClass() == CompilationUnit.class) {
			CompilationUnit root = (CompilationUnit) typeDeclaration.getRoot();

			if (root.getPackage() != null)
				name = root.getPackage().getName().getFullyQualifiedName() + "." + name;
		}

		return name;
	}

	// afficher le nom complet d'une méthode
	public static String getMethodFullyQualifiedName(TypeDeclaration cls, MethodDeclaration method) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getClassFullyQualifiedName(cls));
		buffer.append("::");
		buffer.append(method.getName());

		return buffer.toString();
	}

	/*
	 * Arondis une <code>value</code> selon le nombre de <code>places</code> après
	 * la virgule demandé.
	 */
	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(Double.toString(value));
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static void saveGraph(String dotPath, String graphAsDot) {
		try {
			FileWriter fw = new FileWriter(dotPath, false);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw);
			out.println(graphAsDot);
			out.close();
			bw.close();
			fw.close();
		} catch (IOException e) {
			System.out.println("Exception ecriture fichier");
			e.printStackTrace();
		}
	}

	public static String getGraphAsDot(Map<String, Map<String, Double>> couplings) {
		StringBuilder res = new StringBuilder("digraph G {\n");
		String coupling = "";
		for (String classNameA : couplings.keySet()) {
			for (String classNameB : couplings.get(classNameA).keySet()) {
				coupling = couplings.get(classNameA).get(classNameB) + " ";
				res.append('"').append(classNameA).append('"').append(" -> ").append('"').append(classNameB).append('"')
						.append(" [ label = \"").append(coupling).append("\"] ");
			}
		}
		res.append("\n}");
		return res.toString();
	}

	public static void saveGraphAsPNG(String imgPath, String graphAsDot) throws IOException {
		MutableGraph g = new Parser().read(graphAsDot);
		Graphviz.fromGraph(g).height(1920).render(Format.PNG).toFile(new File(imgPath));
		g.graphAttrs().add(Color.WHITE.gradient(Color.rgb("888888")).background().angle(90)).nodeAttrs()
				.add(Color.WHITE.fill()).nodes()
				.forEach(node -> node.add(Color.named(node.name().toString()), Style.lineWidth(4), Style.FILLED));

	}

	public static String getBidirectionalGraphAsDot(Map<String, Map<String, Double>> bidirectionalCouplings) {
		StringBuilder res = new StringBuilder("graph G {\n");
		String coupling = "";
		for (String classNameA : bidirectionalCouplings.keySet()) {
			for (String classNameB : bidirectionalCouplings.get(classNameA).keySet()) {
				coupling = bidirectionalCouplings.get(classNameA).get(classNameB) + " ";
				res.append('"').append(classNameA).append('"').append('-').append('-').append('"').append(classNameB)
						.append('"').append(" [ label = \"").append(coupling).append("\"] ");
			}
		}
		res.append("\n}");
		return res.toString();
	}
}
