package org.jorigin.jis.io.gml;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.transform.TransformerException;

import org.geotools.feature.FeatureCollection;
import org.geotools.gml.producer.FeatureTransformer;
import org.geotools.gml.producer.FeatureTypeTransformer;
import org.geotools.map.Layer;
import org.opengis.feature.type.FeatureType;


/**
 * A class that handles <a href="http://www.opengeospatial.org/standards/gml">GML</a> document read.
 * For more information about geotools, see the <a href="http://geotools.codehaus.org/"> Geotools homepage</a>. 
 * For more information about GML language, see <a href="http://www.opengeospatial.org/standards/gml">
 * Geographic Markup Language Specification</a>
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 *
 */
public class GMLWriter {

  private String defaultNamespace = "http://www.opengeospatial.org/standards/gml/"; 
  
  
  /**
   * The version of the specification used by the writer to produce a GML file.
   * <code>gmlVersion</code> can be {@link org.jorigin.gis.io.gml.GMLIO#GML_2} or 
   * {@link org.jorigin.gis.io.gml.GMLIO#GML_3}. Default value
   * is {@link org.jorigin.gis.io.gml.GMLIO#GML_3}.
   */
  private int gmlVersion          = GMLIO.GML_3;
  
  /**
   * Write the given {@link org.geotools.map.Layer layer} 
   * onto the GML document wrapped by the given {@link java.io.OutputStream output stream}.
   * @param layer the {@link org.geotools.map.Layer layer} to write.
   * @param os an {@link java.io.OutputStream output stream} opened onto a GML document.
   * @throws IOException if an error occurs.
   */
  public void write(Layer layer, OutputStream os)
  throws IOException{
    
    URI namespace    = null;
    String prefix    = null;
    
    if (layer != null){
      
      if (layer.getFeatureSource() != null){
        
        try {
		  namespace = new URI(layer.getFeatureSource().getSchema().getName().getNamespaceURI());
		} catch (URISyntaxException e) {
			namespace = null;
			System.err.println("Cannot create URI from "+layer.getFeatureSource().getSchema().getName().getNamespaceURI());
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
        prefix    = layer.getFeatureSource().getSchema().getName().getLocalPart();
        
        if (namespace != null){
          write(layer, os, namespace.toString(), prefix, this.gmlVersion);
        } else {
          write(layer, os, this.defaultNamespace, prefix, this.gmlVersion);
        }
      }
    }
    
  }
  
  /**
   * Write the given {@link org.geotools.map.Layer layer} 
   * onto the GML document wrapped by the given {@link java.io.OutputStream output stream}.
   * @param layer the {@link org.geotools.map.Layer layer} to write.
   * @param os an {@link java.io.OutputStream output stream} opened onto a GML document.
   * @param namespace the namespace of the GML document.
   * @param prefix the prefix of the GML document.
   * @param gmlVersion the version of GML to use (can be {@link GMLIO#GML_2 GML_2} or {@link GMLIO#GML_3 GML_3}). 
   * @throws IOException if an error occurs.
   */
  public void write(Layer layer, OutputStream os, String namespace, String prefix, int gmlVersion)
  throws IOException{
    write(layer.getFeatureSource().getFeatures(), os, namespace, prefix, gmlVersion);
  }

  /**
   * Write the given {@link org.geotools.feature.FeatureCollection features collection} 
   * onto the GML document wrapped by the given {@link java.io.OutputStream output stream}.
   * @param features the {@link org.geotools.feature.FeatureCollection features collection} to write.
   * @param os an {@link java.io.OutputStream output stream} opened onto a GML document.
   * @param namespace the namespace of the GML document.
   * @param prefix the prefix of the GML document.
   * @param gmlVersion the version of GML to use (can be {@link GMLIO#GML_2 GML_2} or {@link GMLIO#GML_3 GML_3}). 
   * @throws IOException if an error occurs.
   */
  public void write(FeatureCollection<?,?> features, OutputStream os, String namespace, String prefix, int gmlVersion)
  throws IOException{
    FeatureTransformer ft       = null;
    FeatureType featureType     = null;
    
    if (features == null){
      return;
    }

    // Recuperation du type des features a ecrire
    featureType = features.getSchema();
    
    System.out.println("[GMLWriter] [write(MapLayer, OutputStream, String, int)] Features #   : "+features.size());
    System.out.println("[GMLWriter] [write(MapLayer, OutputStream, String, int)] Features type: "+featureType.getName().getLocalPart());
    System.out.println("[GMLWriter] [write(MapLayer, OutputStream, String, int)] namespace    : "+namespace);

    // Ecrit la collection dans le fichier GML
    ft = new FeatureTransformer();
     
     // Fixe l'indentation
     ft.setIndentation(2);
      
     // Utilisation d'un namespace precis
     ft.getFeatureNamespaces().declarePrefix(prefix, namespace);
     
     // Utilisation d'un namespace par defaut
     //ft.getFeatureTypeNamespaces().declareDefaultNamespace(prefix, namespace);
     
     // transform
     try {
      ft.transform(features,os);
      
    } catch (TransformerException e) {
      throw new IOException("Cannot write layer as GML", e);
    }
  }
  
  /**
   * Write the given {@link org.opengis.feature.type.FeatureType feature schema} 
   * onto the GML document wrapped by the given {@link java.io.OutputStream output stream}.
   * @param featureType the {@link org.opengis.feature.type.FeatureType feature schema} to write.
   * @param os an {@link java.io.OutputStream output stream} opened onto a GML document.
   * @throws IOException if an error occurs.
   */
  public void writeSchema(FeatureType featureType, OutputStream os)
  throws IOException{
    
    FeatureTypeTransformer ftt  = null;
    
    // Ecrit un schema pour les features du layer
    ftt = new FeatureTypeTransformer();
    
    try {
      ftt.setIndentation(2);
      ftt.transform(featureType, os);
    } catch (TransformerException e) {
      throw new IOException("Cannot write schema for type"+featureType.getName().getLocalPart(), e);
    }
  }
  
  /**
   * Get the default GML namespace used by this writer.
   * @return the default GML namespace used by this writer.
   */
  public String getDefaultNamespace(){
    return this.defaultNamespace;
  }
}
