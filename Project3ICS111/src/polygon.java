
import java.awt.Color;
import java.util.*;

public class polygon {

	// creating circle for every verticie
	private int vertexNum;
	private EZCircle[] circle;
	private int scale;

	public polygon(int numOfVertex, int setScale) {

		vertexNum = numOfVertex;
		circle = new EZCircle[vertexNum];
		for (int i = 0; i < vertexNum; i++) {
			circle[i] = EZ.addCircle(-100, -100, 10, 10, new Color(100, 100, 100), true);
			scale = setScale;
		}
	}

	public void draw(int[][] cordinates) {

		// moves circle to the screen position
		for (int i = 0; i < vertexNum; i++) {
			circle[i].translateTo(scale * cordinates[i][0], scale * cordinates[i][1]);

		}
	}

	public void hide() {

		// hides circles
		for (int i = 0; i < vertexNum; i++) {
			circle[i].hide();

		}
	}

	public void show() {

		// shows circles
		for (int i = 0; i < vertexNum; i++) {
			circle[i].show();

		}
	}

}
