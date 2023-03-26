package org.arpenteur.gis.io.gml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.arpenteur.common.lang.PathUtil;

import org.geotools.map.Layer;
import org.opengis.feature.type.FeatureType;

public class GMLIO {

  
  private GMLWriter writer = null;
  
  
  public GMLIO(){
    writer = new GMLWriter();
  }
  
  public void save(List<Layer> layers, String path)
      throws IOException{
    
    if (layers != null){
      save(layers.toArray(new Layer[layers.size()]), path);
    }
  }
  
  
  public void save(Layer[] layers, String path)
  throws IOException{
    
    File directory             = null;
    File gmlFile               = null;
    File schemaFile            = null;
    FileOutputStream fos       = null;
    ArrayList<FeatureType> layerTypes       = null;
    
    if ((layers != null)&&(layers.length > 0)){
      
      
      // Determinaison des types de features correspondant aux layers
      layerTypes = new ArrayList<FeatureType>();
      for(int i = 0; i < layers.length; i++){
        if (!layerTypes.contains(layers[i].getFeatureSource().getSchema())){
          layerTypes.add(layers[i].getFeatureSource().getSchema());
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
            writer.writeSchema(layers[i].getFeatureSource().getSchema(), fos);
            fos.close();
            
          } catch (IOException e) {
            try {
              fos.close();
            } catch (IOException e1) {
              
            }
            throw new IOException("Cannot read GML from path"+path, e);
          }
          
          schemaFile = null;
          fos        = null;
        }
      }
      
      // Ecriture du contenu des layers en GML
      for(int i = 0; i < layers.length; i++){
        try {

            gmlFile    = new File(PathUtil.URIToPath(path)+File.separator+layers[i].getTitle()+".gml");
            
            if (!gmlFile.exists()){
              gmlFile.createNewFile();
            }

            // Creation d'un fichier GML correspondant au contenu du layer.
            fos = new FileOutputStream(gmlFile);
            writer.write(layers[i], fos);
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
}
