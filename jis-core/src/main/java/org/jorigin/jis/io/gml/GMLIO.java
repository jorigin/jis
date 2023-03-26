package org.jorigin.jis.io.gml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.jorigin.lang.PathUtil;
import org.opengis.feature.type.FeatureType;

/**
 * A class that regroup I/O dedicated to <a href="http://www.opengeospatial.org/standards/gml">GML</a>.
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 * @see GMLReader
 * @see GMLWriter
 */
public class GMLIO {

	
  /**
	* Flag specifying a GML version 2 specification.
	*/
  public static final int GML_2   = 2;
	  
  /**
	* Flag specifying a GML version 3 specification.
	*/
  public static final int GML_3   = 3;
  
  
  private GMLWriter writer = null;
  
  private GMLReader reader = null;
  
  /**
   * Create a new GMLIO object.
   */
  public GMLIO(){
    this.writer = new GMLWriter();
    this.reader = new GMLReader();
  }
  
  /**
   * Save the given {@link org.geotools.map.MapContent map content} within the GML file pointed by the given <code>path</code>.
   * @param context the {@link org.geotools.map.MapContent map content} to save.
   * @param path the path of the output GML file.
   * @throws IOException if an error occurs.
   */
  public void save(MapContent context, String path)
  throws IOException{
    if (context != null){
    	if (context.layers() != null){
    	  save(context.layers(), path);
    	}
    }
  }
  
  /**
   * Save the given {@link org.geotools.map.Layer layers} within the GML file pointed by the given <code>path</code>.
   * @param layers the {@link org.geotools.map.Layer layers} to save.
   * @param path the path of the output GML file.
   * @throws IOException if an error occurs.
   */
  public void save(List<Layer> layers, String path)
  throws IOException{
    
    File directory             = null;
    File gmlFile               = null;
    File schemaFile            = null;
    FileOutputStream fos       = null;
    ArrayList<FeatureType> layerTypes       = null;
    
    if ((layers != null)&&(layers.size() > 0)){
      
      
      // Determinaison des types de features correspondant aux layers
      layerTypes = new ArrayList<FeatureType>();
      for(int i = 0; i < layers.size(); i++){
        if (!layerTypes.contains(layers.get(i).getFeatureSource().getSchema())){
          layerTypes.add(layers.get(i).getFeatureSource().getSchema());
        }
      }
      
      System.out.println("[GMLIO] [save(MapLayer[], String)] Types: "+layerTypes.size());
      
      // Creation du fichier représentant le répertoire ou seront sauvegardé
      // les fichiers GML
      directory = new File(PathUtil.URIToPath(path));
      
      if (!directory.exists()){
        directory.mkdirs();
      }
      
      // Ecriture des fichiers schema correspondant au types des layers
      for(int i = 0; i < layerTypes.size(); i++){
        schemaFile = new File(PathUtil.URIToPath(path)+File.separator+layerTypes.get(i).getName().getLocalPart()+".xsd");
        
        System.out.println("[GMLIO] [save(MapLayer[], String)] Writing type: "+layerTypes.get(i));
        System.out.println("[GMLIO] [save(MapLayer[], String)]        file : "+schemaFile);
        
        if (!schemaFile.exists()){
          try {
            schemaFile.createNewFile();
            
            fos = new FileOutputStream(schemaFile);
            this.writer.writeSchema(layers.get(i).getFeatureSource().getSchema(), fos);
            fos.close();
            
          } catch (IOException e) {
            try {
              if (fos != null){
                fos.close();
              }
            } catch (IOException e1) {
              
            }
            throw new IOException("Cannot read GML from path"+path, e);
          }
          
          schemaFile = null;
          fos        = null;
        }
      }
      
      // Ecriture du contenu des layers en GML
      for(int i = 0; i < layers.size(); i++){
        try {

            gmlFile    = new File(PathUtil.URIToPath(path)+File.separator+layers.get(i).getTitle()+".gml");
            
            if (!gmlFile.exists()){
              gmlFile.createNewFile();
            }

            // Creation d'un fichier GML correspondant au contenu du layer.
            fos = new FileOutputStream(gmlFile);
            this.writer.write(layers.get(i), fos);
            fos.close();

            fos      = null;
            gmlFile  = null;
 
        } catch (IOException e) {
          
          try {
            fos.close();
          } catch (IOException e1) {
            
          }
          
          throw new IOException("Cannot read GML from path"+path, e);
        }
        
        fos      = null;
        gmlFile  = null;    
      }
    }    
  }
  
  /**
   * Get the underlying {@link GMLWriter GML writer}.
   * @return the underlying {@link GMLWriter GML writer}.
   */
  public GMLWriter getWriter(){
	  return this.writer;
  }
  
  /**
   * Get the underlying {@link GMLReader GML reader}.
   * @return the underlying {@link GMLReader GML reader}.
   */
  public GMLReader getReader(){
	  return this.reader;
  }
}
