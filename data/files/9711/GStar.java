import acm.graphics.GMath;
import acm.graphics.GPolygon;
/*
 * This code is found in the art and science of java p. 325 by Eric Roberts.
 * 
 * creates a start centered at origin with given horizontal width.
 */
public class GStar extends GPolygon {
	public GStar (double width) {
		double dx = width/2;
		double dy = dx * GMath.tanDegrees(18);
		double edge = width/2 - dy * GMath.tanDegrees(36);
		addVertex(-dx, -dy);
		int angle = 0;
		for (int i = 0; i < 5; i++) {
			addPolarEdge(edge, angle);
			addPolarEdge(edge, angle + 72);
			angle -= 72;
		}
	}
}
