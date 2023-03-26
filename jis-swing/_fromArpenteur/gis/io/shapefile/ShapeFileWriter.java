package org.arpenteur.gis.io.shapefile;

import java.io.File;
import java.io.IOException;

import org.arpenteur.common.lang.PathUtil;
import org.arpenteur.gis.geotools.data.mesurable.MesurableDataStoreFactory;
import org.arpenteur.mesurable.manager.ItemMesurableManager;

import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureStore;
import org.geotools.data.FeatureWriter;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * This class enable to write item mesurable manager in a ESRI shapefile formatted output.<br>
 * To write an item mesurable manager into a shapefile, the simpliest way is to create a default
 * ShapeFileWriter and to use the <code>write(ItemMesurableManager)</code> method.<br>
 * <code> ItemMesurableManager items = ...;</code><br>
 * <code> String path = "/files/shapefile.shp";</code><br>
 * <code> ShapeFileWriter writer = new ShapeFileWriter();</code><br>
 * <code> writer.write(items, path);</code><br>
 * This writer is based on the geotools shapefile writer class. The item mesurable manager is first exported into a
 * {@link MesurableDataStore} and then the data store is exported into a shapefile datastore. Finally, the data store is 
 * writed by the  Shapefile wrapper of geotools.
 * @author Julien Seinturier
 *
 */
public class ShapeFileWriter {

  
  MesurableDataStoreFactory dataStoreFactory    = null;
  
  /**
   * Construct a new default shapefile writer.
   */
  public ShapeFileWriter(){
    dataStoreFactory = new MesurableDataStoreFactory();
  }
  
  /**
   * Create a new ShapeFileWriter using the data store factory given in parameter.
   * @param factory
   */
  public ShapeFileWriter(MesurableDataStoreFactory factory){
    if (factory != null){
      dataStoreFactory = factory;
    } else {
      dataStoreFactory = new MesurableDataStoreFactory();
    }
  }
  
  
  /**
   * Write a shape file from an item mesurable manager. This method create a data store from the
   * item mesurable manager and delegate the shapefile write to the method <code>write(DataStore, String)</code>
   * @param items the item mesurable manager to write.
   * @param filePath the path of the shapefile to write.
   */
  public void write(ItemMesurableManager items, String filePath) throws IOException{

    DataStore data = null;
      
    try {
      data = dataStoreFactory.createDataStore(items);
      if (data == null){
        throw new IOException("Unable to write shapefile "+filePath+"\r\n Cannot create data store from item manager");
      } else {
        write(data, filePath);
      }
    } catch (Exception e) {
      throw new IOException("Unable to write shapefile "+filePath+"\r\n Cannot create data store from item manager", e);
    } 
  }
  
  /**
   * Wrtie an item mesurable data store into a ESRI shapefile formatted file.
   * @param items the DataStore representing an item mesurable manager
   * @param filePath the path of the shapefile
   * @throws IOException if an error occurs.
   */
  public void write(DataStore data, String filePath) 
  throws IOException{
    FeatureSource<SimpleFeatureType, SimpleFeature> source         = null;
    SimpleFeature       feature        = null;
    SimpleFeature       currentFeature = null;
    String[] typeNames                 = null;
    FeatureIterator<SimpleFeature> iter       = null;

    try {
      
      
      typeNames = data.getTypeNames();
     
      // Ecriture d'un fichier shapefile
      File f = new File(PathUtil.URIToPath(filePath));
            
      if (!f.getParentFile().exists()){
        f.getParentFile().mkdirs();
      }
      
      ShapefileDataStore datastore = new ShapefileDataStore(f.toURI().toURL());
      
      // Creation du schema des feature à ecrire
      datastore.createSchema(data.getSchema(typeNames[0]));
      
      // Determination de la liste des writer à utiliser
      FeatureWriter<SimpleFeatureType, SimpleFeature> aWriter = datastore.getFeatureWriter(typeNames[0],((FeatureStore) datastore.getFeatureSource(typeNames[0])).getTransaction());
      
      source = data.getFeatureSource(typeNames[0]);
      iter = source.getFeatures().features();
      
      while(iter.hasNext()){
        currentFeature = iter.next();
        feature        = aWriter.next();
        /*  
        System.out.println("[ShapeFileWriter] [write()] current feature: "+currentFeature);
        System.out.println("[ShapeFileWriter] [write()]        type    : "+currentFeature.getFeatureType());
        System.out.println("[ShapeFileWriter] [write()] writed feature : "+feature);
        System.out.println("[ShapeFileWriter] [write()]        type    : "+feature.getFeatureType());
        */
        for(int i = 0; i < datastore.getSchema().getAttributeCount(); i++){
          //System.out.println("[ShapeFileWriter] [write()]   Attribute: "+currentFeature.getAttribute(i));

          feature.setAttribute(i, currentFeature.getAttribute(i));
        }
          
        aWriter.write(); 
//        System.out.println("[GISPlugin] [processActionEvent] ShapeFile Exported: "+feature);
      }
      
      aWriter.close();
      
//      System.out.println("Data store ended.");
    } catch (Exception e1) {
      System.err.println("[ShapefileWriter] [write()] Exception: "+e1.getMessage());
      e1.printStackTrace(System.err);
      throw new IOException("Unable to write shapefile "+filePath, e1);
    }
 
  }
}
