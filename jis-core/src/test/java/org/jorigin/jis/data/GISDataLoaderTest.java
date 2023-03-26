package org.jorigin.jis.data;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.logging.Level;

import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.map.Layer;
import org.jorigin.Common;
import org.jorigin.jis.data.GISDataLoader;
import org.jorigin.lang.PathUtil;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Testing the {@link org.jorigin.jis.data.GISDataLoader GISDataLoader} class.
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 *
 */
public class GISDataLoaderTest {

  
  static String geoTIFFPath       = null;
  
  static String shpPath           = null;
  
  static String wrfPath           = null;

  static GISDataLoader dataLoader = null;
  
  static boolean isInitialized = false;
  
  /**
   * Initialize the tests
   */
  @BeforeClass
  public static void initTest(){
    shpPath     = "src"+File.separator+"test"+File.separator+"resources"+File.separator+"files"+File.separator+"shapefile"+File.separator;
    geoTIFFPath = "src"+File.separator+"test"+File.separator+"resources"+File.separator+"files"+File.separator+"geoTIFF"+File.separator;
    wrfPath     = "src"+File.separator+"test"+File.separator+"resources"+File.separator+"files"+File.separator+"wrf"+File.separator;
  
    Common.logger.log(Level.INFO, "GISDataLoaderTest");
    if (new File(shpPath).isDirectory()){
      Common.logger.log(Level.INFO, "Shapefiles directory: "+shpPath);
    } else {
      Common.logger.log(Level.WARNING, "Cannot access Shapefiles directory: "+shpPath);
    }
    
    if (new File(geoTIFFPath).isDirectory()){
      Common.logger.log(Level.INFO, "geotiff directory   : "+geoTIFFPath);
    } else {
      Common.logger.log(Level.WARNING, "Cannot access geoTIFF directory: "+shpPath);
    }

    if (new File(wrfPath).isDirectory()){
      Common.logger.log(Level.INFO, "WRF directory       : "+wrfPath);
    } else {
      Common.logger.log(Level.WARNING, "Cannot access WRF directory: "+shpPath);
    }

    dataLoader = new GISDataLoader();
    
    isInitialized = true;
  }
  
  /**
   * Testing shapefile load
   */
  @Test
  public void loadShapeFileTest(){
    ShapefileDataStore dataStore = null;
    String path = null;
    
    try {
      path = PathUtil.URIToPath(System.getProperty("user.dir")+File.separator+shpPath+"01.shp");
      dataStore = dataLoader.loadShapeFile(path);
      
      Common.logger.log(Level.INFO, "Loaded "+dataStore.getFeatureSource().getFeatures().size()+" features from "+path);
      Common.logger.log(Level.INFO, "Bounds: "+dataStore.getFeatureSource().getFeatures().getBounds());
      assertTrue(true);
    } catch (Exception e) {
      Common.logger.log(Level.SEVERE, "Cannot load shapefile from "+path);
      assertTrue(false);
    }
    
    try {
      path = PathUtil.URIToPath(System.getProperty("user.dir")+File.separator+shpPath+"02.shp");
      dataStore = dataLoader.loadShapeFile(path);
      Common.logger.log(Level.INFO, "Loaded "+dataStore.getFeatureSource().getFeatures().size()+" features from "+path);
      Common.logger.log(Level.INFO, "Bounds: "+dataStore.getFeatureSource().getFeatures().getBounds());
      assertTrue(true);
    } catch (Exception e) {
      Common.logger.log(Level.SEVERE, "Cannot load shapefile from "+path);
      assertTrue(false);
    }
    
    try {
      path = PathUtil.URIToPath(System.getProperty("user.dir")+File.separator+shpPath+"03.shp");
      dataStore = dataLoader.loadShapeFile(path);
      Common.logger.log(Level.INFO, "Loaded "+dataStore.getFeatureSource().getFeatures().size()+" features from "+path);
      Common.logger.log(Level.INFO, "Bounds: "+dataStore.getFeatureSource().getFeatures().getBounds());
      assertTrue(true);
    } catch (Exception e) {
      Common.logger.log(Level.SEVERE, "Cannot load shapefile from "+path);
      assertTrue(false);
    }
    
    try {
      path = PathUtil.URIToPath(System.getProperty("user.dir")+File.separator+shpPath+"04.shp");
      dataStore = dataLoader.loadShapeFile(path);
      Common.logger.log(Level.INFO, "Loaded "+dataStore.getFeatureSource().getFeatures().size()+" features from "+path);
      Common.logger.log(Level.INFO, "Bounds: "+dataStore.getFeatureSource().getFeatures().getBounds());
      assertTrue(true);
    } catch (Exception e) {
      Common.logger.log(Level.SEVERE, "Cannot load shapefile from "+path);
      assertTrue(false);
    }
    
    try {
      path = PathUtil.URIToPath(System.getProperty("user.dir")+File.separator+shpPath+"05.shp");
      dataStore = dataLoader.loadShapeFile(path);
      Common.logger.log(Level.INFO, "Loaded "+dataStore.getFeatureSource().getFeatures().size()+" features from "+path);
      Common.logger.log(Level.INFO, "Bounds: "+dataStore.getFeatureSource().getFeatures().getBounds());
      assertTrue(true);
    } catch (Exception e) {
      Common.logger.log(Level.SEVERE, "Cannot load shapefile from "+path);
      assertTrue(false);
    }
  }
  
