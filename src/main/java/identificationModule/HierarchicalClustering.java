package identificationModule;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.eclipse.jdt.core.dom.TypeDeclaration;

import graphs.BidirectionalCouplingGraph;
import graphs.CallGraph2;
import utility.Utility;

public class HierarchicalClustering {

    public HierarchicalClustering() {
        super();
        // TODO Auto-generated constructor stub
    }

    public LinkedList<Cluster> createClustersInitialized(CallGraph2 graph) {
        Set<TypeDeclaration> classes = graph.getClasses();
        LinkedList<Cluster> clusters = new LinkedList<Cluster>();
        Cluster newCluster;
        for (TypeDeclaration t : classes) {

            if (!isClassExistOnCluster(clusters, Utility.getClassFullyQualifiedName(t))) {
                newCluster = createClusterInitialized(Utility.getClassFullyQualifiedName(t));
                clusters.add(newCluster);
            }
        }

        return clusters;
    }

    private boolean isClassExistOnCluster(LinkedList<Cluster> clusters, String className) {
        for (Cluster cluster : clusters) {
            if (cluster.getClasses().contains(className)) {
                return true;
            }
        }
        return false;
    }

    private Cluster createClusterInitialized(String className) {
        Set<String> classes = new HashSet<String>();
        classes.add(className);
        Cluster cluster = new Cluster(classes, 0);
        return cluster;
    }

    public LinkedList<CoupleOfClasses> createListOfClassesCouple(BidirectionalCouplingGraph bidirectionalCouplingGraph) {

        LinkedList<CoupleOfClasses> coupledClassesList = new LinkedList<CoupleOfClasses>();
        CoupleOfClasses newCoupleOfClasses;
        Map<String, Double> tempMap;
        for (String classA : bidirectionalCouplingGraph.getBidirectionalCouplings().keySet()) {
            tempMap = bidirectionalCouplingGraph.getBidirectionalCouplings().get(classA);
            for (String classB : tempMap.keySet()) {
                newCoupleOfClasses = new CoupleOfClasses(classA, classB, tempMap.get(classB));
                coupledClassesList.add(newCoupleOfClasses);
            }
        }
        return coupledClassesList;
    }

    public void createHierarchicalClustering(LinkedList<Cluster> clusters, LinkedList<CoupleOfClasses> couplesOfClasses) {
        Cluster clusterA, clusterB, partA, partB, tempCluster;
        double greaterCouplingMetricValue, tempCouplingMetricValue;
        int indexBeginPartA, indexBeginPartB;
        Set<String> tempClasses;
        // Affichage
        System.out.println(" Valeur des cluster lors de l'initialisation : ");
        clusters.forEach(cluster -> cluster.display());
        System.out.println("\nCréation de la hiérarchie des clusters :");

        while (clusters.size() > 1) {
            indexBeginPartA = 0;
            indexBeginPartB = 0;
            greaterCouplingMetricValue = 0;

            // identification des clusters proches (ayant un fort degré de couplage)
            for (int i = 0; i < clusters.size(); i++) {
                clusterA = clusters.get(i);
                for (int j = 0; j < clusters.size(); j++) {
                    clusterB = clusters.get(j);
                    if (i != j) {
                        tempCouplingMetricValue = getScoreBetweenClusters(clusterA, clusterB, couplesOfClasses);
                        if (tempCouplingMetricValue > greaterCouplingMetricValue) {
                            greaterCouplingMetricValue = tempCouplingMetricValue;
                            indexBeginPartA = i;
                            indexBeginPartB = j;
                        }
                    }
                }
            }
            if (greaterCouplingMetricValue > 0) {
                partA = clusters.get(indexBeginPartA);
                partB = clusters.get(indexBeginPartB);
                tempClasses = new HashSet<String>(partA.getClasses());
                tempCluster = new Cluster(tempClasses, greaterCouplingMetricValue);
                tempCluster.addClasses(partB.getClasses());
                clusters.add(tempCluster);
                clusters.remove(partA);
                clusters.remove(partB);
                // Affichage
                System.out.println("Fusion entre clusters");
                System.out.println("Partie A De la fusion : ");
                partA.getClasses().forEach(classe -> System.out.println("Classe : " + classe.toString()));
                System.out.println("Parie B de la fusion");
                partB.getClasses().forEach(classe -> System.out.println("Classe : " + classe.toString()));
                System.out.println("valeur du couplage : " + greaterCouplingMetricValue);
                System.out.println("Liste des cluster de cette étape : ");
                int i = 0;
                clusters.forEach(cluster -> {
                    System.out.println("Cluster : ");
                    cluster.display();
                });
            } else {
                break;
            }
        }

    }

