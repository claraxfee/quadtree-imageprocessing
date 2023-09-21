/* Name: Clara Fee, Julia Rieger 
 * File: Quadtree.java
 * Desc: 
 * 
 * Constructs a Quadtree to expidite filtering/affecting/working on groups of pixels, working with some threshold.
 * Contains methods to return an outlined-by-compression image or normal "compressed" image 
 *
 * Takes an image object and returns a "compressed" image
 * 
 */

import java.lang.*;
import java.io.*;
import java.util.*;

public class Quadtree<Node> {

    private Node root; //root of Quadtree, in most cases this will be the complete Image
    private int size; //number of pixels in image (maxI x maxJ)
    private Image image; //image to construct a Quadtree from (not yet compressed)
    private double threshold; //threshold for compression, notes in README
    private int numNodes = 1; //number of nodes in tree (mainly used to troubleshoot)
    public final int COMPRESSION_CONSTANT = 1000; //constant with which to divide meanSquaredError by to compare to threshold
    public final int EDGE_DETECTION_CONSTANT = 50; //threshold of when to fill in black for edge detection, change based on image dimensions
    
    private class Node { //Node class to hold a quadrant of pixels alike in color by this.threshold
	
		private MyColor meanColor; //average color from startPixel to endPixel
		private double meanSquaredError; //average squared error from original image color to meanMyColor
		private MyColor startPixel; //start pixel of quadrant
		private MyColor endPixel; //end pixel of quadrant
		private Node NE; //reference to NE child
		private Node NW; //reference to NW child
		private Node SW; //reference to SW child
		private Node SE; //reference to SE child

		public MyColor getColor() {return this.meanColor;}
		public double getError() {return this.meanSquaredError;}
		public MyColor getStartPixel() {return this.startPixel;}
		public MyColor getEndPixel() {return this.endPixel;}
		public Node getNE() {return this.NE;}
		public Node getNW() {return this.NW;}
		public Node getSE() {return this.SE;}
		public Node getSW() {return this.SW;}
		public boolean isLeaf() {return this.NW == null;}
		
		/**
		 * Create a Node obj
		 * @param startPixel
		 * @param endPixel
		 */
		public Node(MyColor startPixel, MyColor endPixel) {
			this.startPixel = startPixel;
			this.endPixel = endPixel;
			this.meanColor = getMeanColor(startPixel, endPixel);
			this.meanSquaredError = getMeanSquaredError(startPixel, endPixel, this.meanColor);
			this.NE = null;
			this.NW = null;
			this.SW = null;
			this.SE = null;
		}
		
		/**
		 * returns MyColor object of mean color between two points using start and end pixels
		 * @param startPixel the top left pixel in node/quadrant
		 * @param endPixel the bottom left pixel in node/quadrant
		 * @return MyColor mean color of node/quadrant
		 */
		private MyColor getMeanColor(MyColor startPixel, MyColor endPixel) {
			if (startPixel.compareTo(endPixel) == 0) { //if this node is 1x1
				return startPixel; //mean color is normal color
			}
			int totalRed = 0;
			int totalGreen = 0;
			int totalBlue = 0;
			int totalPixels = 1;
			MyColor[][] colArr = image.getColorArray();
			//from start to end pixel (for whole quadrant), increment totals to calc average
			for (int i = this.startPixel.getI(); i <= this.endPixel.getI(); i++) {
				for (int j = this.startPixel.getJ(); j <= this.endPixel.getJ(); j++) {
					totalRed += colArr[i][j].getR();
					totalGreen += colArr[i][j].getG();
					totalBlue += colArr[i][j].getB();
					totalPixels++;
				}
			}
			int meanRed = totalRed/totalPixels; //average red val
			int meanGreen = totalGreen/totalPixels; //average green val
			int meanBlue = totalBlue/totalPixels; //average blue val
			return new MyColor(meanRed, meanGreen, meanBlue); //return average color
		}
		
