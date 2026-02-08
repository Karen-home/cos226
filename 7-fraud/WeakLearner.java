import edu.princeton.cs.algs4.MaxPQ;
import edu.princeton.cs.algs4.MinPQ;

public class WeakLearner {

    private static final int ABOVE = 1;
    private static final int BELOW = 0;

    private static final int CLEAN = 0;
    private static final int FRAUD = 1;

    private static final int X = 0;
    private static final int Y = 1;

    private static final int NONE = 0;
    private static final int SUMMARY = 1;
    private static final int DEBUG = 2;
    private static final int ALL = 3;

    private int[][] input;
    private double[] weights;
    private int[] labels;
    private int dimension = 100;
    private int value = -1;
    private int sign = -1;

    private int verbose = NONE;

    // train the weak learner
    public WeakLearner(int[][] input, double[] weights, int[] labels) {
        this.input = input;
        this.weights = weights;
        this.labels = labels;

        validate();

        double champion_accuracy = -1;
        int[] signs = { BELOW, ABOVE };
        int dimensions = input[0].length;

        // iterate through dimensions
        for (int k = 0; k < dimensions; k++) {
            // get range of values to test against, e.g. {1,2,3,3}
            WeightedValue[] sortedValues = getSortedValues(input, k);

            // iterate through the signs {ABOVE, BELOW}
            for (int s = 0; s < signs.length; s++) {
                int curr_sign = signs[s];
                int base_value = sortedValues[0].value; // min value

                if (verbose >= DEBUG) {
                    System.out.printf("STARTING ANALYSIS FOR vp = %d, dp = %d, sp = %d \n",
                                      base_value, k, curr_sign);
                }

                double base_accuracy = getAccuracy(curr_sign, k, base_value);

                if (verbose >= DEBUG) {
                    System.out.printf("BASE ACCURACY WITH THRESHOLD VALUE %d = %f\n", base_value,
                                      base_accuracy);
                }

                int prev_value = base_value;
                double curr_accuracy = base_accuracy;

                // now increment, grouping together all points with the same value
                for (int n = 1; n < sortedValues.length; n++) {
                    int curr_value = sortedValues[n].value;
                    if (curr_value == base_value) // base values were handled already
                        continue;

                    if (curr_value != prev_value) {
                        if (verbose >= DEBUG)
                            System.out.printf("Updating decision stump for %d with accuracy %f.\n",
                                              prev_value, curr_accuracy);
                        champion_accuracy = updateDecisionStump(curr_accuracy, champion_accuracy, k,
                                                                prev_value, curr_sign);
                    }

                    int label = sortedValues[n].label;
                    double weight = sortedValues[n].weight;

                    if (verbose >= DEBUG)
                        System.out.printf("Next point %d has label %d, sign %d -- ", curr_value,
                                          label, curr_sign);

                    if (s == label) {
                        if (verbose >= DEBUG)
                            System.out.printf(
                                    "CORRECT PREDICTION, ADDING %f to curr_accuracy %f = ",
                                    weight, curr_accuracy);
                        curr_accuracy += sortedValues[n].weight;
                        if (verbose >= DEBUG)
                            System.out.printf(" %f\n", curr_accuracy);
                    }
                    else {
                        if (verbose >= DEBUG)
                            System.out.printf(
                                    "INCORRECT PREDICTION, SUBTRACTING %f from curr_accuracy %f = ",
                                    weight, curr_accuracy);
                        curr_accuracy -= sortedValues[n].weight;
                        if (verbose >= DEBUG)
                            System.out.printf(" %f\n", curr_accuracy);
                    }
                    prev_value = curr_value;
                }

                // Last value
                if (verbose >= DEBUG)
                    System.out.printf(
                            "Updating decision stump for last value %d with accuracy %f.\n",
                            prev_value, curr_accuracy);

                champion_accuracy = updateDecisionStump(curr_accuracy, champion_accuracy, k,
                                                        prev_value, curr_sign);
            }
        }

        if (verbose >= SUMMARY)
            System.out.printf("champion: vp = %d, dp = %d, sp = %d, accuracy = %f\n", value,
                              dimension, sign, champion_accuracy);
    }

    // train the weak learner - n^2 solution
    public void WeakLearner2(int[][] input, double[] weights, int[] labels) {
        this.input = input;
        this.weights = weights;
        this.labels = labels;

        validate();

        double champion_weight = -1;
        int[] signs = { BELOW, ABOVE };
        int dimensions = input[0].length;

        // iterate through dimensions
        for (int k = 0; k < dimensions; k++) {
            // iterate through the signs {ABOVE, BELOW}
            for (int s = 0; s < signs.length; s++) {
                int curr_sign = signs[s];

                // get range of values to test against, e.g. {1,2,3,3}
                int[] values = getValues(input, k);
                for (int vn = 0; vn < values.length; vn++) {
                    int curr_value = values[vn];

                    if (verbose >= DEBUG) {
                        System.out.printf("=== START ===\n");
                        System.out.printf("Iterating through dp %d, sp %d, vp %d\n",
                                          k,
                                          curr_sign, curr_value);
                    }

                    // iterate through the points to get total weight
                    double curr_weight = 0;
                    for (int n = 0; n < input.length; n++) {
                        int sample = input[n][k];
                        int label = labels[n];
                        double weight = weights[n];

                        int prediction = predict(curr_sign, sample, curr_value);
                        if (verbose >= DEBUG)
                            System.out.printf("Point %d: %d, Prediction: %d, Label: %d ", n, sample,
                                              prediction, label);

                        if (prediction == label) { // correct prediction
                            if (verbose >= DEBUG)
                                System.out.printf(" CORRECT");
                            curr_weight += weight;
                        }
                        if (verbose >= DEBUG)
                            System.out.printf("\n");
                    }
                    champion_weight = updateDecisionStump(curr_weight, champion_weight, k,
                                                          curr_value, curr_sign);
                    if (verbose >= DEBUG) {
                        System.out.printf("vp = %d, dp = %d, sp = %d, accuracy = %f\n",
                                          curr_value, k, curr_sign,
                                          curr_weight);
                        System.out.printf("=== END=== \n");
                    }
                }

            }
        }

        if (verbose >= SUMMARY)
            System.out.printf("champions: vp = %d, dp = %d, sp = %d, accuracy = %f\n", value,
                              dimension, sign,
                              champion_weight);
    }

