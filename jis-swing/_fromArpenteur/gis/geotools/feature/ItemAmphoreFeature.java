package org.arpenteur.gis.geotools.feature;


import java.util.ArrayList;

import org.arpenteur.common.geometry.geom2D.Polygon2D;
import org.arpenteur.common.geometry.primitive.Plan;
import org.arpenteur.common.geometry.primitive.point.IPoint2D;
import org.arpenteur.mesurable.ItemMesurable;
import org.arpenteur.mesurable.archeologie.amphore.ItemAmphore;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

public class ItemAmphoreFeature extends ItemMesurableFeature {

  private MultiLineString drawings = null; 


  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  //CC CONSTRUCTEUR                                                   CC
  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  /**
   * Construct a new feature representing an amphora. The feature type must
   * be specified because it's the type which contains coordinate reference system
   * informations. The default feature identifier is set to <code>item.getIdentifier()</code>
   * @param type the type of the feature.
   * @param item the amphora represented by the created feature.
   */
  public ItemAmphoreFeature(ItemAmphoreFeatureType type, ItemMesurable item){
    this(type, item, null, null);
  }

  /**
   * Construct a new feature representing an amphora. The feature type must be
   * specified because it's the type which contains coordinate reference system informations.
   * @param type the type of the feature.
   * @param item the amphora represented by the created feature.
   * @param featureId the feature identificator. If this parameter is <code>null</code>, then the default
   * @param projectionPlane the plane where the item is projected.
   * value is given by <code>item.getIdentifier()</code>
   * @see #ItemAmphoreFeature(ItemAmphoreFeatureType, ItemAmphore)
   * @see #org.arpenteur.gis.geotools.feature.ItemAmphoreFeatureType
   */
  public ItemAmphoreFeature(ItemAmphoreFeatureType type, ItemMesurable item, String featureId, Plan projectionPlane){
    super(type, item, featureId, projectionPlane); 

    if (item instanceof ItemAmphore){

      ItemAmphore amphore = (ItemAmphore) item;  

      setAttribute(ItemAmphoreFeatureType.STANDARD_ID             , amphore.getIdStandard());
      setAttribute(ItemAmphoreFeatureType.INVENTORY               , amphore.getInventaire());
      setAttribute(ItemAmphoreFeatureType.LOCALISATION            , amphore.getLocalisation());
      setAttribute(ItemAmphoreFeatureType.FRAGMENT_COUNT          , new Integer(amphore.getNbFragment()));
      setAttribute(ItemAmphoreFeatureType.DESCRIPTION             , item.getDescription());
      setAttribute(ItemAmphoreFeatureType.REMAIN_HEIGHT           , new Double(amphore.getHauteurConserve()));
      setAttribute(ItemAmphoreFeatureType.INTERNAL_DIAMETER       , new Double(amphore.getDiamInt()));
      setAttribute(ItemAmphoreFeatureType.EXTERNAL_DIAMETER       , new Double(amphore.getDiamExt()));
      setAttribute(ItemAmphoreFeatureType.FOOT_DIAMETER           , new Double(amphore.getDiamPied()));
      setAttribute(ItemAmphoreFeatureType.METROLOGY_WIDTH_ATT_NAME, new Double(amphore.getWidth()));
      setAttribute(ItemAmphoreFeatureType.HEIGHT_LIPS             , new Double(amphore.getHauteurLevre()));  
      setAttribute(ItemAmphoreFeatureType.PC_LIPS                 , new Double(amphore.getPClev()));

      amphore = null;
    } else {
      setAttribute(ItemAmphoreFeatureType.STANDARD_ID             , null);
      setAttribute(ItemAmphoreFeatureType.INVENTORY               , null);
      setAttribute(ItemAmphoreFeatureType.LOCALISATION            , null);
      setAttribute(ItemAmphoreFeatureType.FRAGMENT_COUNT          , null);
      setAttribute(ItemAmphoreFeatureType.DESCRIPTION             , item.getDescription());
      setAttribute(ItemAmphoreFeatureType.REMAIN_HEIGHT           , new Double(0.0d));
      setAttribute(ItemAmphoreFeatureType.INTERNAL_DIAMETER       , new Double(0.0d));
      setAttribute(ItemAmphoreFeatureType.EXTERNAL_DIAMETER       , new Double(0.0d));
      setAttribute(ItemAmphoreFeatureType.FOOT_DIAMETER           , new Double(0.0d));
      setAttribute(ItemAmphoreFeatureType.METROLOGY_WIDTH_ATT_NAME, new Double(0.0d));
      setAttribute(ItemAmphoreFeatureType.HEIGHT_LIPS             , new Double(0.0d));  
      setAttribute(ItemAmphoreFeatureType.PC_LIPS                 , new Double(0.0d));
    }

  }
  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  //CC FIN CONSTRUCTEUR                                               CC
  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC

  @Override
  protected Geometry[] createGeometry(Plan projectionPlane){

    GeometryFactory factory = new GeometryFactory();

    Polygon p                         = null;
    ArrayList<Polygon> polygons       = null;
    MultiPolygon multipolygon         = null;

    Geometry[] geoms                  = new Geometry[5];

    if (getItem() != null){


      //System.out.println("[ItemAmphoreFeature] [createGeometry()] Creating geometry for "+getItem().getIdentifier());

      ArrayList<Polygon2D> geometries = getItem().getRepresentation2D(null);

      if ((geometries != null) && (geometries.size() > 0)){

        System.out.println("[ItemAmphoreFeature] [createGeometry()] Available geometries:  "+geometries.size());

        polygons = new ArrayList<Polygon>();


        for(int i = 0; i < geometries.size(); i++){

          //System.out.println("[ItemAmphoreFeature] [createGeometry()] Processing polygon "+i+" #vertices: "+geometries.get(i).getNumPoints());
          p = createPolygon(geometries.get(i));

          if (p != null){
            polygons.add(p);
          }
        }

        //	polygons = new Polygon[2];
        //	polygons[0] = createPolygon((Polygon2D) geometries.get(0));
        //	polygons[1] = createPolygon((Polygon2D) geometries.get(1));

        multipolygon = factory.createMultiPolygon(polygons.toArray(new Polygon[polygons.size()]));

        // Integration des picots
        drawings = createDrawings(geometries);

        //System.out.println("[ItemAmphoreFeature] [init()] Sett drawings: "+drawings);
        setAttribute(ItemAmphoreFeatureType.GEOM_DRAWINGS           , drawings);
        
      } else {
        System.out.println("[ItemAmphoreFeature] [createGeometry()] No computed geometries available");
      }

    } 

    geoms[0] = multipolygon;
    
    return geoms;
  }  


  private Polygon createPolygon(Polygon2D polygon){

    GeometryFactory factory = new GeometryFactory();

    IPoint2D point2D                  = null;

    Coordinate[] coordinates          = null;
    LinearRing edges                  = null;
    Polygon geomI                     = null;

    if ((polygon != null) && (polygon.getPoints().length > 2)){

      // Creation d'un ensemble de point projection des sommets de l'enveloppe convexe
      // de l'item mesurable sur le plan de projection.
      // ATTENTION, le permier et le dernier point doivent être identiques afin de pouvoir
      // creer un polygone fermé. Le premier point est donc doublé au début et à la fin.
      coordinates = new Coordinate[polygon.getPoints().length + 1];

      point2D = polygon.getPoint(0);
      coordinates[0] = new Coordinate(point2D.getX(), point2D.getY());
      coordinates[coordinates.length - 1] = new Coordinate(point2D.getX(), point2D.getY());

      for(int i = 1; i < coordinates.length -1; i++){
        point2D = polygon.getPoint(i);
        coordinates[i] = new Coordinate(point2D.getX(), point2D.getY());
      }

      // Creation des arrêtes du polygone
      edges = factory.createLinearRing(coordinates);

      // Creation d'un polygone à partir d'une liste d'arrêtes.
      // Le deuxième paramètre est un ensemble de trous dans le polygone.
      // Si ce parametre est null, le polygone est plein.
      geomI = factory.createPolygon(edges, null);

      if (!geomI.isValid()){
        System.err.println("[ItemAmphoreFeature] [createPolygon] Geometry is not valid, using external ring");

        geomI = factory.createPolygon((LinearRing)geomI.getExteriorRing(), null);

        if (!geomI.isValid()){
          System.err.println("[ItemAmphoreFeature] [createPolygon] Using external ring fails, no geometry can be created");
          //geomI = null;
        }
      }

    } else {
      //System.err.println("[ItemAmphoreFeature] [createPolygon] Cannot create polygon from "+polygon.getPoints().length+" points: "+polygon.getPoints());
      geomI = null;
    }

    return geomI;
  }

  

