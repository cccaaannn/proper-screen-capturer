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
import org.jnativehook.mouse.NativeMouseListener;

public class capture_frame extends JFrame implements ActionListener, KeyListener, MouseListener, NativeKeyListener, NativeMouseListener {

	//color
	static Color white = new Color(255,255,255);
	static Color light_black = new Color(61,61,61);
	static Color black = new Color(0,0,0);
	static Color green = new Color(108,254,0);
	static Color blue = new Color(0,150,255);
	static Color red = new Color(255,0,50);

	Color background_color = light_black;
	Color foreground_color = white;


	//comboboxes
	String []file_formats = new String[] {"png","JPG","bmp"};
	JComboBox<String> file_formats_combobox = new JComboBox<String>(file_formats);

	String []dirs = new String[] {"root","desktop"};
	JComboBox<String> dirs_combobox = new JComboBox<String>(dirs);

	//labels
	static JLabel info_label = new JLabel();


	//radiobutton
	JRadioButton full_secreen_radiobutton = new JRadioButton("full screen");
	JRadioButton manual_selection_radiobutton = new JRadioButton("manual");

	//button group
	ButtonGroup bg = new ButtonGroup();



	//move frame
	int pX,pY;


	//buttons
	JButton exit =  new JButton(new ImageIcon(this.getClass().getResource(("delete-icon.png"))));
	JButton info =  new JButton(new ImageIcon(getClass().getResource(("Button-Info-icon.png"))));
	JButton minimize =  new JButton(new ImageIcon(getClass().getResource(("math-minus-icon.png"))));
	JButton shoot =  new JButton(new ImageIcon(getClass().getResource(("qr  scanner.png"))));


	//checkboxes
	JCheckBox use_mouse = new JCheckBox("use mouse");
	JCheckBox use_keys = new JCheckBox("use keys");


	//creating ss area
	JFrame ss_area = new JFrame();

	
	//is pointer on frame 
	//this is for disabling position records with mouse clicks when pointer is on teh frame
	boolean is_pointer_on_frame = false;




	capture_frame(){
		super("proper ss capturer");

		create_frame();
		move_frame();
		comboboxes();
		buttons();
		labels();
		radiobuttons();
		checkbox();
		native_key_and_mouse_listener();
		create_ss_area_frame();
	}




	void create_frame() {
		setSize(350,120);
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
		addKeyListener(this);
		addMouseListener(this);
		setLocationRelativeTo(null);
	}




	//--------------------------------------------gui elements-----------------------------------------------------------------
	

	void comboboxes() {
		add(file_formats_combobox);
		file_formats_combobox.setBounds(10,20,100,25);
		file_formats_combobox.setFont(new Font("arial",Font.BOLD,15));
		file_formats_combobox.setBackground(background_color);
		file_formats_combobox.setForeground(white);
		file_formats_combobox.setFocusable(false);
		file_formats_combobox.setSelectedIndex(0);

		add(dirs_combobox);
		dirs_combobox.setBounds(10,50,100,25);
		dirs_combobox.setFont(new Font("arial",Font.BOLD,15));
		dirs_combobox.setBackground(background_color);
		dirs_combobox.setForeground(white);
		dirs_combobox.setFocusable(false);
		dirs_combobox.setSelectedIndex(1);
	}


	void buttons() {

		add(exit);
		exit.setBounds(325,2,17,17);
		exit.addActionListener(this);		
		exit.setFocusable(false);
		exit.setContentAreaFilled(false);
		exit.setBorderPainted(false);

		add(minimize);
		minimize.setBounds(300,2,17,17);
		minimize.addActionListener(this);		
		minimize.setFocusable(false);
		minimize.setContentAreaFilled(false);
		minimize.setBorderPainted(false);

		add(info);
		info.setBounds(275,2,17,17);
		info.addActionListener(this);		
		info.setFocusable(false);
		info.setContentAreaFilled(false);
		info.setBorderPainted(false);

		add(shoot);
		shoot.setBounds(290,25,50,50);
		shoot.addActionListener(this);		
		shoot.setFocusable(false);
		shoot.setContentAreaFilled(false);
		shoot.setForeground(foreground_color);

	}


