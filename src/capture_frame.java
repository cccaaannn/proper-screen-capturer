import java.awt.Color;
import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;

public class capture_frame extends JFrame implements ActionListener, ItemListener, MouseListener, NativeKeyListener, NativeMouseListener {

	//color
	Colors colors = new Colors();

	public final Color background_color = Colors.light_black;
	public final Color foreground_color = Colors.white;


	//buttons
	JButton exit =  new JButton(new ImageIcon(getClass().getResource(("delete-icon.png"))));
	JButton info =  new JButton(new ImageIcon(getClass().getResource(("Button-Info-icon.png"))));
	JButton minimize =  new JButton(new ImageIcon(getClass().getResource(("math-minus-icon.png"))));
	JButton shoot =  new JButton(new ImageIcon(getClass().getResource(("qr  scanner.png"))));

	//comboboxes
	static String []file_formats = new String[] {"png","JPG","bmp"};
	static JComboBox<String> file_formats_combobox = new JComboBox<String>(file_formats);

	static String []dirs = new String[] {"root","desktop"};
	static JComboBox<String> dirs_combobox = new JComboBox<String>(dirs);

	//radiobutton
	JRadioButton full_secreen_radiobutton = new JRadioButton("full screen");
	JRadioButton manual_selection_radiobutton = new JRadioButton("manual");

	//button group
	ButtonGroup bg = new ButtonGroup();


	//checkboxes
	static JCheckBox use_mouse = new JCheckBox("use mouse");
	static JCheckBox use_keys = new JCheckBox("use keys");
	static JCheckBox show_shade = new JCheckBox("show shade");
	static JCheckBox hide_frame = new JCheckBox("");
	//labels
	static JLabel info_label = new JLabel();




	//creating ss area
	SS_Area ss_area = new SS_Area();

	//instantiating SS_Taker
	SS_Taker ss_taker = new SS_Taker();

	//
	Coordinate_Handler coordinate_handler = new Coordinate_Handler(); 

	//move frame
	int pX,pY;

	//is pointer on frame 
	//this is for disabling position records with mouse clicks when pointer is on the frame
	static boolean is_pointer_on_frame = false;







	/*
	 * constructor of the frame 
	 * all functions that needed on start are called
	 */
	capture_frame(){
		super("proper ss capturer");

		create_frame();

		
		move_frame();

		buttons();
		comboboxes();
		radiobuttons();
		checkbox();
		labels();
		
		native_key_and_mouse_listener();
	}



	/*
	 * creates main frame
	 * sets colors size etc.
	 */
	void create_frame() {
		setSize(360,120);
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
		file_formats_combobox.setForeground(Colors.white);
		file_formats_combobox.setFocusable(false);
		file_formats_combobox.setSelectedIndex(0);
		file_formats_combobox.addMouseListener(this);


		add(dirs_combobox);
		dirs_combobox.setBounds(10,50,100,25);
		dirs_combobox.setFont(new Font("arial",Font.BOLD,15));
		dirs_combobox.setBackground(background_color);
		dirs_combobox.setForeground(Colors.white);
		dirs_combobox.setFocusable(false);
		dirs_combobox.setSelectedIndex(1);
		file_formats_combobox.addMouseListener(this);
	}


	/*
	 * button options
	 */
	void buttons() {

		add(exit);
		exit.setBounds(335,2,17,17);
		exit.addActionListener(this);		
		exit.setFocusable(false);
		exit.setContentAreaFilled(false);
		exit.setBorderPainted(false);
		exit.addMouseListener(this);

		add(minimize);
		minimize.setBounds(310,2,17,17);
		minimize.addActionListener(this);		
		minimize.setFocusable(false);
		minimize.setContentAreaFilled(false);
		minimize.setBorderPainted(false);
		minimize.addMouseListener(this);

		add(info);
		info.setBounds(285,2,17,17);
		info.addActionListener(this);		
		info.setFocusable(false);
		info.setContentAreaFilled(false);
		info.setBorderPainted(false);
		info.addMouseListener(this);

		add(shoot);
		shoot.setBounds(300,25,50,50);
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
		info_label.setBounds(10,80,340,35);
		info_label.setFont(new Font("arial",Font.BOLD,20));
		info_label.setForeground(Colors.blue);
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
		full_secreen_radiobutton.setForeground(Colors.white);
		full_secreen_radiobutton.setActionCommand("1");
		full_secreen_radiobutton.addItemListener(this);
		full_secreen_radiobutton.setFocusable(false);
		full_secreen_radiobutton.addMouseListener(this);

		add(manual_selection_radiobutton);
		manual_selection_radiobutton.setBounds(115,55,70,20);
		manual_selection_radiobutton.setBackground(background_color);
		manual_selection_radiobutton.setForeground(Colors.white);
		manual_selection_radiobutton.setActionCommand("2");
		manual_selection_radiobutton.addItemListener(this);
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
		use_keys.setForeground(Colors.white);
		use_keys.setFocusable(false);
		use_keys.setSelected(false);
		use_keys.setEnabled(false);
		use_keys.addMouseListener(this);

		add(use_mouse);
		use_mouse.setBounds(200,40,90,20);
		use_mouse.setBackground(background_color);
		use_mouse.setForeground(Colors.white);
		use_mouse.setFocusable(false);
		use_mouse.setSelected(true);
		use_mouse.setEnabled(false);
		use_mouse.addMouseListener(this);
		use_mouse.addItemListener(this);

		add(show_shade);
		show_shade.setBounds(200,63,92,20);
		show_shade.setBackground(background_color);
		show_shade.setForeground(Colors.white);
		show_shade.setFocusable(false);
		show_shade.setSelected(true);
		show_shade.setEnabled(false);
		show_shade.addItemListener(this);
		show_shade.addMouseListener(this);
		
		
		/*
		 * invisible checkbox for making frame invisible from ss_area frame
		 * since it can't be accessed from ss_area frame
		 */
		add(hide_frame);
		hide_frame.setBounds(0,0,0,0);
		hide_frame.setFocusable(false);
		hide_frame.setSelected(false);
		hide_frame.setEnabled(false);
		hide_frame.addItemListener(this);
	
		
	}

