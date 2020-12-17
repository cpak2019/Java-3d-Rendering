
//Made by Christian Pak
//Version 1.00
import java.io.*;
import java.awt.Color;
import java.util.*;

public class thirdDObj {

	// READ ME
	// main functions, documentation and usage

	// creating a 3d object:
	// thirdDObj name = new thirdDObj(Int numberOfVertecies, String "nameOfTxt file", int[][] startPostionOf3dObject);
		// where starting position array is in the form: float[][] startPos = { { x_i,
		// y_i, z_i } };
		// note you are initially orientated looking along the x axis in the + direction
		// when angle = 0

	// updating force of 3d object to effect motion:
	// setforce(float[][] force);
		// where force array is in the form: float[][] startPos = { { f_x, f_y, f_z } };

	// update and draw the 3d object
	// 3dObj'sName.updatePosition(float angle, float[][] campos);
		// angle is the angle of the camera from the x axis in radians
		// note camera position array is in the form: float[][] startPos = { { x, y, z } }
		// note you should still call EZ.refreshScreen() after executing this function.

	//getVectors()
		//returns a float[][] array containing the position, velocity, and force vectors 
	
	//getRotation()
		// returns rotation about the z axis of the object (float)
	
	//setRotation(float)
		//as I didn't program torque a way to set the rotation about the z axis of the object
	
	// note units are generally Imperial and rotation measured in radians unless
	// specified

	// var to edit
	private float mass = 1;
	// for spherical coordinates and projection onto a plane
	private static int fov = 135; // in degrees
	private double c; // distance to the plane of view (it makes sense to me)
	private float maxX = 240; // number of pixels in x direction
	private int maxY = 180;

	// END of READ ME

	// create array for center velocity,
	// array for rotation in both direction
	// type of object (static or moveing)
	// other properties of object
	// if angle is stored in a float it in radians, if its stored in an int its in
	// degrees
	static float GRAVITY = 9.8f;
	static float zero[][] = { { 0 }, { 0 } };
	static float pi = 3.141592f;
	private float[][] centWorldPos = new float[3][3]; // [0] center World Position, [1] center velocity, [2] forces
	private float[][] normals;
	private float[][] normalsRot;
	private float yaw; // (angle)
	private int scale;
	private long time = System.currentTimeMillis();
	private long changeInTime = 0;
	// create image variables outside of main so they can be called in functions.
	// units are in meters for all cordinates stored in floats,
	// if stored in int the units are in cm
	// your assumed to be 1.8m tall
	private int numberOfVertex;
	private int numberOfFaces;
	private int[][] faces;
	private float[][] Obj1LocalVertex;
	private int[][] TranslatedPos;
	private int[][] display; // stored as x in 0 and y in 1
	private int[][] prevDisplay;
	private boolean drawFace;
	private polygon drawer;
	private ArrayList<EZPolygon> faceList = new ArrayList<EZPolygon>(1);
	private EZPolygon temp;
	private Color[] colour;

	public thirdDObj(int vertexNum, String filename, float[][] startPos) throws java.io.IOException {
		// loading initial position and loading stl data
		numberOfVertex = vertexNum;
		numberOfFaces = faceCounter(filename);
		Obj1LocalVertex = new float[numberOfVertex][5];
		TranslatedPos = new int[numberOfVertex][3];
		display = new int[numberOfVertex][2];
		prevDisplay = new int[numberOfVertex][2];
		faces = new int[numberOfFaces][3];
		normals = new float[numberOfFaces][3];
		normalsRot = new float[numberOfFaces][3];
		centWorldPos[0][0] = startPos[0][0];
		centWorldPos[0][1] = startPos[0][1];
		centWorldPos[0][2] = startPos[0][2];
		c = (maxX / 2) / Math.tan(fov * pi / 360);
		stlReader(Obj1LocalVertex, filename);
		drawer = new polygon(numberOfVertex, 5);
		time = System.currentTimeMillis();
		drawFace = false;
		colour = new Color[numberOfFaces];
//		EZ.setFrameRateASAP(true);
	}