		/**
		 * returns mean squared error of original color to mean color
		 * @param startPixel the top left pixel in node/quadrant
		 * @param endPixel the bottom left pixel in node/quadrant
		 * @param meanColor mean color of node/quadrant returned by getMeanColor() 
		 * @return double mean squared error between original pixel color and node's mean color
		 */
		private double getMeanSquaredError(MyColor startPixel, MyColor endPixel, MyColor meanColor) {
			if (startPixel.compareTo(endPixel) == 0) { //if this node is 1x1 return 0 (dont divide)
				return 0; //mean color is normal color, no error
			}
			int meanRed = meanColor.getR();
			int meanGreen = meanColor.getG();
			int meanBlue = meanColor.getB();
			int squaredError = 0;
			int totalPixels = 0;
			MyColor[][] colArr = image.getColorArray();
			for (int i = startPixel.getI(); i <= endPixel.getI(); i++) {
				for (int j = startPixel.getJ(); j <= endPixel.getJ(); j++) {
					squaredError += Math.pow(((colArr[i][j].getR()) - meanRed), 2); //(og red - meanRed)^2
					squaredError += Math.pow(((colArr[i][j].getG()) - meanGreen), 2); //(og green - meanGreen)^2
					squaredError += Math.pow(((colArr[i][j].getB()) - meanBlue), 2); //(og blue - meanBlue)^2
					totalPixels++;
				}
			}
			return squaredError/totalPixels; //return average squared error
		}
		
		/**
		 * divides a node (quadrant) into 4 smaller (nodes) quadrants, make them this node's children
		 */
		public void divideNode() {
			MyColor[][] colArr = image.getColorArray();
			//if this quadrant's length or width is odd
			if (this.endPixel.getI() - this.startPixel.getI() % 2 == 0 || this.endPixel.getJ() - this.startPixel.getJ() % 2 == 0) {
				//NW node
				int nwEndI = ( (endPixel.getI() - startPixel.getI()) + 1 / 2) + startPixel.getI(); //row of endPixel for NW node
				int nwEndJ = ( (endPixel.getJ() - startPixel.getJ()) + 1 / 2) + startPixel.getJ(); //col of endPixel for NW node
				Node NW = new Node(this.startPixel, colArr[nwEndI][nwEndJ]);
				//NE node
				int neStartJ = ( (endPixel.getJ() - startPixel.getJ()) + 1 / 2) + startPixel.getJ(); //col of startPuxel for NE node
				int neEndI = ( (endPixel.getI() - startPixel.getI()) + 1 / 2) + startPixel.getI(); //row of endPixel for NE node
				Node NE = new Node(colArr[startPixel.getI()][neStartJ], colArr[neEndI][endPixel.getJ() + 1]);
				//SW node
				int swStartI = ( (endPixel.getI() - startPixel.getI()) + 1 / 2) + startPixel.getI() + 1; //row of startPixel for SW node
				int swEndJ = ( (endPixel.getJ() - startPixel.getJ()) + 1 / 2) + startPixel.getJ(); //col of endPixel for SW node
				Node SW = new Node(colArr[swStartI][startPixel.getJ()], colArr[endPixel.getI()][swEndJ]);
				//SE node
				int seStartI = ( (endPixel.getI() - startPixel.getI()) + 1 / 2) + startPixel.getI() + 1; //row of startPixel for SE node
				int seStartJ = ( (endPixel.getJ() - startPixel.getJ()) + 1 / 2) + startPixel.getJ() + 1; //col of startPuxel for SE node
				Node SE = new Node(colArr[seStartI][seStartJ], this.endPixel);
				this.NW = NW;
				this.NE = NE;
				this.SE = SE;
				this.SW = SW;
				numNodes += 4;
			}
			else { //if this quadrant's length and width is even
				//NW node
				int nwEndI = ( (endPixel.getI() - startPixel.getI()) / 2) + startPixel.getI(); //row of endPixel for NW node
				int nwEndJ = ( (endPixel.getJ() - startPixel.getJ()) / 2) + startPixel.getJ(); //col of endPixel for NW node
				Node NW = new Node(this.startPixel, colArr[nwEndI][nwEndJ]);
				
				//NE node
				int neStartJ = ( (endPixel.getJ() - startPixel.getJ()) / 2) + startPixel.getJ()+1; //col of startPuxel for NE node
				int neEndI = ( (endPixel.getI() - startPixel.getI()) / 2) + startPixel.getI(); //row of endPixel for NE node
				Node NE = new Node(colArr[startPixel.getI()][neStartJ], colArr[neEndI][endPixel.getJ()]);
				//SW node
				int swStartI = ( (endPixel.getI() - startPixel.getI()) / 2) + startPixel.getI() + 1; //row of startPixel for SW node
				int swEndJ = ( (endPixel.getJ() - startPixel.getJ()) / 2) + startPixel.getJ(); //col of endPixel for SW node
				Node SW = new Node(colArr[swStartI][startPixel.getJ()], colArr[endPixel.getI()][swEndJ]);
				//SE node
				int seStartI = ( (endPixel.getI() - startPixel.getI()) / 2) + startPixel.getI() + 1; //row of startPixel for SE node
				int seStartJ = ( (endPixel.getJ() - startPixel.getJ()) / 2) + startPixel.getJ() + 1; //col of startPuxel for SE node
				Node SE = new Node(colArr[seStartI][seStartJ], this.endPixel);
				this.NW = NW;
				this.NE = NE;
				this.SE = SE;
				this.SW = SW;
				numNodes += 4;
			}
		}

