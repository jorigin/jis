package org.arpenteur.gis.io.gml;

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
 * This class enable to write geotools feature collection in a GML file.
 * For more information about geotools, see the <a href="http://geotools.codehaus.org/"> Geotools homepage</a>. 
 * For more information about GML language, see <a href="http://www.opengeospatial.org/standards/gml">
 * Geographic Markup Language Specification</a>
 * @author Julien Seinturier
 *
 */
public class GMLWriter {

  /**
   * Flag specifying a GML version 2 specification.
   */
  public static final int GML_2   = 2;
  
  /**
   * Flag specifying a GML version 3 specification.
   */
  public static final int GML_3   = 3;
  
  private final String defaultNamespace = "http://www.arpenteur.net/"; 
  
  
  /**
   * The version of the specification used by the writer to produce a GML file.
   * <code>gmlVersion</code> can be {@link #GML_2} or {@value #GML_3}. Default value
   * is {@link #GML_3}.
   */
  private final int gmlVersion          = GML_3;
  
  
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
          write(layer, os, namespace.toString(), prefix, gmlVersion);
        } else {
          write(layer, os, defaultNamespace, prefix, gmlVersion);
        }
      }
    }
    
  }
  
  
  public void write(Layer layer, OutputStream os, String namespace, String prefix, int gmlVersion)
  throws IOException{
    write(layer.getFeatureSource().getFeatures(), os, namespace, prefix, gmlVersion);
  }

  
  public void write(FeatureCollection features, OutputStream os, String namespace, String prefix, int gmlVersion)
  throws IOException{
    FeatureTransformer ft       = null;
    FeatureType featureType     = null;
    

    // Recuperation du type des features a ecrire
    featureType = features.getSchema();
    
    System.out.println("[GMLWriter] [write(MapLayer, OutputStream, String, int)] Features #   : "+features.size());
    System.out.println("[GMLWriter] [write(MapLayer, OutputStream, String, int)] Features type: "+featureType.getName().getLocalPart());
    System.out.println("[GMLWriter] [write(MapLayer, OutputStream, String, int)] namespace    : "+namespace);
    
    if (features == null){
      return;
    }
        

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
}
