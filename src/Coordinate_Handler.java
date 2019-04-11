import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;

public class Coordinate_Handler {




	/*
	 * gets mouse coordinates for first points 
	 * original second points are cached and subtracted from first points to prevent wrong rectangle because of the point change
	 */
	void set_coordinates_of_first_pints() {

		PointerInfo a = MouseInfo.getPointerInfo();
		Point b = a.getLocation();
		Integer x = (int) b.getX();
		Integer y = (int) b.getY();

		capture_frame.first_point_x = x;
		capture_frame.first_point_y = y;

		capture_frame.second_point_x = capture_frame.original_second_point_x - capture_frame.first_point_x;
		capture_frame.second_point_y = capture_frame.original_second_point_y - capture_frame.first_point_y;

		//print_mouse_coordinates_console();
	}

	
	/*
	 * gets mouse coordinates for second points
	 * second points are bottom right of the rectangle and values are subtracted from first values because of the rectangles function 
	 */
	void set_coordinates_of_second_pints() {
		
		PointerInfo a = MouseInfo.getPointerInfo();
		Point b = a.getLocation();
		Integer x = (int) b.getX();
		Integer y = (int) b.getY();

		capture_frame.original_second_point_x = x;
		capture_frame.original_second_point_y = y;

		capture_frame.second_point_x = capture_frame.original_second_point_x - capture_frame.first_point_x;
		capture_frame.second_point_y = capture_frame.original_second_point_y - capture_frame.first_point_y;

		//print_mouse_coordinates_console();
	}
	
	
	/*
	 * show coordinates on console
	 */
	void print_mouse_coordinates_console() {
		System.out.print("first x: " + capture_frame.first_point_x);
		System.out.println(" first y: " + capture_frame.first_point_y);

		System.out.print("second x: " + capture_frame.second_point_x);
		System.out.println(" second y: " + capture_frame.second_point_y);
	}

	
}
