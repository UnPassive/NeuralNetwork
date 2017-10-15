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
	public double NeuronOutput;
	public static void main(String[] args) 
	{
		/*
		*add setWeight() and getWeight()
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
		double summation = 0;
		for(Double outs: prevLayerOutputs)
		{
			summation = summation + outs;			//add weights logic to addInput? or conputeOutput?
			//fix this to match actual summation function
		}
		double output = activationFun(summation);
		NeuronOutput = output; 
		return output; //summation of all connections ran through activation function
	}
	
	
	private double activationFun(double o)
	{
		//To-Do: code activation functions. Specify what kind in driver and update booleans in Neuron class variables
		
		//if output layer, activation function has to be linear
		if(radialBiasActFun)
		{
			//activation function here
                    
                    //gaussians calc with vectors?
                    //cluster and change how stuff is passed for RBF?
                        //exp((-||x-xj||^2)/2sigma^2))
                        //xj is vector representing the funtion center
                    
                        double s=0; //max pairwise distance between clusters(?)/squareroot of number of clusters*2
                        //large sigma might cause data to become linear
                        //s could be T times the distance between node and neighbor where T=[1,1.5]
                        
                        double c=0; //What would be center? https://link.springer.com/chapter/10.1007/11548706_19 
                        //does he really want us to create another learning method to find centers? he didnt cover centers in class at all
                        //http://www.ieee.cz/knihovna/Zhang/Zhang100-ch03.pdf paper on clustering we could use (published officially)
                        
                        o=Math.exp(-Math.pow(o-c,2)/(2*Math.pow(s, 2)));
		}
		else if(MLFActFun)
		{
			//activation function here
                        //tanh = (2/(1+e^-2x))-1
                        //or e^(2x)-1/e^(2x)+1
                        //produce same output
                    //o=(Math.exp(2*o)-1)/(Math.exp(2*o)+1);
                    o=(2/(1+Math.exp(-2*o)))-1; 
                    //double outDeriv=1-Math.pow(o,2); kept hearing we need derivative so here it is just in case
                    
		}
		return o;
	}
	
}