  /**
   * Create a geometry which is like archaeological hand made drawings
   * @return the geometry.
   */
  protected MultiLineString createDrawings(ArrayList<Polygon2D> geometries){

    GeometryFactory factory = new GeometryFactory();

    LineString lineString             = null;
    ArrayList<LineString> lineStrings = null;
    MultiLineString multiLineStrings  = null;

    if (getItem() != null){

      //System.out.println("[ItemAmphoreFeature] [createGeometry()] Creating geometry for "+getItem().getIdentifier());

      if ((geometries != null) && (geometries.size() > 0)){

        //System.out.println("[ItemAmphoreFeature] [createGeometry()] Available geometries:  "+geometries.size());

        lineStrings = new ArrayList<LineString>();

        for(int i = 0; i < geometries.size(); i++){

          //System.out.println("[ItemAmphoreFeature] [createGeometry()] Processing polygon "+i+" #vertices: "+geometries.get(i).getNumPoints());
          lineString = createLineString(geometries.get(i));
          if (lineString != null){
            lineStrings.add(lineString);
          }
          //System.out.println("[ItemAmphoreFeature] [createGeometry()] Processed polygon "+i+" / "+geometries.size());
        }

        multiLineStrings = factory.createMultiLineString(lineStrings.toArray(new LineString[lineStrings.size()]));

      } else {
        System.out.println("[ItemAmphoreFeature] [createGeometry()] No computed geometries available");
      }

    } 


    return multiLineStrings;
  }  



  /**
   * Create a line string representing a polygon
   * @param polygon the polygon to export in line string
   * @return the line string corresponding to the polygon
   */
  private LineString createLineString(Polygon2D polygon){

    GeometryFactory factory = new GeometryFactory();

    IPoint2D point2D                  = null;

    Coordinate[] coordinates          = null;
    LineString geomI                  = null;

    if ((polygon != null) && (polygon.getPoints().length >= 2)){

      // Creation d'un ensemble de point projection des sommets de l'enveloppe convexe
      // de l'item mesurable sur le plan de projection.
      coordinates = new Coordinate[polygon.getPoints().length];

      for(int i = 0; i < coordinates.length; i++){
        point2D = polygon.getPoint(i);
        coordinates[i] = new Coordinate(point2D.getX(), point2D.getY(), 0.0d);
      }

      // Creation des lignes
      try {
        geomI = factory.createLineString(coordinates);
        geomI.normalize();

        if (!geomI.isValid()){
          geomI = null;
        }

      } catch (Exception e) {
        System.out.println("[ItemAmphoreFeaturePicot] [createLineString()] Invalid geometry...");
        System.out.println("[ItemAmphoreFeaturePicot] [createLineString()] coordinates: "+geomI.getCoordinateSequence());
        e.printStackTrace(System.out);
      }

    } else {
      //System.out.println("[ItemAmphoreFeaturePicot] [createLineString()] Cannot create line string from "+polygon.getPoints().length+" points: "+polygon.getPoints());
      geomI = null;
    }

    return geomI;
  }


}
