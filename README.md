# quadtree-imageprocessing
Image processing via quadtree in Java. Supports PPM file types only. Supports compression at different levels, edge detection, and a tint/color function.


How to run: java Main -i <filename> -o <filename> <optional flags: -c for image compression
       	    	      	 	       		  	    	   				            -e for edge detection
								   									                              -t for outlined quadtree
																	                                -x -1 for invert 
																	                                -x [0, 1] for shading>
Known bugs and limitations: N/A

Discussion:    

Note that our program runs under the assumption all input images will be square P3 with dimensions that are powers of 2.

Note about compression values: the edge detection compression value is currently set to 0.004. This ensures that edge detection is more efficient as it only acts on small enough nodes. To view the outlined version of the edge detected image, change this compression level to 0.4 or higher so that each leaf is bigger and you can see outlines. 

Filtering:
Our filter takes 2 types of values: if -1 is inputted, the image is inverted (negative). If a value between 0 and 1 is inputted, the image will be shaded based on this shade factor. Lower shade factor (such as 0.1) will make a darker image, and higher shade factor (such as 0.9) will make a lighter image.
