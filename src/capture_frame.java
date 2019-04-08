import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;
import org.jnativehook.mouse.NativeMouseListener;

public class capture_frame extends JFrame implements ActionListener, MouseListener, NativeKeyListener, NativeMouseInputListener  {

	//color
	static Color white = new Color(255,255,255);
	static Color light_black = new Color(61,61,61);
	static Color black = new Color(0,0,0);
	static Color green = new Color(108,254,0);
	static Color blue = new Color(0,150,255);
	static Color red = new Color(255,0,50);

	Color background_color = light_black;
	Color foreground_color = white;


	//buttons
	JButton exit =  new JButton(new ImageIcon(this.getClass().getResource(("delete-icon.png"))));
	JButton info =  new JButton(new ImageIcon(getClass().getResource(("Button-Info-icon.png"))));
	JButton minimize =  new JButton(new ImageIcon(getClass().getResource(("math-minus-icon.png"))));
	JButton shoot =  new JButton(new ImageIcon(getClass().getResource(("qr  scanner.png"))));

	//comboboxes
	String []file_formats = new String[] {"png","JPG","bmp"};
	JComboBox<String> file_formats_combobox = new JComboBox<String>(file_formats);

	String []dirs = new String[] {"root","desktop"};
	JComboBox<String> dirs_combobox = new JComboBox<String>(dirs);

	//radiobutton
	JRadioButton full_secreen_radiobutton = new JRadioButton("full screen");
	JRadioButton manual_selection_radiobutton = new JRadioButton("manual");

	//button group
	ButtonGroup bg = new ButtonGroup();


	//checkboxes
	JCheckBox use_mouse = new JCheckBox("use mouse");
	JCheckBox use_keys = new JCheckBox("use keys");
	JCheckBox show_shade = new JCheckBox("show shade");

	//labels
	static JLabel info_label = new JLabel();

	
	

	//creating ss area
	JFrame ss_area = new JFrame();

	//move frame
	int pX,pY;

	//is pointer on frame 
	//this is for disabling position records with mouse clicks when pointer is on the frame
	boolean is_pointer_on_frame = false;

	//executorService is for real time frame drawing to be timed well
	final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();





	/*
	 * constructor of the frame 
	 * all functions that needed on start are called
	 */
	capture_frame(){
		super("proper ss capturer");

		create_frame();
		
		buttons();
		comboboxes();
		radiobuttons();
		checkbox();
		labels();
		
		native_key_and_mouse_listener();
		create_ss_area_frame();
		move_frame();
	}



