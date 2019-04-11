import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;

public class SS_Taker {

	//color
	Colors colors = new Colors();

	//robot for taking ss
	Robot robot;



	
	
	/*
	 * constructor
	 */
	SS_Taker(){
		create_robot();
	}


	/*
	 * creates the robot when instantiated
	 */
	void create_robot(){
		try {
			robot = new Robot();
		}
		catch(AWTException e) {
			System.out.println("robot error");
			capture_frame.info_label.setForeground(Colors.red);
			capture_frame.info_label.setText("something went wrong");
		}
	}


	
	
	

	/*
	 * tries to soot an ss and saves it
	 * 
	 * how it works:
	 * takes the save directory to create folder 
	 * file type and ss size by formated rectangle
	 * 
	 * 1-calls create_screenshoots_dir function to ensure that dir is exits
	 * 2-calls check_dir_for_existing_screenshoots to name the ss that taken
	 * 3-tries to shoot ss via robot class also uses informations from all the functions above to save the file
	 * 4-updates label as image name and saved if ss is succeeded or if ss failed updates label to show error
	 * 
	 * 
	 */
	public String shoot_and_save_ss(String save_dir, String file_type, Rectangle screenRect) {

		//get or create and get dir
		String theDir = create_screenshoots_dir(save_dir);

		//generate possible name
		String filename = check_dir_for_existing_screenshoots(theDir, file_type);


		//try to shoot the ss
		try {
			BufferedImage capture = robot.createScreenCapture(screenRect);
			ImageIO.write(capture, file_type, new File(theDir + File.separator + filename + "." + file_type));

			capture_frame.info_label.setForeground(Colors.green);
			capture_frame.info_label.setText(filename + "." + file_type + " saved");
			
			return (filename + "." + file_type + " saved");
		}
		catch (Exception e) {
			System.out.println("shoot error");

			capture_frame.info_label.setForeground(Colors.red);
			capture_frame.info_label.setText("something went wrong");
			
			return "something went wrong";
		}



	}




	
	/*
	 * creates and returns ss directories location as string called screenshoots_psc or only returns if it exits
	 * location is determined by dir combobox
	 */
	String create_screenshoots_dir(String save_dir) {

		//default creating point is root of the program
		File theDir = new File("screenshoots_psc");


		//if desktop selected create file to desktop
		if(save_dir == "desktop") {

			FileSystemView filesys = FileSystemView.getFileSystemView();
			File[] roots = filesys.getRoots();
			//System.out.println("home dir found: " + filesys.getHomeDirectory());
			theDir = new File(filesys.getHomeDirectory() + File.separator + "screenshoots_psc");
		}


		// if the directory does not exist, create it
		if (!theDir.exists()) {
			System.out.println("creating directory: " + theDir.getName());
			try{
				theDir.mkdir();
				System.out.println("DIR created"); 
			} 
			catch(SecurityException se){
				System.out.println("DIR is not created"); 
			}        
		}

		return theDir.toString();
	}




	
	/*
	 * checks the directory given as parameter for deciding the name of the ss
	 * returns ss name as string
	 */
	String check_dir_for_existing_screenshoots(String theDir, String file_type) {

		int counter = 0;

		//loop until a possible ss file name
		while(true) {

			//directory + basefilename + counter + filetype from combobox 
			File file = new File(theDir + File.separator + "ss" + counter + "." + file_type);
			if(!file.exists()) { 
				return "ss" + counter;
			}
			else {
				counter++;
			}

		}
	}


}