  /**
   * Testing shapefile load as layer.
   */
  @Test
  public void loadShapeFileLayerTest(){
    String path    = null;
    Layer layer = null;
    
    try {
      path = PathUtil.URIToPath(System.getProperty("user.dir")+File.separator+shpPath+"01.shp");
      layer = dataLoader.loadShapeFileLayer(path);
      Common.logger.log(Level.INFO, "Loaded "+layer.getFeatureSource().getFeatures().size()+" features from "+path);
      Common.logger.log(Level.INFO, "Bounds: "+layer.getBounds());
      assertTrue(true);
    } catch (Exception e) {
      Common.logger.log(Level.SEVERE, "Cannot load shapefile from "+path);
      assertTrue(false);
    }
    
    try {
      path = PathUtil.URIToPath(System.getProperty("user.dir")+File.separator+shpPath+"02.shp");
      layer = dataLoader.loadShapeFileLayer(path);
      Common.logger.log(Level.INFO, "Loaded "+layer.getFeatureSource().getFeatures().size()+" features from "+path);
      Common.logger.log(Level.INFO, "Bounds: "+layer.getFeatureSource().getFeatures().getBounds());
      assertTrue(true);
    } catch (Exception e) {
      Common.logger.log(Level.SEVERE, "Cannot load shapefile from "+path);
      assertTrue(false);
    }
    
    try {
      path = PathUtil.URIToPath(System.getProperty("user.dir")+File.separator+shpPath+"03.shp");
      layer = dataLoader.loadShapeFileLayer(path);
      Common.logger.log(Level.INFO, "Loaded "+layer.getFeatureSource().getFeatures().size()+" features from "+path);
      Common.logger.log(Level.INFO, "Bounds: "+layer.getFeatureSource().getFeatures().getBounds());
      assertTrue(true);
    } catch (Exception e) {
      Common.logger.log(Level.SEVERE, "Cannot load shapefile from "+path);
      assertTrue(false);
    }
    
    try {
      path = PathUtil.URIToPath(System.getProperty("user.dir")+File.separator+shpPath+"04.shp");
      layer = dataLoader.loadShapeFileLayer(path);
      Common.logger.log(Level.INFO, "Loaded "+layer.getFeatureSource().getFeatures().size()+" features from "+path);
      Common.logger.log(Level.INFO, "Bounds: "+layer.getFeatureSource().getFeatures().getBounds());
      assertTrue(true);
    } catch (Exception e) {
      Common.logger.log(Level.SEVERE, "Cannot load shapefile from "+path);
      assertTrue(false);
    }
    
    try {
      path = PathUtil.URIToPath(System.getProperty("user.dir")+File.separator+shpPath+"05.shp");
      layer = dataLoader.loadShapeFileLayer(path);
      Common.logger.log(Level.INFO, "Loaded "+layer.getFeatureSource().getFeatures().size()+" features from "+path);
      Common.logger.log(Level.INFO, "Bounds: "+layer.getFeatureSource().getFeatures().getBounds());
      assertTrue(true);
    } catch (Exception e) {
      Common.logger.log(Level.SEVERE, "Cannot load shapefile from "+path);
      assertTrue(false);
    }
  }
  
