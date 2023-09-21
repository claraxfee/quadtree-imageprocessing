/* Name: Clara Fee, Julia Rieger 
 * File: Main.java
 * Desc: 
 * 
 * Main driver file to take flags and compress/edge detect/invert/filter a PPM image
 * 
 */

import java.util.*;
import java.io.*;

public class Main {

    public static final double[][] EDGE_DETECTION_FILTER = {{-1, -1, -1}, {-1, 8, -1}, {-1, -1, -1}}; //weights for convolution filter to edge detect
    public static final double[] COMPRESSION_LEVELS = {0.002, 0.004, 0.01, 0.033, 0.077, 0.2, 0.5, 0.75}; //spectrum of compression values to get different compression thresholds
    public static final double COMPRESSION_EDGE_DETECTION = .004; //compression level for efficient edge detection (to view outline, change this to 0.4)
    
    //read in file from filename into Image object
    public static Image read(String filename) {
        
        Image returnThis = new Image(new MyColor[0][0], 0, 0);
        try {
            Scanner scanner = new Scanner(new File(filename));
            String firstLine = scanner.nextLine();
            if (firstLine.charAt(0) == '#') {
                firstLine = scanner.nextLine();
            }
            String secondLine = scanner.nextLine();
            if (secondLine.charAt(0) == '#') {
                secondLine = scanner.nextLine();
            }
            String thirdLine = scanner.nextLine();
            if (thirdLine.charAt(0) == '#') {
                thirdLine = scanner.nextLine();
            }
            String[] dimensions = secondLine.split(" ");
            int height = Integer.parseInt(dimensions[0]);
            int width = Integer.parseInt(dimensions[1]);

            MyColor[][] colorArray = new MyColor[width][height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int r = scanner.nextInt();
                    int g = scanner.nextInt();
                    int b = scanner.nextInt();
                    colorArray[i][j] = new MyColor(r, g, b, i, j);
                }
            }
            returnThis = new Image(colorArray, width, height);
            return returnThis;
        }
        catch (FileNotFoundException e) {
            System.out.println("Problem opening file");
            System.exit(-1);
        }
        return returnThis;
    }

    
    /**
     * @param filename The name of destination file
     * @param image the image to be written out
     * @throws IOException 
     */
    public static void writeImg(String filename, Image image) throws IOException { 
        MyColor[][] img = image.getColorArray();
        PrintWriter out = new PrintWriter(filename);
        out.print("P3 ");
        out.println(img[0].length+ " " +img.length+ " 255"); 
        for (int i=0; i<img.length; i++) { 
            for (int j = 0; j < img[0].length; j++) { 
                
                    out.print(  img[i][j].getR()+" "+
                            img[i][j].getG() +" "+
                            img[i][j].getB() +" "); 
                
            }
	    out.println(); }
        out.close();
    }


    public static void main(String[] args) throws IOException {
        
        String outputFilename = "";
        String inputFilename = "";
        boolean edgeDetection = false;
        boolean compression = false;
        boolean outline = false;
        boolean extraCredit = false;
        double shadeFactor = 0;
        for (int i = 0; i < args.length; i++) {
            if (args[i].charAt(0) == '-') {
                if (args[i].charAt(1) == 'o') {
                    outputFilename = args[i + 1];
                }
                if (args[i].charAt(1) == 'c') {
                    compression = true;
                }
                if (args[i].charAt(1) == 'e') {
                    edgeDetection = true;
                }
                if (args[i].charAt(1) == 't') {
                    outline = true;
                }
                if (args[i].charAt(1) == 'i') {
                    inputFilename = args[i + 1];
                }
                if (args[i].charAt(1) == 'x') {
                    extraCredit = true;
                    try {
                        shadeFactor = Double.parseDouble(args[i + 1]);
                    }
                    catch (NumberFormatException e) {
                        System.out.println("shadeFactor must be a double from -1 to 1!");
                    }
                }
            }
        }
        
        Image image = read(inputFilename);

        if (edgeDetection) {
            if (outline) {
                Quadtree<MyColor> quadtree = new Quadtree<MyColor>(image, COMPRESSION_EDGE_DETECTION);
                writeImg(outputFilename, quadtree.getImageEdgeDetectedOutlined(EDGE_DETECTION_FILTER));
            }
            else {
                Quadtree<MyColor> quadtree = new Quadtree<MyColor>(image, COMPRESSION_EDGE_DETECTION);
                writeImg(outputFilename, quadtree.getImageEdgeDetected(EDGE_DETECTION_FILTER));
            }
        }
        else if (compression) {
            ArrayList<String> outputs = new ArrayList<String>();
            for (int i = 1; i < 9; i++) {
                outputs.add(outputFilename + "-" + i);
            }
	                
            if (outline) {
                for (int i = 0; i < COMPRESSION_LEVELS.length; i++) {
                    Quadtree<MyColor> curQuadtree = new Quadtree<MyColor>(image, COMPRESSION_LEVELS[i]);
                    writeImg(outputs.get(i), curQuadtree.getImageOutlined());
                }
            }
            else {
                for (int i = 0; i < COMPRESSION_LEVELS.length; i++) {
                    Quadtree<MyColor> curQuadtree = new Quadtree<MyColor>(image, COMPRESSION_LEVELS[i]);
		            writeImg(outputs.get(i), curQuadtree.getImage());
                }
            }
        }
        else if (extraCredit) {
            if (outline) {
                Quadtree<MyColor> quadtree = new Quadtree<MyColor>(image, 0);
                writeImg(outputFilename, quadtree.getImageShadedOutlined(shadeFactor));

            }
            else {
                Quadtree<MyColor> quadtree = new Quadtree<MyColor>(image, 0);
                writeImg(outputFilename, quadtree.getImageShaded(shadeFactor));
            }
        }
    }
}
