package pp;

/** The main method to run the entire analysis.
 Client can input the data txt file needed to be analysis, and the calculation will
 be displayed in the console window.

 @author Baihan Lin (SunnyLin@uw.edu)
 @version 1.0 (Published 5/27/2014) 
 @version 1.2 (Published 6/1/2014) Children's Day!
 @version 2.0 (Published 6/12/2014)
 **/

import ij.*;

import java.io.*;     // for File, FileNotFoundException
import java.util.*;   // for Scanner, List, Set, Collections
import java.util.List;

import acm.graphics.*;
import acm.gui.IntField;
import acm.program.*; // for ConsoleProgram etc

import java.awt.*; // for listeners etc
import java.awt.event.*;

import javax.swing.*; // for JComponents
import javax.swing.event.*;

import pp.file.ExcelFileDealer;
import pp.pi.PISolver;

public class VEPatchProcessorMain extends ConsoleProgram {

	private static final long serialVersionUID = 5906293533760088949L;
	private static final int DEFAULT_SMOOTH_FACTOR = 2;
	private static final String IMAGE1 = "PatchProcessorLogo.jpg";
	private static final String IMAGE2 = "imagej.jpg";
	private static final String IMAGE3 = "P10ME P21DEV PAT-6 I WGA-HRP s5 line.jpg";
	private static final String IMAGE4 = "P10ME P21DEV PAT-6 C WGA-HRP s4 plot.jpg";
	private static final String IMAGE5 = "trend.jpg";
	private static final String IMAGE6 = "file.jpg";
	private static final String INTROFILE = "readMe.txt";
	private static final String OFILE1 = "result.pi";
	private static final String OFILE2 = "result.xls";
	private static final double START_X = 127;
	private static final double START_Y = 40;
	private static final double C_WIDTH = 340;
	private static final double C_HEIGHT = 358;

	private static String PPINTRO;
	private static String SFINTRO;
	private static String IJINTRO;
	private static String[] arguement;

	private JButton back;
	private JButton patchProcessor;
	private JButton smoothFinder;
	private JButton imageJComponent;
	private JButton about;
	private JButton start;
	private JButton browse;
	private JButton optimal;
	private JCheckBox exl;
	private JRadioButton single;
	private JRadioButton group;
	private JLabel instruction1;
	private JLabel instruction2;
	private JLabel input;
	private JLabel sf;
	private JLabel head;
	private JLabel sfPercentage;
	private JLabel PILabel;
	private JLabel SILabel;
	private JTextField path;
	private IntField smoothFactor;
	private IntField sfDynamic;
	private JSlider sfSlider;
	private GImage logo;
	private GImage imagej1;
	private GImage imagej2;
	private GImage imagej3;
	private GImage xlsx;
	private GImage filePI;
	private GRect fChart;
	private GLine track;
	private ChangeListener sfCL;
	private ArrayList<GLine> Glines;
	private ExcelFileDealer excelFD;

	public static GCanvas outCanvas;

	public static void main(String[] args) {
		new VEPatchProcessorMain().start(args);
		arguement = args;
	}