		public String toString() {
			return "startPixel: " + startPixel + " endPixel: " + endPixel;
		}
	
    } //end of Node class

    public Quadtree(Image image, double threshold) {
		this.image = image;
		this.root = new Node(this.image.getStartPixel(), this.image.getEndPixel());
		this.size = image.getSize();
		this.threshold = threshold;
		divide(threshold); //call recursive divide
    }

    /**
     * recursively call recDivide to divide Image into quadrants (nodes) based on threshold
     * @param threshold, double inputed by user to set compression rate 
     */
    //recursively divides image into quadrants based on threshold
    public void divide(double threshold) {
		recDivide(root, threshold);
    }

    /**
     * recursively called to determine if a node needs to be divided. If so, it will satisfy
     * 3 properties: it will not be 1x1, it is a leaf, and it's meanSquaredError divided by
     * some compression constant will be more than a given threshold.
     * @param n, node to divide into 4
     * @param t, threshold to determine whether node should be divided
     */
    private void recDivide(Node n, double t) {
	if (n != null) {
	    if (n.startPixel.compareTo(n.endPixel) == -1) { //if start pixel is not end pixel, i.e. node is not 1x1
		if (n.isLeaf()) { //if n is a leaf
		    if (n.getError()/COMPRESSION_CONSTANT > t) { //if error is > threshold
			n.divideNode(); //3 properties satisfied, divide node!
		    }
		}
		recDivide(n.getNW(), t);
		recDivide(n.getSW(), t);
		recDivide(n.getNE(), t);
		recDivide(n.getSE(), t);
	    }
	}
    }

