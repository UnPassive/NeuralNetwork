package src;

import java.util.ArrayList;
import java.lang.Math;
import java.util.Random;


public class NeuralNetwork 
{
	static boolean converged = false;
	int inputs;
	int hiddenL;
	int nodes;		//nodes by layer?
	int outputs;
	ArrayList<Neuron> inLayer = new ArrayList<Neuron>();
	ArrayList<Neuron> hiddenLayer = new ArrayList<Neuron>();	//make these dynamically
	ArrayList<Neuron> outLayer = new ArrayList<Neuron>();
	Double[][] weights =  null;
	static int error;

	public static void main(String[] args) 
	{
		for (int i = 0; i < args.length; i++) 
		{
			//if we ask the user for inputs instead of making them command line inputs it may be easier
			//also ask for what kind of Neural network this is
		}
		String in = args[1];
		String hid = args[2];
		String node = args[3];
		String out = args[4];
		//parse to ints and construct new NeuralNetwork(in, hid, node, out);

		Neuron.radialBiasActFun = true;		//if Radial Basis network
		//Neuron.MLFActFun = true; 			  if MLF network

		NeuralNetwork net = new NeuralNetwork(1, 1, 1,1);
		net.train(net);
	}

	public NeuralNetwork(int inputs, int hiddenL, int nodes, int outputs)
	{
		this.inputs = inputs;
		this.hiddenL = hiddenL;		//a 3 layer network would have a hiddenL input value of 1
		this.nodes = nodes;			//maybe pass in an array if number of nodes is by layer. Would require some sort of console input instructions
		this.outputs = outputs;		//just one output for this project's implementation of the code.
		//To-Do: initialize random weights
		//Create wights double array and methods to get and update a weight

		for(int i = 0; i < hiddenL; i++)		//construct layer by layer
		{
			if(i==0)		//input layer
			{
				for(int j = 0; j < inputs; j++)
				{
					//inputs form Rosenbrock function
					Neuron n = new Neuron();
					//n.addInput(0);		//add inputs in train function
					inLayer.add(n);
				}
			}
			else if(i > 0 && i < hiddenL || i == 1)		//hidden layers here. If just 1 then construct this and an output layer
			{
				for(int j = 0; j < nodes; j++)
				{
					Neuron n = new Neuron();
					hiddenLayer.add(n);		//I think we will actually need multiple hidden layer ArrayLists. One for each hidden layer. This may need to be changed. Although that would meen deep learning which we don't have to do

				}
			}
			if(i == hiddenL || i == 1)	//output layer, will be constructed even if just one 
			{
				for(int j = 0; j < outputs; j++)
				{
					Neuron n = new Neuron();
					outLayer.add(n);
				}
			}

			//Add bias node to each layer
		}
		//add next connections to next layer:
		for(Neuron n: inLayer)
		{
			for(int i = 0; i < hiddenLayer.size(); i++)
			{
				n.addConnection(hiddenLayer.get(i));
			}
		}
		for(Neuron n: hiddenLayer)
		{
			for(int i = 0; i < outLayer.size(); i++)
			{
				n.addConnection(outLayer.get(i));
			}
		}

	}
	private static void isConverged()
	{
		/*some method to check if converged
		 *Should take into account a maxRuns value and minimum acceptable error value 
		 *When minimum Error is achieved then it's converged?
		 *Otherwise just stop at maxRuns and print results (current error?)
		 */

		//update converged class variable here
	}

	private void train(NeuralNetwork net)
	{

		while(!converged) 		//train network
		{
			/* To-Do: begin computations layer by layer, Neuron by Neuron to create final output.
			 * activation function and such are stored in Neuron class
			 */

			error = 0;
			for(Neuron n: inLayer )
			{
				double ros = 1;		//change to rosenbrock function outputs
				n.addInput(ros);
				//hidden layer activation function??
				
			}

			for(Neuron n: hiddenLayer)
			{
				n.computeOutput();
				
				
			}
			
			/* To add an output the next layer's neuron's input:
			 * nextLayerNeuron.addInput(doubleValueOfOutput)
			 */

			/* Then tell the neuron to compute its output with:
			 * double output = n.computeOutput()
			 */

			//Then pass that output into the inputs of the next layer nodes

			//Then move on to computing next layers outputs

			//Then backpropagate once last layer is reached

			isConverged();		//at end of each iteration check if converged. Method will handle min acceptable error and max runs amount

			//once converged test performance on held-out data
			//performanceMethodCheck()
		}
		//if did converge then loop terminates
		// then call print(NeuralNetwork); to see outputs, weights and error values

	}

	/**
	 * Weight initialization method
	 */
	private void initRandomWeights()
	{
		//update the hashmap/weights table to random values
		int dim = inLayer.size() + hiddenLayer.size() + outLayer.size();
		this.weights = new Double[dim][dim];
		double randUpperBound = Math.sqrt(6/(inLayer.size()+outLayer.size()));      // according to https://stats.stackexchange.com/questions/47590/what-are-good-initial-weights-in-a-neural-network
		double randLowerBound = randUpperBound * -1;
		Random rand = new Random();
		for(int i = 0; i < dim; i++) {
			for(int j = 0; j < dim; j ++) {
				double holder = rand.nextDouble();
				while(holder == 0) {
					holder = rand.nextDouble();
				}
				this.weights[i][j] = randLowerBound + (randUpperBound - randLowerBound) * holder;
			}
		}
	}

	private void generateData(int version)
	{
		//Rosenbrock function here
		//normalized data for radial basis and MLF networks have different ranges
		if(version == 2)		//we have to do the five versions (2-6 dimensions)
		{

		}
		else if(version == 3)
		{

		}
		else if(version == 4)
		{
			//I'm assuming each version will have to execute completely differently
		}
		else if(version == 5)
		{
			//If there's a more elegant way to specialize algorithm please implement it here 
		}
		else if(version ==6)
		{
			//we should save the data so that we don't have to recompute it every time we do an iteration
		}
	}

	private double checkValue(double output)	//check how close output was to true Rosenbrock
	{
		//some sort of closeness-to-true-value error measurement system here. [0, 1] probably
		return 0;
	}

	private void backProp()
	{
		/* calculate error (means squared error)
		 * create global average error value to update each backProp() for isConverged() to check.
		 */

		/*BackProp should be possible by using Neuron IDs to go back through network and update weights
		 * based on closeness to true value. 
		 */

		//neuron.updateWeight(double w);
	}

	private void performanceMetricCheck()
	{
		//uses the withheld data to run through weighted network and check means squared error
	}

	private void print(NeuralNetwork n)
	{
		//To-Do: format print method to:
		//print neural network output, error, and number of runs
		//while testing print weights and error and maybe nodes (to make sure they initialized correctly)
	}

}
