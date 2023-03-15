
import java.io.*; 

import org.xml.sax.*;
import org.xml.sax.helpers.*;       

import org.w3c.dom.*;

import org.apache.xerces.dom.*;
import org.apache.xerces.parsers.*;

import org.apache.xalan.xslt.*;

///////////////////////////////////////////////////////////////////////
/** 
 * Runs an XML file through the Xalan XSL processor and write the 
 * resulting document to an output file. Can also be used to run 
 * an XML file through the Xerces XML parser which can be useful 
 * as a test for well-formedness.
 *  
 * @author David M. Johnson
 * @version $Revision: 1.1.2.1 $
 */
public class runxsl {

   //-----------------------------------------------------------------
   public static void main(String args[]) {
      new runxsl().doMain(args);
   }
   //-----------------------------------------------------------------
   public void doMain(String args[]) {
      if (args.length == 3) {
         runXSLProcessor(args);
      }
      else if (args.length == 1) {
         runXMLParser(args);
      }
      else {
         System.out.println("Usage: java runxsl <xmlfile> <xslfile> <outfile>");
         System.out.println(
            "   Runs xmlfile through Xalan using xslfile, outputs to outfile");
         System.out.println("-- OR --");
         System.out.println("Usage: java runxsl <xmlfile>");
         System.out.println("   Run xmlfile through Xerces parser."); 
         return;
      }  
   }
   //-----------------------------------------------------------------
   /** Runs XML file through XSL transform, writes output to file. */
   public void runXSLProcessor(String args[]) {
      try {
         XSLTProcessor processor = XSLTProcessorFactory.getProcessor();
         processor.process(
            new XSLTInputSource(args[0]),
            new XSLTInputSource(args[1]),
            new XSLTResultTarget(new FileOutputStream(args[2])));
         System.out.println("Transform complete.");
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }
   //-----------------------------------------------------------------
   /** Parse XML file, print exceptions to standard out. */
   public void runXMLParser(String args[]) {
      if (args.length < 1) {
         System.out.println("Usage: java main <filename>");
         return;
      }  
      try {
         File inputFile = new File(args[0]); 
         FileInputStream is = new FileInputStream(inputFile); 
         SAXParser saxParser = new SAXParser();
         saxParser.setContentHandler(new DefaultHandler() {
            public void endDocument() {
               System.out.println("Parsing complete.");
            }
         });
         saxParser.parse(new InputSource(is));
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }
}

