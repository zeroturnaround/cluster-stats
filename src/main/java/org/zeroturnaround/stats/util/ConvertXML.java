package org.zeroturnaround.stats.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Helper utility program to analyze plugins xml file for inconsistencies. This program
 * will convert the XML file provided as an argument into a CSV that you can have your way
 * in some spreadsheet software of choise.
 */
public class ConvertXML {

  private static final String DURATION = "<duration>";
  private static final String STARTED = "<started>";
  private static final String TIME_IN_QUEUE = "<timeInQueue>";
  private static final String CSV_SEPARATOR = ";";

  private static final Set<String> interestingFields = new TreeSet<String>();
  static {
    interestingFields.add("<projectName>");
    interestingFields.add("<nodeName>");
    interestingFields.add(DURATION);
    interestingFields.add(STARTED);
    interestingFields.add(TIME_IN_QUEUE);
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.err.println("Please specify file to process as the first argument");
      System.err.println("Exiting...");
      System.exit(0);
    }

    File inputFile = new File(args[0]);
    System.err.println("Processing " + inputFile.getCanonicalPath());

    processXMLStats(inputFile, System.out);
  }

  public static void processXMLStats(File inputFile, PrintStream stream) throws FileNotFoundException, IOException {
    printCSVHeader(interestingFields, stream);
    printCSVBody(inputFile, stream);
  }

  private static void printCSVBody(File inputFile, PrintStream stream) throws FileNotFoundException, IOException {
    BufferedReader br = new BufferedReader(new FileReader(inputFile));
    try {
      String line = null;

      // Lets use the TreeMap for always correct ordering
      Map<String, String> lineInfo = new TreeMap<String, String>();
      while ((line = br.readLine()) != null) {
        line = line.trim();

        String tagName = line.substring(0, line.indexOf(">") + 1);
        if (interestingFields.contains(tagName)) {
          processRawLine(line, lineInfo);
        }

        if ("<org.zeroturnaround.stats.model.RunStats>".equals(line)) {
          lineInfo = new TreeMap<String, String>();
        }
        else if ("</org.zeroturnaround.stats.model.RunStats>".equals(line)) {
          printCSVLine(lineInfo, stream);
        }
      }
    }
    finally {
      br.close();
    }
  }

  private static void processRawLine(String line, Map<String, String> lineInfo) {
    String data = processLine(line);
    if (line.contains(TIME_IN_QUEUE) || line.contains(DURATION)) {
      long value = Long.parseLong(data);
      // lets convert to seconds
      lineInfo.put(line, (value / 1000) + "");
    }
    else if (line.contains(STARTED)) {
      long value = Long.parseLong(data);
      Calendar cal = new GregorianCalendar();
      cal.setTimeInMillis(value);
      lineInfo.put(line, cal.getTime() + "");
    }
    else {
      lineInfo.put(line, data);
    }
  }

  private static void printCSVLine(Map<String, String> lineInfo, PrintStream stream) {
    for (Iterator<String> ite = lineInfo.values().iterator(); ite.hasNext();) {
      String entry = ite.next();
      stream.print(entry);
      if (ite.hasNext())
        stream.print(CSV_SEPARATOR);
    }
    stream.println();
  }

  private static void printCSVHeader(Set<String> interestingFields, PrintStream stream) {
    for (Iterator<String> ite = interestingFields.iterator(); ite.hasNext();) {
      String field = (String) ite.next();
      stream.print(field);
      if (ite.hasNext())
        stream.print(CSV_SEPARATOR);
    }
    stream.println();
  }

  private static String processLine(String line) {
    int idx = line.indexOf(">") + 1;
    line = line.substring(idx);
    idx = line.indexOf("<");
    line = line.substring(0, idx);
    return line.trim();
  }
}
