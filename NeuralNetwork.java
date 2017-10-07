package src;

import java.util.ArrayList;


public class NeuralNetwork 
{
	int inputs;
	int hiddenL;
	int nodes;		//nodes by layer?
	int outputs;
	ArrayList<Neuron> inLayer = new ArrayList<Neuron>();
	ArrayList<Neuron> HiddenLayer = new ArrayList<Neuron>();	//make these dynamically
	ArrayList<Neuron> OutLayer = new ArrayList<Neuron>();
	/*use hashmap for connection lookup instead of double array? I think its scalable and allows
	*for null values so layer two could lookup its connections with layer one before there were any
	*weights available for layers 3+. Otherwise just multiple double arrays with -1 as null value? or 
	*wait isn't a connection weight of 0 the same as no connection?
	*/

	public static void main(String[] args) 
	{
		for (int i = 0; i < args.length; i++) 
		{
			//if we ask the user for inputs instead of making them command line inputs it may be easier
		}
		String in = args[0];
		String hid = args[1];
		String node = args[2];
		String out = args[3];
		//parse to ints and construct new neuralNet(in, hid, node, out);
	}
	
	public void neuralNet(int inputs, int hiddenL, int nodes, int outputs)
	{
		this.inputs = inputs;
		this.hiddenL = hiddenL;
		this.nodes = nodes;		//maybe pass in an array if by layer
		this.outputs = outputs;
		//To-Do: construct layers of Neurons
		//To-Do: begin computations Neuron by Neuron, layer by layer, to create output
		
		
	}
	
	private void generateData(int version)
	{
		//Rosenbrock function here
		if(version == 2)
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
			
		}
		else if(version ==6)
		{
			
		}
	}
	
	private void backProp()
	{
		//BackProp should be possible by using Neuron IDs to go back through network and update weights
		
	}

}
