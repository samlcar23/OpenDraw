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

	private Graphics2D graphics2D;

	private Color color;

	private String shape;

	private DrawSpace drawSpace;

	private int scale;

	private int clickX, clickY, dragX, dragY;

	private boolean isFilled;

	private int SCALEMIN = 10;

	private int SCALEMAX = 100;

	public Whiteboard() {

		isFilled = true;

		color = Color.black;

		shape = "pen";

		scale = 10;

		drawSpace = new DrawSpace();

		graphics2D = drawSpace.getGraphics();

		JFrame frame = new JFrame();

		JPanel buttonPanel = new JPanel();

		JPanel bottomPanel = new JPanel();
		JPanel bottomPanelWest = new JPanel();
		JPanel bottomPanelEast = new JPanel();

		JButton clearButton = new JButton("Clear");

		JButton saveButton = new JButton("Save");

		JButton exitButton = new JButton("Exit");

		JButton colorButton = new JButton("Color");

		JButton circleButton = new JButton("Circle");
		JButton squareButton = new JButton("Square");
		JButton penButton = new JButton("Pen");
		JButton eraserButton = new JButton("Eraser");
		JButton styleButton = new JButton("Style");
		JButton blankButton = new JButton("blank");
		JButton stampButton = new JButton("Stamp");

		JSlider scaleSlider = new JSlider();

		JCheckBox filledBox = new JCheckBox();

		scaleSlider.setValue(10);
		scaleSlider.setMaximum(SCALEMAX);
		scaleSlider.setMinimum(SCALEMIN);
		scaleSlider.setSnapToTicks(true);
		scaleSlider.setPaintTicks(true);
		scaleSlider.setMajorTickSpacing(10);

		GridLayout layout = new GridLayout(4, 2);

		layout.setHgap(10);
		layout.setVgap(10);

		buttonPanel.setLayout(layout);
		bottomPanel.setLayout(new BorderLayout());

		filledBox.setSelected(true);

		bottomPanelWest.add(clearButton);
		bottomPanelWest.add(saveButton);
		bottomPanelWest.add(exitButton);

		bottomPanelEast.add(new JLabel("Scale:"));
		bottomPanelEast.add(scaleSlider);

		bottomPanelEast.add(new JLabel("Fill:"));
		bottomPanelEast.add(filledBox);

		buttonPanel.add(penButton);
		buttonPanel.add(styleButton);

		buttonPanel.add(circleButton);
		buttonPanel.add(squareButton);

		buttonPanel.add(blankButton);
		buttonPanel.add(stampButton);

		buttonPanel.add(colorButton);
		buttonPanel.add(eraserButton);

		bottomPanel.add(bottomPanelWest, BorderLayout.LINE_START);
		bottomPanel.add(bottomPanelEast, BorderLayout.LINE_END);

		frame.add(drawSpace, BorderLayout.CENTER);
		frame.add(bottomPanel, BorderLayout.SOUTH);
		frame.add(buttonPanel, BorderLayout.AFTER_LINE_ENDS);

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

		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				graphics2D.setPaint(Color.white);
				graphics2D.fillRect(0, 0, drawSpace.getSize().width, drawSpace.getSize().height);
				graphics2D.setPaint(Color.black);

				drawSpace.update();
			}
		});

		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = (String) JOptionPane.showInputDialog(frame, "What would you like to name the file?",
						"", JOptionPane.PLAIN_MESSAGE);
				if (name != null) {
					String filename = "./" + name + ".png";
					
					BufferedImage saveImage = new BufferedImage(drawSpace.getWidth(), drawSpace.getHeight(),
							BufferedImage.TYPE_INT_RGB);
					Graphics2D graphic = saveImage.createGraphics();
					
					drawSpace.paintAll(graphic);
					
					try {
						if (ImageIO.write(saveImage, "png", new File(filename))) {
							JOptionPane.showMessageDialog(frame,
								    name + ".png saved!");
						}
					} catch (IOException exception) {
						exception.printStackTrace();
					}
				}
			}
		});

		exitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		penButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shape = "pen";
			}
		});

		styleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shape = "style";
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
		
		blankButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

			}
		});

		eraserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shape = "eraser";
			}
		});

		stampButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shape = (String) JOptionPane.showInputDialog(frame, "Chose a word to stamp:", "Stamp Tool",
						JOptionPane.PLAIN_MESSAGE);
				if (shape.equals(null) == true) {
					shape = "pen";
				}
			}
		});

		eraserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				shape = "eraser";
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
