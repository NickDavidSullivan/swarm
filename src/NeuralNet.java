import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.lang.Math;

public class NeuralNet {
	private static Random random = new Random();
	
	private int num_inputs;
	private int num_outputs;
	private int num_layers;
	private int num_neurons_per_layer;
	private double bias;
	private double sigmoid_response;
	private List<NeuronLayer> neuron_layers;
	
	public NeuralNet(){
		num_inputs = 6;		//x, y, yaw, distX, distY, ang
		num_outputs = 2;	//lwheel, rwheel
		num_layers = 2;
		num_neurons_per_layer = 6;
		bias = 0.5;
		sigmoid_response = 1.0;
		neuron_layers = new LinkedList<NeuronLayer>();
		createNet();
	}
	
	// Populates the 'neuron_layers' list.
	private void createNet(){
		if (num_layers > 1){
			// Create input layer.
			neuron_layers.add(new NeuronLayer(num_neurons_per_layer, num_inputs));
			// Create middle layers.
			for (int i=0; i < num_layers-2; i++){
				neuron_layers.add(new NeuronLayer(num_neurons_per_layer, num_neurons_per_layer));
			}
			// Create the output layer.
			neuron_layers.add(new NeuronLayer(num_outputs,num_neurons_per_layer));
		} else {
			// Create the only layer.
			neuron_layers.add(new NeuronLayer(num_outputs,num_inputs));
		}
	}
	
	// Given the inputs to the net, returns the outputs.
	public List<Double> update(List<Double> inputs){
		List<Double> outputs = new LinkedList<Double>();
		// Check size is correct.
		if (inputs.size() != num_inputs){
			System.out.println("Wrong sized inputs!");
			return outputs;
		}
		// Loop over each layer.
		for (int i=0; i<num_layers; i++){
			//System.out.println("Layer: " + i);
			NeuronLayer nl = neuron_layers.get(i);
			if (i>0) inputs = new LinkedList<Double>(outputs);
			outputs.clear();
			// Print out inputs
			/*ListIterator<Double> iter = inputs.listIterator();
			String str = "";
			while (iter.hasNext()){
				Double d = iter.next();
				str += String.format("%.03f ", d);
			}
			System.out.println(str); */
			// Loop over each neuron within a layer.
			for (int j=0; j<nl.getNumNeurons(); j++){
				//System.out.println("Neuron: " + j);
				Neuron n = nl.getNeuron(j);
				double sum = 0;
				// Add all the weights*inputs for a neuron.
				for (int k=0; k<n.getNumInputs()-1; k++){
					//System.out.println("Input: " + k);
					sum += n.getWeight(k) * inputs.get(k);
				}
				// Add the bias.
				sum += n.getWeight(n.getNumInputs()-1) * bias;
				outputs.add(sigmoid(sum, sigmoid_response));
			}
		}
		return outputs;
	}
	
	// Returns the total number of weights involved in this net.
	public int getNumWeights(){
		int sum = 0;
		for (int i=0; i<num_layers; i++){
			//System.out.println("Layer: " + i);
			NeuronLayer nl = neuron_layers.get(i);
			for (int j=0; j<nl.getNumNeurons(); j++){
				//System.out.println("Neuron: " + j);
				Neuron n = nl.getNeuron(j);
				sum += n.getNumInputs();
				//System.out.println("Sum: " + sum);
			}
		}
		return sum;
	}
	
	// Sets the weights in order input layer -> output layer. Includes bias.
	public void setWeights(List<Double> weights){
		int weight_index = 0;
		for (int i=0; i<num_layers; i++){
			NeuronLayer nl = neuron_layers.get(i);
			for (int j=0; j<nl.getNumNeurons(); j++){
				Neuron n = nl.getNeuron(j);
				int num_weights = n.getNumInputs();
				n.clearWeights();
				for (int k=0; k<num_weights; k++){
					n.addWeight(weights.get(weight_index));
					weight_index++;
				}
			}
		}
	}
	// Returns the sigmoid function.
	private double sigmoid(double activation, double response){
		return (1 / (1 + Math.exp(-1.0*activation / response)));
	}
	
	@Override
	public String toString(){
		String ret = "";
		int count = 0;
		ListIterator<NeuronLayer> iter = neuron_layers.listIterator();
		while (iter.hasNext()){
			ret += "Layer " + count + "\r\n";
			NeuronLayer nl = iter.next();
			for (int i=0; i<nl.getNumNeurons(); i++){
				ret += "Neuron " + i + "\r\n";
				Neuron n = nl.getNeuron(i);
				for (int j=0; j<n.getNumInputs(); j++){
					ret += "Weight " + j + ": " + String.format("%.03f",n.getWeight(j)) + "\r\n";
				}
			}
			count++;
		}
		return ret;
	}
	/**************************************************************************************
	 * Inner class, NeuronLayer. Creates and contains a number of neurons.
	 *************************************************************************************/
	 private class NeuronLayer {
		private List<Neuron> neurons;
		 
		public NeuronLayer(int num_neurons, int inputs_per_neuron){
			neurons = new LinkedList<Neuron>();
			for (int i=0; i<num_neurons; i++){
				neurons.add(new Neuron(inputs_per_neuron));
			}
		}
		 
		public int getNumNeurons(){
			return neurons.size();
		} 
		public Neuron getNeuron(int index){
			return neurons.get(index);
		}
	 }
	/**************************************************************************************
	 * Inner class, Neuron. If the inputs multiplied by their weights are greater than the 
	 * threshold, it is activated:
	 *		x1w1+x2w2+x3w3 >= t
	 * Rearranging, the threshold becomes another input with its own weighting (bias):
	 *		x1w1+x2w2+x3w3+w4t >= 0
	 *************************************************************************************/
	private class Neuron {
		private List<Double> weights;
		
		// Given the number of inputs, generates random weights and bias.
		public Neuron(int num_inputs){
			weights = new LinkedList<Double>();
			for (int i=0; i<num_inputs+1; i++){
				weights.add(new Double(random.nextDouble()*2 - 1));			//-1 to 1
			}
		}
		
		public int getNumInputs(){
			return weights.size();
		}
		
		public double getWeight(int index){
			return weights.get(index).doubleValue();
		}
		
		public void clearWeights(){
			weights.clear();
		}
		
		public void addWeight(double weight){
			weights.add(new Double(weight));
		}
	}
}