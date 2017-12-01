import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Whiteboard extends Observable {

	/*
	 * Graphics2D structure to hold THIS instance of the image
	 */
	private Graphics2D graphics2D;

	/*
	 * current color
	 */
	private Color color;

	/*
	 * current drawing shape
	 */
	private String shape;

	/*
	 * Component where drawing happens
	 */
	private DrawSpace drawSpace;

	/*
	 * size of drawing cursor
	 */
	private int scale;

	/*
	 * X and Y coordinates for click and drag of the mouse
	 */
	private int clickX, clickY, dragX, dragY;

	/*
	 * boolean for whether the selected object is color filled
	 */
	private boolean isFilled;

	/*
	 * minimum scale size for slider
	 */
	private int SCALEMIN = 10;

	/*
	 * maximum scale size for slider
	 */
	private int SCALEMAX = 100;

	/*
	 * Whiteboard class holding the DrawSpace also builds the complete frame and GUI
	 */
	public Whiteboard() {

		/*
		 * drawn objects are initially set filled
		 */
		isFilled = true;

		/*
		 * initial color is black
		 */
		color = Color.black;

		/*
		 * initial shape is pen
		 */
		shape = "pen";

		/*
		 * initial scale is 10
		 */
		scale = 10;

		/*
		 * create THIS instance of DrawSpace
		 */
		drawSpace = new DrawSpace();

		/*
		 * fill THIS instance of the graphics with the graphics from the server
		 */
		graphics2D = drawSpace.getGraphics();

		/*
		 * set up frames and panels
		 */
		JFrame frame = new JFrame();
		JPanel buttonPanel = new JPanel();
		JPanel bottomPanel = new JPanel();
		JPanel bottomPanelWest = new JPanel();
		JPanel bottomPanelEast = new JPanel();

		/*
		 * create buttons
		 */
		JButton clearButton = new JButton("Clear");
		JButton saveButton = new JButton("Save");
		JButton exitButton = new JButton("Exit");
		JButton colorButton = new JButton("Color");
		JButton circleButton = new JButton("Circle");
		JButton squareButton = new JButton("Square");
		JButton penButton = new JButton("Pen");
		JButton eraserButton = new JButton("Eraser");
		JButton styleButton = new JButton("Style");
		JButton triangleButton = new JButton("Triangle");
		JButton stampButton = new JButton("Stamp");

		/*
		 * create sliders
		 */
		JSlider scaleSlider = new JSlider();

		/*
		 * create check boxes
		 */
		JCheckBox filledBox = new JCheckBox();
		
		filledBox.setSelected(true);
		
		/*
		 * setup our scale slider
		 */
		scaleSlider.setValue(10);
		scaleSlider.setMaximum(SCALEMAX);
		scaleSlider.setMinimum(SCALEMIN);
		scaleSlider.setSnapToTicks(true);
		scaleSlider.setPaintTicks(true);
		scaleSlider.setMajorTickSpacing(10);

		/*
		 * create and modify a layout for the button panel
		 */
		GridLayout layout = new GridLayout(4, 2);

		layout.setHgap(10);
		layout.setVgap(10);

		buttonPanel.setLayout(layout);
		
		/*
		 * set the layout for the bottom panel
		 */
		bottomPanel.setLayout(new BorderLayout());

		/*
		 * add components to the west bottom panel
		 */
		bottomPanelWest.add(clearButton);
		bottomPanelWest.add(saveButton);
		bottomPanelWest.add(exitButton);

		/*
		 * add components to the east bottom panel
		 */
		bottomPanelEast.add(new JLabel("Scale:"));
		bottomPanelEast.add(scaleSlider);
		bottomPanelEast.add(new JLabel("Fill:"));
		bottomPanelEast.add(filledBox);

		/*
		 * add buttons to the button panel
		 */
		buttonPanel.add(penButton);
		buttonPanel.add(styleButton);
		buttonPanel.add(circleButton);
		buttonPanel.add(squareButton);
		buttonPanel.add(triangleButton);
		buttonPanel.add(stampButton);
		buttonPanel.add(colorButton);
		buttonPanel.add(eraserButton);
		
		/*
		 * add icons to buttons
		 */
		penButton.setIcon(new ImageIcon("icons/pen.png"));
		styleButton.setIcon(new ImageIcon("icons/style.png"));
		circleButton.setIcon(new ImageIcon("icons/circle.png"));
		squareButton.setIcon(new ImageIcon("icons/square.png"));
		triangleButton.setIcon(new ImageIcon("icons/triangle.png"));
		eraserButton.setIcon(new ImageIcon("icons/eraser.png"));
		stampButton.setIcon(new ImageIcon("icons/stamp.png"));
		colorButton.setIcon(new ImageIcon("icons/palette.png"));

		/*
		 * add the east and east portions to the bottom panel
		 */
		bottomPanel.add(bottomPanelWest, BorderLayout.LINE_START);
		bottomPanel.add(bottomPanelEast, BorderLayout.LINE_END);

		/*
		 * add components to frame
		 */
		frame.add(drawSpace, BorderLayout.CENTER);
		frame.add(bottomPanel, BorderLayout.SOUTH);
		frame.add(buttonPanel, BorderLayout.AFTER_LINE_ENDS);

		/*
		 * modify frame constraints
		 */
		frame.setSize(1000, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		/*
		 * ********** ********** mouse listeners ********** **********
		 */
		
		/*
		 * updates graphics upon mouse pressed
		 */
		drawSpace.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				clickX = e.getX();
				clickY = e.getY();

				dragX = e.getX();
				dragY = e.getY();

				if (graphics2D != null) {
					drawSpace.draw(clickX, clickY, dragX, dragY, shape, scale, isFilled);
				}

				update();

			}
		});
		
		/*
		 * ********** ********** mouse motion listeners ********** **********
		 */

		/*
		 * updates graphics upon mouse dragged
		 */
		drawSpace.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				dragX = e.getX();
				dragY = e.getY();

				if (graphics2D != null) {
					drawSpace.draw(clickX, clickY, dragX, dragY, shape, scale, isFilled);
				}

				update();

				clickX = e.getX();
				clickY = e.getY();
			}
		});

		/*
		 * ********** ********** action listeners ********** **********
		 */
		
		/*
		 * clears the screen upon client request
		 */
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				graphics2D.setPaint(Color.white);
				graphics2D.fillRect(0, 0, drawSpace.getSize().width, drawSpace.getSize().height);
				graphics2D.setPaint(Color.black);

				drawSpace.update();
			}
		});

		/*
		 * saves the screen on the clients machine
		 */
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = (String) JOptionPane.showInputDialog(frame, "What would you like to name the file?", "",
						JOptionPane.PLAIN_MESSAGE);
				if (name != null) {
					String filename = "./" + name + ".png";

					BufferedImage saveImage = new BufferedImage(drawSpace.getWidth(), drawSpace.getHeight(),
							BufferedImage.TYPE_INT_RGB);
					Graphics2D graphic = saveImage.createGraphics();

					drawSpace.paintAll(graphic);

					try {
						if (ImageIO.write(saveImage, "png", new File(filename))) {
							JOptionPane.showMessageDialog(frame, name + ".png saved!");
						}
					} catch (IOException exception) {
						exception.printStackTrace();
					}
				}
			}
		});

		/*
		 * safely exits the program
		 */
		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		/*
		 * change the drawing cursor to a pen (thin dot)
		 */
		penButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shape = "pen";
			}
		});

		/*
		 * change the drawing cursor to stylistic (slanted line)
		 */
		styleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shape = "style";
			}
		});

		/*
		 * changes the selected color
		 */
		colorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				color = JColorChooser.showDialog(frame, "Choose a color", color);
				drawSpace.getGraphics().setColor(color);
			}
		});

		/*
		 * change the drawing cursor to a circle
		 */
		circleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shape = "circle";
			}
		});

		/*
		 * changes the drawing cursor to a square
		 */
		squareButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shape = "square";
			}
		});

		/*
		 * UNUSED
		 */
		triangleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shape = "triangle";
			}
		});

		/*
		 * changes the drawing cursor to an eraser
		 */
		eraserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shape = "eraser";
			}
		});

		/*
		 * changes the drawing cursor to a word stamp
		 */
		stampButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					shape = (String) JOptionPane.showInputDialog(frame, "Chose a word to stamp:", "Stamp Tool",
							JOptionPane.PLAIN_MESSAGE);
				} catch (Exception exception) {
					shape = "pen";
				}
				
			}
		});
		
		/*
		 * ********** ********** change listeners ********** **********
		 */
		
		/*
		 * changes the scale upon slider movement
		 */
		scaleSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				scale = scaleSlider.getValue();
			}
		});

		/*
		 * changes the boolean filled value upon a change
		 */
		filledBox.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				isFilled = filledBox.isSelected();
			}
		});
	}

	/*
	 * update graphics
	 */
	public void update() {
		drawSpace.update();
		graphics2D = drawSpace.getGraphics();
	}

	/*
	 * main method
	 */
	public static void main(String args[]) {
		Whiteboard whiteboard = new Whiteboard();
	}
}