	// GUI start
	public void init() {

		excelFD = new ExcelFileDealer();

		this.setSize(1000, 650);
		setFont("Courier-15");
		setLayout(new GridLayout(1, 3));

		Glines = new ArrayList<GLine>(); 
		about = new JButton("About");
		patchProcessor = new JButton("Patch Processor");
		smoothFinder = new JButton("Smooth Factor Finder");
		imageJComponent = new JButton("ImageJ Component");
		optimal = new JButton("Optimized SF");
		exl = new JCheckBox("Generate Excel Data on screen");
		exl.setSelected(false);

		sfSlider = new JSlider(0, 100, DEFAULT_SMOOTH_FACTOR);
		sfCL = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (e.getSource() instanceof JSlider) {
					sfDynamic.setValue(((JSlider) e.getSource()).getValue());
					track.setLocation(127 + sfSlider.getValue() * 340 / 100, 40);
					String fileName = path.getText();
					File files = new File(fileName);
					if(isValidFile(files)) {
						if (single.isSelected()) {

							PILabel.setVisible(false);
							SILabel.setVisible(true);
							try {
								drawSingle(files);
							} catch (FileNotFoundException e1) {
								println("Please enter valid file or folder (with ...data.txt).");			
								e1.printStackTrace();
							}
						}
					} else {
						println("Please enter valid file or folder (with ...data.txt).");			
					}
				}
			}
		};
		sfPercentage = new JLabel("Smooth Factor (%)");
		sfDynamic = new IntField(0, 100);
		sfDynamic.setValue(DEFAULT_SMOOTH_FACTOR);
		sfDynamic.setActionCommand("sfDynamic");
		PILabel = new JLabel("<html>    Patch Index Changes vs. Smooth Factor among files"
				+ " -- PI(%)<br> 100<br><br>  "
				+ "90<br><br>  80<br><br>  70<br><br>  60<br><br>  50<br><br>  "
				+ "40<br><br>  30<br><br>  20<br><br>  10<br><br>   0<html>");
		SILabel = new JLabel("<html>    Gray Value Standardized Score Smooth Effect"
				+ " -- SI(%)<br> 100<br><br>  "
				+ "90<br><br>  80<br><br>  70<br><br>  60<br><br>  50<br><br>  "
				+ "40<br><br>  30<br><br>  20<br><br>  10<br><br>   0<html>");
		track = new GLine(START_X + sfSlider.getValue() * C_WIDTH / 100, START_Y, 
				START_X + sfSlider.getValue() * C_WIDTH / 100, C_HEIGHT + START_Y);

		single = new JRadioButton("Single Change");
		group = new JRadioButton("Group Trend");
		ButtonGroup mode = new ButtonGroup();
		mode.add(single);
		mode.add(group);
		group.setSelected(true);
		group.setActionCommand("Test SF");

		sfSlider.addChangeListener(sfCL);
		sfSlider.setPreferredSize(new Dimension(360, 50));
		sfSlider.setMajorTickSpacing(10);
		sfSlider.setPaintTicks(true);
		sfSlider.setPaintLabels(true);

		outCanvas = new GCanvas();

		instruction1 = new JLabel("Instruction: \n");
		instruction2 = new JLabel("<html>First step is to save all your data file ending with "
				+ "\"data.txt\", enter path of the folder <br>with all files to be analyzed. "
				+ "Press enter or click \"analyze\" will start the analyze. <br>Result will "
				+ "be shown on the console window on the left. Two files (\"result.pi\" and "
				+ "<br> excel workbook \"result.xls\" will also be created in your folder "
				+ "with more concise <br>summary of all the results analyzed. (Note: "
				+ "Smooth factor (SF) if not entered will <br>be treated as default 2)<html>");

		logo = new GImage(IMAGE1);
		imagej1 = new GImage(IMAGE2);
		imagej2 = new GImage(IMAGE3);
		imagej3 = new GImage(IMAGE4);
		xlsx = new GImage(IMAGE5);
		filePI = new GImage(IMAGE6);
		logo.scale(0.5);
		imagej1.scale(0.83);
		imagej2.scale(0.33);
		imagej3.scale(0.25);
		xlsx.scale(0.4);
		fChart = new GRect(C_WIDTH, C_HEIGHT);

		outCanvas.add(instruction1, 20, 10);
		outCanvas.add(instruction2, 20, 40);
		outCanvas.add(logo);
		outCanvas.add(imagej1, 10, 10);
		outCanvas.add(imagej2, 10, 140);
		outCanvas.add(imagej3, 160, 180);
		outCanvas.add(xlsx, 10, 20);
		outCanvas.add(filePI, 280, 130);
		outCanvas.add(sfSlider, 120, 400);
		outCanvas.add(sfPercentage, 10, 400);
		outCanvas.add(sfDynamic, 15, 420);
		outCanvas.add(single, 15, 460);
		outCanvas.add(group, 145, 460);
		outCanvas.add(optimal, 370, 460);
		outCanvas.add(exl, 15, 460);
		outCanvas.add(fChart, START_X, 40);
		outCanvas.add(PILabel, 100, 13);
		outCanvas.add(SILabel, 100, 13);
		outCanvas.add(track, START_X + sfSlider.getValue() * C_WIDTH / 100, START_Y);

		head = new JLabel("Version 1.2. Designed by Baihan Lin, Mentored by "
				+ "Dr. Robyn Laing and Prof. Jaime Olavarria. "
				+ "Send all complaints and suggestions to sunnylin@uw.edu.");
		head.setFont(new java.awt.Font("Arial", 0, 12));
		input = new JLabel("File/Folder:");
		back = new JButton("back to menu");
		path = new JTextField(10);
		path.setActionCommand("Analyze");
		browse = new JButton("Browse");
		start = new JButton("Analyze");
		smoothFactor = new IntField(0, 100);
		smoothFactor.setValue(DEFAULT_SMOOTH_FACTOR);
		smoothFactor.setActionCommand("Smooth Factor");
		sf = new JLabel("SF:");	

		add(head, NORTH);
		add(outCanvas);
		add(about, SOUTH);
		add(patchProcessor, SOUTH);
		add(smoothFinder, SOUTH);
		add(imageJComponent, SOUTH);
		add(back, NORTH);
		add(sf, SOUTH);
		add(smoothFactor, SOUTH);
		add(input, SOUTH);
		add(path, SOUTH);
		add(browse, SOUTH);
		add(start, SOUTH);

		addActionListeners();
		path.addActionListener(this);
		smoothFactor.addActionListener(this);
		sfDynamic.addActionListener(this);

		try {
			restore();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// Listen for the command
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "Patch Processor":
			try {
				doAnalysis();
			} catch (FileNotFoundException e1) {
				println("Please enter valid file or folder (with ...data.txt).");
				e1.printStackTrace();
			}
			break;
		case "Smooth Factor Finder":
			try {
				doSmooth();
			} catch (FileNotFoundException e1) {
				println("Please enter valid file or folder (with ...data.txt).");
				e1.printStackTrace();
			}
			break;
		case "ImageJ Component":
			try {
				doImageJ();
			} catch (FileNotFoundException e1) {
				println("Please enter valid file or folder (with ...data.txt).");
				e1.printStackTrace();
			}
			break;
		case "back to menu":
			println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
			try {
				restore();
			} catch (FileNotFoundException e1) {
				println("Please enter valid file or folder (with ...data.txt).");
				e1.printStackTrace();
			}
			break;
		case "Analyze":
			try {
				piMain();
			} catch (FileNotFoundException e1) {
				println("File or folder not found. Please reenter.");
				println("Please enter valid file or folder (with ...data.txt).");
				e1.printStackTrace();
			} catch (IOException e1) {
				println("File input not valid. Please change it.");
				e1.printStackTrace();
			}
			break;
		case "About":
			try {
				doIntroduction();
			} catch (FileNotFoundException e1) {
				println("Please enter valid file or folder (with ...data.txt).");
				e1.printStackTrace();
			}
			break;
		case "Browse":
			JFileChooser fc = new JFileChooser();  
			fc.setMultiSelectionEnabled(true);  
			fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);  
			fc.showOpenDialog(null);  
			File f = fc.getSelectedFile(); 
			path.setText(f.getAbsolutePath());
			break;
		case "sfDynamic":
			sfSlider.setValue(sfDynamic.getValue());
			break;
		case "Test SF":
			while (!Glines.isEmpty()) {
				outCanvas.remove(Glines.get(0));
				Glines.remove(0);
			}
			Glines.clear();
			String fileName = path.getText();
			File files = new File(fileName);
			if(isValidFile(files)) {
				xlsx.setVisible(false);
				fChart.setVisible(true);
				track.setVisible(true);
				if (group.isSelected()) {
					PILabel.setVisible(true);
					SILabel.setVisible(false);
					Map<String, double[]> curve;
					try {
						curve = calc(files);
						drawGroup(curve);
					} catch (FileNotFoundException e1) {
						println("Please enter valid file or folder (with ...data.txt).");			
						e1.printStackTrace();
					}
				} else {
					PILabel.setVisible(false);
					SILabel.setVisible(true);
					try {
						drawSingle(files);
					} catch (FileNotFoundException e1) {
						println("Please enter valid file or folder (with ...data.txt).");			
						e1.printStackTrace();
					}
				}
			} else {
				println("Please enter valid file or folder (with ...data.txt).");			
			}
			break;
		case "Optimized SF":
			println();
			println("***********************************");
			println("Sorry! This function is still under development!");
			break;
		case "Single Change":
			while (!Glines.isEmpty()) {
				outCanvas.remove(Glines.get(0));
				Glines.remove(0);
			}
			Glines.clear();
			String fileName2 = path.getText();
			File files2 = new File(fileName2);
			if(isValidFile(files2)) {
				xlsx.setVisible(false);
				fChart.setVisible(true);
				try {
					drawSingle(files2);
				} catch (FileNotFoundException e1) {
					println("Please enter valid file or folder (with ...data.txt).");
					e1.printStackTrace();
				}
			} else {
				println("Please enter valid file or folder (with ...data.txt).");
			}
			break;
		}
	}

	// Do patch processor analysis. Calculate and output results
	public void doAnalysis() throws FileNotFoundException {
		restore();
		println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		println("*******************************************");
		println(PPINTRO);
		setFont("Courier-25");	
		println("Important Note: please save your data file ending with \"data.txt\".");
		setFont("Courier-15");

		logo.setVisible(false);;
		patchProcessor.setBackground(Color.gray);;

		browse.setVisible(true);
		filePI.setVisible(true);
		back.setVisible(true);
		path.setVisible(true);
		start.setVisible(true);
		sf.setVisible(true);
		smoothFactor.setVisible(true);
		instruction1.setVisible(true);
		instruction2.setVisible(true);
		input.setVisible(true);
		exl.setVisible(true);

		path.setActionCommand("Analyze");
		start.setActionCommand("Analyze");

		path.addActionListener(this);
		smoothFactor.addActionListener(this);
		addActionListeners();
	}

	// Do exploration of smooth factor
	private void doSmooth() throws FileNotFoundException {
		restore();
		smoothFinder.setBackground(Color.gray);
		xlsx.setVisible(true);
		logo.setVisible(false);
		back.setVisible(true);
		browse.setVisible(true);
		path.setVisible(true);
		start.setVisible(true);
		sfPercentage.setVisible(true);
		sfDynamic.setVisible(true);
		sfSlider.setVisible(true);
		single.setVisible(true);
		group.setVisible(true);
		optimal.setVisible(true);

		path.setActionCommand("Test SF");
		start.setActionCommand("Test SF");

		println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		println("*******************************************");
		println(SFINTRO);
		println("********************");
		println("The analysis will take a while. Please be patient.");


		path.addActionListener(this);
		sfDynamic.addActionListener(this);
		addActionListeners();
	}

	// Do the imageJ component function, open the imagej
	private void doImageJ() throws FileNotFoundException {
		restore();
		logo.setVisible(false);
		imagej1.setVisible(true);
		imagej2.setVisible(true);
		imagej3.setVisible(true);
		back.setVisible(true);
		imageJComponent.setBackground(Color.gray);
		println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		println("*******************************************");
		println(IJINTRO);
		ij.ImageJ.doImageJMain(arguement);
		addActionListeners();		
	}

	// restore the screen to make it back to menu
	private void restore() throws FileNotFoundException {
		imagej1.setVisible(false);
		imagej2.setVisible(false);
		imagej3.setVisible(false);
		about.setVisible(true);
		logo.setVisible(true);;
		Color original = about.getBackground();
		patchProcessor.setBackground(original);
		smoothFinder.setBackground(original);
		imageJComponent.setBackground(original);
		back.setVisible(false);
		path.setVisible(false);
		browse.setVisible(false);
		instruction1.setVisible(false);
		instruction2.setVisible(false);
		input.setVisible(false);
		start.setVisible(false);
		sf.setVisible(false);
		smoothFactor.setVisible(false);
		filePI.setVisible(false);
		xlsx.setVisible(false);
		sfSlider.setVisible(false);
		sfPercentage.setVisible(false);
		sfDynamic.setVisible(false);
		single.setVisible(false);
		group.setVisible(false);
		optimal.setVisible(false);
		exl.setVisible(false);
		fChart.setVisible(false);
		PILabel.setVisible(false);
		SILabel.setVisible(false);
		track.setVisible(false);

		while (!Glines.isEmpty()) {
			outCanvas.remove(Glines.get(0));
			Glines.remove(0);
		}
		Glines.clear();

		path.setActionCommand("");
		start.setActionCommand("");
		path.setText("");
		smoothFactor.setValue(DEFAULT_SMOOTH_FACTOR);
		sfDynamic.setValue(DEFAULT_SMOOTH_FACTOR);
		sfSlider.setValue(DEFAULT_SMOOTH_FACTOR);

		doIntroduction();
	}

	// print out the introduction lines as instruction
	private void doIntroduction() throws FileNotFoundException {
		Scanner input = new Scanner(new File(INTROFILE));
		while (input.hasNextLine()) {
			String line = input.nextLine();
			println(line);
			if (line.startsWith("--\"Patch Processor\"")) {
				PPINTRO = line;
			} else if (line.startsWith("--\"Smooth Factor Finder\"")) {
				SFINTRO = line;
			} else if (line.startsWith("--\"ImageJ Component\"")) {
				IJINTRO = line;
			}			
		}
	}

	// submethod for doSmooth. draw out the single file's changes with smooth factor
	private void drawSingle(File f) throws FileNotFoundException {
		if (isValidFile(f)) {
			while (!Glines.isEmpty()) {
				outCanvas.remove(Glines.get(0));
				Glines.remove(0);
			}
			Glines.clear();
			if (f.isDirectory()) {
				File[] fl = f.listFiles();
				f = firstValid(fl);
			}
			List<String> lines = readLines(f);
			List<Double> data = list2ArrayList(lines);
			drawPlot(data, 0, Color.black);

			PISolver solver = new PISolver(Collections.unmodifiableList(data));
			List<Double> newData = solver.smooth(sfSlider.getValue()); 
			Random rand = new Random();
			int red   = rand.nextInt(206) + 50;
			int green = rand.nextInt(206) + 50;
			int blue  = rand.nextInt(206) + 50;
			Color color = new Color(red, green, blue);
			drawPlot(newData, sfSlider.getValue(), color);
		}
	}

	// submethod for drawPlot, return the first valid file in a directory
	private File firstValid(File[] fl) {
		for (File f : fl) {
			if(isValidFile(f)) {
				return f;
			}
		}
		return null;
	}

	// submethod for drawSingle. draw out a plot given a list of data and graphical pen
	private void drawPlot(List<Double> data, int sf, Color color) {
		double max = max(data);
		for (int i = 0; i < data.size() - 1; i++) {
			double shift = C_WIDTH * (sf / 100.0);
			double x1 = START_X + shift + ((100.0 - sf) / 100.0) * C_WIDTH * i / data.size();
			double x2 = START_X + shift + ((100.0 - sf) / 100.0) * C_WIDTH * (i + 1) / data.size();
			double y1 = START_Y + C_HEIGHT * data.get(i) / max;
			double y2 = START_Y + C_HEIGHT * data.get(i + 1) / max;
			GLine line = new GLine(x1, y1, x2, y2);
			line.setColor(color);
			Glines.add(line);
			outCanvas.add(line, x1, y1);
		}		
	}

	// submethod for doSmooth. return the tallest element from a list
	private double max(List<Double> data) {
		double max = data.get(0);
		for (double e : data) {
			if (e > max) {
				max = e;
			}
		}
		return max;
	}

	// submethod for doSmooth. draw out all the files' changes of PI with SF
	private void drawGroup(Map<String, double[]> curve) {
		while (!Glines.isEmpty()) {
			outCanvas.remove(Glines.get(0));
			Glines.remove(0);
		}
		Glines.clear();
		for (String file : curve.keySet()) {
			double[] result = curve.get(file);
			for (int i = 0; i < 99; i++) {
				double x1 = START_X + i * C_WIDTH / 100;
				double x2 = START_X + (i + 1) * C_WIDTH / 100;
				double y1 = START_Y + C_HEIGHT * (1 - result[i]);
				double y2 = START_Y + C_HEIGHT * (1 - result[i + 1]);
				GLine line = new GLine(x1, y1, x2, y2);
				Random rand = new Random();
				int red   = rand.nextInt(206) + 50;
				int green = rand.nextInt(206) + 50;
				int blue  = rand.nextInt(206) + 50;
				Color color = new Color(red, green, blue);
				line.setColor(color);
				Glines.add(line);
				outCanvas.add(line, x1, y1);
			}
		}
	}

	// submethod for drawGroup. Calculate the PI and return the result in a map
	private Map<String, double[]> calc(File files) throws FileNotFoundException {
		Map<String, double[]> result = new TreeMap<String, double[]>();
		if(isValidFile(files)) {
			if (files.isDirectory()) {
				File allFiles[] = files.listFiles();
				for (File file : allFiles) {
					if(file.getName().toLowerCase().endsWith("data.txt")) {
						result.put(file.getName(), calcSingle(file));
					}
				}
			} else {
				result.put(files.getName(), calcSingle(files));
			}
		} else {
			println();
			println("Please enter valid file or folder (with ...data.txt).");
		}		
		return result;
	}

	// submethod for calc. return a single file's PI with a range of SF
	private double[] calcSingle(File f) throws FileNotFoundException {
		double[] trials = new double[100];
		List<String> lines = readLines(f);
		List<Double> data = list2ArrayList(lines);

		for (int i = 0; i < 100; i++) {
			PISolver solver = new PISolver(Collections.unmodifiableList(data));
			solver.smooth(i); 
			trials[i] = solver.doPICalculation();
		}
		return trials;
	}

	// submethod for doAnalysis. calculate and output file
	public void piMain() throws FileNotFoundException, IOException {
		println("************************************");

		ArrayList<String> fileList = new ArrayList<String>();
		ArrayList<String> IC = new ArrayList<String>();
		ArrayList<String> slice = new ArrayList<String>();
		ArrayList<Double> smList = new ArrayList<Double>();
		ArrayList<Double> siList = new ArrayList<Double>();
		ArrayList<Double> piList = new ArrayList<Double>();

		String fileName = path.getText();
		File files = new File(fileName);
		String dir = getDirectory(files);
		File result1 = new File(dir, OFILE1);
		//		File result2 = new File(dir, OFILE2);
		result1.createNewFile(); 
		//		result2.createNewFile();
		PrintStream output = new PrintStream(result1);

		if(isValidFile(files)) {
			if (files.isDirectory()) {
				output.println("result for directory: " + fileName);
				output.println("FileName	SM	SI	PI=1-SM/SI");
				output.println("-----------------------------------");
				File allFiles[] = files.listFiles();
				for (File file : allFiles) {
					String calculated = generate(file);
					if (!calculated.equals("File doesn't exist.")) {
						output.println(calculated);
						String dataFiles = calculated.split("data.txt")[0];
						String[] nameElements = dataFiles.split(" ");
						String nameAll = "";
						for (int i = 0; i < nameElements.length; i++) {
							String isIC = nameElements[i];
							if (isIC.equals("I") || isIC.equals("C")) {
								IC.add(isIC);
							} else if (isIC.startsWith("s") && isIC.length() == 2) {
								slice.add(isIC);
							} else {
								nameAll = nameAll + isIC + " ";
							}
						}
						String[] dataArray = calculated.split("data.txt")[1].split("[ \t]+");
						fileList.add(nameAll);
						smList.add(Double.parseDouble(dataArray[1]));
						siList.add(Double.parseDouble(dataArray[2]));
						piList.add(Double.parseDouble(dataArray[3]));						
					}
				}
			} else {
				output.println("result for file: " + fileName);
				output.println("FileName	SM	SI	PI=1-SM/SI");
				output.println("-----------------------------------");
				String calculated = generate(files);
				output.println(calculated);
				String dataFiles = calculated.split("data.txt")[0];
				String[] nameElements = dataFiles.split("[\t]+");
				String nameAll = "";
				for (int i = 0; i < nameElements.length; i++) {
					String isIC = nameElements[i];
					if (isIC.equals("I") || isIC.equals("C")) {
						IC.add(isIC);
					} else if (isIC.startsWith("s")) {
						slice.add(isIC);
					} else {
						nameAll += (isIC + " ");
					}
				}
				String[] dataArray = calculated.split("data.txt")[1].split("[ \t]+");
				fileList.add(nameAll);
				smList.add(Double.parseDouble(dataArray[1]));
				siList.add(Double.parseDouble(dataArray[2]));
				piList.add(Double.parseDouble(dataArray[3]));						
			}
		} else {
			println();
			println("Please enter valid file or folder (with ...data.txt).");
		}
		output.close();
		excelFD.addData("File", fileList);
		excelFD.addData("Slice", slice);
		excelFD.addData("I/C", IC);
		excelFD.addSubData("SM", smList);
		excelFD.addSubData("SI", siList);
		excelFD.addSubData("Patch Index (PI)", piList);
		//		excelFD.outputExcel(OFILE2);
		excelFD.outputExcel(OFILE2, dir);
		//		FileOutputStream fos = new FileOutputStream(OFILE2);  
		//		excelFD.exportExcel(fos);  

		// print on screen for excel if checked
		if (exl.isSelected()) {
			tempPrintS("File", fileList);
			tempPrintS("Slice", slice);
			tempPrintS("I/C", IC);
			tempPrintD("SM", smList);
			tempPrintD("SI", siList);
			tempPrintD("Patch Index (PI)", piList);
		}

	}

	// submethod for piMain, print out excel-useful data
	private void tempPrintD(String fieldName, ArrayList<Double> list) {
		println("+++++++++++++++++");
		println(fieldName);
		for (Double item : list) {
			println(item);
		}
	}

	// submethod for piMain, print out excel-useful data
	private void tempPrintS(String fieldName, ArrayList<String> list) {
		println("+++++++++++++++++");
		println(fieldName);
		for (String item : list) {
			println(item);
		}
	}

	// submethod for doAnalysis and doSmooth. test whether a file is usable or not
	private boolean isValidFile(File f) {
		if (f.isDirectory()) {
			String[] fl = f.list();
			if (fl.length != 0) {
				for (int i = 0; i < fl.length; i++) {
					if (fl[i].endsWith("data.txt")) {
						return true;
					}
				}
			}
		} else {
			if (f.getName().endsWith("data.txt")) {
				return true;
			}
		}
		return false;
	}

	// submethod for doAnalysis. Return a folder directory of a given file or folder.
	private String getDirectory(File f) {
		if (f.isDirectory()) {
			return f.getPath();
		} else {
			String allPath = f.getPath();
			int indexOfHash = allPath.lastIndexOf("\\");
			String trimedPath = allPath.substring(0, indexOfHash);
			if (indexOfHash == allPath.length() - 1) {
				trimedPath = trimedPath.substring(0, trimedPath.lastIndexOf("\\"));				
			}
			return trimedPath;
		}
	}

	// submethod for doAnalysis. Return the output result in a string
	private String generate(File file) throws FileNotFoundException {

		String fileName = file.getName();
		String output = "File doesn't exist.";
		if (fileName.toLowerCase().endsWith("data.txt")) {
			println("-----------------------------");
			println(fileName);

			List<String> lines = readLines(file);
			List<Double> data = list2ArrayList(lines);
			// construct Patch Index solver and begin user input loop
			PISolver solver = new PISolver(Collections.unmodifiableList(data));

			// do the calculations on patch index solver: Patch Index = 1 - (SM / SI)
			solver.smooth(smoothFactor.getValue());  
			String sm = String.valueOf(solver.getSM());
			String si = Double.toString(solver.getSI());
			String pi = Double.toString(solver.doPICalculation());

			output = fileName + "	" + sm + "	" + si  + "	" + pi;
			println("SM: " + sm);
			println("SI: " + si);
			println("PI: " + pi);		
		}
		return output;
	}

	// submethod for doAnalysis. transfer a file into a list
	private static List<String> readLines(File file) throws FileNotFoundException {
		List<String> lines = new ArrayList<String>();
		Scanner input = new Scanner(file);
		while (input.hasNextLine()) {
			String line = input.nextLine().trim();
			if (line.length() > 0) {
				lines.add(line);
			}
		}
		return lines;
	}

	// transfer the list of data into a list of X-Y correlations
	private static List<Double> list2ArrayList(List<String> data) {
		if (data == null) {  
			throw new IllegalArgumentException();
		}
		List<Double> grayValue = new ArrayList<Double>();
		for (String element : data) {
			int index = Integer.parseInt(element.split("[ \t]+")[0]);
			double value = Double.parseDouble(element.split("[ \t]+")[1]);
			grayValue.add(index, value);	
		}
		return grayValue; 
	}

}

