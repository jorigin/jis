package org.arpenteur.gis.geotools.feature;




import org.arpenteur.common.geometry.geom2D.Polygon2D;
import org.arpenteur.common.geometry.manager.Point2DManager;
import org.arpenteur.common.geometry.manager.Point2DManagerImpl;
import org.arpenteur.common.geometry.manager.IPoint3DManager;
import org.arpenteur.common.geometry.polyedre.BoundingBox;
import org.arpenteur.common.geometry.primitive.Plan;
import org.arpenteur.common.geometry.primitive.point.IPoint2D;
import org.arpenteur.common.geometry.primitive.point.IPoint3D;
import org.arpenteur.mesurable.ItemMesurable;
import org.arpenteur.mesurable.architecture.elementDeParement.Bloc;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

public class BlocFeature extends ItemMesurableFeature {

  
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC CONSTRUCTEUR                                                   CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  /**
   * Construct a new feature representing an ashlar bloc. The feature type must
   * be specified because it's the type which contains coordinate reference system
   * informations. The default feature identifier is set to <code>item.getIdentifier()</code>
   * @param type the type of the feature.
   * @param item the bloc represented by the created feature.
   */
  public BlocFeature(BlocFeatureType type, ItemMesurable item){
    this(type, item, null, null);
  }
  
  /**
   * Construct a new feature representing an ashlar bloc. The feature type must be
   * specified because it's the type which contains coordinate reference system informations.
   * @param type the type of the feature.
   * @param item the bloc represented by the created feature.
   * @param featureId the feature identificator. If this parameter is <code>null</code>, then the default
   * @param projectionPlane the plane where the item is projected.
   * value is given by <code>item.getIdentifier()</code>
   * @see #BlocFeature(ItemAmphoreFeatureType, Bloc)
   * @see #org.arpenteur.gis.geotools.feature.BlocFeatureType
   */
  public BlocFeature(BlocFeatureType type, ItemMesurable item, String featureId, Plan projectionPlane){
    super(type, item, featureId, projectionPlane); 

    if (item instanceof Bloc){
      
      Bloc bloc = (Bloc) item;
      setAttribute(BlocFeatureType.USM_ID     , bloc.getUSMId());
      setAttribute(BlocFeatureType.UMS_NAME   , bloc.getUSMName());
      setAttribute(BlocFeatureType.LITOTIPO   , bloc.getLitotipo());
      setAttribute(BlocFeatureType.LAVORAZIONE, bloc.getLavorazione());
      setAttribute(BlocFeatureType.FINITURA   , bloc.getFinitura());
      setAttribute(BlocFeatureType.DESCRIPTION, bloc.getDescription());
      bloc  = null;
    } else {
      setAttribute(BlocFeatureType.USM_ID     , new Integer(-1));
      setAttribute(BlocFeatureType.UMS_NAME   , null);
      setAttribute(BlocFeatureType.LITOTIPO   , null);
      setAttribute(BlocFeatureType.LAVORAZIONE, null);
      setAttribute(BlocFeatureType.FINITURA   , null);
      setAttribute(BlocFeatureType.DESCRIPTION, null);
    }
  }
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC FIN CONSTRUCTEUR                                               CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC

  
  @Override
  protected Geometry[] createGeometry(Plan projectionPlane){
    
    GeometryFactory factory           = new GeometryFactory();
    
    IPoint3DManager points3D             = null;
    Point2DManager projectedPoints    = null;
    Polygon2D polygon                 = null;
    
    IPoint3D point                    = null;
    IPoint2D point2D                  = null;

    
    
    Coordinate[] coordinates          = null;
    LinearRing edges                  = null;
    Polygon geomI                     = null;
    
    com.vividsolutions.jts.geom.Geometry[] geometries             = new com.vividsolutions.jts.geom.Geometry[1];
    
    Bloc bloc                         = null;
    
    Plan plane                        = null;
    
    if (getItem() != null){
      
      
      if (projectionPlane == null){
        bloc = (Bloc) getItem();
        plane = (Plan) bloc.getPrimitiveExtrude();
      } else {
	plane = projectionPlane;
      }
      
      if (plane == null){
	 System.out.println("[BlocFeature] [constructor] No projection plane available");
      } else {
	System.out.println("[BlocFeature] [constructor] Using projection plane: "+plane);
      }

      //  Creation de la geometrie 2D de l'item
      //  points3D = item.computeConvexHull().getVertices();
      points3D = getItem().getPolyhedronRepresentation().getVertices();
      
      if (points3D == null){
        System.out.println("[BlocFeature] [constructor] No 3D point in bounding box, using min vol bbox");
        BoundingBox bm = getItem().getBoundingBox();
        if (bm != null)
          points3D = bm.getListePoint();
      }
      
      if (points3D == null){
        System.out.println("[BlocFeature] [constructor] No 3D point in OOBBOX, using measured 3D points");
        points3D = getItem().getPoint3DManager();
      }
    
      if ((points3D != null) && (points3D.size() > 0)){

        projectedPoints = new Point2DManagerImpl();
 
        for(int i = 0; i < points3D.size(); i++){
          point = points3D.get(i);
         
          point = plane.projetteSur(points3D.get(i));
          
          // Application de la transformation du plan au point
	  point = plane.getTransformed(point);
           
          if (point != null){
            // ATTENTION MODIF HYPER SALE POUR GENERER UN EXEMPLE, X est inversé
            projectedPoints.add(org.arpenteur.common.geometry.ArpenteurGeometryTools.newPoint2D(point.getName(), point.getIdn(), -point.getX(), point.getY()));
          }
        }
      
        // Creation d'un polygone simple reliant tous les points projetés
        // Un pre-traitement s'assure qu'aucun point n'est en double.
        Point2DManager points = new Point2DManagerImpl();
        int j = 0;
        boolean found = false;
        IPoint2D l = null;
        for(int i = 0; i < projectedPoints.size(); i++){
          l = projectedPoints.get(i);
          
          j = 0;
          found = false;
          while((!found) && (j < points.size())){
            if (l.equals(projectedPoints.get(j))){
              found = true;
            }
            j++;
          }
          
          if (!found){
            points.add(l);
          }
        }
        
        //polygon = org.arpenteur.common.geometry.Geometry.computeConvexHull(projectedPoints);
        polygon = new Polygon2D(points, true);
        
        // Calcul de l'enveloppe convexe 2D du polygone regroupant tous les points
        polygon = polygon.getConvexHull();
        
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

      } else {
        System.out.println("[BlocFeature] [constructor] No 3D point available");
        geomI = null;
      }

    }
    
    geometries[0] = geomI;
    
    return geometries;
  }
  
  
}