  /**
   * Testing geotiff load.
   */
  @Test
  public void loadGeoTIFFTest(){
    String path    = null;
    Layer layer = null;

    try {
      path = PathUtil.URIToPath(System.getProperty("user.dir")+File.separator+geoTIFFPath+"01.tif");
      layer = dataLoader.loadGeoTIFF(path);
      Common.logger.log(Level.INFO, "Loaded "+layer.getFeatureSource().getFeatures().size()+" features from "+path);
      Common.logger.log(Level.INFO, "Bounds: "+layer.getBounds());
      assertTrue(true);
    } catch (Exception e) {
      Common.logger.log(Level.SEVERE, "Cannot load geoTIFF from "+path);
      assertTrue(false);
    }
    
    try {
      path = PathUtil.URIToPath(System.getProperty("user.dir")+File.separator+geoTIFFPath+"02.tif");
      layer = dataLoader.loadGeoTIFF(path);
      Common.logger.log(Level.INFO, "Loaded "+layer.getFeatureSource().getFeatures().size()+" features from "+path);
      Common.logger.log(Level.INFO, "Bounds: "+layer.getBounds());
      assertTrue(true);
    } catch (Exception e) {
      Common.logger.log(Level.SEVERE, "Cannot load geoTIFF from "+path);
      assertTrue(false);
    }
    
    try {
      path = PathUtil.URIToPath(System.getProperty("user.dir")+File.separator+geoTIFFPath+"03.tif");
      layer = dataLoader.loadGeoTIFF(path);
      Common.logger.log(Level.INFO, "Loaded "+layer.getFeatureSource().getFeatures().size()+" features from "+path);
      Common.logger.log(Level.INFO, "Bounds: "+layer.getBounds());
      assertTrue(true);
    } catch (Exception e) {
      Common.logger.log(Level.SEVERE, "Cannot load geoTIFF from "+path);
      assertTrue(false);
    }
    
    try {
      path = PathUtil.URIToPath(System.getProperty("user.dir")+File.separator+geoTIFFPath+"04.tif");
      layer = dataLoader.loadGeoTIFF(path);
      Common.logger.log(Level.INFO, "Loaded "+layer.getFeatureSource().getFeatures().size()+" features from "+path);
      Common.logger.log(Level.INFO, "Bounds: "+layer.getBounds());
      assertTrue(true);
    } catch (Exception e) {
      Common.logger.log(Level.SEVERE, "Cannot load geoTIFF from "+path);
      assertTrue(false);
    }
    
    try {
      path = PathUtil.URIToPath(System.getProperty("user.dir")+File.separator+geoTIFFPath+"05.tif");
      layer = dataLoader.loadGeoTIFF(path);
      Common.logger.log(Level.INFO, "Loaded "+layer.getFeatureSource().getFeatures().size()+" features from "+path);
      Common.logger.log(Level.INFO, "Bounds: "+layer.getBounds());
      assertTrue(true);
    } catch (Exception e) {
      Common.logger.log(Level.SEVERE, "Cannot load geoTIFF from "+path);
      assertTrue(false);
    }
  }
  
  /**
   * Testing World Referenced File load.
   */
  @Test
  public void test(){
    String path    = null;
    Layer layer = null;

    try {
      path = PathUtil.URIToPath(System.getProperty("user.dir")+File.separator+wrfPath+"01.tif");
      layer = dataLoader.loadWorldReferencedImage(path);
      Common.logger.log(Level.INFO, "Loaded "+layer.getFeatureSource().getFeatures().size()+" features from "+path);
      Common.logger.log(Level.INFO, "Bounds: "+layer.getBounds());
      assertTrue(true);
    } catch (Exception e) {
      Common.logger.log(Level.SEVERE, "Cannot load geoTIFF from "+path);
      assertTrue(false);
    }
  }
}
