package org.arpenteur.gis.io.svg;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapContent;

/**
 * This class enable to write geotools feature collection in a SVG file.
 * For more information about geotools, see the <a href="http://geotools.codehaus.org/"> Geotools homepage</a>. 
 * For more information about SVG language, see <a href="http://www.w3.org/Graphics/SVG/">
 * Geographic Markup Language Specification</a>
 * @author Julien Seinturier
 *
 */
public class SVGWriter {

  
  /**
   * Write the given map context into a SVG file.
   * @param context the context to write.
   * @param os the output stream
   * @throws IOException if an error occurs
   */
  public void write(MapContent context, OutputStream os)
  throws IOException{
  
   if (context != null){
     write(context, context.getViewport().getBounds(), os);
   }

  }
  
  /**
   * Write the given map context area specified by the enveloppe into a SVG file.
   * @param context the context to write.
   * @param enveloppe the enveloppe specifying the area to write
   * @param os the output stream
   * @throws IOException if an error occurs
   */
  public void write(MapContent context, ReferencedEnvelope env, OutputStream os)
  throws IOException{
  
    GenerateSVG generator = new GenerateSVG();
    
    try {
      generator.go(context, env, os);
    } catch (ParserConfigurationException e) {
      generator = null;
      throw new IOException("Cannot write SVG stram", e);
    }
    
    generator = null;
    
  }
}