	//---------------------------------------------------------------------------------------------------------------------------------------------






	//--------------------------------------------ss capture------------------------------------------------------------------

	/*
	 * uses selected options and ss_taker class to take a screenshot
	 * 1-gets dir from combobox
	 * 2-gets type from combobx
	 * 3-gets size from points or if selected full screen
	 * 4-makes ss_area invisible if it is visible
	 * 5-makes frame invisible
	 * 6-calls shoot_and_save_ss from ss taker and send requered parameters to it
	 * 7-makes frame visible
	 * 8-makes ss_area visible if it was visible
	 * 
	 */
	void shoot_ss() {

		//get save directory from dirs combobox
		String save_dir = dirs_combobox.getSelectedItem().toString();

		//get file type from file types combobox
		String file_type = file_formats_combobox.getSelectedItem().toString();



		//decide ss size via radiobuttons
		Rectangle screenRect = new Rectangle(0,0,0,0);

		//first option is fullscreen
		if(full_secreen_radiobutton.isSelected()) {
			screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		}
		//second option is manual
		if(manual_selection_radiobutton.isSelected()) {
			screenRect = new Rectangle(first_point_x,first_point_y,second_point_x,second_point_y);
		}


		//make ss area invisible before ss if it is visible 
		if(show_shade.isSelected()) {
			ss_area.setVisible(false);
		}


		//make main frame invisible
		setVisible(false);


		//try to take ss via ss_taker class
		String ss_name = ss_taker.shoot_and_save_ss(save_dir, file_type, screenRect);


		//set info label on the ss_area as the image name 
		ss_area.info_label_top.setText(ss_name);

		//make main frame visible
		setVisible(true);


		//if ss area was visible before make it visible again
		if(show_shade.isSelected() && manual_selection_radiobutton.isSelected()) {
			ss_area.setVisible(true);
		}

	}


	//-------------------------------------------------------------------------------------------------------------------------





	//----------------------------------mouse coordinates for manual selected area ss----------------------------------------------------------


	//mouse positions
	static int first_point_x = 0;
	static int	first_point_y = 0;
	static int	second_point_x = 0;
	static int	second_point_y = 0;
	static int original_second_point_x = 0;
	static int original_second_point_y = 0;



	/*
	 * position caller is required because if user uses keyboard program should keep taking pointer location even if it enters frame
	 * creating rectangle from mouse coordinates:
	 * this function is called by global mouse click function or global key listener function to save first coordinates of the rectangle
	 * also function calls update_ss_area to update ss area real time
	 */
	void record_first_position(String position_caller) {
		if(manual_selection_radiobutton.isSelected() && (!is_pointer_on_frame || position_caller.equals("keyboard"))) {

			//set coordinates
			coordinate_handler.set_coordinates_of_first_pints();

			//update label
			print_mouse_coordinates_label();

			//update area
			ss_area.update_ss_area();
		}

	}



	/*
	 * creating rectangle from mouse coordinates:
	 * position_caller parameter is required to prevent position change while pointer is on the frame by accident 
	 * if caller is mouse check also pointers location
	 * if caller is keyboard not check it 
	 * 
	 * this function is called by global mouse click function or global key listener function to save second coordinates of the rectangle
	 * or executive service calls it when native mouse listener triggers it
	 * 
	 * function calls update_ss_area to update ss area real time
	 */

	void record_second_position(String position_caller) {

		//second coordinates for keyboard
		if(manual_selection_radiobutton.isSelected() && position_caller.equals("keyboard")) {

			//set coordinates
			coordinate_handler.set_coordinates_of_second_pints();

			//update label
			print_mouse_coordinates_label();

			//update area
			ss_area.update_ss_area();
		}	

		//second coordinates for mouse
		if(manual_selection_radiobutton.isSelected() && !is_pointer_on_frame && position_caller.equals("mouse")) {

			//set coordinates
			coordinate_handler.set_coordinates_of_second_pints();

			//update label
			print_mouse_coordinates_label();

			//update area
			ss_area.update_ss_area();

		}
	}	


