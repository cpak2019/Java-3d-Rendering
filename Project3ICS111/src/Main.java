
//Written by Christian Pak
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.io.*; // Import the java input / output library
import java.util.Scanner;
import java.util.*;

public class Main {

	int globalVertex = 0;
	float camRotation = 0;
	float camPos[][] = { { 0 }, { 0 }, { 0 } };
	float velocity;

	// the main thingy
	public static void main(String[] args) throws IOException {
		// Initialize and setting up variables and stuff
//		//object and EZ initialization
		EZ.initialize(1200, 900); // set up EZ, creates a screen, multiply pixel size by 5 so actually 240 x 180
									// display
		EZ.setBackgroundColor(new Color(0, 0, 0));
		float pi = 3.141592f;
		boolean hit = true;
		long time = System.currentTimeMillis();
		long changeInTime = 0;
		float[][] startPos = { { 0, 0, 1 }, { 0, 0, 0 }, { 0, 0, 0 } };
		float[][] startPos2 = { { 5, 0, 1 }, { 0, 0, 0 }, { 0, 0, 0 } };
		float[][] startPos3 = { { -5, 0, 1 }, { 0, 0, 0 }, { 0, 0, 0 } };

		// create the 3d objects
		thirdDObj test = new thirdDObj(8, "cube.txt", startPos);
		thirdDObj test2 = new thirdDObj(8, "cube.txt", startPos2);
		thirdDObj test3 = new thirdDObj(28, "rocket.txt", startPos3);

		// variables fore movement
		float movAmount = .1f;
		float movAmountf = movAmount;
		float[][] tempcampos = { { -2, 0, 1 }, { 0, 0, 0 }, { 0, 0, 0 } };
		test.updatePosition((float) (0), tempcampos);
		float angle = 0;

		EZRectangle ground = EZ.addRectangle(600, 675, 1200, 450, new Color(50, 50, 50), true);

		while (hit) {

			ground.pushToBack();
			changeInTime = System.currentTimeMillis() - time;
			time = System.currentTimeMillis();

			// looks a bit funky at certain angles (multiples of around pi/4) due to 1 point
			// perspective but I tested it and i think it works...
			// movement system, set movement rate, uses trig to figure out what direction to
			// move
			if (angle > pi) {
				angle = angle - (2 * pi);
			}
			if (angle < -pi) {
				angle = angle + (2 * pi);
			}
			angle = angle % (2 * pi);

			// fixes weird period problem with trig functions
			if ((Math.abs(angle) > (pi / 4)) && (Math.abs(angle) < (3 * pi / 4))) {
				movAmountf = -.05f;
			} else {
				movAmountf = .05f;
			}
			if (EZInteraction.isKeyDown('d')) {
				if (angle == 0) {
					tempcampos[0][1] += movAmount;

				} else if (angle == pi / 2) {
					tempcampos[0][0] += movAmount;
				} else {
					tempcampos[0][1] += movAmount * Math.cos(angle);
					tempcampos[0][0] += movAmount * Math.sin(angle);
				}
			}
			if (EZInteraction.isKeyDown('a')) {
				if (angle == 0) {
					tempcampos[0][1] -= movAmount;

				} else if (angle == pi / 2) {
					tempcampos[0][0] -= movAmount;
				} else {
					tempcampos[0][1] -= movAmount * Math.cos(angle);
					tempcampos[0][0] -= movAmount * Math.sin(angle);
				}
			}

			if (EZInteraction.isKeyDown('w')) {
				if (angle == pi / 2) {
					tempcampos[0][1] -= movAmount;
				} else if (angle == 0) {
					tempcampos[0][0] += movAmount;
				} else {
					tempcampos[0][1] += movAmountf * Math.sin(angle);
					tempcampos[0][0] += movAmount * Math.cos(angle);
				}

			}
			if (EZInteraction.isKeyDown('s')) {
				if (angle == pi / 2) {
					tempcampos[0][1] += movAmount;
				} else if (angle == 0) {
					tempcampos[0][0] -= movAmount;
				} else {
					tempcampos[0][1] -= movAmountf * Math.sin(angle);
					tempcampos[0][0] -= movAmount * Math.cos(angle);

				}
			}

			if (EZInteraction.isKeyDown('q')) {
				angle += .005 * pi;

			}
			if (EZInteraction.isKeyDown('e')) {
				angle -= .005 * pi;
			}

			System.out.println(time);
//			System.out.println(angle/pi*180);

			
			//fills faces instead of showing a point cloud render
			if (EZInteraction.isKeyDown('f')) {

				test.setdrawFace(true);
				test2.setdrawFace(true);
				test3.setdrawFace(true);

			}
			if (EZInteraction.isKeyDown('g')) {

				test.setdrawFace(false);
				test2.setdrawFace(false);
				test3.setdrawFace(false);

			}
			// updates position of the 3d objects
			test.updatePosition(angle, tempcampos);
			test2.updatePosition(angle, tempcampos);
			test3.updatePosition(angle, tempcampos);

			EZ.refreshScreen();
			// refresh screen with updated positions
		}
	}

}
