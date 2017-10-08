package src;

import java.util.ArrayList;
import java.util.List;

public class Neuron 
{
	static boolean radialBiasActFun = false;	//set whichever to true for type of neural network
	static boolean MLFActFun = false;			//these are for the activation function
	static int counter = 0; 	//global neuron count
	public final int id;		//might need an individual node ID for backprop
	private ArrayList<Neuron> connections = new ArrayList<Neuron>();	//list of nodes this node is connected to (next layer of network)
	private List<Double> prevLayerOutputs = new ArrayList<Double>();	//This way neuron can receive previous layer's outputs dinamically and then compute summation later

	public static void main(String[] args) 
	{
		/*do weights go on the connections or on the nodes themselves?
		*if on nodes then add setWeight() and getWeight()
		*if not then do we need to store the connection and the weight of the connection?
		*/
	}

	public Neuron()
	{
		
		id = counter;	//start IDs at 0
		counter++;
	}
	
	public void addConnection(Neuron n)
	{
		connections.add(n);
		/* adding every connection to every node we create seems really tedious,
		 * even if it's just during initialization. Is there another way maybe?
		 */
	}
	
	public void addInput(double o)
	{
		prevLayerOutputs.add(o);		//receives and stores a previous layer's node's output
	}
	
	public double computeOutput()
	{
		//insert bias nodes here or handle directly in drive? Use bias nodes at all?
		double summation = 0;
		for(Double outs: prevLayerOutputs)
		{
			summation = summation + outs;			//perform summation of prevLayerOutputs
			//does the summation function have any special computations I'm forgetting?
		}
		double output = activationFun(summation);
		
		return output; //summation of all connections ran through activation function
	}
	
	public void updateWeight(double w)
	{
		//for backpropagation function in Driver
	}
	
	private double activationFun(double o)
	{
		//To-Do: code activation functions. Specify what kind in driver and update booleans in Neuron class variables
		if(radialBiasActFun)
		{
			//activation function here
		}
		else if(MLFActFun)
		{
			//activation function here
		}
		return o;
	}
	
}
