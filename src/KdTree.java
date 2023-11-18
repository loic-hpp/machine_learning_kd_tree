import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
/**
 * Class used to implement a machine learning
 * We will use it to train our model for N cells
 * This class will have k dimension
* */
public class KdTree {
    /**
     *  Class use to represent a cell in the kdTree
     * */
    private static class Node{
        private double[] diagnosisAttribute;
        public int height;
        public Node left, right;
        /**
         * Node constructor
         * @param attribute an array of 10 information about a cell
         * */
        public Node(double[] attribute){
            this.diagnosisAttribute = attribute;
            this.left = null;
            this.right = null;
            height = 0;
        }
        /**
         * Node constructor
         * @param attribute  an array of 10 information about a cell
         * @param height the position of the node in the tree
         * */
        public Node(double[] attribute, int height){
            this(attribute);
            this.height = height;
        }
        /**
         * To determine if a cell is malignant or benign
         * */
        public boolean isMalignant  (){
            return this.diagnosisAttribute[0]==1.0;
        }
        /**
         * Calculate distance between 2 cells
         * This function uses Euclidean norm
         * */
        public double distanceToOtherNode(Node otherNode, int k){
            double SumOfSquares = 0.0;
            for(int i=1; i<=k; i++){
                double difference = this.diagnosisAttribute[i] - otherNode.diagnosisAttribute[i];;
                SumOfSquares += difference * difference;
            }
            return Math.sqrt(SumOfSquares);
        }
    }
    private Node root;
    private int dimension;
    public KdTree(int k){
        this.dimension = k;
        this.root = null;
    }
    private int getNodeDimension(Node node){
        return (node.height % this.dimension) + 1;
    }
    /**
     * Add a node to the tree from the root
     * @param value array of double
     *              Take cells information as parameter
     *              create a node from it and add it to the kdTree
     * */
    public Node addNode(double[] value){
        Node newNode = new Node(value);
        if(root==null)
            root = newNode;
        else
            newNode = this.recursiveAddNode(root, value, 0);
        return newNode;
    }/**
     * @param currNode Node from where we start
     * @param value cell information
     * @param height position in the tree
     */
    private Node recursiveAddNode(Node currNode, double[] value, int height){
        // If the current Node is null means we reach a leaf position break the function
        if (currNode == null)
            return new Node(value, height+1);
        int currDimension = getNodeDimension(currNode);
        // consider left node child if the value in the dimension is lesser that current node and right node child else
        if (value[currDimension] < currNode.diagnosisAttribute[currDimension]) {
            currNode.left = recursiveAddNode(currNode.left, value, currNode.height);
        } else {
            currNode.right = recursiveAddNode(currNode.right, value, currNode.height);
        }

        return currNode;
    }
    /** Find the k nearest neighbors of a given cell
     * @param queryValue information on the cell we are trying to find neighbors
     * */
    public List<double[]> nearestNeighbors(double[] queryValue){
        Node node = new Node(queryValue);
        // Create a maxheap of size k
        PriorityQueue<Node> nearestNeighbors = new PriorityQueue<>(this.dimension,
        (node1, node2) -> (int) (node.distanceToOtherNode(node2,dimension) - node.distanceToOtherNode(node1,dimension)));
        // Use the heap to get the k nearest neighbors
        recursiveNearestNeighbors(root, queryValue, dimension, nearestNeighbors);
        //add neighbors to the list
        List<double[]> kNearest = new ArrayList<>();
        while (!nearestNeighbors.isEmpty()) {
            kNearest.add(nearestNeighbors.poll().diagnosisAttribute);
        }
        return kNearest;
    }
    /** Find recursively neighbors of a cell
     * @param  node start node
     * @param queryValue cell to which we are searching neighbors
     * @param nearestNeighbors max heap of Node
     * */
    private void recursiveNearestNeighbors(Node node, double[] queryValue, int k, PriorityQueue<Node>nearestNeighbors){
        // Break the function if the current node is null
        if (node == null) {
            return;
        }

        nearestNeighbors.offer(node);
        if (nearestNeighbors.size() > k) {
            nearestNeighbors.poll();
        }
        int currDimension = getNodeDimension(node);
        double queryValueInDimension = queryValue[currDimension];
        double nodeValueInDimension = node.diagnosisAttribute[currDimension];

        // Divide the tree into 2 spaces near and far
        Node nearChild, farChild;
        if(queryValueInDimension < nodeValueInDimension){
            nearChild = node.left;
            farChild = node.right;
        } else {
            nearChild = node.right;
            farChild = node.left;
        }
        // add near child of near space
        recursiveNearestNeighbors(nearChild, queryValue, k, nearestNeighbors);
        // If the heap isn't full or if there is nearer child in the far space add child from far space
        if (nearestNeighbors.size() < k || Math.abs(queryValueInDimension - nodeValueInDimension) <= nearestNeighbors.peek().distanceToOtherNode(new Node(queryValue), k)) {
            recursiveNearestNeighbors(farChild, queryValue, k, nearestNeighbors);
        }

    }
    /**
     * Reading the csv file
     * and formatting data into a suitable format for our needs
     * */
    private static double[][] readCSVFile(){
        double[][] data = new double[569][11];
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader("data.csv"))) {
            // Here is the array structure by index
            // 0 diagnosis	1 radius_mean	2 texture_mean	3 perimeter_mean	4 area_mean
            // 5 smoothness_mean	6 compactness_mean	7 concavity_mean	8 concave points_mean	9 symmetry_mean
            // 10 fractal_dimension_mean

            // Read the first line of the csv, but we don't use it
            String[] row = br.readLine().split(",");
            int index = 0;
            while ((line = br.readLine()) != null) {
                row = line.split(",");
                double[] rowDouble = new double[11];
                rowDouble[0] = row[1].equals("M")? 1.0:0.0;
                // Get only the first eleven properties of the cell
                for(int i=1; i< 11; i++)
                    rowDouble[i] = Double.parseDouble(row[i+1]);
                data[index++] = rowDouble;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
/**
 * Each instance of this class represent a test instance and give the test result
 * */
    public static class Test{
        public int trainningInstanceNumber, numberOfTestInstance;
        int k;
        KdTree tree;
        double[][] data;
        public Test(int N, int k, double[][] data){
            trainningInstanceNumber = N;
            numberOfTestInstance = N/4;
            this.k = k;
            tree = new KdTree(k);
            this.data = data;
        }
        /**
         * This function gives the result of a test instance
         * */
        public double processTest(int indexCellToTest){
            double diagnosis = 0.0;
            int countDiagnosis = 0;
            List<double[]> knn = tree.nearestNeighbors(data[indexCellToTest]);
            for(double[] cell : knn){
                // count the number of malignant cell
                if(cell[0]==1.0)
                    countDiagnosis++;
            }
            // The cell will be malignant if the majority of the neighbors are malignant
            if(countDiagnosis > k/2)
                diagnosis = 1.0;
            return diagnosis;
        }

    }
    /**
     * Generate N random index to train the model
     * and T random index for the test instance
     * */
    private static List<List<Integer>> randomGenerator(int N){
        List<Integer> numberList = new ArrayList<>();
        for (int i = 0; i < 569; i++) {
            numberList.add(i);
        }
        Collections.shuffle(numberList);
        List<Integer> modelValue = numberList.subList(0, N);
        List<Integer> testValue = numberList.subList(N, N+N/4);
        List<List<Integer>> result = new ArrayList<>();
        result.add(modelValue);
        result.add(testValue);
        return result;
    }
    public static void main(String[] args) {
        int[] NValues = {50, 150, 250, 350, 450};
        int[] kValues = {3, 5, 7};
        double[][] data = readCSVFile();
        System.out.println("K Value\t|N Value\t|Accuracy(%)\t|run time(in ns)\t");
        System.out.println("--------|-----------|---------------|-------------");
        for(int i = 0; i< kValues.length; i++)
        {
            for(int j = 0; j<NValues.length; j++)
            {
                KdTree.Test testInstance = new KdTree.Test(NValues[j], kValues[i],data);

        // realize n test instance
                int numberOfConcordance = 0;
                long runningTime=0;
                for(int ii = 0; ii< testInstance.numberOfTestInstance; ii++)
                 {
        // create the randon list for model trainning and test instance
                     List<List<Integer>> testIndex = randomGenerator(NValues[j]);
                     List<Integer> modelValue = testIndex.get(0);
                     List<Integer> testValue = testIndex.get(1);
                     testInstance.tree = new KdTree(testInstance.k);
            // retrain the model
                    for(int jj=0; jj<modelValue.size(); jj++)
                        testInstance.tree.addNode(data[modelValue.get(ii)]);
                 long startTime=System.nanoTime();
            // if test result is the same as cell data increment numberOfConcordance
                    if(testInstance.processTest(testValue.get(ii))==data[testValue.get(ii)][0])
                        numberOfConcordance++;
                 long endTime=System.nanoTime();
                     runningTime += (endTime-startTime);
                }
                System.out.println(kValues[i]+"\t\t|" + NValues[j] + "\t\t|\t\t" + (numberOfConcordance*100/testInstance.numberOfTestInstance)
                        + "\t\t|\t\t" + runningTime);
                System.out.println("--------|-----------|---------------|-------------");
            }
        }


    KdTree tree = new KdTree(3);

    }
}