	/*
	 * creates main frame
	 * sets colors size etc.
	 */
	void create_frame() {
		setSize(355,120);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setUndecorated(true);
		setOpacity(0.7f);
		setVisible(true);
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource(("qr  scanner.png"))));
		//setAlwaysOnTop(true);
		setLayout(null);
		getContentPane().setBackground(background_color);
		setFocusable(true);
		addMouseListener(this);
		setLocationRelativeTo(null);
	}




	//--------------------------------------------gui elements-----------------------------------------------------------------

	/*
	 * combbox options
	 */
	void comboboxes() {
		add(file_formats_combobox);
		file_formats_combobox.setBounds(10,20,100,25);
		file_formats_combobox.setFont(new Font("arial",Font.BOLD,15));
		file_formats_combobox.setBackground(background_color);
		file_formats_combobox.setForeground(white);
		file_formats_combobox.setFocusable(false);
		file_formats_combobox.setSelectedIndex(0);
		file_formats_combobox.addMouseListener(this);


		add(dirs_combobox);
		dirs_combobox.setBounds(10,50,100,25);
		dirs_combobox.setFont(new Font("arial",Font.BOLD,15));
		dirs_combobox.setBackground(background_color);
		dirs_combobox.setForeground(white);
		dirs_combobox.setFocusable(false);
		dirs_combobox.setSelectedIndex(1);
		file_formats_combobox.addMouseListener(this);
	}


	/*
	 * button options
	 */
	void buttons() {

		add(exit);
		exit.setBounds(330,2,17,17);
		exit.addActionListener(this);		
		exit.setFocusable(false);
		exit.setContentAreaFilled(false);
		exit.setBorderPainted(false);
		exit.addMouseListener(this);

		add(minimize);
		minimize.setBounds(305,2,17,17);
		minimize.addActionListener(this);		
		minimize.setFocusable(false);
		minimize.setContentAreaFilled(false);
		minimize.setBorderPainted(false);
		minimize.addMouseListener(this);

		add(info);
		info.setBounds(280,2,17,17);
		info.addActionListener(this);		
		info.setFocusable(false);
		info.setContentAreaFilled(false);
		info.setBorderPainted(false);
		info.addMouseListener(this);

		add(shoot);
		shoot.setBounds(295,25,50,50);
		shoot.addActionListener(this);		
		shoot.setFocusable(false);
		shoot.setContentAreaFilled(false);
		shoot.setForeground(foreground_color);
		shoot.addMouseListener(this);
	}

	/*
	 * info label options
	 */
	void labels() {
		add(info_label);
		info_label.setBounds(10,80,335,35);
		info_label.setFont(new Font("arial",Font.BOLD,20));
		info_label.setForeground(blue);
		info_label.setText("proper ss capturer");
		info_label.setHorizontalAlignment(SwingConstants.CENTER);
		//info_label.addMouseListener(this);
	}



	/*
	 * radiobutton options
	 */
	void radiobuttons() {
		add(full_secreen_radiobutton);
		full_secreen_radiobutton.setBounds(115,20,89,20);
		full_secreen_radiobutton.setBackground(background_color);
		full_secreen_radiobutton.setForeground(white);
		full_secreen_radiobutton.setActionCommand("1");
		full_secreen_radiobutton.addActionListener(this);
		full_secreen_radiobutton.setFocusable(false);
		full_secreen_radiobutton.addMouseListener(this);

		add(manual_selection_radiobutton);
		manual_selection_radiobutton.setBounds(115,55,70,20);
		manual_selection_radiobutton.setBackground(background_color);
		manual_selection_radiobutton.setForeground(white);
		manual_selection_radiobutton.setActionCommand("2");
		manual_selection_radiobutton.addActionListener(this);
		manual_selection_radiobutton.setFocusable(false);
		manual_selection_radiobutton.addMouseListener(this);

		bg.add(full_secreen_radiobutton);
		bg.add(manual_selection_radiobutton);

		full_secreen_radiobutton.setSelected(true);
	}


	/*
	 * checkbox options
	 */
	void checkbox() {
		add(use_keys);
		use_keys.setBounds(200,17,90,20);
		use_keys.setBackground(background_color);
		use_keys.setForeground(white);
		use_keys.setFocusable(false);
		use_keys.setSelected(true);
		use_keys.setEnabled(false);
		use_keys.addMouseListener(this);

		add(use_mouse);
		use_mouse.setBounds(200,40,90,20);
		use_mouse.setBackground(background_color);
		use_mouse.setForeground(white);
		use_mouse.setFocusable(false);
		use_mouse.setSelected(true);
		use_mouse.setEnabled(false);
		use_mouse.addMouseListener(this);

		add(show_shade);
		show_shade.setBounds(200,63,92,20);
		show_shade.setBackground(background_color);
		show_shade.setForeground(white);
		show_shade.setFocusable(false);
		show_shade.setSelected(true);
		show_shade.setEnabled(false);
		show_shade.addActionListener(this);
		show_shade.addMouseListener(this);
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------------






	//--------------------------------------------ss capture and create file functions-------------------------------------------------------------


	/*
	 * tries to soot an ss and saves it
	 * 
	 * how it works:
	 * 1-calls create_screenshoots_dir function to ensure that dir is exits
	 * 2-calls check_dir_for_existing_screenshoots to name the ss that taken
	 * 3-takes file format from file_formats_combobox 
	 * 4-makes ss area invisible if it is visible
	 * 5-gets ss size by looking at the radiobuttons
	 * 6-tries to shoot ss via robot class also uses informations from all the functions above to save the file
	 * 7-updates label as image name and saved if ss is succeeded or if ss failed updates label to show error
	 * 8-makes ss area visible again if it was visible
	 * 
	 */
	void shoot_ss() {

		//get or create and get dir
		String theDir = create_screenshoots_dir();

		//generate possible name
		String filename = check_dir_for_existing_screenshoots(theDir);

		//get file type
		String file_type = file_formats_combobox.getSelectedItem().toString();


		//make ss area invisible before ss if it is visible 
		if(show_shade.isSelected()) {
			ss_area.setVisible(false);
		}



		Rectangle screenRect = new Rectangle(0,0,0,0);

		//capture selection form radio buttons
		//first option is full screen
		if(full_secreen_radiobutton.isSelected()) {
			screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		}
		//second option is manual
		if(manual_selection_radiobutton.isSelected()) {
			screenRect = new Rectangle(first_point_x,first_point_y,second_point_x,second_point_y);
		}


		//try to shoot the ss
		try {
			BufferedImage capture = new Robot().createScreenCapture(screenRect);
			ImageIO.write(capture, file_type, new File(theDir + File.separator + filename + "." + file_type));

			info_label.setForeground(green);
			info_label.setText(filename + "." + file_type + " saved");
		}
		catch (Exception e) {
			System.out.println("shoot error");

			info_label.setForeground(red);
			info_label.setText("something went wrong");
		}


		//if ss area was visible before make it visible again
		if(show_shade.isSelected()) {
			ss_area.setVisible(true);
		}

	}



	
	/*
	 * creates and returns ss directories location as string called screenshoots_psc or only returns if it exits
	 * location is determined by dir combobox
	 */
	String create_screenshoots_dir() {

		//default creating point is root of the program
		File theDir = new File("screenshoots_psc");


		//if desktop selected create file to desktop
		if(dirs_combobox.getSelectedIndex() == 1) {

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
	String check_dir_for_existing_screenshoots(String theDir) {

		String file_type = file_formats_combobox.getSelectedItem().toString();

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


	//-------------------------------------------------------------------------------------------------------------------------







	//----------------------------------display second frame to show the ss are----------------------------------------------------------


	
	/*
	 * creates ss area
	 * ss area is the area that shows the area that will be captured by the program
	 */
	void create_ss_area_frame() {
		ss_area.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		ss_area.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ss_area.getContentPane().setBackground(light_black);
		ss_area.setResizable(false);
		ss_area.setUndecorated(true);
		ss_area.setOpacity(0.4f);
		ss_area.setVisible(false);
		ss_area.setAlwaysOnTop(true);

		//this is for detecting if mouse exits the frame both of the frames uses same mouselistener
		ss_area.addMouseListener(this);
	}



	/*
	 * updates ss area
	 * 
	 * this function is called by mouse coordinate saving functions for making the area updates right after point updates
	 */
	void update_ss_area() {

		if(manual_selection_radiobutton.isSelected() && show_shade.isSelected()) {
			Rectangle ss_area_size = new Rectangle(first_point_x,first_point_y,second_point_x,second_point_y);
			ss_area.setBounds(ss_area_size);

		}

	}


	//-------------------------------------------------------------------------------------------------------------------------







	//----------------------------------mouse coordiates for manual selected area ss----------------------------------------------------------


	//mouse positions
	int first_point_x = 0;
	int	first_point_y = 0;
	int	second_point_x = 0;
	int	second_point_y = 0;
	int original_second_point_x = 0;
	int original_second_point_y = 0;


	
	/*
	 * creating rectangle from mouse coordinates:
	 * 
	 * gets mouse coordinates for first points
	 * 
	 * original second points are cached and subtracted from first points to prevent wrong rectangle because of the point change
	 * 
	 * this function is called by global mouse click function or global key listener function to save first coordinates of the rectangle
	 * also function calls update_ss_area to update ss area real time
	 */
	void record_first_position(String position_caller) {
		//position caller is required because if user uses keyboard program should keep taking pointer location even if it enters frame
		if(manual_selection_radiobutton.isSelected() && (!is_pointer_on_frame || position_caller.equals("keyboard"))) {
			PointerInfo a = MouseInfo.getPointerInfo();
			Point b = a.getLocation();
			Integer x = (int) b.getX();
			Integer y = (int) b.getY();

			first_point_x = x;
			first_point_y = y;

			second_point_x = original_second_point_x - first_point_x;
			second_point_y = original_second_point_y - first_point_y;

			//print_mouse_coordinates_console();
			print_mouse_coordinates_label();
			update_ss_area();
		}

	}



	/*
	 * creating rectangle from mouse coordinates:
	 * 
	 * gets mouse coordinates for second points
	 * second points are bottom right of the rectangle and values are subtracted from first values because of the rectangles function 
	 * 
	 * position_caller parameter is required to prevent changing coordinates by accident 
	 * if caller is mouse check also pointers location
	 * if caller is keyboard not check it 
	 * 
	 * also if caller is mouse executorService is used to prevent extra redrawings of the frame
	 * 
	 * 
	 * this function is called by global mouse click function or global key listener function to save second coordinates of the rectangle
	 * also function calls update_ss_area to update ss area real time
	 */

	void record_second_position(String position_caller) {
		
		if(manual_selection_radiobutton.isSelected() && position_caller.equals("keyboard")) {

			PointerInfo a = MouseInfo.getPointerInfo();
			Point b = a.getLocation();
			Integer x = (int) b.getX();
			Integer y = (int) b.getY();

			original_second_point_x = x;
			original_second_point_y = y;

			second_point_x = original_second_point_x - first_point_x;
			second_point_y = original_second_point_y - first_point_y;

			//print_mouse_coordinates_console();
			print_mouse_coordinates_label();
			update_ss_area();
		}	

		if(manual_selection_radiobutton.isSelected() && !is_pointer_on_frame && position_caller.equals("mouse")) {

			PointerInfo a = MouseInfo.getPointerInfo();
			Point b = a.getLocation();
			Integer x = (int) b.getX();
			Integer y = (int) b.getY();

			original_second_point_x = x;
			original_second_point_y = y;

			second_point_x = original_second_point_x - first_point_x;
			second_point_y = original_second_point_y - first_point_y;

			//print_mouse_coordinates_console();
			print_mouse_coordinates_label();
			update_ss_area();


			//100 miliseconds redraw timer
			executorService.scheduleAtFixedRate(new Runnable() {

				public void run() {
					update_ss_area();
				}

			}, 0, 100, TimeUnit.MILLISECONDS);
		}


	}


	/*
	 * show coordinates on console
	 */
	void print_mouse_coordinates_console() {
		System.out.print("first x: " + first_point_x);
		System.out.println(" first y: " + first_point_y);

		System.out.print("second x: " + second_point_x);
		System.out.println(" second y: " + second_point_y);
	}

	/*
	 * draw coordinates to the information label
	 */
	void print_mouse_coordinates_label() {
		info_label.setForeground(blue);
		info_label.setText("x1:"+first_point_x+"y1:"+first_point_y+" x2:"+original_second_point_x+"y2:"+original_second_point_y);
	}


	//-------------------------------------------------------------------------------------------------------------------------







	//-------------------------------------------------other--------------------------------------------------------------------

	/*
	 * joptionpane for information
	 * html used for better look 
	 */
	void show_info() {

		//info button, html for bold and dots
		String msg = "<html><ul><li>For taking ss use the back quote key (\") or the button.<br/><br/>"
				+ "<li>For displaying the shade check show shade box.<br/><br/>"
				+ "<li>For manual sized ss with keys, place the mouse pointer to the TOP LEFT corner of the rectangle that you want <br/>"
				+ "and press or hold 'a' from keyboard to save the position and press or hold 's' for the BOTTOM RIGHT corner of the rectangle.<br/><br/>"
				+ "<li>For manual sized ss with mouse, click the TOP LEF corner of the rectangle that you want, <br/>"
				+ "and drag until you reach the BOTTOM RIGHT corner of the rectangle and release the pointer. <br/><br/>"
				+ "<li>You can also toggle mouse or keyboard usage for manual sizing to prevent changing them by accident.<br/><br/>"
				+ "<li>Author: <b>Can Kurt.</b></ul></html>";

		JLabel label = new JLabel(msg);
		label.setFont(new Font("arial", Font.PLAIN, 15));

		JOptionPane.showMessageDialog(null, label ,"Info", JOptionPane.INFORMATION_MESSAGE);


	}


	/*
	 * minimize frame
	 */
	void minimize_frame() {
		setExtendedState(JFrame.ICONIFIED);
	}


	/*
	 * frame is undecorated so this method is required for moving the frame on drag
	 */
	void move_frame() {

		addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent me){
				pX=me.getX();
				pY=me.getY();
			}
		});

		addMouseMotionListener(new MouseAdapter(){
			public void mouseDragged(MouseEvent me){
				setLocation(getLocation().x+me.getX()-pX,getLocation().y+me.getY()-pY);
			}
		});
	}


	/*
	 * activate native keylistener mouse listener and mousemotionlisteners
	 * disable logger of jnativehook
	 */
	void native_key_and_mouse_listener() {
		// Initialze native hook.
		try {
			GlobalScreen.registerNativeHook();
		}
		catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());
			ex.printStackTrace();

			System.exit(1);
		}

		GlobalScreen.addNativeKeyListener(this);

		GlobalScreen.addNativeMouseListener(this);

		GlobalScreen.addNativeMouseMotionListener(this);

		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
	}


	//-------------------------------------------------------------------------------------------------------------------------







	//-------------------------------------------------regular listeners------------------------------------------------------------



	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * 
	 * handle button preses 
	 */

	@Override
	public void actionPerformed(ActionEvent e) {

		if(e.getSource() == exit) {
			System.exit(0);
		}
		if(e.getSource() == info) {
			show_info();
		}
		if(e.getSource() == minimize) {
			minimize_frame();
		}
		if(e.getSource() == shoot) {
			shoot_ss();
		}


		if(e.getSource() == full_secreen_radiobutton) {

			use_keys.setEnabled(false);
			use_mouse.setEnabled(false);
			show_shade.setEnabled(false);

			ss_area.setVisible(false);
		}


		if(e.getSource() == manual_selection_radiobutton) {

			use_keys.setEnabled(true);
			use_mouse.setEnabled(true);
			show_shade.setEnabled(true);

			//if radiobuton selected and ss area is visible change size on select
			update_ss_area();
			ss_area.setVisible(true);
		}

		if(e.getSource() == show_shade) {
			if(show_shade.isSelected()) {
				update_ss_area();
				ss_area.setVisible(true);
			}
			else {
				ss_area.setVisible(false);
			}
		}


	}





	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}



	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 * 
	 * ALL SWING COMPONENTS have mouse listener because we should not update manual ss rectangle when hovering on frame
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
		is_pointer_on_frame = true;	

	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 * 
	 * ALL SWING COMPONENTS have mouse listener because we should not update manual ss rectangle when hovering on frame
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {
		is_pointer_on_frame = false;

	}


	
	


	
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}


	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	//---------------------------------------------------------------------------------------------------------------------------







	//-------------------------------------------------native listeners------------------------------------------------------------


	/*
	 * (non-Javadoc)
	 * @see org.jnativehook.keyboard.NativeKeyListener#nativeKeyPressed(org.jnativehook.keyboard.NativeKeyEvent)
	 * 
	 * native key listener for ss and resizing ss area if use keys selected 
	 */
	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		//System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));

		if (e.getKeyText(e.getKeyCode()) == "Back Quote") {
			shoot_ss();
		}
		//if use keys button is selected record posisons on press
		if(e.getKeyCode() == NativeKeyEvent.VC_A) {
			if(use_keys.isSelected()) {
				record_first_position("keyboard");
			}
		}
		//if use keys button is selected record posisons on press
		if(e.getKeyCode() == NativeKeyEvent.VC_S) {
			if(use_keys.isSelected()) {
				record_second_position("keyboard");
			}
		}

	}



	
	@Override
	public void nativeKeyReleased(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void nativeMouseClicked(NativeMouseEvent arg0) {
		// TODO Auto-generated method stub

	}


	
	

	/*
	 * (non-Javadoc)
	 * @see org.jnativehook.mouse.NativeMouseListener#nativeMousePressed(org.jnativehook.mouse.NativeMouseEvent)
	 *
	 * record first positions on click if use mouse is selected
	 */

	@Override
	public void nativeMousePressed(NativeMouseEvent arg0) {

		if(use_mouse.isSelected()) { 
			record_first_position("mouse");
		}


	}



	@Override
	public void nativeMouseReleased(NativeMouseEvent arg0) {
		// TODO Auto-generated method stub

	}




	/*
	 * (non-Javadoc)
	 * @see org.jnativehook.mouse.NativeMouseMotionListener#nativeMouseDragged(org.jnativehook.mouse.NativeMouseEvent)
	 *
	 *keep updating position real time while draging
	 */

	@Override
	public void nativeMouseDragged(NativeMouseEvent arg0) {
		if(use_mouse.isSelected()) { 
			record_second_position("mouse");
		}

	}



	
	@Override
	public void nativeMouseMoved(NativeMouseEvent arg0) {
		// TODO Auto-generated method stub

	}






	//-------------------------------------------------------------------------------------------------------------------------



}