	// CHANGE LATER load using array lists then transfer to array for better
	// efficiency so it can count number of faces
	// loads vertex and face data from an stl file into arrays
	private void stlReader(float store[][], String filename) throws java.io.IOException {
		Scanner scanner;
		try {
			// code template from https://www.journaldev.com/867/java-read-text-file, then
			// modified
			int counter = 0;
			int faceCounter = 0;
			int check = 0;
			scanner = new Scanner(new File(filename));
			// Enter this while loop if the scanner has more text to read
			while (scanner.hasNextLine()) {
				// Read 1 word (that is delimited / separated by a space)

				String line = scanner.nextLine();
				String delims = "[ ]+";
				String[] tokens = line.split(delims);

				if (tokens[0].equals("facet")) {
					normals[faceCounter / 3][0] = Float.valueOf(tokens[2]);
					normals[faceCounter / 3][1] = Float.valueOf(tokens[3]);
					normals[faceCounter / 3][2] = Float.valueOf(tokens[4]);
				}
				// assumes faces are triangles
				if (tokens[0].equals("vertex")) {
					check = checkForDuplicateVertex(tokens, store, counter);
					if (check == -1) {
						store[counter][0] = Float.valueOf(tokens[1]);
						store[counter][1] = Float.valueOf(tokens[2]);
						store[counter][2] = Float.valueOf(tokens[3]);
						faces[faceCounter / 3][(faceCounter % 3)] = counter;
						counter++;

					} else {
						faces[faceCounter / 3][(faceCounter % 3)] = check;
					}
					faceCounter++;
				}
			}
//			printArrayf(normals);
			scanner.close();
//			System.out.print("done");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private int faceCounter(String filename) throws java.io.IOException {
		Scanner scanner;
		try {
			// code template from https://www.journaldev.com/867/java-read-text-file, then
			// modified
			int counterface = 0;
			scanner = new Scanner(new File(filename));
			// Enter this while loop if the scanner has more text to read
			while (scanner.hasNextLine()) {
				// Read 1 word (that is delimited / separated by a space)

				String line = scanner.nextLine();
				String delims = "[ ]+";
				String[] tokens = line.split(delims);

				if (tokens[0].equals("facet")) {
					counterface++;
				}

			}
//			printArrayf(normals);
			scanner.close();
//			System.out.print("done");
			return counterface;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	// checks if vertex has already been recorded
	private static int checkForDuplicateVertex(String vertex[], float storage[][], int length) {
		int check = 0;

		for (int i = 0; i < length + 1; i++) {
			for (int j = 0; j != 3; j++) {
				if (storage[i][j] == Float.valueOf(vertex[j + 1])) {
					check++;
				}
			}
			if (check == 3) {
				return i;
			} else {
				check = 0;
			}
		}
		return -1;
	}

//	private void stLcounter(String filename) throws java.io.IOException {
//		ArrayList<Integer> facesL = new ArrayList<Integer>();
//		ArrayList<Float> verteciesL = new ArrayList<Float>();
//
//		Scanner scanner;
//		try {
//			// code template from https://www.journaldev.com/867/java-read-text-file, then
//			// modified
//			int counter = 0;
//			int faceCounter = 0;
//			int check = 0;
//			scanner = new Scanner(new File(filename));
//			// Enter this while loop if the scanner has more text to read
//			while (scanner.hasNextLine()) {
//				// Read 1 word (that is delimited / separated by a space)
//
//				String line = scanner.nextLine();
//				String delims = "[ ]+";
//				String[] tokens = line.split(delims);
//
////				if (tokens[0].equals("facet")) {
////					normals[faceCounter / 3][0] = Float.valueOf(tokens[2]);
////					normals[faceCounter / 3][1] = Float.valueOf(tokens[3]);
////					normals[faceCounter / 3][2] = Float.valueOf(tokens[4]);
////				}
//
//				if (tokens[0].equals("vertex")) {
//					check = checkForDuplicateVertexmod(tokens, verteciesL, counter);
//					if (check == -1) {
//						verteciesL.add(new Float (Float.valueOf(tokens[1])));
//						verteciesL.add(new Float (Float.valueOf(tokens[2])));
//						verteciesL.add(new Float (Float.valueOf(tokens[3])));
//						facesL.add(new Integer(counter));
//						counter++;
//
//					} else {
//						facesL.add(new Integer(check));
//					}
//					faceCounter++;
//				}
//			}
////			printArrayf(normals);
//			scanner.close();
////			System.out.print("done");
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//		private static int checkForDuplicateVertexmod(String vertex[], ArrayList<Float> verteciesCheck, int length) {
//			int check = 0;
//			for (int i = 0; i < verteciesCheck.size() + 1; i++) {
//				for (int j = 0; j != 3; j++) {
//					if (verteciesCheck.get(3*i + j) == Float.valueOf(vertex[j + 1])) {
//						check++;
//					}
//				}
//				if (check == 3) {
//					return i;
//				} else {
//					check = 0;
//				}
//				return -1;
//			}
//			return -1;
//		}

	// matrix multiplication with matrices in array form
	public static int[][] matrixMulti(int one[][], int two[][]) {
		int y = one.length;
		int x = two[0].length;
		int z = one[0].length;
		// in case of an invalid matrix multiplication operation due to dimensions
		if (z != two.length) {
			int zero[][] = { {} };
			System.out.print("Invalid operation due to incompatable matric dimensions");
			return (zero);
		}
		int a = 0;
		int temp[][] = new int[y][x];
		int sum = 0;
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				for (int k = 0; k < y; k++) {
					a = (one[j][k]) * (two[k][i]);
					sum += a;
				}
				temp[j][i] = sum;
				sum = 0;
				a = 0;
			}
		}
		return (temp);
	}

	public static float[][] matrixMultiF(float one[][], float two[][]) {
		int y = one.length;
		int x = two[0].length;
		int z = one[0].length;
		// in case of an invalid matrix multiplication operation due to dimensions
		if (z != two.length) {
			float zero[][] = { {} };
			System.out.print("Invalid operation due to incompatable matric dimensions");
			return (zero);
		}
		float a = 0;
		float temp[][] = new float[y][x];
		float sum = 0;
		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				for (int k = 0; k < y; k++) {
					a = (one[j][k]) * (two[k][i]);
					sum += a;
				}
				temp[j][i] = sum;
				sum = 0;
				a = 0;
			}
		}
		return (temp);
	}

	// to prints array given (in a grid format)
	// used to check logic
	public static void printArray(int array[][]) {
		int y = array.length;
		int x = array[0].length;
		for (int i = 0; i < y; i++) {
			for (int j = 0; j < x; j++) {
				System.out.print(array[i][j]);
				System.out.print(" ");
			}
			System.out.println();
		}
		System.out.println();
		System.out.println();
	}

	public static void printArrayf(float array[][]) {
		int y = array.length;
		int x = array[0].length;
		for (int i = 0; i < y; i++) {
			for (int j = 0; j < x; j++) {
				System.out.print(array[i][j]);
				System.out.print(" ");
			}
			System.out.println();
		}
		System.out.println();
		System.out.println();
	}

	// multiply a double by 1000 then rounding it
	public static int rX1000(double smthg) {
		double temp = smthg * 1000;
		return ((int) Math.round(temp));
	}

	// New of coordinates in respect to local coordinate system of the localOrgin
	// object
	// completed through matrix multiplication and will be done be radian amount
	// given
	// assumed to be a 2d vector in matrix form {{x},{y}}
	// (and I'm not really feeling it rn)
	private static int[][] cordinateRotation(int object[][], int localOrgin[][], float radians) {
		int objectLocalC[][] = new int[2][1];
		objectLocalC[0][0] = object[0][0] - localOrgin[0][0];
		objectLocalC[1][0] = object[1][0] - localOrgin[1][0];
		int rotation[][] = { { rX1000(Math.cos(radians)), rX1000(-1 * Math.sin(radians)) },
				{ rX1000(Math.sin(radians)), rX1000(Math.cos(radians)) } };
		int output[][] = matrixMulti(rotation, objectLocalC);
		// divide out the 1000 scaler multiplied in when rounding
		output[0][0] /= 1000;
		output[1][0] /= 1000;
		return (output);
	}

	// calculates the position of the Center of the object with respect to the
	// camera,
	// then calculates the vertices position relative to the center based on the
	// rotation of the object
	// adds the center of objects position to find the position of the vertex
	// relative to the camera
	private int[][] updateLocalPos(float camRotation, float camPos[][]) {
		// set up variables and rotation matrix
		float rotAng = yaw + camRotation;
		float rotation[][] = { { rX1000(Math.cos(rotAng)), rX1000(-1 * Math.sin(rotAng)) },
				{ rX1000(Math.sin(rotAng)), rX1000(Math.cos(rotAng)) } };
//		float rotationN[][] = { { rX1000(Math.cos(yaw)), rX1000(-1 * Math.sin(yaw)) },
//				{ rX1000(Math.sin(yaw)), rX1000(Math.cos(yaw)) } };
		// divide out the 1000 scaler multiplied in when rounding
		float rotationCM[][] = { { rX1000(Math.cos(camRotation)), rX1000(-1 * Math.sin(camRotation)) },
				{ rX1000(Math.sin(camRotation)), rX1000(Math.cos(camRotation)) } };
		float[][] objCenter = new float[2][1];
		objCenter[0][0] = centWorldPos[0][0] - camPos[0][0];
		objCenter[1][0] = centWorldPos[0][1] - camPos[0][1];

		objCenter = matrixMultiF(rotationCM, objCenter);
		float[][] temp = { { 0 }, { 0 } };
		int output[][] = new int[numberOfVertex][3];
		// Rotates each point in respect to the objects origin then translates to
		// camera's local coordinates
		for (int i = 0; i < numberOfVertex; i++) {
			temp[0][0] = Obj1LocalVertex[i][0];
			temp[1][0] = Obj1LocalVertex[i][1];
			temp = matrixMultiF(rotation, temp);
			output[i][0] = (int) (temp[0][0] + objCenter[0][0]) / 10;
			output[i][1] = (int) (temp[1][0] + objCenter[1][0]) / 10;
			output[i][2] = (int) (Obj1LocalVertex[i][2] + centWorldPos[0][2] - camPos[0][2]) * 100;
		}
		for (int i = 0; i < numberOfFaces; i++) {
			temp[0][0] = normals[i][0];
			temp[1][0] = normals[i][1];
			temp = matrixMultiF(rotation, temp);
			normalsRot[i][0] = (temp[0][0]) / 1000;
			normalsRot[i][1] = (temp[1][0]) / 1000;
			normalsRot[i][2] = normals[i][2];
		}
		return (output);
	}

	// translating from global coordinates to local in respect to the objects
	private static int[][] cordinateTransform(int object[][], int localOrgin[][], float radians) {
		int objectLocalC[][] = new int[2][1];
		objectLocalC[0][0] = object[0][0] - localOrgin[0][0];
		objectLocalC[1][0] = object[1][0] - localOrgin[1][0];
		return (objectLocalC);
	}

	// checks if two images are overlapping based of of their own individual hit box
	// radius
	private static boolean checkColision(int object1[][], int object2[][], int size1, int size2, boolean circleCol) {
		int radius = Math.abs(size1 - size2);
		int y = Math.abs(object1[0][0] - object2[0][0]);
		int x = Math.abs(object1[1][0] - object2[1][0]);
		if (circleCol) {
			if (x * x + y * y <= Math.pow(radius, 2)) {
				return (true);
			} else
				return (false);
		} else if ((x <= radius) && (y <= radius)) {
			return (true);
		}
		return (false);
	}

	// physics operations are put in here
	private void updateGlobalPosition() {
		changeInTime = System.currentTimeMillis() - time;
		time = System.currentTimeMillis();
		// Acceleration from forces
		centWorldPos[1][0] = changeInTime * centWorldPos[2][0] / mass;
		centWorldPos[1][1] = changeInTime * centWorldPos[2][1] / mass;
		centWorldPos[1][2] = changeInTime * centWorldPos[2][2] / mass;

		// TO DO...
		// torque
		// air resistance

		// updates position
		centWorldPos[0][0] += changeInTime * centWorldPos[1][0];
		centWorldPos[0][1] += changeInTime * centWorldPos[1][1];
		centWorldPos[0][2] += changeInTime * centWorldPos[1][2];

	}

	// see polygon, but basically draws the object based on the plane projection
	private void draw() {
		colorCalc();
		if (drawFace) {
			drawFaces();
		} else {
			drawer.draw(display);
		}
	}

	// uses dot product of a color vector with normal to create gradient depending
	// on direction of face's normal, kinda
	private void colorCalc() {
		int dot = 0;

		for (int i = 0; i < numberOfFaces; i++) {
			dot = ((int) (normalsRot[i][0] + normalsRot[i][1] + normalsRot[i][2]) * 100) % 120;
//			if (dot > 120) {
//				dot = 120;
//			}
//			if (dot < 120) {
//				dot = -120;
//			}
			colour[i] = new Color(127 + dot, 127 + dot, 127 + dot);
		}
	}

