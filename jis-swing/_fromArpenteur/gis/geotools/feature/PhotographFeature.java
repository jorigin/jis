package org.arpenteur.gis.geotools.feature;


import org.arpenteur.common.geometry.geom2D.Polygon2D;
import org.arpenteur.common.geometry.primitive.Plan;
import org.arpenteur.common.geometry.primitive.point.IPoint2D;
import org.arpenteur.common.geometry.primitive.point.IPoint3D;
import org.arpenteur.common.geometry.referentiel.Transformation3D;
import org.arpenteur.photogrammetry.image.Photograph;

import org.geotools.filter.identity.FeatureIdImpl;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

public class PhotographFeature extends ListedSimpleFeature{

  /**
   * The geometries of the feature. By default, the default geometry is the
   * first one
   */
  Geometry[] geometries = null;
  /** The unique id of this feature */
  protected String featureId = null;


  /**
   * The item mesurable represented by this feature.
   */
  private Photograph photograph = null;

  private Polygon2D polygon     = null;
  
  
  //RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
  //RR REDEFINITION                                                   RR
  //RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
  // -- Object ---------------------------------------------------------  
  @Override
  public String toString() {
    String retString = "Feature[ id=" + getID() + " , ";
    SimpleFeatureType featType = getFeatureType();
    for (int i = 0, n = getAttributeCount(); i < n; i++) {
      retString += (featType.getDescriptor(i).getName() + "=");
      retString += this.getAttribute(i);
      if ((i + 1) < n) {
        retString += " , ";
      }
    }
    return retString += " ]";
  }

  /**
   * override of equals. Returns if the passed in object is equal to this.
   * @param obj the Object to test for equality.
   * @return <code>true</code> if the object is equal, <code>false</code>
   *         otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof PhotographFeature)) {
      return false;
    }
    PhotographFeature feat = (PhotographFeature) obj;
    if (!feat.getFeatureType().equals(getFeatureType())) {
      return false;
    }
    // this check shouldn't exist, by contract, 
    //all features should have an ID.
    if (featureId == null) {
      if (feat.getID() != null) {
        return false;
      }
    }
    if (!featureId.equals(feat.getID())) {
      return false;
    }
    for (int i = 0, ii = getAttributeCount(); i < ii; i++) {
      Object otherAtt = feat.getAttribute(i);
      if (getAttribute(i) == null) {
        if (otherAtt != null) {
          return false;
        }
      } else {
        if (!getAttribute(i).equals(otherAtt)) {
          if (getAttribute(i) instanceof Geometry
              && otherAtt instanceof Geometry) {
            // we need to special case Geometry
            // as JTS is broken
            // Geometry.equals( Object ) and Geometry.equals( Geometry )
            // are different 
            // (We should fold this knowledge into AttributeType...)
            // 
            if (!((Geometry) getAttribute(i)).equals((Geometry) otherAtt)) {
              return false;
            }
          } else {
            return false;
          }
        }
      }
    }
    return true;
  }

  //-------------------------------------------------------------------- 
  //RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
  //RR FIN REDEFINITION                                               RR
  //RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
  
  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  //CC CONSTRUCTEUR                                                   CC
  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  /**
   * Construct a new feature representing a photograph. The feature type
   * must be specified because it's the type which contains coordinate reference
   * system informations. The default feature identifier is set to
   * <code>photograph.getIdentifier()</code>
   * @param type the type of the feature.
   * @param photograph the photograph represented by the created feature.
   */
  public PhotographFeature(PhotographFeatureType type, Photograph photograph, Polygon2D polygon) {
    this(type, photograph, null, null, polygon);
  }

