import java.awt.*;
import java.awt.geom.*;
import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;

public class Vehicle{
	public double pos_x, pos_y;						//Pixel coords
	public double yaw;						//Radians
	public double vel_x, vel_y;
	public double acc_x, acc_y;
	
	//public double rotation_vel;
	public double dist_to_checkpoint_x;		//Pixels
	public double dist_to_checkpoint_y;
	public int next_checkpoint_index;
	
	private List<Vehicle> vehicles;			//Pointer to list of all vehicles.
	private double width = 15;				//Pixels
	private double height = 15;
	
	// Constructor. Sets vars, creates a neural network, creates a genome based on that network,
	// then updates the network to have the same values as the genome.
	public Vehicle(double pos_x, double pos_y, double yaw){
		this.pos_x = pos_x;
		this.pos_y = pos_y;
		this.yaw = yaw;
		vel_x = 0;
		vel_y = 0;
		acc_x = 0;
		acc_y = 0;
		dist_to_checkpoint_x = 0;
		dist_to_checkpoint_y = 0;
		next_checkpoint_index = 0;
	}
	
	// Constructor. Copy another vehicle.
	public Vehicle(Vehicle v){
		this.pos_x = v.pos_x;
		this.pos_y = v.pos_y;
		this.yaw = v.yaw;
		this.vel_x = v.vel_x;
		this.vel_y = v.vel_y;
		this.acc_x = v.acc_x;
		this.acc_y = v.acc_y;
		this.dist_to_checkpoint_x = v.dist_to_checkpoint_x;
		this.dist_to_checkpoint_y = v.dist_to_checkpoint_y;
		this.next_checkpoint_index = v.next_checkpoint_index;
		
	}
	
