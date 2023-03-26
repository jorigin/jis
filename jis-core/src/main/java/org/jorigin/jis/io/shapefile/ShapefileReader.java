package org.jorigin.jis.io.shapefile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;

import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.FeatureCollection;
import org.geotools.map.FeatureLayer;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.Style;
import org.jorigin.Common;
import org.jorigin.jis.io.reader.GISDataReader;
import org.jorigin.jis.styling.GISStyleFactory;
import org.jorigin.lang.PathUtil;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * A reader that can decode <a href="http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf">ESRI shapefile</a> files.
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 *
 */
public class ShapefileReader implements GISDataReader{

  /**
   * The style creator used with this loader.
   */
  protected GISStyleFactory styleCreator = null;

  /**
   * The default CRS to use
   */
  //private CoordinateReferenceSystem defaultCS = org.geotools.referencing.crs.DefaultEngineeringCRS.GENERIC_2D;
  private CoordinateReferenceSystem defaultCS   = DefaultGeographicCRS.WGS84_3D;

  private boolean forceDefaultCS              = false;
  
  @Override
  public boolean canRead(Object object) {
    return true;
  }

  /**
   * Create a {@link org.geotools.data.shapefile.ShapefileDataStore ShapefileDataStore} from 
   * an <a href="http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf">ESRI shapefile</a>.
   * @param path the path of the shapefile to load.
   * @return the {@link org.geotools.data.shapefile.ShapefileDataStore data store} that wraps the shapefile given as input.
   */
  public ShapefileDataStore readDataStore(String path){

    ShapefileDataStore dataStore  = null;

    // Instanciation d'un data store
    try {
      dataStore = new ShapefileDataStore(PathUtil.pathToURI(path).toURL());

      try {
        Common.logger.log(Level.FINE, "data store type: "+dataStore.getSchema(dataStore.getTypeNames()[0]));
      } catch (IOException e) {
        Common.logger.log(Level.SEVERE, "Cannot create data store from path "+path, e);
      }

      return dataStore;
    } catch (MalformedURLException e) {
      Common.logger.log(Level.SEVERE, "Cannot create data store from "+path, e);
      return null;
    }
  }

  /**
   * Create a {@link org.geotools.map.MapLayer MapLayer} from 
   * an <a href="http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf">ESRI shapefile</a> 
   * which path is given in parameter.
   * @param path the path of the shapefile.
   * @return the layer representing the shape file.
   */
  public FeatureLayer readLayer(String path){
    return readLayer(path, null);
  }

  /**
   * Create a {@link org.geotools.map.MapLayer MapLayer} from a shapefile which <code>path</code> 
   * and <code>style</code> are given in parameter. 
   * If the given <code>style</code> is <code>null</code>, a default {@link org.geotools.styling.Style Style} is created using the default style creator.
   * @param path the path of the shapefile.
   * @param style the style to apply to the created layer
   * @return the layer representing the shape file.
   */
  public FeatureLayer readLayer(String path, Style style){
    FeatureLayer layer                = null;

    String[] typeNames            = null;

    ShapefileDataStore dataStore  = null;

    // Source dans lequele récupérer les objets du shapefile
    FeatureSource<SimpleFeatureType, SimpleFeature> featureSource   = null;

    // Les objets charges depuis le shape file
    FeatureCollection<SimpleFeatureType, SimpleFeature> features    = null;

    Style tmpStyle = null;

    dataStore  = readDataStore(path);

    CoordinateReferenceSystem crs = null;

    if (dataStore != null){

      try {
        crs = dataStore.getSchema().getGeometryDescriptor().getCoordinateReferenceSystem();
        if ((crs == null) || (this.forceDefaultCS)){
          dataStore.forceSchemaCRS(this.defaultCS);
          Common.logger.log(Level.FINE, "Force default CS: "+this.defaultCS);
        } else {
          Common.logger.log(Level.FINE, "Using shapefile CS: "+crs.getName());
        }
      } catch (IOException e2) {
        Common.logger.log(Level.SEVERE, "Unable to set CRS to data store", e2);
      }

      // Recuperation des types presents dans le data store
      // associe au shape file
      try {
        typeNames = dataStore.getTypeNames();
      } catch (IOException e2) {
        Common.logger.log(Level.SEVERE, "Cannot get type names from data store: "+e2.getMessage(), e2);
        typeNames = null;
      }

      if (typeNames != null){

        // Recuperation des objets représentés dans le shape file.
        // Un seul type ne peut être représenté dans un shape file
        try {
          featureSource = dataStore.getFeatureSource();
        } catch (IOException e1) {
          featureSource = null;
          Common.logger.log(Level.SEVERE, "Unable to set CRS to data store", e1);
        }
      }

      if (featureSource != null){

        try {

          features = featureSource.getFeatures(Filter.INCLUDE);

          if ((features != null) && (features.size() > 0)){

            // Creation d'un style par defaut si aucun n'existe.
            if (style == null){
              tmpStyle = this.styleCreator.createStyle(featureSource);
            } else {
              tmpStyle = style;
            }

            // Creation du layer
            layer = new FeatureLayer(features, tmpStyle, typeNames[0]);
            layer.setQuery(Query.ALL);
          }
        } catch (Exception e) {
          Common.logger.log(Level.SEVERE, "Unable to create layer", e);
        }
      }
    } else {
      layer = null;
    }


    return layer;
  }

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isForceCRS() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public CoordinateReferenceSystem getCRS() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setCRS(CoordinateReferenceSystem crs) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public org.geotools.renderer.style.Style getStyle() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setStyle(org.geotools.renderer.style.Style style) {
    // TODO Auto-generated method stub
    
  }
  
}
