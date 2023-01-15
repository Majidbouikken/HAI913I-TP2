import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.Scanner;

import graphs.BidirectionalCouplingGraph;
import graphs.CallGraph;
import graphs.CallGraph2;
import graphs.CouplingGraph;
import graphs.StaticCallGraph;
import graphs.StaticCallGraph2;
import identificationModule.Cluster;
import identificationModule.CoupleOfClasses;
import identificationModule.HierarchicalClustering;
import metrics.CouplingMetric;
import processors.ASTProcessor;
import spoon.HierarchicalClusteringSpoon;
import spoon.SpoonCouplingGraph;
import spoon.SpoonCouplingMetric;
import spoon.SpoonParser;
import utility.Utility;

/*
 * TP2 : Compréhension	des	programmes
 * Module : HAI913I - Évolution et restructuration des logiciels
 * Option : Génie Logiciel (M2)
 *
 * Nom : Bouikken Bahi Amar
 * Prénom : Abdelmadjid
 * Email : bouikkenmajid@gmail.com
 *
 * Encadrant: M. Abdelhak-Djamel Seriai
 */
public class MainApp {
    static String path;
    public final static String indentationFormat = "\s|\s-\t";

    public static void main(String[] args) throws FileNotFoundException, IOException {
        if (args.length > 0) {
            MainApp.path = args[0];
            BufferedReader inputReader;
            CallGraph callGraph = null;
            try {
                inputReader = new BufferedReader(new InputStreamReader(System.in));
                if (args.length < 1) {
                }
                String userInput = "";
                do {
                    MainApp.Menu();
                } while (!userInput.equals("0"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Veuillez insérer le chemin du projet OO à analyser en argument");
        }
    }

    protected static void Menu() throws IOException {
        Scanner sc = new Scanner(System.in);
        int choice = 0;
        ASTProcessor processor = null;
        CallGraph callGraph = (CallGraph) processor;
        CallGraph2 callGraph2 = (CallGraph2) processor;
        CouplingMetric couplingMetric;
        CouplingGraph couplingGraph;
        BidirectionalCouplingGraph bidirectionalCouplingGraph;

        LinkedList<Cluster> clusters;
        LinkedList<CoupleOfClasses> couplesOfClasses;
        LinkedList<Cluster> partitions;
        SpoonParser spoonParser;
        SpoonCouplingGraph spoonCouplingGraph;
        HierarchicalClusteringSpoon hierarchicalClusteringSpoon;
        long start;
        long end;
        while (true) {
            System.out.println(
                    "******** TP2 : Compréhension des programmes *******");
            System.out.println("1. Générer un graphe d'appel statique");
            System.out.println("Exercice 1");
            System.out.println("2. Mesurer la métrique de couplage entre deux classes");
            System.out.println("3. Générer le graphe de couplage pondéré entre les classes de l'application");
            System.out.println("Exercice 2");
            System.out.println("4. Regrouper les classes de manière hiérarchique");
            System.out.println("5. Afficher les groupes de classes liées (partitions)");
            System.out.println("Exercice 3 : utilisation de Spoon");
            System.out.println("6. Mesurer la métrique de couplage entre deux classes avec Spoon");
            System.out.println("7. Générer un graphe de couplage pondéré entre les classes de l'application avec Spoon");
            System.out.println("0. Quitter le programme");
            System.out.println("");
            System.out.print("Veuillez introduire votre choix : ");
            choice = sc.nextInt();
            switch (choice) {
                case 1 -> {
                    callGraph = StaticCallGraph.createCallGraph(MainApp.path);
                    callGraph.log();
                }
                case 2 -> {
                    callGraph2 = StaticCallGraph2.createCallGraph(MainApp.path);
                    System.out.print("Insérez le nom de la première classe : ");
                    String classNameA = sc.next();
                    System.out.print("Insérez le nom de la deuxième classe : ");
                    String classNameB = sc.next();
                    couplingMetric = new CouplingMetric(callGraph2);
                    System.out.println("La métrique de couplage entre " + classNameA + " et " + classNameB + " de "
                            + (couplingMetric.getCouplingMetricBetweenAB(classNameA, classNameB)
                            + couplingMetric.getCouplingMetricBetweenAB(classNameB, classNameA)));
                }
                case 3 -> {
                    start = System.currentTimeMillis();
                    callGraph2 = StaticCallGraph2.createCallGraph(MainApp.path);
                    end = System.currentTimeMillis();
                    System.out
                            .println("Temps d'exécution de la generation du model par ASTParser est de " + (end - start) + "Ms");
                    start = System.currentTimeMillis();
                    couplingMetric = new CouplingMetric(callGraph2);
                    couplingGraph = new CouplingGraph(callGraph2, couplingMetric);
                    couplingGraph.generateCouplingGraph();
                    Utility.saveGraph(MainApp.path + "graphe-de-couplage-unidirectional.dot",
                            Utility.getGraphAsDot(couplingGraph.getCouplings()));
                    Utility.saveGraphAsPNG(MainApp.path + "graphe-de-couplage-bidirectional.png",
                            Utility.getGraphAsDot(couplingGraph.getCouplings()));
                    end = System.currentTimeMillis();
                    System.out.println(
                            "Temps d'exécution de génération du graphe de couplage unidirectionnel avec ASTParser : "
                                    + (end - start) + " Ms");
                    start = System.currentTimeMillis();
                    bidirectionalCouplingGraph = new BidirectionalCouplingGraph(callGraph2, couplingMetric);
                    bidirectionalCouplingGraph.generateBidirectionalCouplingGraph();
                    Utility.saveGraph(MainApp.path + "graph-de-couplage-bidirectionel.dot",
                            Utility.getBidirectionalGraphAsDot(bidirectionalCouplingGraph.getBidirectionalCouplings()));
                    Utility.saveGraphAsPNG(MainApp.path + "graph-de-couplage-bidirectionel.png",
                            Utility.getBidirectionalGraphAsDot(bidirectionalCouplingGraph.getBidirectionalCouplings()));
                    end = System.currentTimeMillis();
                    System.out
                            .println("Temps d'exécution de génération du graphe de couplage bidirectionnel avec ASTParser : "
                                    + (end - start) + " Ms");
                }
                case 4 -> {
                    start = System.currentTimeMillis();
                    callGraph2 = StaticCallGraph2.createCallGraph(MainApp.path);
                    end = System.currentTimeMillis();
                    System.out
                            .println("Temps d'exécution de la generation du model par ASTParser : " + (end - start) + " Ms");
                    start = System.currentTimeMillis();
                    couplingMetric = new CouplingMetric(callGraph2);
                    bidirectionalCouplingGraph = new BidirectionalCouplingGraph(callGraph2, couplingMetric);
                    bidirectionalCouplingGraph.generateBidirectionalCouplingGraph();
                    end = System.currentTimeMillis();
                    System.out
                            .println("Temps d'exécution de génération du graphe de couplage bidirectionnel avec ASTParser : "
                                    + (end - start) + " Ms");
                    start = System.currentTimeMillis();
                    HierarchicalClustering hierarchicalClustering = new HierarchicalClustering();
                    clusters = hierarchicalClustering.createClustersInitialized(callGraph2);
                    couplesOfClasses = hierarchicalClustering.createListOfClassesCouple(bidirectionalCouplingGraph);
                    hierarchicalClustering.createHierarchicalClustering(clusters, couplesOfClasses);
                    System.out.println("Liste final des Clusters :");
                    hierarchicalClustering.displayHierarchicalClustering(clusters);
                    end = System.currentTimeMillis();
                    System.out.println("Temps d'exécution de calcul des clusters avec ASTParser : " + (end - start) + " Ms");
                }
                case 5 -> {
                    start = System.currentTimeMillis();
                    callGraph2 = StaticCallGraph2.createCallGraph(MainApp.path);
                    end = System.currentTimeMillis();
                    System.out
                            .println("Temps d'exécution de la generation du model par ASTParser : " + (end - start) + " Ms");
                    start = System.currentTimeMillis();
                    couplingMetric = new CouplingMetric(callGraph2);
                    couplingGraph = new CouplingGraph(callGraph2, couplingMetric);
                    bidirectionalCouplingGraph = new BidirectionalCouplingGraph(callGraph2, couplingMetric);
                    bidirectionalCouplingGraph.generateBidirectionalCouplingGraph();
                    end = System.currentTimeMillis();
                    System.out
                            .println("Temps d'exécution de génération du graphe de couplage bidirectionnel avec ASTParser : "
                                    + (end - start) + " Ms");
                    start = System.currentTimeMillis();
                    HierarchicalClustering hierarchicalClustering = new HierarchicalClustering();
                    clusters = hierarchicalClustering.createClustersInitialized(callGraph2);
                    couplesOfClasses = hierarchicalClustering.createListOfClassesCouple(bidirectionalCouplingGraph);
                    partitions = hierarchicalClustering.createPartitions(clusters, couplesOfClasses);
                    System.out.println("Liste des Partitions :");
                    hierarchicalClustering.displayAllPartitions(partitions);
                    end = System.currentTimeMillis();
                    System.out
                            .println("Temps d'exécution de calcul des partitions avec ASTParser : " + (end - start) + " Ms");
                }
                case 6 -> {
                    start = System.currentTimeMillis();
                    spoonParser = new SpoonParser(MainApp.path);
                    System.out.print("Insérez le nom de la première classe : ");
                    String classNameA = sc.next();
                    System.out.print("Insérez le nom de la deuxième classe : ");
                    String classNameB = sc.next();
                    SpoonCouplingMetric spoonCouplingMetric = new SpoonCouplingMetric(spoonParser.getModel());
                    end = System.currentTimeMillis();
                    System.out.println("Temps d'exécution de la generation du model par spoon est de " + (end - start) + " Ms");
                    start = System.currentTimeMillis();
                    System.out.println("la métrique de couplage entre " + classNameA + " et " + classNameB + " est de "
                            + (spoonCouplingMetric.getCouplingMetricBetweenAB(classNameA, classNameB)
                            + spoonCouplingMetric.getCouplingMetricBetweenAB(classNameB, classNameA)));
                    end = System.currentTimeMillis();
                    System.out.println("Temps d'exécution du calcul de la métrique avec spoon est de " + (end - start) + "Ms");

                }
                case 7 -> {
                    start = System.currentTimeMillis();
                    spoonParser = new SpoonParser(MainApp.path);
                    spoonCouplingGraph = new SpoonCouplingGraph(spoonParser.getModel());
                    end = System.currentTimeMillis();
                    System.out.println("Temps d'exécution de la generation du model par spoon est de " + (end - start) + "Ms");
                    start = System.currentTimeMillis();
                    spoonCouplingGraph.createUnidiractionalWeightedCouplingGraph();
                    Utility.saveGraph(MainApp.path + "graph-de-couplage-unidirectionel-spoon.dot",
                            Utility.getGraphAsDot(spoonCouplingGraph.getUnidirectionalWeightedCouplingGraph()));
                    Utility.saveGraphAsPNG(MainApp.path + "graph-de-couplage-unidirectionel-spoon.png",
                            Utility.getGraphAsDot(spoonCouplingGraph.getUnidirectionalWeightedCouplingGraph()));
                    end = System.currentTimeMillis();
                    System.out.println("Temps d'exécution de la generation du graph unidirectionnel avec spoon : "
                            + (end - start) + " Ms");

                    start = System.currentTimeMillis();
                    spoonCouplingGraph.createBidirectionalWeightedCouplingGraph();
                    Utility.saveGraph(MainApp.path + "graph-de-couplage-bidirectionel-spoon.dot",
                            Utility.getBidirectionalGraphAsDot(spoonCouplingGraph.getBidirectionalWeightedCouplingGraph()));
                    Utility.saveGraphAsPNG(MainApp.path + "graph-de-couplage-bidirectionel-spoon.png",
                            Utility.getBidirectionalGraphAsDot(spoonCouplingGraph.getBidirectionalWeightedCouplingGraph()));
                    end = System.currentTimeMillis();
                    System.out.println("Temps d'exécution de la generation du graph bidirectionnel avec spoon : "
                            + (end - start) + "Ms");
                }
                case 0 -> {
                    System.out.println("Au revoir ! ");
                    return;
                }
                default -> {
                    System.err.println("Not Recognized!");
                    return;
                }
            }
        }
    }
}