    // return the prediction of the learner for a new sample
    public int predict(int[] sample) {
        return predict(sign, sample[dimension], value);
    }

    // return the dimension the learner uses to separate the data
    public int dimensionPredictor() {
        return dimension;
    }

    // return the value the learner uses to separate the data
    public int valuePredictor() {
        return value;
    }

    // return the sign the learner uses to separate the data
    public int signPredictor() {
        return sign;
    }


    private MaxPQ<Integer> getPQValues(int[][] input, int k) {
        MaxPQ<Integer> values = new MaxPQ<Integer>(input.length);
        for (int i = 0; i < input.length; i++) {
            int[] point = input[i];
            values.insert(point[k]);
        }
        return values;
    }

    private static class WeightedValue implements Comparable<WeightedValue> {

        private int value;
        private int label;
        private double weight;

        // initializes index and value
        public WeightedValue(int value, int label, double weight) {
            this.value = value;
            this.label = label;
            this.weight = weight;
        }

        // compares two WeightedValues based on their values
        public int compareTo(WeightedValue that) {
            return Integer.compare(this.value, that.value);
        }

    }


    private WeightedValue[] getSortedValues(int[][] input, int k) {
        WeightedValue[] values = new WeightedValue[input.length];
        MinPQ<WeightedValue> pq = new MinPQ<WeightedValue>(input.length);
        for (int i = 0; i < input.length; i++) {
            int[] point = input[i];
            WeightedValue w = new WeightedValue(point[k], labels[i], weights[i]);
            pq.insert(w);
        }
        if (verbose >= ALL)
            System.out.printf("Created sorted value array:\n");
        for (int i = 0; i < input.length; i++) {
            values[i] = pq.delMin();
            if (verbose >= ALL)
                System.out.printf("Sorted value %d = %d\n", i, values[i].value);
        }
        return values;
    }

    private int[] getValues(int[][] input, int k) {

        int[] values = new int[input.length];
        for (int i = 0; i < input.length; i++) {
            int[] point = input[i];
            values[i] = point[k];
        }
        return values;
    }

    private int predict(int s, int sample, int value) {

        if (s == BELOW) {
            return (sample <= value) ? 0 : 1;
        }
        if (s == ABOVE) {
            return (sample <= value) ? 1 : 0;
        }
        return -1;
    }


    private double getAccuracy(int sign, int dimension, int value) {

        // iterate through the input points to get accuracy
        double accuracy = 0;
        for (int n = 0; n < input.length; n++) {
            int sample = input[n][dimension];
            int label = labels[n];
            double weight = weights[n];

            int prediction = predict(sign, sample, value);
            if (verbose >= DEBUG)
                System.out.printf("Sample %d: %d, Prediction: %d, Label: %d, Weight: %f ", n,
                                  sample,
                                  prediction, label, weight);

            if (prediction == label) { // correct prediction
                if (verbose >= DEBUG)
                    System.out.printf("CORRECT");
                accuracy += weight;
            }
            if (verbose >= DEBUG)
                System.out.printf("\n");
        }
        return accuracy;
    }


    private double updateDecisionStump(double curr_weight, double champion_weight, int k,
                                       int curr_value, int curr_sign) {

        if (curr_weight > champion_weight) {
            dimension = k;
            value = curr_value;
            sign = curr_sign;
            champion_weight = curr_weight;
        }
        // when there is a tie-breaker -
        // in case of a tie,
        // always pick the smallest possible dimension predictor,
        // if a tie still persists pick the smallest possible value predictor,
        // and if a tie still yet persists, pick the smallest sign predictor.
        if (curr_weight == champion_weight) {
            if (k < dimension) {
                dimension = k;
                value = curr_value;
                sign = curr_sign;
                champion_weight = curr_weight;
            }
            else if (k == dimension) {
                if (curr_value < value) {
                    dimension = k;
                    value = curr_value;
                    sign = curr_sign;
                    champion_weight = curr_weight;
                }
                else if (curr_value == value) {
                    if (curr_sign < sign) {
                        dimension = k;
                        value = curr_value;
                        sign = curr_sign;
                        champion_weight = curr_weight;
                    }
                }
            }
        }
        return champion_weight;

    }


    private void validate() {
        if (input == null || weights == null || labels == null) {
            throw new IllegalArgumentException("Null argument");
        }

        if (input.length == 0) {
            throw new IllegalArgumentException("No inputs");
        }
        if (input.length != weights.length || input.length != labels.length) {
            throw new IllegalArgumentException("Argument arrays have different lengths");
        }
        for (int i = 0; i < labels.length; i++) {
            if (labels[i] != 0 && labels[i] != 1) {
                throw new IllegalArgumentException("Labels must to be 0 or 1");
            }
        }

    }


    // unit testing
    public static void main(String[] args) {

    }
}
