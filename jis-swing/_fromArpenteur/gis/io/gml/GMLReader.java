package org.arpenteur.gis.io.gml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.geotools.feature.FeatureCollection;
import org.xml.sax.SAXException;

public class GMLReader {

  
  
  
  public FeatureCollection read(InputStream is)
  throws IOException {
    
    //create the parser with the gml 2.0 configuration
    org.geotools.xml.Configuration configuration = new org.geotools.gml2.GMLConfiguration();
    org.geotools.xml.Parser parser = new org.geotools.xml.Parser(configuration);
    
    //parse
    try {
      FeatureCollection fc = (FeatureCollection) parser.parse(is);
      return fc;
      
    } catch (IOException e) {
      throw new IOException("Cannot read GML from input stream"+is, e);
    } catch (SAXException e) {
      throw new IOException("Cannot read GML from input stream"+is, e);
    } catch (ParserConfigurationException e) {
      throw new IOException("Cannot read GML from input stream"+is, e);
    }
 
  }
  
}