    /**
     * returns new Image with properties: 2d MyColor array, width, and height to be returned
     * to main for display
     * @return Image, "compressed" image of original
     */
    public Image getImage() {       	
		ArrayList<Node> leaves = new ArrayList<Node>(); //create new temp arrayList with every leaf
		
		leaves = getAllLeavesRec(root, leaves); //call helper method to populate array with only leaves (iterate through tree)
		
		//populate empty 2d array with meanMyColor based on start/end pixel of each leaf node
		MyColor[][] newImageArr =  new MyColor[image.getHeight()][image.getWidth()];
		for (int n = 0; n < leaves.size(); n++) {
			Node currentLeaf = leaves.get(n);

			int startI = currentLeaf.getStartPixel().getI(); //i index of startpixel of this node
			int startJ = currentLeaf.getStartPixel().getJ(); //j index of startpixel of this node
			int endI = currentLeaf.getEndPixel().getI(); //i index of endPixel of this node
			int endJ = currentLeaf.getEndPixel().getJ(); //j index of endpixel of this node
			
			//set quadrant of this leaf to meanMyColor of this leaf
			for (int i = startI; i <= endI; i++) { //for every row in node
				for (int j = startJ; j <= endJ; j++) { //for every col in node
					//set pixel to mean color
					newImageArr[i][j] = currentLeaf.getColor();
				}
			}
		}
		
		//make image object to return 'compressed' image
		Image newImage = new Image(newImageArr, image.getWidth(), image.getHeight());
		return newImage;
    }

	/**
     * returns new shaded Image with properties: 2d MyColor array, width, and height to be returned
     * to main for display
	 * @param shadeFactor the key to shade or invert the image
     * @return Image, "compressed" image of original
     */
	public Image getImageShaded(double shadeFactor) {
		ArrayList<Node> leaves = new ArrayList<Node>(); //create new temp arrayList with every leaf
		
		leaves = getAllLeavesRec(root, leaves); //call helper method to populate array with only leaves (iterate through tree)
		
		//populate empty 2d array with meanMyColor based on start/end pixel of each leaf node
		MyColor[][] newImageArr =  new MyColor[image.getHeight()][image.getWidth()];
		for (int n = 0; n < leaves.size(); n++) {
			Node currentLeaf = leaves.get(n);

			int startI = currentLeaf.getStartPixel().getI(); //i index of startpixel of this node
			int startJ = currentLeaf.getStartPixel().getJ(); //j index of startpixel of this node
			int endI = currentLeaf.getEndPixel().getI(); //i index of endPixel of this node
			int endJ = currentLeaf.getEndPixel().getJ(); //j index of endpixel of this node
			
			//set quadrant of this leaf to meanMyColor of this leaf
			for (int i = startI; i <= endI; i++) { //for every row in node
				for (int j = startJ; j <= endJ; j++) { //for every col in node
					//set pixel to mean color
					newImageArr[i][j] = currentLeaf.getColor();
				}
			}
		}
		
		//make image object to return 'compressed' image
		Image newImage = new Image(newImageArr, image.getWidth(), image.getHeight());
		newImage.shade(shadeFactor);
		return newImage;
	}

    /**
     * returns new Image with outline edges on nodes/quadrants and properties: 2d MyColor array, width, and height to be returned
     * to main for display
     * @return Image, "compressed" image of original with outlined nodes/quadrants
     */
    public Image getImageOutlined() {       	
		ArrayList<Node> leaves = new ArrayList<Node>();	//create new temp arrayList with every leaf
		
		leaves = getAllLeavesRec(root, leaves);	//call helper method to populate array with only leaves (iterate through tree)

		//populate empty 2d array with meanMyColor based on start/end pixel of each leaf node
		MyColor[][] newImageArr =  new MyColor[image.getHeight()][image.getWidth()];
		for (int n = 0; n < leaves.size(); n++) {
			Node currentLeaf = leaves.get(n);

			int startI = currentLeaf.getStartPixel().getI(); //i index of startpixel of this node
			int startJ = currentLeaf.getStartPixel().getJ(); //j index of startpixel of this node
			int endI = currentLeaf.getEndPixel().getI(); //i index of endPixel of this node
			int endJ = currentLeaf.getEndPixel().getJ(); //j index of endpixel of this node

			//set quadrant of this leaf to outline OR meanColor of this leaf
			for (int i = startI; i <= endI; i++) { //for every row in node
				for (int j = startJ; j <= endJ; j++) { //for every col in node
					if (i == startI || i == endI || j == startJ || j == endJ) { //if outline or 1x1
						newImageArr[i][j] = new MyColor(0, 0, 0, i, j); //make pixel (outline) black
					}
					else { //set pixel to mean color
						newImageArr[i][j] = currentLeaf.getColor();
					}
				}
			}
		}
		//make image object to return 'compressed' image
		Image newImage = new Image(newImageArr, image.getWidth(), image.getHeight());
		return newImage;
    }

