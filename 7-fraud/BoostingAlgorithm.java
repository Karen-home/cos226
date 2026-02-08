import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

public class BoostingAlgorithm {

    private Clustering cluster;
    private int[][] reduced_input;
    private double[] weights;
    private int[] labels;
    private Queue<WeakLearner> weakLearners;

    // create the clusters and initialize your data structures
    public BoostingAlgorithm(int[][] input, int[] labels, Point2D[] locations, int k) {

        // todo: throw exceptions if arguments are invalid

        this.labels = labels;
        weakLearners = new Queue<WeakLearner>();

        // Create cluster
        cluster = new Clustering(locations, k);
        reduced_input = new int[input.length][input[0].length];
        for (int i = 0; i < input.length; i++) {
            int[] reduced_transactions = cluster.reduceDimensions(input[i]);
            reduced_input[i] = reduced_transactions;
        }

        // Initialize weights
        weights = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            double l = input.length;
            double w = (1.0 / l);
            // System.out.printf("Input Length: %d Weight: %f\n", input.length, w);
            weights[i] = w;
        }
        // printWeights();

    }

    // return the current weight of the ith point
    public double weightOf(int i) {

        if (weights.length < i)
            throw new IllegalArgumentException("Index invalid");

        return weights[i];
    }

    // apply one step of the boosting algorithm
    public void iterate() {
        WeakLearner weakLearner = new WeakLearner(reduced_input, weights, labels);

        for (int i = 0; i < reduced_input.length; i++) {
            int[] inputs = reduced_input[i];
            int prediction = weakLearner.predict(inputs);
            if (prediction != labels[i]) {
                weights[i] *= 2;
            }
        }

        // renormalize weights to 1

        double total = 0;
        for (int i = 0; i < weights.length; i++) {
            total += weights[i];
        }

        for (int i = 0; i < weights.length; i++) {
            double newweight = weights[i] / total;
            weights[i] = newweight;
        }
        // printWeights();

        weakLearners.enqueue(weakLearner);

    }

    // return the prediction of the learner for a new sample
    public int predict(int[] sample) {

        // reduce sample
        int[] reduced_sample = cluster.reduceDimensions(sample);

        int[] predictions = new int[weakLearners.size()];
        int counter = 0;
        for (WeakLearner w : weakLearners) {
            int prediction = w.predict(reduced_sample);
            predictions[counter] = prediction;
            counter++;
        }

        // get majority
        int ones = 0;
        int zeros = 0;
        for (int i = 0; i < predictions.length; i++) {
            int p = predictions[i];
            if (p == 0)
                zeros += 1;
            else if (p == 1)
                ones += 1;
        }

        return (zeros >= ones) ? 0 : 1;
    }

    private void printWeights() {

        System.out.print("\n\nWeights: {");
        for (int i = 0; i < weights.length; i++) {
            System.out.printf("%f ", weights[i]);
        }
        System.out.print("}\n");

    }

    // unit testing
    public static void main(String[] args) {
        // read in the terms from a file
        DataSet training = new DataSet(args[0]);
        DataSet testing = new DataSet(args[1]);
        int k = Integer.parseInt(args[2]);
        int T = Integer.parseInt(args[3]);

        int[][] trainingInput = training.getInput();
        int[][] testingInput = testing.getInput();
        int[] trainingLabels = training.getLabels();
        int[] testingLabels = testing.getLabels();
        Point2D[] trainingLocations = training.getLocations();

        // train the model
        BoostingAlgorithm model = new BoostingAlgorithm(trainingInput, trainingLabels,
                                                        trainingLocations, k);
        for (int t = 0; t < T; t++)
            model.iterate();

        // calculate the training data set accuracy
        double training_accuracy = 0;
        for (int i = 0; i < training.getN(); i++)
            if (model.predict(trainingInput[i]) == trainingLabels[i])
                training_accuracy += 1;
        training_accuracy /= training.getN();

        // calculate the test data set accuracy
        double test_accuracy = 0;
        for (int i = 0; i < testing.getN(); i++)
            if (model.predict(testingInput[i]) == testingLabels[i])
                test_accuracy += 1;
        test_accuracy /= testing.getN();

        StdOut.println("Training accuracy of model: " + training_accuracy);
        StdOut.println("Test accuracy of model: " + test_accuracy);
    }
}
