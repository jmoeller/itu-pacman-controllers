package jumo.mlp;

import java.util.Random;

public class NeuralNetwork {
	public int[] topology;
	
	// Represents the weights from node i to node j.
	// Wastes a lot of space (because every node is not connected to every other), but lookup is easy.
	// 1-indexed, because i = 0 represents the threshold weight.
	public double[][] weights;
	public double[] outputs;
	public double[] inputs; // Input to the activation functions at each perceptron
	
	// Indices of the in- and output layers in the topology
	public int INPUT = 0;
	public int OUTPUT;
	
	public static int EXAMPLE_INPUT = 0;
	public static int EXAMPLE_EXPECTED = 1;
	public static int BIAS = 0;
	
	public NeuralNetwork(int[] topology) {
		this.topology = topology;
		
		int perceptrons = PerceptronCount(topology);
		
		this.weights = new double[perceptrons + 1][perceptrons + 1];
		this.outputs = new double[perceptrons + 1];
		this.inputs  = new double[perceptrons + 1];
		
		this.OUTPUT = topology.length - 1;
	}
	
	public NeuralNetwork copy() {
		NeuralNetwork copy = new NeuralNetwork(this.topology);
		
		return copy;
	}
	
	public double[] evaluate(int[] x) {
		NeuralNetwork network = this;
		int node, start_node;
		
		// for each node in the input layer ...
		network.outputs[BIAS] = 1;
		start_node = FirstNodeNumber(network.topology, network.INPUT); 
		for (node = start_node; node < start_node + network.topology[network.INPUT]; node++) {
			int i = node - start_node;
			network.outputs[node] = x[i];
		}
		
		// for each node j in layer ... 
		for(int layer = 1; layer < network.topology.length; layer++) {
			start_node = FirstNodeNumber(network.topology, layer);
			
			for (node = start_node; node < start_node + network.topology[layer]; node++) {
				double in = 0;
				
				int start_i = FirstNodeNumber(network.topology, layer - 1);
				for (int i = start_i; i < start_i + network.topology[layer - 1]; i++) {
					in += network.weights[i][node] * network.outputs[i];
				}
				
				in += network.weights[BIAS][node] * network.outputs[BIAS];
				
				network.inputs[node] = in;
				network.outputs[node] = Activation(in);
			}
		}
		
		return output();
	}
	
	private double[] output() {
		double[] output = new double[this.topology[this.OUTPUT]];
		
		int node, start_node;
		
		start_node = FirstNodeNumber(this.topology, this.OUTPUT);
		for (node = start_node; node < start_node + this.topology[this.OUTPUT]; node++) {
			int i = node - start_node;
			
			output[i] = this.outputs[node];
		}
		
		return output;
	}
	
	private static int PerceptronCount(int[] topology) {
		int perceptrons = 0;
		
		for (int perceptron_count : topology) {
			perceptrons += perceptron_count;
		}
		
		return perceptrons;
	}
	
	private static double Activation(double x) { return Sigmoid(x); }
	private static double ActivationDerivative(double x) { return SigmoidDerivative(x); }
	
	private static double Sigmoid(double x) {
		return 1.0 / (1.0 + Math.pow(Math.E, -x));
	}
	
	private static double SigmoidDerivative(double x) {
		//return Math.pow(Math.E, x) / Math.pow(1.0 + Math.pow(Math.E, x), 2);
		return Sigmoid(x) * (1 - Sigmoid(x));
	}
	
	private static void InitializeWeights(NeuralNetwork network) {
		Random rnd = new Random();
		
		for (int i = 1; i < PerceptronCount(network.topology); i++) {
			network.weights[0][i] = rnd.nextDouble() - 0.5;
		}
		
		for (int layer = 0; layer < network.topology.length - 1; layer++) {
			int start_i = FirstNodeNumber(network.topology, layer);
			
			for (int i = start_i; i < start_i + network.topology[layer]; i++) {
				
				int next_layer = layer + 1;
				int start_j = FirstNodeNumber(network.topology, next_layer);
				for (int j = start_j; j < start_j + network.topology[next_layer]; j++) {
					network.weights[i][j] = rnd.nextDouble() - 0.5;
				}
			}
		}
	}
	
	// examples contain distinct examples. examples[i] contain inputs and outputs. examples[i][j] contains specific values.
	public static NeuralNetwork BackPropLearning(int[][][] examples, NeuralNetwork network, double learning_rate, double stopping_threshold) {
		double errors[] = new double[PerceptronCount(network.topology) + 1];
		
		InitializeWeights(network);
		
		double mean_error;
		
		int iterations = 0;
		do {
			for (int[][] example : examples) {
				int[] x = example[EXAMPLE_INPUT];
				int[] y = example[EXAMPLE_EXPECTED];
				
				/* Propagate the inputs forward to compute the outputs */
				int node, start_node;
				
				network.evaluate(x);
				
				/* Propagate deltas backward from output layer to input layer */
				
				start_node = FirstNodeNumber(network.topology, network.OUTPUT);
				for (node = start_node; node < start_node + network.topology[network.OUTPUT]; node++) {
					int i = node - start_node;
					errors[node] = ActivationDerivative(network.inputs[node]) * (y[i] - network.outputs[node]);
				}
				
				for (int layer = network.OUTPUT - 1; layer >= 0; layer--) {
					start_node = FirstNodeNumber(network.topology, layer);
					for (node = start_node; node < start_node + network.topology[layer]; node++) {
						double error_sum = 0;
						
						int start_j = FirstNodeNumber(network.topology, layer + 1);
						for (int j = start_j; j < start_j + network.topology[layer + 1]; j++) {
							error_sum += network.weights[node][j] * errors[j];
						}
						
						errors[node] = ActivationDerivative(network.inputs[node]) * error_sum;
					}
				}
				
				/* Update every weight in network using deltas */
				for (int i = 0; i < network.weights.length; i++) {
					for (int j = 0; j < network.weights[i].length; j++) {
						network.weights[i][j] = network.weights[i][j] + learning_rate * network.outputs[i] * errors[j];
					}
				}
			}
			
			iterations += 1;
			
			double error = 0;
			int counts = 0;
			for (int[][] example : examples) {
				int[] x = example[EXAMPLE_INPUT];
				int[] y = example[EXAMPLE_EXPECTED];
				
				double[] outputs = network.evaluate(x);
				
				for (int i = 0; i < outputs.length; i++) {
					error += Math.pow(y[i] - outputs[i], 2);
					System.out.print(x[i] + " -> " + y[i] + ": " + outputs[i] + "\t");
					counts += 1;
				}
			}
			
			mean_error = error / counts;
			
			System.out.println("E: " + mean_error + "\t(" + iterations + ")");
		}
		while (mean_error > stopping_threshold);
		
		
		
		return network;
	}
	
	private static int FirstNodeNumber(int[] topology, int layer) {
		int beginning_node = 1;
		
		for (int current_layer = 0; current_layer < layer; current_layer++) {
			beginning_node += topology[current_layer];
		}
		
		return beginning_node;
	}
	
	public static void main(String[] args) {
		int[][][] examples = {
				// Inp.     Outp.
				{ { 170 }, { 1 }, }, // Example 1
				{ { 190 }, { 0 }, }, // ..... 2
				{ { 165 }, { 1 }, }, // ... 3
				{ { 180 }, { 0 }, }, // . 4
				{ { 210 }, { 1 }, }, // 5
		};
		
		NeuralNetwork nn = new NeuralNetwork(new int[] { 1, 2, 1 });
		
		NeuralNetwork.BackPropLearning(examples, nn, 0.1, 0.1);
	}
};