	// Set track speeds based on internal logic.
	public void update(){
		double DIST_FAR = 120;
		double DIST_STABLE = 60;
		//double MAX_ROTATION_VEL = 3;
		double MAX_ACC_MAG = 3;
		//double MAX_PERPEN_VEL = 0.5;

		//vel_mag = 1;
		//vel_ang = 0;
		acc_x = 1;
		acc_y = 0;
		// Find k nearest neighbours.
		
		// Sum forces due to neighbours position.
		int ring_1 = 0;
		int ring_2 = 0;
		int ring_3 = 0;
		double total_x = 0;
		double total_y = 0;
		ListIterator<Vehicle> iter = vehicles.listIterator();
		while (iter.hasNext()){
			Vehicle v = iter.next();
			double dx = v.pos_x-pos_x;
			double dy = v.pos_y-pos_y;
			double dist = Math.sqrt(dx*dx+dy*dy);
			double ang = Math.atan2(dy,dx);
			
			if (dist == 0) continue;
			// Ring 1: Cohesion. Far away, meet at central location.
			if (dist > DIST_FAR){
				total_x += (dist-DIST_FAR)*Math.cos(ang);
				total_y += (dist-DIST_FAR)*Math.sin(ang);
				ring_1++;
			}
			// Ring 2: Alignment. Within stable ring, maintain position.
			if (dist <= DIST_FAR && dist > DIST_STABLE){
				ring_2++;
				total_x += 7.0;
			}
			// Ring 3: Separation. Too close, move away.
			if (dist <= DIST_STABLE){
				total_x -= 5*(DIST_STABLE-dist)*Math.cos(ang);
				total_y -= 5*(DIST_STABLE-dist)*Math.sin(ang);
				ring_3++;
			}
		}
		double scaler = ring_1+ring_3;
		if (scaler == 0) scaler=1;
		double total_force = Math.sqrt(total_x*total_x + total_y*total_y) / (scaler);
		double total_ang = Math.atan2(total_y, total_x);
		acc_x = 0.01 * total_force * Math.cos(total_ang);
		acc_y = 0.01 * total_force * Math.sin(total_ang);
		
		// Apply damping.
		acc_x -= Math.pow(Math.abs(vel_x),2)*vel_x/20;
		acc_y -= Math.pow(Math.abs(vel_y),3)*vel_y/20;
		
		// Cap accelerations.
		//if (acc_mag > MAX_ACC_MAG) acc_mag = MAX_ACC_MAG;
		//if (acc_mag < -MAX_ACC_MAG) acc_mag = -MAX_ACC_MAG;
		//System.out.println("vel: " + vel_mag+","+vel_ang);
		
		/*
		linear_vel = 1;
		rotation_vel = 0;
		double total_x = 0;
		double total_y = 0;
		double total_yaw = 0;
		double total_x_3 = 0;
		double total_y_3 = 0;
		int ring_1 = 0;
		int ring_2 = 0;
		int ring_3 = 0;
		ListIterator<Vehicle> iter = vehicles.listIterator();
		while (iter.hasNext()){
			Vehicle v = iter.next();
			if (v == this) continue;
			double dx = v.x-x;
			double dy = v.y-y;
			double dyaw = v.yaw - yaw;
			double dist = Math.sqrt(dx*dx+dy*dy);
			
			// Category 1: Cohesion. Far away, meet at central location.
			if (dist > DIST_FAR){
				total_x += dx;
				total_y += dy;
				ring_1++;
			}
			// Category 2: Alignment. Within stable ring, align yaw.
			if (dist <= DIST_FAR && dist > DIST_STABLE){
				total_yaw += dyaw;
				ring_2++;
			}
			// Category 3: Separation. Too close, move away.
			if (dist < DIST_STABLE){
				total_x_3 += dx;
				total_y_3 += dy;
				ring_3++;
			}
			
		}
		System.out.println("Rings: " + ring_1+","+ring_2+","+ring_3);
		// Category 1.
		double ring_1_prop = (double)ring_1 / (double)(ring_1+ring_2+ring_3);
		if (ring_1 > 0){
			total_x = total_x / ring_1;
			total_y = total_y / ring_1;
			double ang = Math.atan2(total_y, total_x);
			double dang = ang - yaw;
			rotation_vel += dang * ring_1_prop;
		}
		// Category 2.
		double ring_2_prop = (double)ring_2 / (double)(ring_1+ring_2+ring_3);
		if (ring_2 > 0){
			total_yaw = total_yaw / ring_2;
			rotation_vel += total_yaw * ring_2_prop;
		}
		// Category 3.
		double ring_3_prop = (double)ring_3 / (double)(ring_1+ring_2+ring_3);
		if (ring_3 > 0){
			total_x_3 = total_x_3 / ring_3;
			total_y_3 = total_y_3 / ring_3;
			double total_dist_3 = Math.sqrt(total_x_3*total_x_3+total_y_3*total_y_3);
			if (total_dist_3 != 0){
				double vel_x_3 = -total_x_3 / total_dist_3;
				double vel_y_3 = -total_y_3 / total_dist_3;
				double total_vel = Math.sqrt(vel_x_3*vel_x_3+vel_y_3*vel_y_3);
				double total_ang = Math.atan2(vel_y_3, vel_x_3);
				linear_vel += total_vel * Math.cos(total_ang-yaw);
				perpen_vel += total_vel * Math.sin(total_ang-yaw);
			}
			//double ang = Math.atan2(total_y_3, total_x_3);
			//double dang = ang - yaw;
			//rotation_vel += -1 * dang * ring_3_prop ;
		}
		
		// Cap velocities.
		if (rotation_vel > MAX_ROTATION_VEL) rotation_vel = MAX_ROTATION_VEL;
		if (rotation_vel < -MAX_ROTATION_VEL) rotation_vel = -MAX_ROTATION_VEL;
		if (linear_vel > MAX_LINEAR_VEL) linear_vel = MAX_LINEAR_VEL;
		if (linear_vel < -MAX_LINEAR_VEL) linear_vel = -MAX_LINEAR_VEL;	
		if (perpen_vel > MAX_PERPEN_VEL) perpen_vel = MAX_PERPEN_VEL;
		if (perpen_vel < -MAX_PERPEN_VEL) perpen_vel = -MAX_PERPEN_VEL;	
		//yaw = ang;*/
	}
	
	// Returns a shape, ready for displaying.
	public Shape getShape(){
		Ellipse2D.Double circle = new Ellipse2D.Double(pos_x, pos_y, width, height);
		return circle;
		
		/*Rectangle2D.Double rectangle = new Rectangle2D.Double(x, y, width, height);
		AffineTransform transform = new AffineTransform();
		transform.rotate(yaw, x + width/2.0, y + height/2.0);
		Shape transformed = transform.createTransformedShape(rectangle);
		return transformed;*/
	}
	
	// Returns the internal color of the shape.
	public Color getColor(){
		Color col = Color.BLACK;
		switch (next_checkpoint_index){
			case(0):
				col = Color.GRAY;
				break;
			case(1):
				col = Color.GREEN;
				break;
			case(2):
				col = Color.RED;
				break;	
			case(3):
				col = Color.BLUE;
				break;	
			case(4):
				col = Color.ORANGE;
				break;
			case(5):
				col = Color.CYAN;
				break;	
			case(6):
				col = Color.YELLOW;
				break;	
		}
		return col;
	}
	
	public void setVehicles(List<Vehicle> vehicles){
		this.vehicles = vehicles;
	}
}