	void labels() {
		add(info_label);
		info_label.setBounds(10,80,330,35);
		info_label.setFont(new Font("arial",Font.BOLD,20));
		info_label.setForeground(blue);
		info_label.setText("proper ss capturer");
		info_label.setHorizontalAlignment(SwingConstants.CENTER);
	}




	void radiobuttons() {
		add(full_secreen_radiobutton);
		full_secreen_radiobutton.setBounds(115,25,90,20);
		full_secreen_radiobutton.setBackground(background_color);
		full_secreen_radiobutton.setForeground(white);
		full_secreen_radiobutton.setActionCommand("1");
		full_secreen_radiobutton.addActionListener(this);
		full_secreen_radiobutton.setFocusable(false);

		add(manual_selection_radiobutton);
		manual_selection_radiobutton.setBounds(115,50,70,20);
		manual_selection_radiobutton.setBackground(background_color);
		manual_selection_radiobutton.setForeground(white);
		manual_selection_radiobutton.setActionCommand("2");
		manual_selection_radiobutton.addActionListener(this);
		manual_selection_radiobutton.setFocusable(false);

		bg.add(full_secreen_radiobutton);
		bg.add(manual_selection_radiobutton);

		full_secreen_radiobutton.setSelected(true);
	}


	void checkbox() {
		add(use_keys);
		use_keys.setBounds(200,25,90,20);
		use_keys.setBackground(background_color);
		use_keys.setForeground(white);
		use_keys.setFocusable(false);
		use_keys.setSelected(true);
		use_keys.setEnabled(false);
		
		add(use_mouse);
		use_mouse.setBounds(200,50,90,20);
		use_mouse.setBackground(background_color);
		use_mouse.setForeground(white);
		use_mouse.setFocusable(false);
		use_mouse.setSelected(true);
		use_mouse.setEnabled(false);
	}
	//---------------------------------------------------------------------------------------------------------------------------------------------

	
	
	
	
	
	//--------------------------------------------ss capture and create file functions-------------------------------------------------------------