	//for stoping executive service
	boolean stop_drawing_ss_area_real_time = false;
	/*
	 * draws ss frame on screen real time with using executive services separate thread 
	 * executive service needed because we have to control redrawing speed of the frame if it is too fast it causes performance issues.
	 * every time creates a new instance of the executive service because it can't be paused
	 * 
	 * if stop value is true stops the service stop value is controled by native mouse listeners if mouse is released stop value becomes true 
	 * so service stops
	 */
	void start_drawing_ss_area_real_time() {

		//executorService is for real time frame drawing to be timed well
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

		executorService.scheduleAtFixedRate(new Runnable() {

			public void run() {
				if(stop_drawing_ss_area_real_time) {
					executorService.shutdownNow();
				}
				record_second_position("mouse");

			}

		}, 0, 50, TimeUnit.MILLISECONDS);

	}


	/*
	 * draw coordinates to the information label
	 */
	void print_mouse_coordinates_label() {
		info_label.setForeground(Colors.blue);
		info_label.setText("x1:"+first_point_x+"y1:"+first_point_y+" x2:"+original_second_point_x+"y2:"+original_second_point_y);
	}


	//-------------------------------------------------------------------------------------------------------------------------







	//-------------------------------------------------other--------------------------------------------------------------------

	/*
	 * JOptionPane for information
	 * html used for better look 
	 * 
	 * function makes ss area not always on top so JOptionPane can be on top of it after JOptionPane opened 
	 * makes ss area always on top again 
	 */
	void show_info() {

		ss_area.setAlwaysOnTop(false);

		//info button, html for bold and dots
		String msg = "<html><ul><li>For taking ss use the back quote key (\") or the button.<br/><br/>"
				+ "<li>For displaying the shade check show shade box.<br/><br/>"
				+ "<li>For manual sized ss with keys, place the mouse pointer to the TOP LEFT corner of the rectangle that you want <br/>"
				+ "and press or hold 'a' from keyboard to save the position and press or hold 's' for the BOTTOM RIGHT corner of the rectangle.<br/><br/>"
				+ "<li>For manual sized ss with mouse, click the TOP LEF corner of the rectangle that you want, <br/>"
				+ "and drag until you reach the BOTTOM RIGHT corner of the rectangle and release the pointer. <br/><br/>"
				+ "<li>You can also toggle mouse or keyboard usage for manual sizing to prevent changing coordinates by accident.<br/><br/>"
				+ "<li>Author: <b>Can Kurt.</b></ul></html>";

		JLabel label = new JLabel(msg);
		label.setFont(new Font("arial", Font.PLAIN, 15));

		JOptionPane.showMessageDialog(null, label ,"Info", JOptionPane.INFORMATION_MESSAGE);

		ss_area.setAlwaysOnTop(true);

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

		//GlobalScreen.addNativeMouseMotionListener(this);

		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
	}


	//-------------------------------------------------------------------------------------------------------------------------







	//-------------------------------------------------regular listeners------------------------------------------------------------



	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * 
	 * handle button presses 
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
			setExtendedState(JFrame.ICONIFIED);
		}
		if(e.getSource() == shoot) {
			shoot_ss();
		}

	}



	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 * 
	 * item listener for radiobuttons and checkboxes
	 */
	@Override
	public void itemStateChanged(ItemEvent e) {

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

			//check for show shade checkbox for redrawing
			if(show_shade.isSelected()) {
				//if radiobuton selected and ss area is visible change size on select
				ss_area.update_ss_area();
				ss_area.setVisible(true);
			}
		}

		if(e.getSource() == show_shade) {
			if(show_shade.isSelected()) {
				ss_area.update_ss_area();
				ss_area.setVisible(true);
			}
			else {
				ss_area.setVisible(false);
			}
		}

		if(e.getSource() == use_mouse) {
			ss_area.toggle_info_label_text();
			ss_area.toggle_use_mouse_button_icon();
		}

		/*
		 * invisible checkbox for making frame invisible from ss_area frame
		 */
		if(e.getSource() == hide_frame) {
			if(hide_frame.isSelected()) {
				setVisible(false);
			}
			else {
				setVisible(true);
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
	 * 
	 * and start the executive service that draws ss area real time
	 */

	@Override
	public void nativeMousePressed(NativeMouseEvent arg0) {
		if(arg0.getButton() == 1) {
			if(use_mouse.isSelected() && manual_selection_radiobutton.isSelected()) { 
				stop_drawing_ss_area_real_time=false;
				record_first_position("mouse");
				start_drawing_ss_area_real_time();
			}
		}

	}


	/*
	 * (non-Javadoc)
	 * @see org.jnativehook.mouse.NativeMouseListener#nativeMouseReleased(org.jnativehook.mouse.NativeMouseEvent)
	 * 
	 * stops executive service that runs real time frame drawing
	 */
	@Override
	public void nativeMouseReleased(NativeMouseEvent arg0) {
		if(arg0.getButton() == 1) {
			if(use_mouse.isSelected() && manual_selection_radiobutton.isSelected()) { 
				stop_drawing_ss_area_real_time = true;
			}
		}
	}






	//-------------------------------------------------------------------------------------------------------------------------



}
