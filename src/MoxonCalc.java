package moxoncalc;

import java.lang.*;
import java.util.*;
import java.text.DecimalFormat;

/*
 * The online Moxon calculators at http://tippete.net/cgi-bin/moxgen.pl and 
 * http://ab1jx.1apps.com/ham/calcs/moxon/ provided both inspiration and helped
 * get me going in the right direction.
 *
 */

public class MoxonCalc {
	public double freq;
	public double wireSize;
	public double aWire;
	public double bWire;
	public double cWire;
	public double dWire;
	public double eWire;

	// constants(-ish) for calculating dimensions
	static final double a1 = -0.0008571428571;
	static final double a2 = -0.009571428571;
	static final double a3 = 0.3398571429;
	static final double b1 = -0.002142857143;
	static final double b2 = -0.02035714286;
	static final double b3 = 0.008285714286;
	static final double c1 = 0.001809523381;
	static final double c2 = 0.01780952381;
	static final double c3 = 0.05164285714;
	static final double d1 = 0.001;
	static final double d2 = 0.07178571429;
	static final double dd1cal = 0.4342945;
	static final double cMeters = 299.7925;
	static final double cMillimeters = 299792.5;
	static final double cFeet = 983.5592;
	static final double cInches = 11802.71;

	static final String version = "0.2.0";

	private enum DisplayUnit {
		METER, MM, FEET, INCH, WL
	}

	private enum WireUnit {
		AWG, INCH, MM, WL
	}

	private enum OutputFormat {
		FULL, SHORT, JSON
	}

	private double dd1;
	private DisplayUnit dispUnit;
	private WireUnit wireUnit;

	public static void main(String[] args) {
		// Process arguments
		int argLen = args.length;
		double frequency = 0, wire = 0;
		String dunits = "";
		String wunits = "";
		String formatOut = "";
		for (int i = 0; i < argLen; i++) {
			if (args[i].startsWith("-", 0)) {
				String flag = args[i];
				i++;
				String argument;
				if (i < argLen) {
					argument = args[i];
				} else {
					argument = "";
				}
				switch (flag) {
					case "-f":
						frequency = Double.parseDouble(argument);
						break;
					case "-d":
						wire = Double.parseDouble(argument);
						break;
					case "-u":
						dunits = argument;
						break;
					case "-w":
						wunits = argument;
						break;
					case "-F":
						formatOut = argument;
						break;
					case "-h":
						System.out.println(helpMessage());
						System.exit(0);
					case "-v":
						System.out.println("moxoncalc Moxon antenna calculator version " + version);
						System.exit(0);
					default:
						System.err.printf("invalid argument %s\n", flag);
						System.exit(-1);
				}
			}
		}

		if (frequency == 0) {
			frequency = 14.0;
		}
		if (wire == 0) {
			wire = 12;
		}

		// initialize our antenna calculator
		MoxonCalc m = null;
		try {
			m = new MoxonCalc(frequency, wire, dunits, wunits);
		} catch (Exception e) {
			System.err.println("Caught exception initializing antenna: " + e.getMessage());
			System.exit(-2);
		}

		// run the calculation
		m.calcAntenna();

		OutputFormat fmt = null;
		try {
			fmt = setOutputFormat(formatOut);
		} catch (Exception e) {
			System.err.println("Caught exception setting output format: " + e.getMessage());
			System.exit(-5);
		}

		DecimalFormat df = new DecimalFormat();
		String wireDisp = m.wireDisplayUnit();
		
		switch (fmt) {
			case FULL:
				// FULL: display the diagram & numbers
				System.out.printf("Diagram for Moxon antenna for %s MHz, wire %s %s, in %s.\n\n", df.format(m.freq), df.format(wire), wireDisp, m.displayUnit());
				System.out.println(m.antennaPicture());
				System.out.println(m.antennaSidesTable());
				break;
			case SHORT:
				// Display minimal information, without the
				// diagram of the antenna
				System.out.printf("%s MHz %s %s %s\n", df.format(m.freq), df.format(wire), wireDisp, m.displayUnit());
				System.out.println(m.antennaSidesTable());
				break;
			case JSON:
				// Print out a JSON representation of the short
				// display
				HashMap<String, String> hm = new HashMap<>();
				hm.put("freq", df.format(m.freq));
				hm.put("wire_size", df.format(wire));
				hm.put("wire_unit", wireDisp);
				hm.put("size_unit", m.displayUnit());
				hm.put("A", m.formatWireLength(m.aWire));
				hm.put("B", m.formatWireLength(m.bWire));
				hm.put("C", m.formatWireLength(m.cWire));
				hm.put("D", m.formatWireLength(m.dWire));
				hm.put("E", m.formatWireLength(m.eWire));
				System.out.printf("{\n\t\"moxon\": {\n");
				Set set = hm.entrySet();
				Iterator it = set.iterator();
				String [] jsonRows = new String[hm.size()];
				int cnt = 0;
				while (it.hasNext()) {
					Map.Entry me = (Map.Entry)it.next();
					jsonRows[cnt++] = String.format("\t\t\"%s\": \"%s\"", me.getKey(), me.getValue());
				}
				System.out.println(join(jsonRows, ",\n"));
				System.out.println("\t}\n}");
				break;
		}
	}