	/**
     * returns new Image with outline edges on nodes/quadrants and properties: 2d MyColor array, width, and height to be returned
     * to main for display
     * @return Image, "compressed" image of original with outlined nodes/quadrants
     */
    public Image getImageShadedOutlined(double shadeFactor) {       	
		ArrayList<Node> leaves = new ArrayList<Node>();	//create new temp arrayList with every leaf
		
		leaves = getAllLeavesRec(root, leaves);	//call helper method to populate array with only leaves (iterate through tree)

		//populate empty 2d array with meanMyColor based on start/end pixel of each leaf node
		MyColor[][] newImageArr =  new MyColor[image.getHeight()][image.getWidth()];
		for (int n = 0; n < leaves.size(); n++) {
			Node currentLeaf = leaves.get(n);

			int startI = currentLeaf.getStartPixel().getI(); //i index of startpixel of this node
			int startJ = currentLeaf.getStartPixel().getJ(); //j index of startpixel of this node
			int endI = currentLeaf.getEndPixel().getI(); //i index of endPixel of this node
			int endJ = currentLeaf.getEndPixel().getJ(); //j index of endpixel of this node

			//set quadrant of this leaf to outline OR meanColor of this leaf
			for (int i = startI; i <= endI; i++) { //for every row in node
				for (int j = startJ; j <= endJ; j++) { //for every col in node
					if (i == startI || i == endI || j == startJ || j == endJ) { //if outline or 1x1
						newImageArr[i][j] = new MyColor(0, 0, 0, i, j); //make pixel (outline) black
					}
					else { //set pixel to mean color
						newImageArr[i][j] = currentLeaf.getColor();
					}
				}
			}
		}
		//make image object to return 'compressed' image
		Image newImage = new Image(newImageArr, image.getWidth(), image.getHeight());
		newImage.shade(shadeFactor);
		return newImage;
    }

    /**
     * returns Image with edge detection filter 
     * @param edgeDetectionFilter 2d array of doubles for color weights
     * @return Image with edge detection filter
     */
    public Image getImageEdgeDetected(double[][] edgeDetectionFilter) {       	
		MyColor[][] newImageArr = this.getImage().getColorArray();
			
		newImageArr = edgeDetection(edgeDetectionFilter, newImageArr); //edge detect all nodes
		
		
		//make image object to return 'compressed' image
		Image newImage = new Image(newImageArr, image.getWidth(), image.getHeight());
		return newImage;
    }

