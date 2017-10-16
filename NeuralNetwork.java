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
    int nodes;		//nodes by layer?
    int outputs;
    ArrayList<Neuron> inLayer = new ArrayList<Neuron>();
    ArrayList<Neuron> hiddenLayer = new ArrayList<Neuron>();	//make these dynamically
    ArrayList<Neuron> outLayer = new ArrayList<Neuron>();
    Double[][] weights = null;			//weights[i][j] = weight at connection node i to node j
    Double[][] prevDeltaWeights = null;
    Double[] networkOutput;
    double[] expectedOutputs;
    double learningRate = .5;			//CHANGE THIS to a real learning rate!
    int error;
    double aveError;
    int maxRuns = 3000;
    double minError = 0.0001;
    ArrayList<Double>[] inputVector;
    ArrayList<Double> output = new ArrayList<Double>();
    String pathToData = "C:\\Users\\jadin_000\\Documents\\DS-5 Workspace\\NeuralNetwork\\src\\";        //Change this for your machine.

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
			//if we ask the user for inputs instead of making them command line inputs it may be easier
            //also ask for what kind of Neural network this is
        }
        String in = args[0];
        String hid = args[1];
        String node = args[2];
        String out = args[3];
		Neuron.radialBiasActFun = true;		//if Radial Basis network
        //Neuron.MLFActFun = true; 			//if MLF network
        NeuralNetwork net = new NeuralNetwork(Integer.parseInt(in), Integer.parseInt(hid), Integer.parseInt(node), Integer.parseInt(out));
        net.loadData(Integer.parseInt(in));
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

        for (int i = 0; i < hiddenL + 2; i++) //construct layer by layer
        {
            if (i == 0) //input layer
            {
                for (int j = 0; j < inputs; j++) {
                    //inputs form Rosenbrock function
                    Neuron n = new Neuron();
                    //n.addInput(0);		//add inputs in train function
                    inLayer.add(n);
                }
            } else if (i > 0 && i < hiddenL || i == 1) //hidden layers here. If just 1 then construct this and an output layer
            {
                for (int j = 0; j < nodes; j++) {
                    Neuron n = new Neuron();
                    hiddenLayer.add(n);		//I think we will actually need multiple hidden layer ArrayLists. One for each hidden layer. This may need to be changed. Although that would meen deep learning which we don't have to do

                }
            }
            if (i == hiddenL || i == 1) //output layer, will be constructed even if just one hidden layer
            {
                for (int j = 0; j < outputs; j++) {
                    Neuron n = new Neuron();
                    outLayer.add(n);
                }
            }

            //Add bias node to each layer
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

	private boolean isConverged()
	{
		if(maxRuns <= 0)
		{
			if(aveError <= minError)
			{
				System.out.println("Hey the Neural Network converged on its last run!");
				converged = true;
				print(this);
			}
			else
			{
				System.out.println("Max runs reached; Neural Network did not converge.");
				converged = true;		//so that training stops
				print(this);
			}
		}
		else
		{
			if(aveError <= minError)
			{
				System.out.println("Neural Network converged");
				converged = true;
				print(this);
			}
		}
		maxRuns = maxRuns - 1;
		//oh and overfitting
		//a large enough difference in the aveError and testing data error based on acceleration of the difference

		return converged;
	}

    private void train(NeuralNetwork net) {
    	int run = 0;
    	double[] networkOutput = new double[outLayer.size()];
        while (!converged) //train network
        {
        	
            expectedOutputs = new double[inputs];

            error = 0;
            int j = 0;
            double[] temp = new double[inputs];
            for(int i = 0; i < inputs; i++)
            {
            	System.out.println("i: " + i);
            	System.out.println("run: " + run);
            	temp[i] = inputVector[i].get(run);
            }
            System.out.println("run: " + run);
            run++;
            for (Neuron n : inLayer) 
            {
                double ros = temp[j];		//change to rosenbrock function outputs
                n.addInput(ros);
                double o = n.computeOutput();
                
                int k = inLayer.size() - 1;		//first hidden layer node
                for (Neuron h : hiddenLayer) //add to all nodes in next layer
                {
                	double w = weights[j][k];
                	o = o * w;
                    h.addInput(o);
                    k++;
                }
                j++;
            }

            j = inLayer.size() - 1;
            for (Neuron n : hiddenLayer) 
            {
            	int k = inLayer.size() + hiddenLayer.size() -1; 	//first output layer node
                double hiddenOut = n.computeOutput(); 	// Then tell the neuron to compute its output
                
                for (Neuron o: outLayer) 
                {
                	double w = weights[j][k];
                	hiddenOut = hiddenOut * w;
                    o.addInput(hiddenOut);				//Then pass (output * weight) into the inputs of the next layer nodes
                    k++;
                }
                j++;
            }
            
            
            int iter = 0;
            for (Neuron o : outLayer) {
                double out = o.computeOutput();
                networkOutput[iter] = out;
                expectedOutputs[iter] = output.get(iter);
                
                System.out.println("computedOutput: " + out);
                System.out.println("expectedOut: " + expectedOutputs[iter]);
                double e = Math.pow(out - expectedOutputs[iter], 2);
                System.out.println("e: " + e);
                error += e;		//for sum of output errors
                iter++;
            }
            System.out.println("error: " + error);
            System.out.println("outSize: " + outLayer.size());
            aveError = (error/(outLayer.size()));

            if (isConverged()) //at end of each iteration check if converged.
            {
                break; 		//no need to backprop
            }

            backProp();
        }
        performanceMetricCheck(); 	//add this method
        //print(this);

    }

    /**
     * Weight initialization method for weight array.
     */
    private void initRandomWeights() {
        //update the hashmap/weights table to random values
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


    private void backProp() {
        int i = inLayer.size() - 1; 	//first hidden neuron
        int j = inLayer.size() + hiddenLayer.size() - 1; //first output layer neuron
        int k = 0;
        for (Neuron n : outLayer) {
            for (Neuron h : hiddenLayer) //for every output neuron, for every in-connection: int k = 0; k < hiddenLayer.size(); k++
            {
                double bo = n.NeuronOutput;
                double bh = h.NeuronOutput;
                double bOutput = expectedOutputs[k];	//make sure expected outputs are working

                double bPartialDerivative = -bo * (1 - bo) * bh * (bOutput - bo);
                double bDeltaWeight = -learningRate * bPartialDerivative;

                double bNewWeight = weights[i][j] + bDeltaWeight;
                weights[i][j] = bNewWeight * prevDeltaWeights[i][j];		//for momentum, add m to bNewWeight

                prevDeltaWeights[i][j] = bDeltaWeight;
                i++;
            }
            i = inLayer.size() - 1;
            j++;
            k++;
        }

        i = 0;
        j = inLayer.size() - 1; //first hidden neuron
        k = 0;
        for (Neuron n : hiddenLayer) {
            for (Neuron in : inLayer) {
                double bh = n.NeuronOutput;
                double bi = in.NeuronOutput;
                double bSumOutputs = 0;
                int l = inLayer.size() + hiddenLayer.size() - 1;	//first output node
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
                weights[i][j] = bNewWeight * prevDeltaWeights[i][j];
                prevDeltaWeights[i][j] = bDeltaWeight;
                k = 0;
                i++;
            }
            j++;
        }
    }

    private void performanceMetricCheck() {
        //uses the held out data to run through weighted network and check means squared error
    }

    private void print(NeuralNetwork n)
	{
		printWeights();
		System.out.println("Outputs: " + networkOutput[0]);
		System.out.println("Error: " + aveError);
		
		//print number of runs
	}
	
	private void printWeights()
	{
		int i = 0;
		System.out.println("Input Layer to Hidden Layer:");
		for(i = 0; i < inLayer.size();i++)
		{
			System.out.print("Node " + i + ": ");
			double[] ws = new double[inLayer.size()];
			for(int j = 0; j < hiddenLayer.size(); j++)
			{
				ws[j] = weights[i][j];
				System.out.print(" " + ws[j]);
			}
			
		}
		
		System.out.println("Hidden Layer to Output Layer:");
		for(i = inLayer.size() + 1; i < hiddenLayer.size();i++)
		{
			double[] ws = new double[hiddenLayer.size()];
			System.out.print("Node " + i + ": ");
			for(int j = hiddenLayer.size() + 1; j < outLayer.size(); j++)
			{
				ws[j] = weights[i][j];
				System.out.print(" " + ws[j]);
			}
			System.out.println("");
			
		}
	}

}
