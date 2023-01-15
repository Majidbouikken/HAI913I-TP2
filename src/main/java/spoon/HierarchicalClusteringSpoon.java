package spoon;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import identificationModule.Cluster;
import identificationModule.CoupleOfClasses;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtType;

public class HierarchicalClusteringSpoon {

	private CtModel model;

	public HierarchicalClusteringSpoon(CtModel model) {
		super();
		this.model = model;
	}

	// initialisation des clusters
	// @return liste de cluster initialisé

	public LinkedList<Cluster> createClustersInitialized() {

		LinkedList<Cluster> clusters = new LinkedList<Cluster>();
		Cluster newCluster;
		for (CtType<?> type : model.getAllTypes()) {
			if (!isClassExistOnCluster(clusters, type.getQualifiedName())) {
				newCluster = createClusterInitialized(type.getQualifiedName());
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

	/**
	 * création d'une liste contenant pour chaque éléméent la classeA, la ClasseB et
	 * le couplage entre ces 2 classes
	 * 
	 * @params spoonCouplingGraph
	 * @return liste de <CoupleOfClasses>
	 */
	public LinkedList<CoupleOfClasses> createListOfClassesCouple(SpoonCouplingGraph spoonCouplingGraph) {

		LinkedList<CoupleOfClasses> coupledClassesList = new LinkedList<CoupleOfClasses>();
		CoupleOfClasses newCoupleOfClasses;
		Map<String, Double> tempMap;
		for (String classA : spoonCouplingGraph.getBidirectionalWeightedCouplingGraph().keySet()) {
			tempMap = spoonCouplingGraph.getBidirectionalWeightedCouplingGraph().get(classA);
			for (String classB : tempMap.keySet()) {
				newCoupleOfClasses = new CoupleOfClasses(classA, classB, tempMap.get(classB));
				coupledClassesList.add(newCoupleOfClasses);
			}
		}
		return coupledClassesList;
	}

	/**
	 * creation du regroupement (clustering) hiérarchique des classes d’une
	 * application Question 1 Exercice 2
	 * 
	 */

	public void createHierarchicalClustering(LinkedList<Cluster> clusters,
			LinkedList<CoupleOfClasses> couplesOfClasses) {
		Cluster clusterA, clusterB, partA, partB, tempCluster;
		double greaterCouplingMetricValue, tempCouplingMetricValue;
		int indexBeginPartA, indexBeginPartB;
		Set<String> tempClasses;
		// affichage
		System.out.println(" Valeur des cluster lors de l'initialisation : ");
		clusters.forEach(cluster -> cluster.display());
		System.out.println("\nCréation de la hiérarchie des clusters :");

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
				System.out.println("\nFusion entre clusters");
				System.out.println(" Partie A De la fussion : ");
				partA.getClasses().forEach(classe -> System.out.println("Classe :  " + classe.toString()));
				System.out.println("Parie B de la fusion");
				partB.getClasses().forEach(classe -> System.out.println("Classe :  " + classe.toString()));
				System.out.println("valeur du couplage : " + greaterCouplingMetricValue);
				System.out.println("\n\n");
				System.out.println("Liste des cluster de cette étape: ");
				clusters.forEach(cluster -> {
					System.out.println("\n Cluster :");
					cluster.display();
				});
			} else {
				break;
			}
		}

	}

	private double getScoreBetweenClusters(Cluster clusterA, Cluster clusterB,
			LinkedList<CoupleOfClasses> couplesOfClasses) {
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
		System.out.println("Affichage de la hierarchie des clusters");
		for (Cluster cluster : clusters) {
			System.out.println("nouveau Cluster");
			cluster.display();
			System.out.println("\n");
		}
	}

	/**
	 * 
	 */

	public Stack<Cluster> createHierarchicalClusteringForPartition(LinkedList<Cluster> clusters,
			LinkedList<CoupleOfClasses> couplesOfClasses) {
		Stack<Cluster> hierarchicalStackOfClusters = new Stack<Cluster>();
		Cluster clusterA, clusterB, partA, partB, tempCluster;
		double greaterCouplingMetricValue, tempCouplingMetricValue;
		int indexBeginPartA, indexBeginPartB;
		Set<String> tempClasses;
		// ouputs
		System.out.println(" Valeur des cluster lors de l'initialisation : ");
		clusters.forEach(cluster -> cluster.display());
		System.out.println("\nCréation de la hiérarchie des clusters :");

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
			if (greaterCouplingMetricValue > 0) {
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
			} else {
				break;
			}
		}
		return hierarchicalStackOfClusters;
	}

	public LinkedList<Cluster> creatPartitions(LinkedList<Cluster> clustersInialized,
			LinkedList<CoupleOfClasses> couplesOfClassesFilled) {
		LinkedList<Cluster> listOfpartitions = new LinkedList<Cluster>();
		Stack<Cluster> stackOfCluster = createHierarchicalClusteringForPartition(clustersInialized,
				couplesOfClassesFilled);
		Cluster father;
		Cluster firstSon;
		Cluster secondSon;
		LinkedList<Cluster> useLessPartitions = new LinkedList();
		double tempMoy = 0;
		System.out.println("\nConstruction des partitions");
		while (!stackOfCluster.isEmpty()) {
			father = stackOfCluster.pop();
			secondSon = stackOfCluster.pop();
			firstSon = stackOfCluster.pop();
			if (isValidCluster(father, useLessPartitions)) {
				System.out.println("\n Couplage du père : " + father.getMetricCouplingValue());
				tempMoy = (firstSon.getMetricCouplingValue() + secondSon.getMetricCouplingValue()) / 2;
				System.out.println("\n moyenne des couplages des fils : " + tempMoy);
				if (father.getMetricCouplingValue() > tempMoy) {
					System.out.println("\nOn ajoute le père à la partition");
					listOfpartitions.add(father);
					useLessPartitions.add(father);

				} else {
					System.out.println(
							"\nLa valeur du couplage du père est inférieur à la moyenne des valeurs du couplage des fils donc on décompose le module en 2 partition ");
				}

			}

			System.out.println("\n Partitions Construites à cette étape : ");
			listOfpartitions.forEach(partition -> partition.display());
		}

		return listOfpartitions;
	}

	private void getUseLessPartitions(Cluster cluster, Stack<Cluster> stackOfCluster,
			LinkedList<Cluster> uselessCLusters) {
		for (int i = 0; i < stackOfCluster.size(); i++) {
			for (String clsName : cluster.getClasses()) {
				if (stackOfCluster.get(i).getClasses().contains(clsName)) {
					uselessCLusters.add(cluster);
				}
			}
		}
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
			System.out.println("\n partition : ");
			partition.displayP();
		});
		System.out.println("Nombre de partitions : " + listOfPartitions.size());
	}

}