    private double getScoreBetweenClusters(Cluster clusterA, Cluster clusterB, LinkedList<CoupleOfClasses> couplesOfClasses) {
        double couplingValue = 0;
        for (String classOfClusterA : clusterA.getClasses()) {
            for (String classOfClusterB : clusterB.getClasses()) {
                if (!classOfClusterA.equals(classOfClusterB)) {
                    for (CoupleOfClasses couple : couplesOfClasses) {
                        if (couple.getClassNameA().equals(classOfClusterA)
                                && couple.getClassNameB().equals(classOfClusterB)) {
                            couplingValue += couple.getCouplageMetricValue();
                        }
                    }
                }
            }
        }
        return couplingValue;
    }

    public void displayHierarchicalClustering(LinkedList<Cluster> clusters) {
        System.out.println("Regroupement hiérarchique des clusters :");
        int i = 0;
        for (Cluster cluster : clusters) {
            i++;
            System.out.println("Cluster " + i);
            cluster.display();
        }
    }

    // Exercice 2, Question 2
    public Stack<Cluster> createHierarchicalClusteringForPartition(LinkedList<Cluster> clusters, LinkedList<CoupleOfClasses> couplesOfClasses) {
        Stack<Cluster> hierarchicalStackOfClusters = new Stack<Cluster>();
        Cluster clusterA, clusterB, partA, partB, tempCluster;
        double greaterCouplingMetricValue, tempCouplingMetricValue;
        int indexBeginPartA, indexBeginPartB;
        Set<String> tempClasses;
        System.out.println("Valeur des cluster lors de l'initialisation : ");
        clusters.forEach(Cluster::display);
        System.out.println("Création de la hiérarchie des clusters :");
        while (clusters.size() > 1) {
            indexBeginPartA = 0;
            indexBeginPartB = 0;
            greaterCouplingMetricValue = 0;
            for (int i = 0; i < clusters.size(); i++) {
                clusterA = clusters.get(i);
                for (int j = 0; j < clusters.size(); j++) {
                    clusterB = clusters.get(j);
                    if (i != j) {
                        tempCouplingMetricValue = getScoreBetweenClusters(clusterA, clusterB, couplesOfClasses);
                        if (tempCouplingMetricValue > greaterCouplingMetricValue) {
                            greaterCouplingMetricValue = tempCouplingMetricValue;
                            indexBeginPartA = i;
                            indexBeginPartB = j;
                        }
                    }
                }
            }
            partA = clusters.get(indexBeginPartA);
            partB = clusters.get(indexBeginPartB);
            tempClasses = new HashSet<String>(partA.getClasses());
            tempCluster = new Cluster(tempClasses, (greaterCouplingMetricValue));
            tempClasses = new HashSet<String>(partB.getClasses());
            tempCluster.addClasses(tempClasses);
            clusters.add(tempCluster);
            clusters.remove(partA);
            clusters.remove(partB);

            hierarchicalStackOfClusters.push(new Cluster(partA));
            hierarchicalStackOfClusters.push(new Cluster(partB));
            hierarchicalStackOfClusters.push(new Cluster(tempCluster));
        }
        return hierarchicalStackOfClusters;
    }

    public LinkedList<Cluster> createPartitions(LinkedList<Cluster> clustersInitialized, LinkedList<CoupleOfClasses> couplesOfClassesFilled) {
        LinkedList<Cluster> listOfPartitions = new LinkedList<Cluster>();
        Stack<Cluster> stackOfCluster = createHierarchicalClusteringForPartition(clustersInitialized,
                couplesOfClassesFilled);
        Cluster father;
        Cluster firstSon;
        Cluster secondSon;
        LinkedList<Cluster> useLessPartitions = new LinkedList();
        double tempMoy = 0;
        System.out.println("Construction des partitions");
        while (!stackOfCluster.isEmpty()) {
            father = stackOfCluster.pop();
            secondSon = stackOfCluster.pop();
            firstSon = stackOfCluster.pop();
            if (isValidCluster(father, useLessPartitions)) {
                System.out.println("Couplage du père : " + father.getMetricCouplingValue());
                tempMoy = (firstSon.getMetricCouplingValue() + secondSon.getMetricCouplingValue()) / 2;
                System.out.println("Moyenne des couplages des fils : " + tempMoy);
                if (father.getMetricCouplingValue() > tempMoy) {
                    System.out.println("On ajoute le père à la partition");
                    listOfPartitions.add(father);
                    useLessPartitions.add(father);
                } else {
                    System.out.println("La valeur du couplage du père est inférieur à la moyenne des valeurs du couplage des fils donc on décompose le module en 2 partition ");
                }
            }
            System.out.println("Partitions Construites à cette étape : ");
            listOfPartitions.forEach(Cluster::display);
        }
        return listOfPartitions;
    }

    private boolean isValidCluster(Cluster cluster, LinkedList<Cluster> clusters) {
        for (Cluster cluster2 : clusters) {
            if (cluster2.hasSameClasses(cluster)) {
                return false;
            }
        }
        return true;

    }

    public void displayAllPartitions(LinkedList<Cluster> listOfPartitions) {
        listOfPartitions.forEach(partition -> {
            System.out.println("Partition");
            partition.displayP();
        });
        System.out.println("Nombre de partitions : " + listOfPartitions.size());
    }

}