	public MoxonCalc (double frequency, double wireDiameter, String dunit, String wunit) throws Exception {
		freq = frequency;
		dispUnit = getDispUnitFromArg(dunit);
		wireUnit = getWireUnitFromArg(wunit);

		wireSize = calcWireSize(wireDiameter, freq, wireUnit);
		
		dd1 = dd1cal * Math.log(wireSize);
	}

	public void calcAntenna() {
		aWire = calcA();
		bWire = calcB();
		cWire = calcC();
		dWire = calcD();
		eWire = calcE();
	}

	public double displayMultiplier() {
		double c;
		switch(dispUnit) {
			case METER:
				c = cMeters;
				break;
			case MM:
				c = cMillimeters;
				break;
			case FEET:
				c = cFeet;
				break;
			case INCH:
				c = cInches;
				break;
			default:
				return 1;
		}
		return c / freq;
	}

	public String displayUnit() {
		String u;
		switch(dispUnit) {
			case METER:
				u = "meters";
				break;
			case MM:
				u = "millimeters";
				break;
			case FEET:
				u = "feet";
				break;
			case INCH:
				u = "inches";
				break;
			default:
				u = "wavelengths";
		}
		return u;
	}

	public String shortDisplayUnit() {
		String u;
		switch(dispUnit) {
			case METER:
				u = " m";
				break;
			case MM:
				u = " mm";
				break;
			case FEET:
				u = "'";
				break;
			case INCH:
				u = "\"";
				break;
			default:
				u = " wl";
		}
		return u;
	}

	public String wireDisplayUnit() {
		String wu;
		switch (wireUnit) {
			case AWG:
				wu = "AWG";
				break;
			case INCH:
				wu = "inch";
				break;
			case MM:
				wu = "mm";
				break;
			default:
				wu = "wavelengths";
				break;
		}
		return wu;
	}