	 /**
     * returns Image with edge detection filter 
     * @param edgeDetectionFilter 2d array of doubles for color weights
     * @return Image with edge detection filter
     */
    public Image getImageEdgeDetectedOutlined(double[][] edgeDetectionFilter) {       	
		ArrayList<Node> leaves = new ArrayList<Node>(); //create new temp arrayList with every leaf
		
		leaves = getAllLeavesRec(root, leaves);	//call helper method to populate array with only leaves (iterate through tree)
		//populate empty 2d array with meanMyColor based on start/end pixel of each leaf node
		MyColor[][] newImageArr =  new MyColor[image.getHeight()][image.getWidth()];

		for (int n = 0; n < leaves.size(); n++) {
			
			Node currentLeaf = leaves.get(n);
			int startI = currentLeaf.getStartPixel().getI(); //i index of startpixel of this node
			int startJ = currentLeaf.getStartPixel().getJ(); //j index of startpixel of this node
			int endI = currentLeaf.getEndPixel().getI(); //i index of endPixel of this node
			int endJ = currentLeaf.getEndPixel().getJ(); //j index of endpixel of this node
			
			
			//set quadrant of this leaf to meanMyColor of this leaf
			for (int i = startI; i <= endI; i++) { //for every row in node
				for (int j = startJ; j <= endJ; j++) { //for every col in node
					//set pixel to mean color
					newImageArr[i][j] = currentLeaf.getColor();
				}
			}
		}
			
		newImageArr = edgeDetection(edgeDetectionFilter, newImageArr); //edge detect all nodes

		for (int n = 0; n < leaves.size(); n++) {
			Node currentLeaf = leaves.get(n);

			int startI = currentLeaf.getStartPixel().getI(); //i index of startpixel of this node
			int startJ = currentLeaf.getStartPixel().getJ(); //j index of startpixel of this node
			int endI = currentLeaf.getEndPixel().getI(); //i index of endPixel of this node
			int endJ = currentLeaf.getEndPixel().getJ(); //j index of endpixel of this node
			
			//set quadrant of this leaf to meanMyColor of this leaf
			for (int i = startI; i <= endI; i++) { //for every row in node
				for (int j = startJ; j <= endJ; j++) { //for every col in node
					
					if (i == startI || i == endI || j == startJ || j == endJ) { //if outline or 1x1
						newImageArr[i][j] = new MyColor(0, 0, 0, i, j); //make pixel (outline) black
					}
				}
			}
			
		}

		//make image object to return 'compressed' image
		Image newImage = new Image(newImageArr, image.getWidth(), image.getHeight());
		return newImage;
    }

