package src;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.Math;
import java.util.List;
import java.util.Random;

public class NeuralNetwork {

	boolean converged = false;
	int inputs;
	int hiddenL;
	int nodes;		
	int outputs;
	ArrayList<Neuron> inLayer = new ArrayList<Neuron>();
	ArrayList<Neuron> hiddenLayer = new ArrayList<Neuron>();	
	ArrayList<Neuron> outLayer = new ArrayList<Neuron>();
	Double[][] weights = null;			//weights[i][j] = weight at connection node i to node j
	Double[][] prevDeltaWeights = null;
	double[] networkOutput;
	double[] expectedOutputs;
	double learningRate = .7;		//tunable	
	int error;
	double aveError;
	int maxRuns = 300000 - 1;		//parameter for max runs allowed
	double minError = 0.0001;		//parameter for minimum acceptable error
	int pIterator = 0;
	double lastHunError = 0;
	int trainingDataStart;
	ArrayList<Double>[] inputVector;
	ArrayList<Double> output = new ArrayList<Double>();
	String pathToData = "C:\\Users\\jadin_000\\Documents\\DS-5 Workspace\\NeuralNetwork\\src\\";        //Change this for your machine.

	public static void main(String[] args) {
		String in = args[0];
		String hid = args[1];
		String node = args[2];
		String out = args[3];
		Neuron.radialBiasActFun = true;		//set to true if Radial Basis network
		//Neuron.MLFActFun = true; 			//set to true if MLF network
		NeuralNetwork net = new NeuralNetwork(Integer.parseInt(in), Integer.parseInt(hid), Integer.parseInt(node), Integer.parseInt(out));
		net.loadData(Integer.parseInt(in));
		net.train(net);
	}

	public NeuralNetwork(int inputs, int hiddenL, int nodes, int outputs) 
	{
		this.inputs = inputs;
		this.hiddenL = hiddenL;		//a 3 layer network would have a hiddenL input value of 1
		this.nodes = nodes;			
		this.outputs = outputs;		//just one output for this project's implementation of the code.


		for (int i = 0; i < hiddenL + 2; i++) //construct layer by layer
		{
			if (i == 0) //input layer
			{
				for (int j = 0; j < inputs; j++) {
					Neuron n = new Neuron();
					inLayer.add(n);
				}
			} else if (i > 0 && i < hiddenL || i == 1) //hidden layers here. If just 1 then construct this and an output layer
			{
				for (int j = 0; j < nodes; j++) {
					Neuron n = new Neuron();
					hiddenLayer.add(n);		

				}
			}
			if (i == hiddenL || i == 1) //output layer, will be constructed even if just one hidden layer
			{
				for (int j = 0; j < outputs; j++) {
					Neuron n = new Neuron();
					outLayer.add(n);
				}
			}

		}
		//add next connections to next layer:
		for (Neuron n : inLayer) {
			for (int i = 0; i < hiddenLayer.size(); i++) {
				n.addConnection(hiddenLayer.get(i));
			}
		}
		for (Neuron n : hiddenLayer) {
			for (int i = 0; i < outLayer.size(); i++) {
				n.addConnection(outLayer.get(i));
			}
		}
		initRandomWeights();
	}