	public String antennaPicture() {
		// define some variables with chars to draw the box
		char ltCorner = '\u250f';
		char rtCorner = '\u2513';
		char lbCorner = '\u2517';
		char rbCorner = '\u251b';
		char hLine = '\u2501';
		char vLine = '\u2503';
		char hDotLine = '\u2505';
		char vDotLine = '\u2507';

		char upArrow = '\u2191';
		char downArrow = '\u2193';
		char leftArrow = '\u2190';
		char rightArrow = '\u2192';

		int boxWidth = 71;
		int boxHeight = 15;

		String[] boxSides = new String[15];

		// very top - dotted line with a in the middle
		char[] aDotted = new char[boxWidth];
		aDotted[0] = leftArrow;
		aDotted[boxWidth - 1] = rightArrow;
		for (int i = 1; i < boxWidth - 2; i++) {
			aDotted[i] = hDotLine;
			i++;
			aDotted[i] = ' ';
		} 
		int aDotHalf = (int)Math.ceil((double)boxWidth / 2);
		aDotted[aDotHalf] = 'A';
		
		boxSides[0] = new String(aDotted);
	
		// top of box
		char[] topLine = new char[boxWidth];
		topLine[0] = ltCorner;
		topLine[boxWidth - 1] = rtCorner;
		for (int i = 1; i < boxWidth - 1; i++) {
			topLine[i] = hLine;
		}
		boxSides[1] = new String(topLine);


		// bottom line
		char []bottomLine = new char[boxWidth];
		bottomLine[0] = lbCorner;
		bottomLine[boxWidth - 1] = rbCorner;
		for (int i = 1; i < boxWidth - 1; i++) {
			bottomLine[i] = hLine;
		}
		boxSides[boxHeight - 1] = new String(bottomLine);
		int bLen = 7;
		int cLen = 3;
		int dLen = bLen + cLen;

		// B side
		for (int j = 2; j < bLen; j++) {
			char[] line = new char[boxWidth];
			line[0] = vLine;
			line[boxWidth - 1] = vLine;
			for (int i = 1; i < boxWidth - 1; i++) {
				line[i] = ' ';
			}
			boxSides[j] = new String(line);
		}

		// C "side" (gap, really)
		for (int j = bLen; j < bLen + cLen; j++) {
			char[] line = new char[boxWidth];
			for (int i = 0; i < boxWidth; i++) {
				line[i] = ' ';
			}
			boxSides[j] = new String(line);
		}

		// D side
		for (int j = bLen + cLen; j < boxHeight - 1; j++) {
			char[] line = new char[boxWidth];
			line[0] = vLine;
			line[boxWidth - 1] = vLine;
			for (int i = 1; i < boxWidth - 1; i++) {
				line[i] = ' ';
			}
			boxSides[j] = new String(line);
		}

		// dotted lines to illustrate B, C, D, and E
		char[][] bcdeLines = new char[boxHeight - 1][];
		int bcdeWidth = 5;
		for (int i = 0; i < boxHeight - 1; i++){
			bcdeLines[i] = new char[bcdeWidth];
			bcdeLines[i][0] = ' ';
			bcdeLines[i][2] = ' ';
			bcdeLines[i][3] = ' ';
		}
		bcdeLines[0][1] = upArrow;
		bcdeLines[0][4] = upArrow;
		bcdeLines[boxHeight - 2][1] = downArrow;
		bcdeLines[boxHeight - 2][4] = downArrow;

		// fill in the interior dotted lines
		for (int i = 1; i < boxHeight - 2; i++) {
			bcdeLines[i][1] = vDotLine;
			bcdeLines[i][4] = vDotLine;
		}
		// set up arrows to end B, show C, and start D
		bcdeLines[bLen - 2][1] = downArrow;
		bcdeLines[bLen - 1][1] = upArrow;
		bcdeLines[bLen + cLen - 2][1] = downArrow;
		bcdeLines[bLen + cLen - 1][1] = upArrow;

		// and label
		int bDotHalf = (int)Math.ceil((double)bLen / 2);
		bcdeLines[bDotHalf - 2][1] = 'B';
		int cDotHalf = bLen + (int)Math.ceil((double)cLen / 2);
		bcdeLines[cDotHalf - 2][1] = 'C';

		int dLineLen = boxHeight - bLen - cLen - 1;
		int dDotHalf = bLen + cLen + (int)Math.ceil((double)dLineLen / 2);
		bcdeLines[dDotHalf - 1][1] = 'D';

		bcdeLines[bcdeLines.length / 2][4] = 'E';

		// add dotted lines to box
		for (int i = 1; i < boxHeight; i++) {
			boxSides[i] += new String(bcdeLines[i - 1]);
		}

		String picture = join(boxSides, "\n");
		return picture;
	}

	// a text listing of the length of the different pieces of the antenna
	public String antennaSidesTable() {
		String[] sides = new String[5];
		sides[0] = String.format("A: %s", formatWireLength(aWire));
		sides[1] = String.format("B: %s", formatWireLength(bWire));
		sides[2] = String.format("C: %s", formatWireLength(cWire));
		sides[3] = String.format("D: %s", formatWireLength(dWire));
		sides[4] = String.format("E: %s", formatWireLength(eWire));

		String table = join(sides, "\n");
		return table;
	}

	public String formatWireLength(double wireSide) {
		double wm = displayMultiplier();
		String sd = shortDisplayUnit();
		return String.format("%.4f%s", wireSide * wm, sd);
	}

	// private methods

	private static String join(String[] strArr, String joiner) {
		String combined = strArr[0];
		for (int i = 1; i < strArr.length; i++) {
			combined = combined + joiner + strArr[i];
		}
		return combined;
	}