    /**
     * returns 2d array of MyColor objects to represent image with edge detection filter 
     * @param edgeDetectionFilter 2d array of doubles to hold the color weights of every pixel
     * @param colorArray, original image MyColor array to apply filter to
     * @return 2d MyColor array of original Image with edgeDetectionFilter applied
     */
    public MyColor[][] edgeDetection(double[][] edgeDetectionFilter, MyColor[][] newImageArr) {

		MyColor[][] temp = new MyColor[newImageArr.length][newImageArr[0].length];
		
		ArrayList<Node> leaves = new ArrayList<Node>(); //create new temp arrayList with every leaf
		
		leaves = getAllLeavesRec(root, leaves);	//call helper method to populate array with only leaves (iterate through tree)
		
		//populate empty 2d array with meanMyColor based on start/end pixel of each leaf node

		for (int n = 0; n < leaves.size(); n++) {
			
			Node currentLeaf = leaves.get(n);
			int currentLeafHeight = currentLeaf.getEndPixel().getI() - currentLeaf.getStartPixel().getI() + 1;
			int currentLeafWidth = currentLeaf.getEndPixel().getJ() - currentLeaf.getStartPixel().getJ() + 1;
			
			int startI = currentLeaf.getStartPixel().getI(); //i index of startpixel of this node
			int startJ = currentLeaf.getStartPixel().getJ(); //j index of startpixel of this node
			int endI = currentLeaf.getEndPixel().getI(); //i index of endPixel of this node
			int endJ = currentLeaf.getEndPixel().getJ(); //j index of endpixel of this node
			
			//if node is bigger than some amount, make it all black
			if ((currentLeafHeight > EDGE_DETECTION_CONSTANT) && (currentLeafWidth > EDGE_DETECTION_CONSTANT)) {
				for (int i = startI; i <= endI; i++) { //for every row in node
					for (int j = startJ; j <= endJ; j++) { //for every col in node
						temp[i][j] = new MyColor(0, 0, 0, i, j);
					}
				}
			}

			else { //if node small enough

				//neighborhood
				double w1 = edgeDetectionFilter[0][0];
				double w2 = edgeDetectionFilter[1][0];
				double w3 = edgeDetectionFilter[2][0];
				double w4 = edgeDetectionFilter[0][1];
				double w5 = edgeDetectionFilter[1][1];
				double w6 = edgeDetectionFilter[2][1];
				double w7 = edgeDetectionFilter[0][2];
				double w8 = edgeDetectionFilter[1][2];
				double w9 = edgeDetectionFilter[2][2];

				double newRed = 0;
				double newBlue = 0;
				double newGreen = 0;

				MyColor bottomLeft;
				MyColor left;
				MyColor topLeft;
				MyColor bottom;
				MyColor center;
				MyColor top;
				MyColor bottomRight;
				MyColor right;
				MyColor topRight;

				if (currentLeafHeight == 1) {
					MyColor curColor = currentLeaf.getStartPixel();
					
					int i = curColor.getI();
					int j = curColor.getJ();
					if (((i - 1) > 0) && ((i + 1) < newImageArr.length) && ((j - 1) > 0) && ((j + 1) < newImageArr[0].length)) {

						topLeft = newImageArr[i - 1][j - 1];
						left = newImageArr[i][j - 1];
						bottomLeft = newImageArr[i + 1][j - 1];
						top = newImageArr[i - 1][j];
						center = newImageArr[i][j];
						bottom = newImageArr[i + 1][j];
						topRight = newImageArr[i - 1][j + 1];
						right = newImageArr[i][j + 1];
						bottomRight = newImageArr[i + 1][j + 1];
						

						newRed = Math.abs((topLeft.getR() * w1) + (top.getR() * w2) + (topRight.getR() * w3) + (left.getR() * w4) + (center.getR() * w5) + (right.getR() * w6) + (bottomLeft.getR() * w7) + (bottom.getR() * w8) + (bottomRight.getR() * w9));
		
						newGreen = Math.abs((topLeft.getG() * w1) + (top.getG() * w2) + (topRight.getG() * w3) + (left.getG() * w4) + (center.getG() * w5) + (right.getG() * w6) + (bottomLeft.getG() * w7) + (bottom.getG() * w8) + (bottomRight.getG() * w9));
		
						newBlue = Math.abs((topLeft.getB() * w1) + (top.getB() * w2) + (topRight.getB() * w3) + (left.getB() * w4) + (center.getB() * w5) + (right.getB() * w6) + (bottomLeft.getB() * w7) + (bottom.getB() * w8) + (bottomRight.getB() * w9));

						if (newRed > 255) {
							newRed = 255;
						}
						if (newGreen > 255) {
							newGreen = 255;
						}
						if (newBlue > 255) {
							newBlue = 255;
						}
						temp[i][j] = new MyColor((int) newRed, (int) newGreen, (int) newBlue, i, j);
					}
					else {
						temp[i][j] = new MyColor((int) newRed, (int) newGreen, (int) newBlue, i, j);
					}
				}
				
				else if (currentLeafHeight == 2) {
					for (int i = startI; i <= endI; i++) {
						for (int j = startJ; j <= endJ; j++) {
							if (((i - 1) > 0) && ((i + 1) < newImageArr.length) && ((j - 1) > 0) && ((j + 1) < newImageArr[0].length)) {

								topLeft = newImageArr[i - 1][j - 1];
								left = newImageArr[i][j - 1];
								bottomLeft = newImageArr[i + 1][j - 1];
								top = newImageArr[i - 1][j];
								center = newImageArr[i][j];
								bottom = newImageArr[i + 1][j];
								topRight = newImageArr[i - 1][j + 1];
								right = newImageArr[i][j + 1];
								bottomRight = newImageArr[i + 1][j + 1];
		
								newRed = Math.abs((topLeft.getR() * w1) + (top.getR() * w2) + (topRight.getR() * w3) + (left.getR() * w4) + (center.getR() * w5) + (right.getR() * w6) + (bottomLeft.getR() * w7) + (bottom.getR() * w8) + (bottomRight.getR() * w9));
				
								newGreen = Math.abs((topLeft.getG() * w1) + (top.getG() * w2) + (topRight.getG() * w3) + (left.getG() * w4) + (center.getG() * w5) + (right.getG() * w6) + (bottomLeft.getG() * w7) + (bottom.getG() * w8) + (bottomRight.getG() * w9));
				
								newBlue = Math.abs((topLeft.getB() * w1) + (top.getB() * w2) + (topRight.getB() * w3) + (left.getB() * w4) + (center.getB() * w5) + (right.getB() * w6) + (bottomLeft.getB() * w7) + (bottom.getB() * w8) + (bottomRight.getB() * w9));
		
								if (newRed > 255) {
									newRed = 255;
								}
								if (newGreen > 255) {
									newGreen = 255;
								}
								if (newBlue > 255) {
									newBlue = 255;
								}
								temp[i][j] = new MyColor((int) newRed, (int) newGreen, (int) newBlue, i, j);
							}
							else {
								temp[i][j] = new MyColor((int) newRed, (int) newGreen, (int) newBlue, i, j);
							}
						}
					}
				}

				else {
					for (int i = startI; i <= endI; i++) {
						for (int j = startJ; j <= endJ; j++) {
							
							if (((i - 1) > 0) && ((i + 1) < newImageArr.length) && ((j - 1) > 0) && ((j + 1) < newImageArr[0].length)) {
								
								topLeft = newImageArr[i - 1][j - 1];
								left = newImageArr[i][j - 1];
								bottomLeft = newImageArr[i + 1][j - 1];
								top = newImageArr[i - 1][j];
								center = newImageArr[i][j];
								bottom = newImageArr[i + 1][j];
								topRight = newImageArr[i - 1][j + 1];
								right = newImageArr[i][j + 1];
								bottomRight = newImageArr[i + 1][j + 1];
								
								newRed = Math.abs((topLeft.getR() * w1) + (top.getR() * w2) + (topRight.getR() * w3) + (left.getR() * w4) + (center.getR() * w5) + (right.getR() * w6) + (bottomLeft.getR() * w7) + (bottom.getR() * w8) + (bottomRight.getR() * w9));
	
								newGreen = Math.abs((topLeft.getG() * w1) + (top.getG() * w2) + (topRight.getG() * w3) + (left.getG() * w4) + (center.getG() * w5) + (right.getG() * w6) + (bottomLeft.getG() * w7) + (bottom.getG() * w8) + (bottomRight.getG() * w9));
	
								newBlue = Math.abs((topLeft.getB() * w1) + (top.getB() * w2) + (topRight.getB() * w3) + (left.getB() * w4) + (center.getB() * w5) + (right.getB() * w6) + (bottomLeft.getB() * w7) + (bottom.getB() * w8) + (bottomRight.getB() * w9));
	
								if (newRed > 255) {
									newRed = 255;
								}
								if (newGreen > 255) {
									newGreen = 255;
								}
								if (newBlue > 255) {
									newBlue = 255;
								}
								temp[i][j] = new MyColor((int) newRed, (int) newGreen, (int) newBlue, i, j);
							}
							else {
								temp[i][j] = new MyColor((int) newRed, (int) newGreen, (int) newBlue, i, j);
							}
						}
					}
				}
			}
		}
		return temp;
    }
    
    /**
     * returns list of every leaf in tree to iterate through + create Image
     * @param root of subtree to get chilren of
     * @param list of leaves to add leaves to
     * @return ArrayList<Node> of leaves in tree (final call will return all leaves in tree)
     */
    private ArrayList<Node> getAllLeavesRec(Node root, ArrayList<Node> list) {
		//base case
		if (root == null) {
			return list;
		}
		
		//if root is leaf
		if (root.isLeaf()) {
			list.add(root); //add node to list of leaves
		}
		
		//move to next nodes (called recursively)
		list = getAllLeavesRec(root.getNW(), list);
		list = getAllLeavesRec(root.getNE(), list);
		list = getAllLeavesRec(root.getSW(), list);
		list = getAllLeavesRec(root.getSE(), list);

		return list;
    }
}