	//take the screenshoot but first checks the dir via create_screenshoots_dir()
	void shoot_ss() {

		//get or create and get dir
		String theDir = create_screenshoots_dir();

		//generate possible name
		String filename = check_dir_for_existing_screenshoots(theDir);

		//get file type
		String file_type = file_formats_combobox.getSelectedItem().toString();


		//make ss area invisible before ss if it is visible 
		boolean was_ss_area_visible = false;
		if(ss_area.isVisible()) {
			ss_area.setVisible(false);
			was_ss_area_visible = true;
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
		if(was_ss_area_visible) {
			ss_area.setVisible(true);
		}

	}



	//creating the screenshoot dir for screenshoots
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

	
	//ss area for displaying the are for ss
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

	
	
	//toggle ss area
	void toggle_display_ss_area() {

		Rectangle ss_area_size;

		//if area is visible make it invisible
		if(ss_area.isVisible()) {
			ss_area.setVisible(false);
		}
		//else make it visible
		else {
			//first option is full screen
			if(full_secreen_radiobutton.isSelected()) {
				ss_area_size = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
				ss_area.setBounds(ss_area_size);
				ss_area.setVisible(true);
			}
			//second option is manual
			if(manual_selection_radiobutton.isSelected()) {
				ss_area_size = new Rectangle(first_point_x,first_point_y,second_point_x,second_point_y);
				ss_area.setBounds(ss_area_size);

				if(ss_area_size.getSize().getHeight() > 1 || ss_area_size.getSize().getWidth() > 1 ) {
					ss_area.setVisible(true);
				}
			}


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


	//first points are the top left corner of the rectangle
	//original second points are cached and subtracted from first points to prevent wrong rectangle because of the point change
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
			print_mouse_coordinates();
		}
		redraw_ss_area_frame();
	}


	
	//second points are bottom right of the rectangle and values are subtracted from first values because of the rectangles function 
	void record_second_position(String position_caller) {
		//position caller is required because if user uses keyboard program should keep taking pointer location even if it enters frame
		if(manual_selection_radiobutton.isSelected() && (!is_pointer_on_frame || position_caller.equals("keyboard"))) {
			
			PointerInfo a = MouseInfo.getPointerInfo();
			Point b = a.getLocation();
			Integer x = (int) b.getX();
			Integer y = (int) b.getY();

			original_second_point_x = x;
			original_second_point_y = y;

			second_point_x = original_second_point_x - first_point_x;
			second_point_y = original_second_point_y - first_point_y;
			print_mouse_coordinates();
		}	
		redraw_ss_area_frame();
	}


	
	void print_mouse_coordinates() {
		System.out.print("first x: " + first_point_x);
		System.out.println(" first y: " + first_point_y);

		System.out.print("second x: " + second_point_x);
		System.out.println(" second y: " + second_point_y);

		info_label.setForeground(blue);
		info_label.setText("x1:"+first_point_x+"y1:"+first_point_y+" x2:"+original_second_point_x+"y2:"+original_second_point_y);


		
	}

	
	
	void redraw_ss_area_frame() {
		if(ss_area.isVisible()) {
			Rectangle ss_area_size = new Rectangle(first_point_x,first_point_y,second_point_x,second_point_y);
			ss_area.setBounds(ss_area_size);
		}
	}

	
	//-------------------------------------------------------------------------------------------------------------------------

	
	
	
	
	
	
	//-------------------------------------------------other--------------------------------------------------------------------


	void show_info() {

		//info button, html for bold and dots
		String msg = "<html><ul><li>For taking ss use the back quote key (\") or the button.<br/><br/>"
				+ "<li>For displaying the area that will be captured press SHIFT key.<br/><br/>"
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

	

	void minimize_frame() {
		setExtendedState(JFrame.ICONIFIED);
	}


	
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

		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
	}
	
	
	//-------------------------------------------------------------------------------------------------------------------------


	
	
	
	
	
	//-------------------------------------------------regular listeners------------------------------------------------------------


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
			
			
			//if radiobuton selected and ss area is visible change size on select
			if(ss_area.isVisible()) {
				Rectangle ss_area_size = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			ss_area.setBounds(ss_area_size);
			}
			
		}

		
		if(e.getSource() == manual_selection_radiobutton) {
			
			use_keys.setEnabled(true);
			use_mouse.setEnabled(true);
			
			
			//if radiobuton selected and ss area is visible change size on select
			if(ss_area.isVisible()) {
				Rectangle ss_area_size = new Rectangle(first_point_x,first_point_y,second_point_x,second_point_y);
			ss_area.setBounds(ss_area_size);
			}
			
		}

	}




	public void keyPressed(KeyEvent e) {


	}




	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	


	@Override
	public void mouseEntered(MouseEvent arg0) {
		//this if is ensures that if the pointer entered from frame NOT THE COMPONENTS 
		Rectangle r = new Rectangle(this.getSize());
		
        if (r.contains(arg0.getPoint())) {
        	is_pointer_on_frame = true;
        }
	}




	@Override
	public void mouseExited(MouseEvent arg0) {
		//this if is ensures that if the pointer exited from frame NOT THE COMPONENTS 
		
		//first rectangle is original frame 
		
		//second rectangle is for ss_area if mouse enters the original frame but exits from ss_frame it cant be detected without second rectangle 
		//and a mouse listener for ss_frame
		
		Rectangle r = new Rectangle(this.getSize());
		Rectangle r2 = new Rectangle(ss_area.getSize());
        if (!r.contains(arg0.getPoint()) || !r2.contains(arg0.getPoint())) {
        	is_pointer_on_frame = false;	
        }
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
		if(e.getKeyCode() == NativeKeyEvent.VC_SHIFT) {
			toggle_display_ss_area();
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


	}




	@Override
	public void nativeMousePressed(NativeMouseEvent arg0) {
		//if use mouse button is selected record posisons on click
		if(use_mouse.isSelected()) { 
			record_first_position("mouse");
		}
	}



	
	
	@Override
	public void nativeMouseReleased(NativeMouseEvent arg0) {
		//if use mouse button is selected record posisons on click
		if(use_mouse.isSelected()) { 
			record_second_position("mouse");
		}
	}




	

	//-------------------------------------------------------------------------------------------------------------------------



}
