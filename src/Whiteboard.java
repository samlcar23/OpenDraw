import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

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

public class Whiteboard {
	
	private Graphics2D graphics2D;
	
	private Color color;
	
	private String shape;
	
	private DrawSpace drawSpace;
	
	private int scale;
	
	private int clickX, clickY, dragX, dragY, prevX, prevY;
	
	private boolean isFilled;
	
	private int SCALEMIN = 10;
			
	private int SCALEMAX = 100;
	

	public Whiteboard() {
		
		isFilled = true;

		color = Color.black;

		shape = "line";
		
		scale = 10;
		
		drawSpace = new DrawSpace();
		
		graphics2D = drawSpace.getGraphics();

		JFrame frame = new JFrame();

		JPanel buttonPanel = new JPanel();
		
		JPanel bottomPanel = new JPanel();

		JButton clearButton = new JButton("Clear");
		
		JButton colorButton = new JButton("Color");

		JButton circleButton = new JButton("Circle");
		JButton squareButton = new JButton("Square");
		JButton lineButton = new JButton("Line");
		JButton stampButton = new JButton("Stamp");

		JSlider scaleSlider = new JSlider();

		JCheckBox filledBox = new JCheckBox();
		
		scaleSlider.setValue(10);
		scaleSlider.setMaximum(SCALEMAX);
		scaleSlider.setMinimum(SCALEMIN);
		scaleSlider.setSnapToTicks(true);
		scaleSlider.setPaintTicks(true);
		scaleSlider.setMajorTickSpacing(10);

		buttonPanel.setLayout(new GridLayout(9, 1));
		
		filledBox.setSelected(true);
		
		bottomPanel.add(clearButton);

		buttonPanel.add(colorButton);
		buttonPanel.add(circleButton);
		buttonPanel.add(squareButton);
		buttonPanel.add(lineButton);
		buttonPanel.add(stampButton);
		buttonPanel.add(new JLabel("Scale:"));
		buttonPanel.add(scaleSlider);
		buttonPanel.add(new JLabel("Fill:"));
		buttonPanel.add(filledBox);

		frame.add(drawSpace, BorderLayout.CENTER);
		frame.add(bottomPanel, BorderLayout.SOUTH);
		frame.add(buttonPanel, BorderLayout.EAST);

		frame.setSize(1000, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
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
		 * BROKEN: mouse listener to show cursor as selected paint brush
		 */
		
//		drawSpace.addMouseMotionListener(new MouseMotionAdapter() {
//			public void mouseMoved(MouseEvent e) {
//				int moveX = e.getX();
//				int moveY = e.getY();
//
//				if (graphics2D != null) {
//					drawSpace.getGraphics().setColor(Color.WHITE);
//					drawSpace.draw(prevX, prevY, prevX, prevY, shape, scale, isFilled);
//					
//					drawSpace.getGraphics().setColor(color);
//					drawSpace.draw(moveX, moveY, moveX, moveY, shape, scale, isFilled);
//				}
//
//				update();
//				
//				prevX = moveX;
//				prevY = moveY;
//			}
//		});

		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				graphics2D.setPaint(Color.white);
				graphics2D.fillRect(0, 0, drawSpace.getSize().width, drawSpace.getSize().height);
				graphics2D.setPaint(Color.black);
				
				drawSpace.update();
			}
		});

		colorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				color = JColorChooser.showDialog(frame, "Choose a color", color);
				drawSpace.getGraphics().setColor(color);
			}
		});

		circleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shape = "circle";
			}
		});

		squareButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shape = "square";
			}
		});

		lineButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shape = "line";
			}
		});

		stampButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shape = (String) JOptionPane.showInputDialog(frame, "Chose a word to stamp:", "Stamp Tool",
						JOptionPane.PLAIN_MESSAGE);
				if (shape.equals(null) == true) {
					shape = "line";
				}
			}
		});

		scaleSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				scale = scaleSlider.getValue();
			}
		});
		
		filledBox.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				isFilled = filledBox.isSelected();
			}
		});
	}
	
	public void update() {
		drawSpace.update();
		graphics2D = drawSpace.getGraphics();
	}
	
	public static void main(String args[]) {
		Whiteboard whiteboard = new Whiteboard();
	}
}