	//Loads the dataset from the correct csv file. You must adjust "pathToData" instance variable above
	public void loadData(int dimension) {
		this.inputVector = new ArrayList[dimension];
		for (int i = 0; i < dimension; i++) {
			this.inputVector[i] = new ArrayList<Double>();
		}
		String fileToLoad = "";
		switch (dimension) {
		case 2:
			fileToLoad = "2dData.tsv";
			break;
		case 3:
			fileToLoad = "3dData.tsv";
			break;
		case 4:
			fileToLoad = "4dData.tsv";
			break;
		case 5:
			fileToLoad = "5dData.tsv";
			break;
		case 6:
			fileToLoad = "6dData.tsv";
			break;
		}
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = "\\s";
		try {
			br = new BufferedReader(new FileReader(pathToData + fileToLoad));
			while ((line = br.readLine()) != null) {
				// use tab as separator
				String[] values = line.split(cvsSplitBy);
				for (int i = 0; i < values.length - 1; i++) {
					inputVector[i].add(Double.parseDouble(values[i]));  //Variable length of input vector size is loaded into the 2d arraylist inputVector.
				}
				output.add(Double.parseDouble(values[values.length - 1]));      //This is the last value in csv, so it is the funciton target output.
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//method to check if neural network converges
	private boolean isConverged()
	{
		if(maxRuns <= 0)
		{
			if(performanceMetricCheck())	//checks network against held out data
			{
				System.out.println("------------------");
				System.out.println("Hey the Neural Network converged on its last allowed run!");	//its unlikely
				converged = true;	//this stops the network from training
				print(this);
			}
			else
			{
				System.out.println("------------------");
				System.out.println("Max runs reached; Neural Network did not converge.");
				converged = true;		//so that training stops
				print(this);
			}
		}
		else
		{
			if(performanceMetricCheck())
			{
				System.out.println("------------------");
				System.out.println("Neural Network converged");
				converged = true;
				print(this);
			}
		}
		maxRuns = maxRuns - 1;


		return converged;
	}

	//until convergence: adds inputs into neurons, runs network, then checks output against expected output
	private void train(NeuralNetwork net) {
		int run = 0;
		networkOutput = new double[outLayer.size()];
		int trainingIter = 0;
		int maxTrainingIter = output.size() - ( (int)(output.size() / 5) * 4);
		trainingDataStart = maxTrainingIter + 1;
		System.out.println("maxTrain: " + maxTrainingIter);
		while (!converged) //train network
		{
			System.out.println("run: " + run);
			error = 0;
			int j = 0;
			int tempi = 0;
			double[] temp = new double[inputs];
			System.out.print("inputs:");
			for(int i = 0; i < inputs; i++)
			{
				temp[i] = inputVector[i].get(trainingIter);
				System.out.print(" " + temp[i]);
			}
			tempi++;
			System.out.println("");
			
			//normalize vectors before inputting into Neurons
			double norm = 0;
			for(int i = 0; i < temp.length; i++)
			{
				norm = norm + (temp[i] * temp[i]);
			}
			norm = Math.sqrt(norm);
			for(int i = 0; i < temp.length; i++)
			{
				temp[i] = temp[i]/norm;
			}

			//begin computations
			for (Neuron n : inLayer) 
			{
				double ros = temp[j];		
				n.addInput(ros);
				double o = n.computeOutput();
				if(o == 0.0)
				{
					o = .00001;
				}

				int k = inLayer.size();		//first hidden layer node arraylist location
				for (Neuron h : hiddenLayer) //add output to all nodes in next layer
				{
					double w = weights[j][k];
					o = o * w;
					h.addInput(o);
					k++;
				}
				j++;
			}

			j = inLayer.size();
			for (Neuron n : hiddenLayer) 
			{
				int k = inLayer.size() + hiddenLayer.size(); 	//first output layer node
				double hiddenOut = n.computeOutput(); 	// Then tell the neuron to compute its output
				if(hiddenOut == 0.0)
				{
					hiddenOut = .00001;
				}

				for (Neuron o: outLayer) 
				{
					double w = weights[j][k];
					hiddenOut = hiddenOut * w;
					o.addInput(hiddenOut);	
					k++;
				}
				j++;
			}

			expectedOutputs = new double[outLayer.size()];
			int iter = 0;
			for (Neuron o : outLayer) {
				double out = o.computeOutput();
				networkOutput[iter] = out;
				expectedOutputs[iter] = output.get(trainingIter);
				System.out.println("computedOutput: " + out);
				System.out.println("expectedOut: " + expectedOutputs[iter]);
				double e = Math.pow(out - expectedOutputs[iter], 2);
				System.out.println("e: " + e);
				error += e;		//for sum of output errors
				iter++;
			}
			run++;
			aveError = (error/(outLayer.size()));

			if (isConverged()) //at end of each iteration check if converged.
			{
				break; 		//no need to backprop
			}
			
			backProp();
			trainingIter++;
			if(trainingIter >= maxTrainingIter)
			{
				trainingIter = 0;
			}
			System.out.println("");
		}

		performanceMetricCheck(); 	

	}

	/*
	 * Weight initialization method for weight array.
	 */
	private void initRandomWeights() {
		int dim = inLayer.size() + hiddenLayer.size() + outLayer.size();
		this.weights = new Double[dim][dim];
		this.prevDeltaWeights = new Double[dim][dim];
		double randUpperBound = Math.sqrt(6 / (inLayer.size() + outLayer.size()));      // according to https://stats.stackexchange.com/questions/47590/what-are-good-initial-weights-in-a-neural-network
		double randLowerBound = randUpperBound * -1;
		Random rand = new Random();
		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {
				double holder = rand.nextDouble();
				while (holder == 0) {
					holder = rand.nextDouble();
				}
				this.weights[i][j] = randLowerBound + (randUpperBound - randLowerBound) * holder;
				this.prevDeltaWeights[i][j] = 1.0;
			}
		}
	}


	//backpropigation algorithm
	private void backProp() {
		int i = inLayer.size(); 	//first hidden neuron
		int j = inLayer.size() + hiddenLayer.size(); //first output layer neuron
		int k = 0;
		for (Neuron n : outLayer) {
			for (Neuron h : hiddenLayer) //for every output neuron, for every in-connection: 
			{
				double bo = n.NeuronOutput;
				double bh = h.NeuronOutput;
				double bOutput = expectedOutputs[k];

				double bPartialDerivative = -bo * (1 - bo) * bh * (bOutput - bo);
				double bDeltaWeight = -learningRate * bPartialDerivative;

				double bNewWeight = weights[i][j] + bDeltaWeight;
				double temp = bNewWeight * prevDeltaWeights[i][j];
				if(temp == 0.0)
				{
					temp = 0.0001;
				}
				if(temp == 1.0)
				{
					temp = 0.99999;
				}
				weights[i][j] = temp;		//update weight

				prevDeltaWeights[i][j] = bDeltaWeight;
				i++;
			}
			i = inLayer.size();
			j++;
			k++;
		}

		i = 0;
		j = inLayer.size(); //first hidden neuron
		k = 0;
		for (Neuron n : hiddenLayer) {
			for (Neuron in : inLayer) {
				double bh = n.NeuronOutput;
				double bi = in.NeuronOutput;
				double bSumOutputs = 0;
				int l = inLayer.size() + hiddenLayer.size();	//first output node
				for (Neuron o : outLayer) {
					double bw = weights[j][l];
					double bOutput = expectedOutputs[k];
					double bo = o.NeuronOutput;
					bSumOutputs = bSumOutputs + (-(bOutput - bo) * bo * (1 - bo) * bw);
					k++;
				}
				double bPartialDerivative = bh * (1 - bh) * bi * bSumOutputs;
				double bDeltaWeight = -learningRate * bPartialDerivative;
				double bNewWeight = weights[i][j] + bDeltaWeight;
				double temp = bNewWeight * prevDeltaWeights[i][j];
				if(temp == 0.0)
				{
					temp = 0.0001;
				}
				if(temp == 1.0)
				{
					temp = 0.99999;
				}
				weights[i][j] = temp;		//update weight
				prevDeltaWeights[i][j] = bDeltaWeight;
				k = 0;
				i++;
			}
			j++;
		}
	}

	//checks network on held out data
	private boolean performanceMetricCheck() {
		lastHunError += Math.pow(networkOutput[0] - expectedOutputs[0], 2);
		double pe = lastHunError/2.0;

		if(pIterator >= 100)
		{
			lastHunError = .2;
			pIterator = 0;
		}
		pIterator++;
		if(lastHunError <= minError)
		{
			return true;
		}else{
			return false;
		}
	}

	//prints important and relative neural network data
	private void print(NeuralNetwork n)
	{
		printWeights();
		if(networkOutput != null)
		{
			System.out.println("Expected Out: " + expectedOutputs[0]);
			System.out.println("Outputs: " + networkOutput[0]);
			System.out.println("Error: " + aveError);
		}

	}

	//weight printing method
	private void printWeights()
	{
		int i = 0;
		System.out.println("Input Layer to Hidden Layer weights:");
		for(i = 0; i < inLayer.size();i++)
		{
			System.out.print("Node " + i + ": ");
			double[] ws = new double[inLayer.size()];
			for(int j = 0; j < hiddenLayer.size(); j++)
			{
				ws[j] = weights[i][j];
				System.out.print(" " + ws[j]);
			}
			System.out.println("");
		}
		System.out.println("");

		System.out.println("Hidden Layer to Output Layer weights:");
		for(i = inLayer.size() ; i < hiddenLayer.size() + inLayer.size();i++)
		{
			double[] ws = new double[hiddenLayer.size()];
			System.out.print("Node " + i + ": ");
			int k = 0;
			for(int j = hiddenLayer.size() + inLayer.size(); j < inLayer.size() + hiddenLayer.size() + outLayer.size(); j++)
			{
				ws[k] = weights[i][j];
				System.out.print(" " + ws[k]);
			}
			k++;
			System.out.println("");

		}
		System.out.println("");
	}

}
