import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;
import java.lang.Math;

public class Main {
	private static final int NUM_VEHICLES = 20;
	private static Random random = new Random();
	
	public static void main(String[] args){
		// Create lists to hold the vehicles and checkpoints.
		List<Vehicle> vehicles = new LinkedList<Vehicle>();
		List<Checkpoint> checkpoints = new LinkedList<Checkpoint>();
		Object mutex_lock = new Object();
		
		// Create and add neural networks and vehicles.
		for (int i=0; i<NUM_VEHICLES; i++){
			Vehicle vehicle = new Vehicle(random.nextInt(500), random.nextInt(500), 0);//Math.PI/4.0);
			vehicles.add(vehicle);
			vehicle.setVehicles(vehicles);			//Pointer to list.
		}
		
		// Create and add the checkpoints.
		//checkpoints.add(new Checkpoint(200,300));
		
		// Create the environment to hold and update the vehicles and checkpoints.
		Environment environment = new Environment(vehicles, checkpoints, mutex_lock);
		
		// Display the environment.
		Display display = new Display(environment, mutex_lock);
		
		try {
			Thread.sleep(1000);
		} catch (Exception e){};
			
		// Continue to update.
		for (int i=0; i<1000; i++){
			// Run decision making for vehicles to set vehicle speeds.
			ListIterator<Vehicle> iter = vehicles.listIterator();
			while (iter.hasNext()){
				iter.next().update();
			}
			
			// Update the environment.
			environment.update(1);
			display.canvasRepaint();
			try {
				Thread.sleep(20);
			} catch (Exception e){};
			
			
		}
		
		
		
		// Repeat for generations.
		/*for (int gen=0; gen<NUM_GENERATIONS; gen++){
			System.out.println("Generation " + gen);
			try {
				Thread.sleep(1000);
			} catch (Exception e){};
			
			// Set the distances to checkpoints.
			environment.update(0.0);
			display.canvasRepaint();
			// Start the run
			for (int i=0; i<TICKS_PER_RUN; i++){
				// Run inputs through the neural network to get vehicle speeds.
				ListIterator<Vehicle> iter = vehicles.listIterator();
				while (iter.hasNext()){
					iter.next().useNeuralNetwork();
				}
				// Update the environment.
				environment.update(1);
				display.canvasRepaint();
				try {
					Thread.sleep(2);
				} catch (Exception e){};
			}
			if (gen < NUM_GENERATIONS-1){
				// Move onto the next generation.
				vehicles = geneAlgorithm.nextGeneration(NUM_FIT, NUM_VEHICLES);
				environment.setVehicles(vehicles);
				display.canvasRepaint();
				
				// Move vehicles back to start point.
				environment.moveVehiclesToStart();
			}
		}
		
		// Final run.
		System.out.println("Result.");
		//Display display = new Display(environment, mutex_lock);
		
		vehicles = geneAlgorithm.nextGeneration(1, 1);
		environment.setVehicles(vehicles);
		Vehicle v = vehicles.get(0);
		Genome g = v.getGenome();
		double max_fitness = g.getMaxFitness();
		environment.moveVehiclesToStart();
		environment.update(0.0);
		display.canvasRepaint();
		while (g.getMaxFitness() < max_fitness){
			v.useNeuralNetwork();
			environment.update(1);
			display.canvasRepaint();
			try {
				Thread.sleep(2);
			} catch (Exception e){};
		}*/
	}
}