	private double calcBase(double f1, double f2, double f3, double dd1) {
		double val;
		val = (f1 * Math.pow(dd1, 2.0)) + (f2 * dd1) + f3;
		return val;
	}

	private double calcA() {
		return calcBase(a1, a2, a3, dd1);
	}

	private double calcB() {
		return calcBase(b1, b2, b3, dd1);
	}

	private double calcC() {
		return calcBase(c1, c2, c3, dd1);
	}

	private double calcD() {
		return (d1 * dd1) + d2;
	}

	private double calcE() {
		return calcB() + calcC() + calcD();
	}

	private static WireUnit getWireUnitFromArg(String unit) throws Exception {
		WireUnit u;

		switch (unit.toLowerCase()) {
			case "a":
			case "awg":
			case "":
				u = WireUnit.AWG;
				break;
			case "mm":
				u = WireUnit.MM;
				break;
			case "i":
			case "inch":
				u = WireUnit.INCH;
				break;
			case "wl":
				u = WireUnit.WL;
				break;
			default:
				throw new Exception(String.format("invalid wire size unit %s", unit));
		}

		return u;
	}

	private static DisplayUnit getDispUnitFromArg(String unit) throws Exception {
		DisplayUnit u;

		switch (unit.toLowerCase()){
			case "meter":
			case "m":
			case "":
				u = DisplayUnit.METER;
				break;
			case "mm":
				u = DisplayUnit.MM;
				break;
			case "f":
			case "feet":
				u = DisplayUnit.FEET;
				break;
			case "i":
			case "inch":
				u = DisplayUnit.INCH;
				break;
			case "wl":
				u = DisplayUnit.WL;
				break;
			default:
				throw new Exception(String.format("invalid display unit %s", unit));
		}
		return u;
	}

	private static OutputFormat setOutputFormat(String formatOut) throws Exception {
		OutputFormat o;
		switch (formatOut.toLowerCase()) {
			case "full":
			case "":
				o = OutputFormat.FULL;
				break;
			case "short":
				o = OutputFormat.SHORT;
				break;
			case "json":
				o = OutputFormat.JSON;
				break;
			default:
				throw new Exception(String.format("invalid format option %s", formatOut));
		}
		return o;
	}

	private double calcWireSize(double diam, double freq, WireUnit wu) {
		double ws = 0;
		
		switch (wu) {
			case AWG:
				ws = wireSizeAWG(diam, freq);
				break;
			case MM:
				ws = wireSizeMM(diam, freq);
				break;
			case INCH:
				ws = wireSizeInch(diam, freq);
				break;
			default:
				ws = diam;
		}
		return ws;
	}

	private double wireSizeAWG(double diam, double freq) {
		double zi = 0.005 * Math.pow(92, (36 - diam) / 39.0);
		double ws = zi / (cInches / freq);
		return ws;
	}

	private double wireSizeMM(double diam, double freq) {
		double ws = diam / (cMillimeters / freq);
		return ws;
	}

	private double wireSizeInch(double diam, double freq) {
		double ws = diam / (cInches / freq);
		return ws;
	}
	
	private static String helpMessage() {
		String helpMsg =  "Usage:\n"
				+ "  java moxoncalc-<version>.jar [options]\n\n"
				+ "Application options:\n"
				+ "-f:\tFrequency of antenna in megaHertz (Defaults to 14.0 MHz).\n"
				+ "-d:\tDiameter of wire (specify unit with -w) (Defaults to 14).\n"
				+ "-u:\tUnits to use to display output. Choices are 'meter' (or 'm'), 'mm' (for millimeters), 'feet' (or 'f'), 'inch' (or 'i'), and 'wl' (for wavelength). Defaults to 'meter'.\n"
				+ "-w:\tWire size units. Choices are 'AWG' (or 'a', or 'awg'), 'mm' (for millimeters), 'inch' (or 'i'), and 'wl' (for wavelength). Defaults to 'AWG'.\n"
				+ "-F:\tFormat for output. Choices are 'full' (includes diagram and the lengths of the antenna components), 'short' (only the lenghts of the components, along with the frequency and wire size), and 'json' (a JSON representation of 'short'). Defaults to 'full'."
				+ "\n-v:\tPrint version number.\n"
				+ "-h:\tPrint this help message.\n";
		return helpMsg;
	}
}
