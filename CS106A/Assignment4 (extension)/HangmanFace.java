/*
 * File: HangmanFace.java
 * ----------------------
 * This file defines a compound HangmanFace class
 */
import acm.graphics.*;

public class HangmanFace extends GCompound {
	
	private static final double EYE_WIDTH    = 0.15;
	private static final double EYE_HEIGHT   = 0.15;
	private static final double NOSE_WIDTH   = 0.15;
	private static final double NOSE_HEIGHT  = 0.10;
	private static final double MOUTH_WIDTH  = 0.50;
	private static final double MOUTH_HEIGHT = 0.05;
	
	private GOval head;
	private GOval leftEye;
	private GOval rightEye;
	private GPolygon nose;
	private GRect mouth;
	
	public HangmanFace ( int radius ) {
		head = new GOval( radius, radius );
		leftEye = new GOval ( EYE_WIDTH*radius, EYE_HEIGHT*radius );
		rightEye = new GOval ( EYE_WIDTH*radius, EYE_HEIGHT*radius );
		nose = createNose( NOSE_WIDTH*radius, NOSE_HEIGHT*radius );
		mouth = new GRect( MOUTH_WIDTH*radius, MOUTH_HEIGHT*radius );
		add( head, 0, 0);
		add( leftEye, 0.25*radius - EYE_WIDTH*radius/2 , 0.25*radius - EYE_HEIGHT*radius/2 );
		add( rightEye, 0.75*radius - EYE_WIDTH*radius/2 , 0.25*radius - EYE_HEIGHT*radius/2 );
		add( nose, 0.50*radius, 0.50*radius );
		add( mouth, 0.50*radius -MOUTH_WIDTH*radius/2, 0.75*radius - MOUTH_HEIGHT*radius/2 );
	}
	
	private GPolygon createNose(double width, double height) {
		GPolygon poly = new GPolygon();
		poly.addVertex( 0, -height/2 );
		poly.addVertex( width/2, height/2 );
		poly.addVertex( -width/2, height/2 );
		return poly;
	}
}
