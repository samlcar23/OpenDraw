import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class WhiteboardGUI extends JComponent {

	private Graphics2D graphics2D;

	private Image image;

	private int clickX, clickY, dragX, dragY;

	private Color color;

	private String shape;

	private int scale;

	private int SCALEMIN = 10;

	private int SCALEMAX = 100;

	public Graphics2D getGraphics() {
		return graphics2D;
	}

	public void setGraphics(Graphics2D graphicInput) {
		graphics2D = graphicInput;
	}

	public int[] getClicked() {
		return new int[] { clickX, clickY };
	}

	public int[] getDrag() {
		return new int[] { dragX, dragY };
	}

	public WhiteboardGUI() {

		color = Color.black;

		shape = "line";
		
		scale = 10;

		JFrame frame = new JFrame();

		JPanel buttonPanel = new JPanel();

		JButton clearButton = new JButton("Clear");
		JButton colorButton = new JButton("Color");

		JButton circleButton = new JButton("Circle");
		JButton squareButton = new JButton("Square");
		JButton lineButton = new JButton("Line");
		JButton stampButton = new JButton("Stamp");

		JSlider scaleSlider = new JSlider();

		JCheckBox filledBox = new JCheckBox();

		this.setPreferredSize(frame.getPreferredSize());
		
		scaleSlider.setValue(10);
		scaleSlider.setMaximum(SCALEMAX);
		scaleSlider.setMinimum(SCALEMIN);
		scaleSlider.setSnapToTicks(true);
		scaleSlider.setPaintTicks(true);
		scaleSlider.setMajorTickSpacing(10);

		buttonPanel.setLayout(new GridLayout(2, 2));

		buttonPanel.add(clearButton);
		buttonPanel.add(colorButton);
		buttonPanel.add(circleButton);
		buttonPanel.add(squareButton);
		buttonPanel.add(lineButton);
		buttonPanel.add(stampButton);
		buttonPanel.add(new JLabel("Scale:"));
		buttonPanel.add(scaleSlider);

		frame.add(this, BorderLayout.CENTER);
		frame.add(buttonPanel, BorderLayout.SOUTH);

		frame.setSize(1000, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				clickX = e.getX();
				clickY = e.getY();

				dragX = e.getX();
				dragY = e.getY();

				if (graphics2D != null) {
					draw(clickX, clickY, dragX, dragY, shape);
				}

				update();
			}
		});

		this.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				dragX = e.getX();
				dragY = e.getY();

				if (graphics2D != null) {
					draw(clickX, clickY, dragX, dragY, shape);
				}

				update();

				clickX = dragX;
				clickY = dragY;
			}
		});

		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				graphics2D.setPaint(Color.white);
				graphics2D.fillRect(0, 0, getSize().width, getSize().height);
				graphics2D.setPaint(Color.black);
				
				update();
			}
		});

		colorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				color = JColorChooser.showDialog(frame, "Choose a color", color);
				graphics2D.setColor(color);
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

		//

		scaleSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				scale = scaleSlider.getValue();
			}
		});

	}

	public void paintComponent(Graphics g) {
		if (image == null) {
			image = createImage(this.getSize().width, this.getSize().height);
			graphics2D = (Graphics2D) image.getGraphics();

			graphics2D.setPaint(Color.white);
			graphics2D.fillRect(0, 0, getSize().width, this.getSize().height);

			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			graphics2D.setPaint(Color.black);

			update();
		}

		g.drawImage(image, 0, 0, null);
	}

	public void draw(int oX, int oY, int cX, int cY, String shape) {
		if (shape.equals("line") == true) {
			graphics2D.drawLine(oX, oY, cX, cY);
		} else if (shape.equals("circle") == true) {
			graphics2D.fillOval(cX - scale/2, cY - scale/2, scale, scale);
		} else if (shape.equals("square") == true) {
			graphics2D.fillRect(cX - scale/2, cY - scale/2, scale, scale);
		} else if (shape.equals(null) == false) {
			graphics2D.setFont(new Font("TimesRoman", Font.PLAIN, scale));
			graphics2D.drawString(shape, cX - shape.length() * 2, cY);
		}
	}
	
	public void update() {
		/*
		 * send change to server
		 */
		System.out.println("changes sent");
		/*
		 * receive updated graphics
		 */
		System.out.println("graphics updated");
		/*
		 * update client graphics
		 */
		System.out.println("repaint");
		repaint();
	}

	public static void main(String[] args) {
		WhiteboardGUI whiteboard = new WhiteboardGUI();
	}
}
