package org.jorigin.jis.io.gml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.geotools.feature.FeatureCollection;
import org.geotools.gml2.GMLConfiguration;
import org.geotools.xsd.Parser;
import org.xml.sax.SAXException;


/**
 * A class that handles <a href="http://www.opengeospatial.org/standards/gml">GML</a> document read.
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 * @see GMLIO
 * @see GMLWriter
 */
public class GMLReader {

  
  /**
	* The version of the specification used by the writer to produce a GML file.
	* <code>gmlVersion</code> can be {@link org.jorigin.gis.io.gml.GMLIO#GML_2} or 
	* {@link org.jorigin.gis.io.gml.GMLIO#GML_3}. Default value
	* is {@link org.jorigin.gis.io.gml.GMLIO#GML_3}.
	*/
  private int gmlVersion          = GMLIO.GML_3;
  
  /**
   * Read a {@link org.geotools.feature.FeatureCollection feature collection} from an {@link java.io.InputStream input stream} that maps a GML document.
   * @param is the {@link java.io.InputStream input stream} that maps a GML document.
   * @return the {@link org.geotools.feature.FeatureCollection feature collection} representing the GML document content.
   * @throws IOException if an error occurs.
   */
  public FeatureCollection<?, ?> read(InputStream is)
  throws IOException {
    
    //create the parser with the gml 2.0 configuration
    GMLConfiguration configuration = new GMLConfiguration();
    Parser parser = new Parser(configuration);
    
    //parse
    try {
      FeatureCollection<?, ?> fc = (FeatureCollection<?,?>) parser.parse(is);
      return fc;
      
    } catch (IOException e) {
      throw new IOException("Cannot read GML from input stream"+is, e);
    } catch (SAXException e) {
      throw new IOException("Cannot read GML from input stream"+is, e);
    } catch (ParserConfigurationException e) {
      throw new IOException("Cannot read GML from input stream"+is, e);
    }
 
  }
  
  /**
   * Set the handled GML version.
   * @param gmlVersion the GML version to handle.
   * @see #getGMLVersion()
   */
  public void setGMLVersion(int gmlVersion){
    this.gmlVersion = gmlVersion;
  }
  
  /**
   * Set the handled GML version.
   * @return the handled GML version.
   * @see #setGMLVersion(int)
   */
  public int getGMLVersion(){
	  return this.gmlVersion;
  }
}