  /**
   * Construct a new feature representing the photograph. The feature type
   * must be specified because it's the type which contains coordinate reference
   * system informations.
   * @param type the type of the feature.
   * @param photograph the photograph represented by the created feature.
   * @param featureId the feature identificator. If this parameter is
   *          <code>null</code>, then the default
   * @param projectionPlane the plane where the item is projected. value is
   *          given by <code>item.getIdentifier()</code>
   * @see #PhotographFeature(PhotographFeatureFeatureType, Photograph)
   * @see #org.arpenteur.gis.geotools.feature.PhotographFeatureType
   */
  public PhotographFeature(PhotographFeatureType type, Photograph photograph, String featureId, Plan projectionPlane, Polygon2D polygon) {
    super(type, new FeatureIdImpl(featureId != null ? featureId : PhotographFeatureType.getFreeID()));
	Transformation3D transformation3D = null;
    IPoint3D translation = null;
    double[] rotation = null;
    
    Geometry defaultGeometryType = null;
    
    // Conservation d'une reference vers l'item
    this.photograph = photograph;

    this.polygon = polygon;
    
    if (photograph != null) {
      // Creation d'un polygone à partir d'une liste d'arrêtes.
      // Le deuxième paramètre est un ensemble de trous dans le polygone.
      // Si ce parametre est null, le polygone est plein.
      geometries = createGeometry(projectionPlane);
      defaultGeometryType = geometries[0];
      
      // Creation du feature et ajout dans la liste des features
      transformation3D = photograph.getTransformation();
      if (transformation3D != null) {
        translation = transformation3D.getTranslation();
        rotation = transformation3D.getAnglesRotation(transformation3D
            .getConvAngulaire());
      } else {
        translation = org.arpenteur.common.geometry.ArpenteurGeometryTools.newPoint3D(0.0d, 0.0d, 0.0d);
        rotation = new double[] { 0.0d, 0.0d, 0.0d };
      }
      // Transformation des attributs de l'item en attributs de la feature.
      setAttribute(PhotographFeatureType.GEOMETRIC_ATT_NAME, defaultGeometryType);
      setAttribute(PhotographFeatureType.IDN_ATT_NAME, photograph.getIdn());
      setAttribute(PhotographFeatureType.NAME_ATT_NAME, photograph.getName());
      setAttribute(PhotographFeatureType.URL_ATT_NAME, photograph.getUrl().toExternalForm());  
      setAttribute(PhotographFeatureType.LOC_X_ATT_NAME, translation.getX());
      setAttribute(PhotographFeatureType.LOC_Y_ATT_NAME, translation.getY());
      setAttribute(PhotographFeatureType.LOC_Y_ATT_NAME, translation.getZ());
      setAttribute(PhotographFeatureType.LOC_O_ATT_NAME, rotation[0]);
      setAttribute(PhotographFeatureType.LOC_P_ATT_NAME, rotation[1]);
      setAttribute(PhotographFeatureType.LOC_K_ATT_NAME, rotation[2]);

    } else {
    	
      setAttribute(PhotographFeatureType.GEOMETRIC_ATT_NAME, null);
      setAttribute(PhotographFeatureType.IDN_ATT_NAME, new Integer(-1));
      setAttribute(PhotographFeatureType.NAME_ATT_NAME, null);
      setAttribute(PhotographFeatureType.URL_ATT_NAME, null);  
      setAttribute(PhotographFeatureType.LOC_X_ATT_NAME, Double.NaN);
      setAttribute(PhotographFeatureType.LOC_Y_ATT_NAME, Double.NaN);
      setAttribute(PhotographFeatureType.LOC_Y_ATT_NAME, Double.NaN);
      setAttribute(PhotographFeatureType.LOC_O_ATT_NAME, Double.NaN);
      setAttribute(PhotographFeatureType.LOC_P_ATT_NAME, Double.NaN);
      setAttribute(PhotographFeatureType.LOC_K_ATT_NAME, Double.NaN);
    }
    
    
  }

  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  //CC FIN CONSTRUCTEUR                                               CC
  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  //AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  //AA ACCESSEURS                                                     AA
  //AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  /**
   * Set the photograph represented by the feature.
   * @param photograph the photograph
   */
  public void setPhotograph(Photograph photograph) {
    this.photograph = photograph;
  }

  /**
   * Get the photograph represented by the feature.
   * @return the photograph.
   */
  public Photograph getPhotograph() {
    return this.photograph;
  }

  /**
   * Get the polygon of the photograph on the ground
   * @return the polygon
   */
  public Polygon2D getPolygon(){
    return polygon;
  }
  //AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  //AA FIN ACCESSEURS                                                 AA
  //AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA  
  protected Geometry[] createGeometry(Plan projectionPlane) {

    IPoint2D point2D = null;
    Coordinate[] coordinates = null;
    LinearRing edges = null;
    Polygon geomI = null;
    Geometry[] geometries = new Geometry[1];
    
    GeometryFactory factory = new GeometryFactory();
    
    if (getPhotograph() != null) {
      if (polygon != null) {
        // Creation d'un ensemble de point projection des sommets de l'enveloppe convexe
        // de l'item mesurable sur le plan de projection.
        // ATTENTION, le permier et le dernier point doivent être identiques afin de pouvoir
        // creer un polygone fermé. Le premier point est donc doublé au début et à la fin.
        coordinates = new Coordinate[polygon.getPoints().length + 1];
        point2D = polygon.getPoint(0);
        coordinates[0] = new Coordinate(point2D.getX(), point2D.getY());
        coordinates[coordinates.length - 1] = new Coordinate(point2D.getX(),
            point2D.getY());
        for (int i = 1; i < coordinates.length - 1; i++) {
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
        geomI = null;
      }
    }
    geometries[0] = geomI;
    return geometries;
  }
  
}
