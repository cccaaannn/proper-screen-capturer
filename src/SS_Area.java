import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class SS_Area  extends JFrame implements ActionListener, MouseListener{

	//color
	Colors colors = new Colors();

	public final Color background_color = Colors.light_black;
	public final Color foreground_color = Colors.white;


	//images
	ImageIcon unlocked = new ImageIcon(getClass().getResource(("Lock-Unlock-icon.png")));
	ImageIcon locked = new ImageIcon(getClass().getResource(("Lock-Lock-icon.png")));

	//texts
	String unlocked_info_text = "<html><div align=\"center\">You can toggle mouse usage by clicking lock icon.<br>(unlocked)</div><html>";
	String locked_info_text = "<html><div align=\"center\">You can toggle mouse usage by clicking lock icon.<br>(locked)</div><html>";


	//layouts
	FlowLayout fl = new FlowLayout();
	BorderLayout bl = new BorderLayout();
	
	//panels
	JPanel panel_bottom = new JPanel();
	JPanel panel_top = new JPanel();
	
	//buttons
	JButton exit =  new JButton(new ImageIcon(getClass().getResource(("delete-icon.png"))));
	JButton shoot =  new JButton(new ImageIcon(getClass().getResource(("qr  scanner - samall.png"))));
	JButton use_mouse =  new JButton(unlocked);

	//labels
	JLabel info_label = new JLabel();
	JLabel info_label_top = new JLabel();

	//instantiating SS_Taker
	SS_Taker ss_taker = new SS_Taker();



	SS_Area(){
		create_frame();
		panels();
		buttons();
		labels();
		layouts();
	}





	/*
	 * creates ss area
	 * ss area is the area that shows the area that will be captured by the program
	 */
	void create_frame() {
		setSize(Toolkit.getDefaultToolkit().getScreenSize());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setBackground(Colors.light_black);
		setResizable(false);
		setUndecorated(true);
		setOpacity(0.4f);
		setVisible(false);
		setAlwaysOnTop(true);

		//no mouse listener for this frame because you cant resize it if you are on the frame and it stuck
		//ss_area.addMouseListener(this);
	}





	/*
	 * button options
	 */
	void buttons(){
		
		exit.setPreferredSize(new Dimension(20,20));
		exit.addActionListener(this);		
		exit.setFocusable(false);
		exit.setContentAreaFilled(false);
		exit.setForeground(foreground_color);
		exit.addMouseListener(this);
		
		shoot.setPreferredSize(new Dimension(32,32));
		shoot.addActionListener(this);		
		shoot.setFocusable(false);
		shoot.setContentAreaFilled(false);
		shoot.setForeground(foreground_color);
		shoot.addMouseListener(this);

		use_mouse.setPreferredSize(new Dimension(32,32));
		use_mouse.addActionListener(this);		
		use_mouse.setFocusable(false);
		use_mouse.setContentAreaFilled(false);
		use_mouse.setForeground(foreground_color);
		use_mouse.addMouseListener(this);
	}


	/*
	 * label options
	 */
	void labels() {

		add(info_label,BorderLayout.CENTER);
		info_label.setFont(new Font("arial",Font.BOLD,27));
		info_label.setForeground(Colors.white);
		info_label.setText(unlocked_info_text);
		info_label.setHorizontalAlignment(SwingConstants.CENTER);


		info_label_top.setFont(new Font("arial",Font.BOLD,27));
		info_label_top.setForeground(Colors.green);
		info_label_top.setText("");
		info_label_top.setHorizontalAlignment(SwingConstants.CENTER);
	}


	/*
	 * panel options
	 */
	void panels(){
		add(panel_bottom,BorderLayout.SOUTH);
		panel_bottom.setBackground(background_color);
		panel_bottom.setLayout(fl);
		panel_bottom.add(use_mouse);
		panel_bottom.add(shoot);
		
		
		add(panel_top,BorderLayout.NORTH);
		panel_top.setBackground(background_color);
		panel_top.setLayout(bl);
		panel_top.add(info_label_top,BorderLayout.CENTER);
		panel_top.add(exit,BorderLayout.EAST);
	}


	/*
	 * layout options
	 */
	void layouts() {
		fl.setAlignment(FlowLayout.RIGHT);
	}








	/*
	 * updates ss area
	 * 
	 * this function is called by mouse coordinate saving functions for making the area updates right after point updates
	 */
	public void update_ss_area() {
		Rectangle ss_area_size = new Rectangle(capture_frame.first_point_x,capture_frame.first_point_y,capture_frame.second_point_x,capture_frame.second_point_y);
		setBounds(ss_area_size);

	}





	/*
	 * works like the soot ss function on main frame
	 * but there is no full screen option because it is not possible to use full screen option via this button
	 */
	void shoot_ss() {

		//get save directory from dirs combobox
		String save_dir = capture_frame.dirs_combobox.getSelectedItem().toString();

		//get file type from file types combobox
		String file_type = capture_frame.file_formats_combobox.getSelectedItem().toString();

		//get size from points
		Rectangle screenRect = new Rectangle(capture_frame.first_point_x,capture_frame.first_point_y,capture_frame.second_point_x,capture_frame.second_point_y);


		//make frame invisible
		setVisible(false);
		
		//make main frame invisible via using an invisible checkbox it cant be done without checkbox because it is not possible to access main frame from here
		capture_frame.hide_frame.setSelected(true);

		//try to take ss via ss_taker class
		String ss_name = ss_taker.shoot_and_save_ss(save_dir, file_type, screenRect);

		//make main frame visible via using an invisible checkbox
		capture_frame.hide_frame.setSelected(false);

		//set info label on the ss_area as the image name 
		info_label_top.setText(ss_name);


		//make frame visible again
		setVisible(true);

	}


	/*
	 * toggles mouse usage checkbox on the main frame
	 */
	void toggle_mouse_usage() {
		if(capture_frame.use_mouse.isSelected()) {
			capture_frame.use_mouse.setSelected(false);

		}
		else {
			capture_frame.use_mouse.setSelected(true);

		}
	}



	/*
	 * toggles mouse usage checkbox on the main frame
	 * 
	 * html is for aligning and auto scale
	 */
	void toggle_info_label_text() {
		if(capture_frame.use_mouse.isSelected()) {
			info_label.setText(unlocked_info_text);

		}
		else {
			info_label.setText(locked_info_text);

		}
	}


	/*
	 * toggles lock icon at the bottom
	 */
	void toggle_use_mouse_button_icon() {
		if(capture_frame.use_mouse.isSelected()) {
			use_mouse.setIcon(unlocked);
		}
		else {
			use_mouse.setIcon(locked);
		}
	}





	/*
	 * handle buttons(non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == exit) {
			System.exit(0);
		}
		if(e.getSource() == shoot) {
			shoot_ss();
		}
		if(e.getSource() == use_mouse) {
			toggle_mouse_usage();
		}
	}


	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 * 
	 * if cursor enters buttons area disable ss_area scaling
	 */
	@Override
	public void mouseEntered(MouseEvent arg0) {
		capture_frame.is_pointer_on_frame = true;
	}


	/*
	 * (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 * 
	 * if cursor exits buttons area enable ss_area scaling
	 */
	@Override
	public void mouseExited(MouseEvent arg0) {		
		capture_frame.is_pointer_on_frame = false;
	}








	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}



}
