package utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import config.IOSetup;

/**
 * Class with methods for input/output
 * 
 * @author Andraz
 */
public class IOUtils {

    private IOUtils() {}

    /**
     * prefix of files that contain whites average DTM difference from optimal
     * move.
     */
    public static final String WHITE_DTM_DIFFERENCE_FILE_NAME          = "whiteDTMDiff";

    /**
     * Prefix of files that contain number MCTS tree collapses.
     */
    public static final String NUMBER_OF_MCTS_TREE_COLLAPSES_FILE_NAME = "numberOfMCTSCollapses";

    /** Prefix of files that contain all output parameters */
    public static final String ULTIMATE_FILE_NAME                      = "all";

    /**
     * Prefix of files that contain (average) MCTS tree size
     */
    public static final String TREE_SIZE_FILE_NAME                     = "treeSize";

    /** Prefix of files that represent data connected to game length. */
    public static final String GAME_LENGTH_FILE_NAME                   = "gameLength";

    /** Prefix of files that represent white players success rate. */
    public static final String WHITE_SUCCESS_RATE_FILE_NAME            = "whiteSuccessRate";

    /** Default height for saving graphics */
    public static final int    DEFAULT_GRAPH_HEIGHT                    = 500;

    /** Default width for saving graphics */
    public static final int    DEFAULT_GRAPH_WIDTH                     = 1200;


    /**
     * Writes string into file
     * 
     * @param fileName
     *            file in which string will be written
     * @param input
     *            string to be written in file
     */
    public static void writeToFile(String fileName, String input) {
        try {

            FileWriter fw = new FileWriter(new File(fileName));
            fw.write(input);
            fw.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Writes CSV file
     * 
     * @param filePath
     *            file path
     * @param columnNames
     *            column names of CSV file (first line)
     * @param data
     *            data that will be written to CSV
     */
    public static void writeCSV(String filePath, Vector<String> columnNames, Vector<Vector<Object>> data) {
        StringBuffer sb = new StringBuffer();

        // generate first line in file
        for (String columnName : columnNames) {
            sb.append(columnName + "\t");
        }

        // generate CSV data
        for (Vector<Object> row : data) {
            sb.append("\r\n");
            for (Object columnData : row) {
                sb.append(columnData.toString() + "\t");
            }
        }

        // write to a file
        IOUtils.writeToFile(filePath, sb.toString());
    }


    /**
     * Saves {@link JFreeChart} as jpg picture to desired location
     * 
     * @param filePath
     *            file to which generated picture will be saved
     * @param chart
     *            {@link JFreeChart} that we want to save
     */
    public static void saveChart(String filePath, JFreeChart chart) {
        try {
            ChartUtilities.saveChartAsJPEG(new File(filePath), chart, DEFAULT_GRAPH_WIDTH, DEFAULT_GRAPH_HEIGHT);
        }
        catch (IOException e) {
            System.err.println("Could not save " + filePath);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * Reads data from xml file.
     * 
     * @param file
     *            xml file
     * @return vector of label loaded from xml file
     * @throws IllegalArgumentException
     *             if file does not exist
     */
    public static ArrayList<String> readXMLFile(String file) throws IllegalArgumentException {
        ArrayList<String> ret = new ArrayList<String>();
        File xmlFile = new File(file);
        if (!xmlFile.exists()) { throw new IllegalArgumentException("File does not exist"); }

        try {
            // initialize document reader
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(xmlFile);
            document.getDocumentElement().normalize();

            // fill logic with labels
            NodeList experimentList = document.getElementsByTagName("Experiment");
            for (int i = 0; i < experimentList.getLength(); i++) {
                Node label = experimentList.item(i).getFirstChild().getFirstChild();
                ret.add(label.getNodeValue());
            }
        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
            System.exit(1);
        }
        catch (SAXException e) {
            e.printStackTrace();
            System.exit(1);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return ret;
    }


    /**
     * Creates new xml file.
     * 
     * @param path
     *            file path
     * @throws IllegalArgumentException
     *             if xml file already exists
     */
    public static void createNewSettingsXMLFile(String path) {

        try {

            File xmlFile = new File(path);
            if (xmlFile.exists()) { throw new IllegalArgumentException("File " + xmlFile.getAbsolutePath() + " already exists"); }

            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();

            Document doc = documentBuilder.newDocument();

            // create root node
            Element root = doc.createElement("MCTSTests");
            doc.appendChild(root);

            Element experimentElement = doc.createElement("Experiment");
            root.appendChild(experimentElement);

            // create id node with default value
            Element labelNode = doc.createElement("label");
            experimentElement.appendChild(labelNode);
            labelNode.appendChild(doc.createTextNode("none"));

            // put default label to logic
            IOSetup.testLabels.add(IOSetup.DEFAULT_TEST_LABEL);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            DOMSource domSource = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(xmlFile);

            transformer.transform(domSource, streamResult);

        }
        catch (ParserConfigurationException e) {
            e.printStackTrace();
            System.exit(1);
        }
        catch (TransformerConfigurationException e) {
            e.printStackTrace();
            System.exit(1);
        }
        catch (TransformerException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
