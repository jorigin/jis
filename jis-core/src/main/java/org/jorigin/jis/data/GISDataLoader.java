package org.jorigin.jis.data;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

import javax.swing.filechooser.FileFilter;

import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.coverage.grid.io.GridCoverage2DReader;
import org.geotools.data.DataSourceException;
import org.geotools.data.FeatureSource;
import org.geotools.data.Query;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.feature.FeatureCollection;

import org.geotools.gce.geotiff.GeoTiffReader;
import org.geotools.gce.image.WorldImageReader;
import org.geotools.map.FeatureLayer;
import org.geotools.map.GridReaderLayer;
import org.geotools.map.Layer;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.styling.Style;
import org.geotools.util.factory.Hints;
import org.jorigin.Common;
import org.jorigin.jis.styling.GISStyleFactory;
import org.jorigin.lang.PathUtil;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;



/**
 * This class gathers various loader used to access geo data.
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 */
public class GISDataLoader {

  private static final String TIFF_EXTENSION      = "tif";

  private static final String SHAPEFILE_EXTENSION = "SHP";

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

  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  //CC CONSTRUCTEUR                                                   CC
  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  /**
   * Construct a default data loader. A default {@link org.jorigin.jis.styling.GISStyleFactory GISStyleFactory} 
   * and the {@link org.geotools.referencing.crs.DefaultGeographicCRS#WGS84_3D WGS84_3D} 
   * default coordinate reference system are used.
   */
  public GISDataLoader(){
    this(null, null);
  }

  /**
   * Construct a data loader. A default {@link org.jorigin.jis.styling.GISStyleFactory GISStyleFactory} 
   * and the given {@link org.opengis.referencing.crs.CoordinateReferenceSystem CoordinateReferenceSystem} are used.
   * @param defaultCRS the {@link org.opengis.referencing.crs.CoordinateReferenceSystem CoordinateReferenceSystem} to use as default.
   */
  public GISDataLoader(CoordinateReferenceSystem defaultCRS){
    this(null, defaultCRS);
  }

  /**
   * Construct a data loader with given {@link org.jorigin.jis.styling.GISStyleFactory GISStyleFactory} 
   * and {@link org.opengis.referencing.crs.CoordinateReferenceSystem CoordinateReferenceSystem}.
   * @param styleCreator the {@link org.jorigin.jis.styling.GISStyleFactory GISStyleFactory} to use to create styles.
   * @param defaultCRS the {@link org.opengis.referencing.crs.CoordinateReferenceSystem CoordinateReferenceSystem} to use by default.
   */
  public GISDataLoader(GISStyleFactory styleCreator, CoordinateReferenceSystem defaultCRS){

    if (defaultCRS != null){
      this.defaultCS = defaultCRS;
    } else {
      this.defaultCS = DefaultGeographicCRS.WGS84;
    }

    if (styleCreator != null){
      this.styleCreator = styleCreator;
    } else {
      this.styleCreator = new GISStyleFactory();
    }
  }
  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  //CC FIN CONSTRUCTEUR                                               CC
  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC

  //LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
  //LL LOADING                                                        LL
  //LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL

  /**
   * Create a {@link org.geotools.data.shapefile.ShapefileDataStore ShapefileDataStore} from 
   * an <a href="http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf">ESRI shapefile</a>.
   * @param path the path of the shapefile to load.
   * @return the {@link org.geotools.data.shapefile.ShapefileDataStore data store} that wraps the input source.
   */
  public ShapefileDataStore loadShapeFile(String path){

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
   * Create a {@link org.geotools.map.FeatureLayer FeatureLayer} from 
   * an <a href="http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf">ESRI shapefile</a> 
   * which path is given in parameter.
   * @param path the path of the shapefile.
   * @return the layer representing the shape file.
   */
  public FeatureLayer loadShapeFileLayer(String path){
    return loadShapeFileLayer(path, null);
  }

  /**
   * Create a {@link org.geotools.map.FeatureLayer FeatureLayer} from a shapefile which <code>path</code> 
   * and <code>style</code> are given in parameter. 
   * If the given <code>style</code> is <code>null</code>, a default {@link org.geotools.styling.Style Style} is created using the default style creator.
   * @param path the path of the shapefile.
   * @param style the style to apply to the created layer
   * @return the layer representing the shape file.
   */
  public FeatureLayer loadShapeFileLayer(String path, Style style){
    FeatureLayer layer            = null;

    String[] typeNames            = null;

    ShapefileDataStore dataStore  = null;

    // Source dans lequele récupérer les objets du shapefile
    FeatureSource<SimpleFeatureType, SimpleFeature> featureSource   = null;

    // Les objets charges depuis le shape file
    FeatureCollection<SimpleFeatureType, SimpleFeature> features    = null;

    Style tmpStyle = null;

    dataStore  = loadShapeFile(path);

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
        Common.logger.log(Level.SEVERE, "Cannot get type names from datastore: "+e2.getMessage(), e2);
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

  /**
   * Create a {@link org.geotools.map.GridReaderLayer GridReaderLayer} from a 
   * <a href="http://trac.osgeo.org/geotiff/">geoTIFF</a> image which <code>path</code>
   * is given in parameter. 
   * A default {@link org.geotools.styling.Style Style} is created using the default style creator.
   * @param path the path of the geoTIFF image
   * @return the layer representing the geoTIFF.
   */
  public GridReaderLayer loadGeoTIFF(String path){
    return loadGeoTIFF(path, null);
  }

  /**
   * Create a {@link org.geotools.map.GridReaderLayer GridReaderLayer} from a 
   * <a href="http://trac.osgeo.org/geotiff/">geoTIFF</a> image which <code>path</code>
   * is given in parameter. The given {@link org.geotools.styling.Style Style} is associated to the layer.
   * If the <code>style</code> parameter is <code>null</code>, a default {@link org.geotools.styling.Style Style} 
   * is created using the default style creator.
   * @param path the path of the geoTIFF image to load.
   * @param style the style to associate to the created layer.
   * @return the layer representing the geoTIFF.
   */
  public GridReaderLayer loadGeoTIFF(String path, Style style){

    GridCoverage2DReader reader = null;

    File file             = null;

    URL url               = null;

    GridReaderLayer layer = null;

    Style tmpStyle        = null;

    String fileName       = null;


    // 0) Recuperation du nom de fichier
    fileName = PathUtil.getFileName(path);

    // 1) Détermination de l'input à choisir en fonction du type de chemin
    switch(PathUtil.getProtocol(path)){
    case PathUtil.SYSTEM:
    case PathUtil.URL_FILE:
      file = new File(PathUtil.URIToPath(path));

      if (file != null){

        try {

          reader       = new GeoTiffReader(file);

        } catch (DataSourceException e) {
          Common.logger.log(Level.SEVERE, "Cannot load GEOTIFF from path "+path, e);
          reader = null;
        } catch (Exception e2){
          Common.logger.log(Level.SEVERE, "Cannot load GEOTIFF from path "+path, e2);
          reader = null;
        }

        // Si la creation du reader n'a pas marché, on utilise les hints par 
        // défaut
        if (reader == null){
          Common.logger.log(Level.WARNING, "No reader available for "+path);
          Hints hints = new Hints(Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM, this.defaultCS);
          try {
            reader      = new GeoTiffReader(file, hints);
            Common.logger.log(Level.WARNING, "Using reader default CRS: "+CRS.getHorizontalCRS(reader.getCoordinateReferenceSystem()));
          } catch (DataSourceException e) {
            Common.logger.log(Level.SEVERE, "Cannot load GEOTIFF from path "+path, e);
            reader = null;
          }catch (Exception e2) {
            Common.logger.log(Level.SEVERE, "Cannot load GEOTIFF from path "+path, e2);
            reader = null;
          }
        }                
      } 

      break;

    case PathUtil.URL_HTTP:
      url = PathUtil.pathToURL(path);

      if (url != null){

        try {

          reader       = new GeoTiffReader(url);

        } catch (DataSourceException e) {
          Common.logger.log(Level.SEVERE, "Cannot load GEOTIFF from path "+path, e);

          reader = null;
        } catch (Exception e2){
          Common.logger.log(Level.SEVERE, "Cannot load GEOTIFF from path "+path, e2);
          reader = null;
        }

        // Si la creation du reader n'a pas marché, on utilise les hints par 
        // défaut
        if (reader == null){
          Common.logger.log(Level.INFO, "No reader available for "+path);
          Hints hints = new Hints(Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM, this.defaultCS);


          try {
            reader      = new GeoTiffReader(url, hints);
          } catch (DataSourceException e) {
            Common.logger.log(Level.SEVERE, "Cannot load GEOTIFF from path "+path, e);
            reader = null;
          }catch (Exception e2) {
            Common.logger.log(Level.SEVERE, "Cannot load GEOTIFF from path "+path, e2);
            reader = null;
          }

        }          
      } 
      break;  

    default:
      Common.logger.log(Level.WARNING, "Geotiff loader cannont handle this data protocol "+path);
      break;
    }

    // 2) Creation d'un style si aucun n'est fourni
    //    if (gridCoverage != null){
    if (reader != null){ 
      if (style == null){
        tmpStyle = this.styleCreator.createRasterStyle();
      } else {
        tmpStyle = style;
      }

      // 3) Creation d'un layer à partir du style et du grid coverage
      try {
        layer = new GridReaderLayer(reader, tmpStyle, fileName);
        Common.logger.log(Level.FINE, "geoTIFF layer loaded from "+path);
      } catch (Exception e) {
        layer = null;
        Common.logger.log(Level.SEVERE, "Cannot create layer", e);
      }
    } else {
      layer = null;
    }

    return layer;
  }

  /**
   * Create a {@link org.geotools.map.GridReaderLayer GridReaderLayer} from a 
   * <a href="http://en.wikipedia.org/wiki/World_file">World Referenced</a> image which <code>path</code>
   * is given in parameter.
   * A default {@link org.geotools.styling.Style Style} is created for the layer using the default style creator.
   * @param path the path of the World Referenced Image to load.
   * @return the layer representing the World Referenced Image.
   */
  public GridReaderLayer loadWorldReferencedImage(String path){
    return loadWorldReferencedImage(path, null);
  }

  /**
   * Create a {@link org.geotools.map.GridReaderLayer GridReaderLayer} from a 
   * <a href="http://en.wikipedia.org/wiki/World_file">World Referenced</a> image which <code>path</code>
   * is given in parameter. The given {@link org.geotools.styling.Style Style} is associated to the layer.
   * If the <code>style</code> parameter is <code>null</code>, a default {@link org.geotools.styling.Style Style} 
   * is created using the default style creator.
   * @param path the path of the World Referenced Image to load.
   * @param style the style to associate to the created layer.
   * @return the layer representing the World Referenced Image.
   */
  public GridReaderLayer loadWorldReferencedImage(String path, Style style){

    AbstractGridCoverage2DReader reader = null;

    File file             = null;

    URL url               = null;

    GridReaderLayer layer = null;

    Style tmpStyle        = null;

    String fileName       = null;

    // 0) Recuperation du nom de fichier
    fileName = PathUtil.getFileName(path);

    // 1) Détermination de l'input à choisir en fonction du type de chemin
    switch(PathUtil.getProtocol(path)){
    case PathUtil.SYSTEM:
    case PathUtil.URL_FILE:
      file = new File(PathUtil.URIToPath(path));

      if (file != null){
        // Lecture du GeoTiff en utilisant le systeme de reference défini par le
        // geotiff
        try {
          //Hints hints = new Hints(Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM, defaultCS);
          //reader = new WorldImageReader(file, hints);
          reader = new WorldImageReader(file);
        } catch (DataSourceException e) {
          Common.logger.log(Level.SEVERE, "Cannot create reader for "+path, e);
          reader = null;
        } catch (Exception e2){
          Common.logger.log(Level.SEVERE, "Cannot create reader for "+path, e2);
          reader = null;
        }          
      }        
      break;

    case PathUtil.URL_HTTP:
      url = PathUtil.pathToURL(path);

      try {
        //Hints hints = new Hints(Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM, defaultCS);
        //reader = new WorldImageReader(url, hints);
        reader = new WorldImageReader(url);
        Common.logger.log(Level.FINE, "Grid Coverage Reader ready for "+path);
      } catch (DataSourceException e) {
        Common.logger.log(Level.SEVERE, "Cannot create reader for "+path, e);
        reader = null;
      } catch (Exception e2){
        Common.logger.log(Level.SEVERE, "Cannot create reader for "+path, e2);
        reader = null;
      }               

      break;

    default:
      Common.logger.log(Level.WARNING, "World file reader loader cannont handle this data protocol "+path);
      break;
    }

    // 2) Creation d'un style si aucun n'est fourni
    if (reader != null){ 
      if (style == null){
        tmpStyle = this.styleCreator.createRasterStyle();
      } else {
        tmpStyle = style;
      }

      // 3) Creation d'un layer à partir du style et du grid coverage
      try {
        layer = new GridReaderLayer(reader, tmpStyle, fileName);
      } catch (Exception e) {
        layer = null;
        Common.logger.log(Level.SEVERE, "Cannot create layer", e);
      }
    } else {
      layer = null;
      Common.logger.log(Level.SEVERE, "Cannot create layer, no reader available");
    }

    return layer;
  }

  /**
   * Read a referenced image as an 
   * {@link org.geotools.coverage.grid.io.AbstractGridCoverage2DReader AbstractGridCoverage2DReader}. 
   * This image can be a <a href="http://trac.osgeo.org/geotiff/">geoTIFF</a> 
   * or a <a href="http://en.wikipedia.org/wiki/World_file">World Referenced</a>.
   * @param path the path of the image file to load
   * @return an {@link org.geotools.coverage.grid.io.AbstractGridCoverage2DReader AbstractGridCoverage2DReader}.
   */
  public AbstractGridCoverage2DReader loadReferencedImage(String path){

    AbstractGridCoverage2DReader reader = null;

    File file            = null;

    // 1) Détermination de l'input à choisir en fonction du type de chemin
    switch(PathUtil.getProtocol(path)){
    case PathUtil.SYSTEM:
    case PathUtil.URL_FILE:
      file = new File(PathUtil.URIToPath(path));

      if (file != null){


        // Lecture du GeoTiff en utilisant le systeme de reference défini par le
        // geotiff
        try {
          Hints hints = new Hints(Hints.DEFAULT_COORDINATE_REFERENCE_SYSTEM, this.defaultCS);
          reader = new WorldImageReader(file, hints);
          Common.logger.log(Level.FINE, "Grid Coverage Reader instanciated for "+path);
          Common.logger.log(Level.FINE, "Default CRS used: "+this.defaultCS.toWKT());
        } catch (DataSourceException e) {
          Common.logger.log(Level.SEVERE, "Cannot instanciate Grid Coverage Reader for "+path);            
          reader = null;
        } catch (Exception e2){
          Common.logger.log(Level.SEVERE, "Cannot instanciate Grid Coverage Reader for "+path);      
          reader = null;
        }            
      }

      break;

    default:
      Common.logger.log(Level.WARNING, "World file reader loader cannont handle this data protocol "+path);
      break;
    }

    return reader;
  }

  /**
   * Create a {@link org.geotools.map.Layer Layer} from a GIS file. This file can be:
   * <ul>
   * <li>an <a href="http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf">ESRI shapefile</a>
   * <li>a <a href="http://trac.osgeo.org/geotiff/">geoTIFF</a>
   * <li>a <a href="http://en.wikipedia.org/wiki/World_file">World Referenced</a>
   * </ul>
   * a default {@link org.geotools.styling.Style Style} is created using the default style creator.<br>
   * This method delegates to specific methods according to the extension of the read file.
   * @param path the path of the file to read.
   * @return the {@link org.geotools.map.Layer layer} representing the GIS file.
   * @see #loadShapeFileLayer(String)
   * @see #loadGeoTIFF(String)
   * @see #loadWorldReferencedImage(String)
   */
  public Layer loadFile(String path){
    return loadFile(path, null);
  }
  /**
   * Create a {@link org.geotools.map.Layer Layer} from a GIS file. This file can be:
   * <ul>
   * <li>an <a href="http://www.esri.com/library/whitepapers/pdfs/shapefile.pdf">ESRI shapefile</a>
   * <li>a <a href="http://trac.osgeo.org/geotiff/">geoTIFF</a>
   * <li>a <a href="http://en.wikipedia.org/wiki/World_file">World Referenced</a>
   * </ul>
   * The given {@link org.geotools.styling.Style Style} is associated to the layer.
   * If the <code>style</code> parameter is <code>null</code>, a default {@link org.geotools.styling.Style Style} 
   * is created using the default style creator.<br>
   * This method delegates to specific methods according to the extension of the read file.
   * @param path the path of the file to read.
   * @param style the style to associate to the created layer.
   * @return the {@link org.geotools.map.Layer Layer} representing the GIS file.
   * @see #loadShapeFileLayer(String, Style)
   * @see #loadGeoTIFF(String, Style)
   * @see #loadWorldReferencedImage(String, Style)
   */
  public Layer loadFile(String path, Style style){

    Layer layer;

    if (path == null){
      return null;
    } else {
      if (path.toUpperCase().endsWith(TIFF_EXTENSION.toUpperCase())){

        layer = loadGeoTIFF(path, style);

        if (layer != null){
          return layer;
        } else {
          return loadWorldReferencedImage(path, style);
        }
      } else if (path.toUpperCase().endsWith(SHAPEFILE_EXTENSION.toUpperCase())){
        return loadShapeFileLayer(path, style);
      } else if (   path.toUpperCase().endsWith(".GIF") 
          || path.toUpperCase().endsWith(".JPG")
          || path.toUpperCase().endsWith(".JPEG") 
          || path.toUpperCase().endsWith(".TIF")
          || path.toUpperCase().endsWith(".TIFF") 
          || path.toUpperCase().endsWith(".PNG") 
          || path.toUpperCase().endsWith(".BMP")){
        return loadWorldReferencedImage(path, style);
      } else {
        return null;
      }
    }
  }
  //LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL
  //LL FIN LOADING                                                    LL
  //LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL

  //AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  //AA ACCESSEURS                                                     AA
  //AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

  /**
   * Set the default {@link org.opengis.referencing.crs.CoordinateReferenceSystem CoordinateReferenceSystem} 
   * to use with the loaded objets.
   * @param crs the default CRS to use.
   * @see #getDefaultCRS()
   */
  public void setDefaultCRS(CoordinateReferenceSystem crs){
    this.defaultCS = crs;
  }

  /**
   * Get the default {@link org.opengis.referencing.crs.CoordinateReferenceSystem CoordinateReferenceSystem} 
   * to use with the loaded objets.
   * @return the default CRS to use.
   * @see #setDefaultCRS(CoordinateReferenceSystem)
   */
  public CoordinateReferenceSystem getDefaultCRS(){
    return this.defaultCS;
  }

  /**
   * Set if the loader have to always force the {@link org.opengis.referencing.crs.CoordinateReferenceSystem CoordinateReferenceSystem} 
   * of loaded objects to the default CRS.
   * @param force <code>true</code> if the default CRS has to be forced, <code>false</code> otherwise.
   * @see #getDefaultCRS()
   */
  public void setForceDefaultCS(boolean force){
    this.forceDefaultCS = force;
  }

  /**
   * Get if the loader have to always force the {@link org.opengis.referencing.crs.CoordinateReferenceSystem CoordinateReferenceSystem} 
   * of loaded objects to the default CRS.
   * @return force <code>true</code> if the default CRS is forced, <code>false</code> otherwise.
   * @see #setForceDefaultCS(boolean)
   */
  public boolean isForceDefaultCRS(){
    return this.forceDefaultCS;
  }
  //AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  //AA FIN ACCESSEURS                                                 AA
  //AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

  /**
   * Get the {@link javax.swing.filechooser.FileFilter FileFilters} validating the files handled by this data loader.
   * @return the {@link javax.swing.filechooser.FileFilter FileFilters} validating the files handled by this data loader.
   */
  public FileFilter[] getFileFilters(){
    FileFilter[] filters = new FileFilter[3];


    filters[0] = new FileFilter(){

      @Override
      public boolean accept(File pathname) {

        String path    = null;
        boolean accept = true;

        if (pathname != null){

          if (pathname.isDirectory()){
            accept = true;
          } else {

            path    = pathname.getPath();

            if (!path.toUpperCase().endsWith(".SHP")) {
              accept = false;
            }
          }

        } else {
          accept = false;
        }

        return accept;
      }

      @Override
      public String getDescription() {
        return "ESRI Shapefile";
      }};

      filters[1] = new FileFilter(){

        @Override
        public boolean accept(File pathname) {

          String path    = null;
          boolean accept = true;

          if (pathname != null){

            if (pathname.isDirectory()){
              accept = true;
            } else {

              path    = pathname.getPath();

              if (!(path.toUpperCase().endsWith(".TIF")|| (path.toUpperCase().endsWith(".TIFF")))) {
                accept = false;
              }
            }	  
          } else {
            accept = false;
          }

          return accept;
        }

        @Override
        public String getDescription() {
          return "GeoTIFF standalone files";
        }};  

        filters[2] = new FileFilter(){

          @Override
          public boolean accept(File pathname) {

            String path    = null;
            boolean accept = true;

            if (pathname != null){

              if (pathname.isDirectory()){
                accept = true;
              } else {

                path    = pathname.getPath();

                if (!(   path.toUpperCase().endsWith(".GIF") 
                    || path.toUpperCase().endsWith(".JPG")
                    || path.toUpperCase().endsWith(".JPEG") 
                    || path.toUpperCase().endsWith(".TIF")
                    || path.toUpperCase().endsWith(".TIFF") 
                    || path.toUpperCase().endsWith(".PNG") 
                    || path.toUpperCase().endsWith(".BMP"))) {
                  accept = false;
                }
              }

            } else {
              accept = false;
            }

            return accept;
          }

          @Override
          public String getDescription() {
            return "World referenced files (gif, jpeg, tiff, png, bmp)";
          }};

          return filters;

  }
}