//requires a sorting algorithm and I'm having trouble accessing old files and I'm not rewriting one so...
//	private void faceOrder() {
	// figure out center of faces
	// scale by 1/(distance ^2) away
	// don product with position vector and normal vector, then compare and arange
	// in order from largest to smallest, largest in front
//		int[] order = new int[numberOfFaces];
//		int faces 
//		for (int i = 0; i < numberOfFaces; i++) {	
//			
//		}
//		
//	}

	// checks for change in camera position to see if need to redraw polygons
	private boolean changeTest() {
		for (int i = 0; i < numberOfVertex; i++) {
			if (prevDisplay[i][0] != display[i][0] || prevDisplay[i][1] != display[i][1]) {
				return true;
			}
		}
		return false;
	}

	// creates a polygon at calculated screen position as endpoints
	private void drawFaces() {

		boolean change = changeTest();
		if (change) {
			clear(change);

			faceList.clear();
			int[][] faceCordinates = new int[2][3];
			for (int i = 0; i < numberOfFaces; i++) {

				if (display[faces[i][0]][0] == -100 || display[faces[i][1]][0] == -100
						|| display[faces[i][2]][0] == -100) {
					continue;
				}

				faceCordinates[0][0] = 5 * display[faces[i][0]][0];
				faceCordinates[0][1] = 5 * display[faces[i][1]][0];
				faceCordinates[0][2] = 5 * display[faces[i][2]][0];

				faceCordinates[1][0] = 5 * display[faces[i][0]][1];
				faceCordinates[1][1] = 5 * display[faces[i][1]][1];
				faceCordinates[1][2] = 5 * display[faces[i][2]][1];

				faceList.add(EZ.addPolygon(faceCordinates[0], faceCordinates[1], colour[i], true));
			}
//			faceOrder();
		}
	}

	// deletes drawn polygons
	private void clear(boolean test) {
		if (test) {
			for (int i = 0; i < faceList.size(); i++) {
				temp = faceList.get(i);
				EZ.removeEZElement(temp);
			}
		}
	}

	// updates position of object then figures out how to draw the object in EZ
	public void updatePosition(float cameraRotation, float cameraPos[][]) {
		updateGlobalPosition();
		TranslatedPos = updateLocalPos(cameraRotation, cameraPos);
		projectionToPlane();
		draw();
	}

	// setting force values
	public void setforce(float[][] force) {
		centWorldPos[2][0] = force[0][0];
		centWorldPos[2][1] = force[1][0];
		centWorldPos[2][2] = force[2][0];
	}

	// sets if should draw faces of vertecies
	public void setdrawFace(boolean tF) {
		drawFace = tF;
		clear((!drawFace));
		if (drawFace) {
			drawer.hide();
		} else {
			drawer.show();
		}

		display[0][0] += 100;
	}

	// returns position, velocity, and force vectors
	public float[][] getVectors() {
		float[][] rPos = new float[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				rPos[i][j] = centWorldPos[i][j];
			}
		}
		return rPos;
	}

	// returns rotations along z axis
	public float getRotation() {
		return yaw;
	}
	
	public void setRotation(float rot) {
		 yaw = rot;
	}

	// figures out projection of vertices to a plane distance c away
	// (note c is calculated a bit arbitrarily with a mix of the pixel count and
	// FOV)
	private void projectionToPlane() {
		double phi = 0;
		double theta = 0;
		double rho = 0;
		for (int i = 0; i < numberOfVertex; i++) {
			rho = Math.sqrt(TranslatedPos[i][0] * TranslatedPos[i][0] + TranslatedPos[i][1] * TranslatedPos[i][1]
					+ TranslatedPos[i][2] * TranslatedPos[i][2]);
			phi = Math.acos((double) TranslatedPos[i][2] / rho);
			prevDisplay[i][0] = display[i][0];
			prevDisplay[i][1] = display[i][1];
			if ((TranslatedPos[i][0] < 0) || TranslatedPos[i][0] == 0) {
				display[i][0] = -100;
				display[i][1] = -100;
			} else if (TranslatedPos[i][1] == 0) {
				display[i][0] = 120;
				display[i][1] = 90 - ((int) (Math.round(Math.tan((double) pi / 2 - phi) * c)));
			} else {
				// check
				theta = Math.atan((double) TranslatedPos[i][1] / TranslatedPos[i][0]); // check to make sure thats y/x
																						// ...
				if (Math.abs(theta) > ((float) fov / 360 * pi)) {
					display[i][0] = -100;
					display[i][1] = -100;
				} else {
					display[i][0] = (int) Math.round(c * Math.tan(theta)) + 120;
					display[i][1] = 90 - ((int) (Math.round(Math.tan((double) pi / 2 - phi) * c)));
				}
			}
		}
	}

}
