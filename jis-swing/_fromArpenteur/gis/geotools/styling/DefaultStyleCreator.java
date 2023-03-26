package org.arpenteur.gis.geotools.styling;

import java.awt.Color;

import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.styling.Fill;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleBuilder;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * A default implementation of the style creator interface.
 * @author Julien Seinturier
 *
 */
public class DefaultStyleCreator 
       implements IStyleCreator {


  /** The feature type associated to this style creator */
  private SimpleFeatureType schema = null;
  
  /** The used style builder */
  StyleBuilder styleBuilder           = new StyleBuilder();
  
  /** The default stroke color for line*/
  private final Color lineStrokeColor    = new Color(255,20,20);
  
  /** The default line stroke opacity */
  private final double lineStrokeOpacity = 1.0d;
  
  /** The default stroke width for line */
  private final double lineStrokeWidth   = 1.0d;
  
  
  /** The default stroke color for polygons */
  private final Color polygonStrokeColor    = new Color(163,151,97);
  
  /** The default polygon stroke opacity */
  private final double polygonStrokeOpacity = 1.0d;
  
  /** The default stroke width for polygons */
  private final double polygonStrokeWidth   = 1.0d;
  
  /** The default fill color for polygons */
  private final Color polygonFillColor      = new Color(253, 241, 187);
  
  /** The default polygon fill opacity */
  private final double polygonFillOpacity   = 1.0d;
  
  
  
  /** The default stroke color for line string*/
  private final Color lineStringStrokeColor    = new Color(255,20,20);
  
  /** The default line string stroke opacity */
  private final double lineStringStrokeOpacity = 1.0d;
  
  /** The default stroke width for line string */
  private final double lineStringStrokeWidth   = 1.0d;
  

//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
//II IMPLEMENTATION                                                 II
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
  
  @Override
  public Style createStyle(SimpleFeatureType schema) {
    Class<?> geometryName            = null;

    Style style                   = null;
    StyleFactory sf               = null;

    Stroke stroke                 = null;
    Fill fill                     = null;
    Symbolizer symbolizer         = null;  
    
    if (schema != null){
      
      // 1) Initialisation des fabricants
      sf = CommonFactoryFinder.getStyleFactory(GeoTools.getDefaultHints());

        //System.out.println("[GISA_Frame] [loadShapeFile(String)] type "+i+": "+typeNames[i]);

        // Creation de styles **************************************************************
        // Attributs de style pour les objets
        geometryName = schema.getGeometryDescriptor().getType().getBinding();
        
        
        // Géométrie composée de points
        if (geometryName.getName().toUpperCase().endsWith("POINT")){
          
          stroke          = styleBuilder.createStroke(polygonStrokeColor, polygonStrokeWidth, polygonStrokeOpacity);
          fill            = styleBuilder.createFill(polygonFillColor, polygonFillOpacity);
          symbolizer      = sf.createPointSymbolizer();

          ((PointSymbolizer)symbolizer).setGraphic(null);
          style = styleBuilder.createStyle();
         
          style.featureTypeStyles().add(styleBuilder.createFeatureTypeStyle(schema.getTypeName(), symbolizer));
          
        // Géométrie composée de ligne  
        } else if (geometryName.getName().toUpperCase().endsWith("LINE")){
          
          stroke          = styleBuilder.createStroke(lineStrokeColor, lineStrokeWidth, lineStrokeOpacity);
          symbolizer      = sf.createLineSymbolizer();

          ((LineSymbolizer)symbolizer).setStroke(stroke);
          style = styleBuilder.createStyle();
          
          style.featureTypeStyles().add(styleBuilder.createFeatureTypeStyle(schema.getTypeName(), symbolizer));
          
          
        // Géométrie composée d'ensembles de lignes
        } else if (geometryName.getName().toUpperCase().endsWith("LINESTRING")){
          
          stroke          = styleBuilder.createStroke(lineStringStrokeColor, lineStringStrokeWidth, lineStringStrokeOpacity);
          symbolizer      = sf.createLineSymbolizer();

          ((LineSymbolizer)symbolizer).setStroke(stroke);
          style = styleBuilder.createStyle();
          
          style.featureTypeStyles().add(styleBuilder.createFeatureTypeStyle(schema.getTypeName(), symbolizer));
          
          
        // Géométrie composée de polygones
        } else if (geometryName.getName().toUpperCase().endsWith("POLYGON")){
          
          stroke          = styleBuilder.createStroke(polygonStrokeColor, polygonStrokeWidth, polygonStrokeOpacity);
          fill            = styleBuilder.createFill(polygonFillColor, polygonFillOpacity);
          symbolizer      = sf.createPolygonSymbolizer();

          ((PolygonSymbolizer)symbolizer).setStroke(stroke);
          ((PolygonSymbolizer)symbolizer).setFill(fill);
          style = styleBuilder.createStyle();
          
          style.featureTypeStyles().add(styleBuilder.createFeatureTypeStyle(schema.getTypeName(), symbolizer));
          
        // Geometrie raster.  
        } else if (geometryName.getName().toUpperCase().endsWith("RASTER")){
          System.err.println("[GaiaStyleCreator] [createStyle(DataStore)] raster geometry not yet handled");
        } else {
          System.err.println("[GaiaStyleCreator] [createStyle(DataStore)] Cannot determine geometry style for "+geometryName.getName());
        }
        
        System.out.println("[GaiaStyleCreator] [createStyle(DataStore)] geometry: "+geometryName);

        // ***********************************************************************************
               

        
        return style;
 
    } else {
      return null;
    }
  }

  @Override
  public SimpleFeatureType getSchema() {
    return schema;
  }
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
//II FIN IMPLEMENTATION                                             II
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII

//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC CONSTRUCTEUR                                                   CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC

  /**
   * Create a new default style creator attached to the given feature type.
   * @param schema the feature type.
   */
  public DefaultStyleCreator(SimpleFeatureType schema){
    this(schema, null);
  }
 
  /**
   * Create a new default style creator attached to the given feature type  and using the
   * given style builder. 
   * @param schema  the feature type.
   * @param builder the style builder to use.
   */
  public DefaultStyleCreator(SimpleFeatureType schema, StyleBuilder builder){
    
    if (builder != null){
      this.styleBuilder = builder;
    } else {
      new StyleBuilder();
    }
    
    this.schema = schema;
  }
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC FIN CONSTRUCTEUR                                               CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC

//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA ACCESSEURS                                                     AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  
  /**
   * Set the feature type attached to the created styles.
   * @param the feature type.
   */
  public void setSchema(SimpleFeatureType schema){
    this.schema = schema;
  }
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA FIN ACCESSEURS                                                 AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

  
}
