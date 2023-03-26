package org.arpenteur.gis.geotools.feature;


import java.util.ArrayList;

import org.arpenteur.common.geometry.geom2D.Polygon2D;
import org.arpenteur.common.geometry.primitive.Plan;
import org.arpenteur.common.geometry.primitive.point.IPoint2D;
import org.arpenteur.common.geometry.primitive.point.IPoint3D;
import org.arpenteur.common.geometry.referentiel.Transformation3D;
import org.arpenteur.mesurable.ItemMesurable;

import org.geotools.filter.identity.FeatureIdImpl;
import org.opengis.feature.type.Name;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

public class ItemMesurableFeature extends ListedSimpleFeature {

  /**
   * The geometries of the feature. By default, the default geometry is the
   * first one
   */
  Geometry[] geometries                   = null;
  
  /** The unique id of this feature */
  protected String featureId              = null;
  
  /**
   * The item mesurable represented by this feature.
   */
  private ItemMesurable item = null;
  
  /**
   * The name of the attribute
   */
  protected Name name = null;

//RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
//RR REDEFINITION                                                   RR
//RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
//-- Object ---------------------------------------------------------  
 
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
    if (!(obj instanceof ItemMesurableFeature)) {
      return false;
    }
    ItemMesurableFeature feat = (ItemMesurableFeature) obj;
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
            if (!((Geometry) getAttribute(i)).equals(otherAtt)) {
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
   * Construct a new feature representing an item mesurable. The feature type
   * must be specified because it's the type which contains coordinate reference
   * system informations. The default feature identifier is set to
   * <code>item.getIdentifier()</code>
   * @param type the type of the feature.
   * @param item the item represented by the created feature.
   */
  public ItemMesurableFeature(ItemMesurableFeatureType type, ItemMesurable item) {
    this(type, item, null, null);
  }

  /**
   * Construct a new feature representing the item mesurable. The feature type
   * must be specified because it's the type which contains coordinate reference
   * system informations.
   * @param type the type of the feature.
   * @param item the item represented by the created feature.
   * @param featureId the feature identificator. If this parameter is
   *          <code>null</code>, then the default
   * @param projectionPlane the plane where the item is projected. value is
   *          given by <code>item.getIdentifier()</code>
   * @see #ItemMesurableFeature(ItemMesurableFeatureType, ItemMesurable)
   * @see #org.arpenteur.gis.geotools.feature.ItemMesurableFeatureType
   */
  public ItemMesurableFeature(ItemMesurableFeatureType type,
      ItemMesurable item, String featureId, Plan projectionPlane) {
    
    
    super(type, new FeatureIdImpl(featureId != null ? featureId : ItemMesurableFeatureType.getFreeID()));
    
    Geometry defaultGeometryType      = null;
    Transformation3D transformation3D = null;
    IPoint3D translation              = null;
    double[] rotation                 = null;
    
    // Conservation d'une reference vers l'item
    this.item = item;

    if (item != null) {
      // Creation d'un polygone à partir d'une liste d'arrêtes.
      // Le deuxième paramètre est un ensemble de trous dans le polygone.
      // Si ce parametre est null, le polygone est plein.
      geometries = createGeometry(projectionPlane);
      defaultGeometryType = geometries[0];
      
      // Creation du feature et ajout dans la liste des features
      transformation3D = item.getTransformation();
      if (transformation3D != null) {
        translation = transformation3D.getTranslation();
        rotation = transformation3D.getAnglesRotation(transformation3D
            .getConvAngulaire());
      } else {
        translation = org.arpenteur.common.geometry.ArpenteurGeometryTools.newPoint3D(0.0d, 0.0d, 0.0d);
        rotation = new double[] { 0.0d, 0.0d, 0.0d };
      }
      // Transformation des attributs de l'item en attributs de la feature.
      setAttribute(ItemMesurableFeatureType.GEOMETRIC_ATT_NAME       , defaultGeometryType);
      setAttribute(ItemMesurableFeatureType.IDN_ATT_NAME             , new Integer(item.getIdn()));
      setAttribute(ItemMesurableFeatureType.NAME_ATT_NAME            , item.getName());
      setAttribute(ItemMesurableFeatureType.TIMEKEY_ATT_NAME         , new Long(item.getTimekey()));
      setAttribute(ItemMesurableFeatureType.SUVEYID_ATT_NAME         , item.getSurveyId());
      setAttribute(ItemMesurableFeatureType.JAVACLASS_ATT_NAME       , item.getTypologyName());
      setAttribute(ItemMesurableFeatureType.LOC_X_ATT_NAME           , new Double(translation.getX()));
      setAttribute(ItemMesurableFeatureType.LOC_Y_ATT_NAME           , new Double(translation.getY()));
      setAttribute(ItemMesurableFeatureType.LOC_Z_ATT_NAME           , new Double(translation.getZ()));
      setAttribute(ItemMesurableFeatureType.LOC_O_ATT_NAME           , new Double(rotation[0]));
      setAttribute(ItemMesurableFeatureType.LOC_P_ATT_NAME           , new Double(rotation[1]));
      setAttribute(ItemMesurableFeatureType.LOC_K_ATT_NAME           , new Double(rotation[2]));
      setAttribute(ItemMesurableFeatureType.METROLOGY_HEIGHT_ATT_NAME, new Double(item.getHeight()));
      setAttribute(ItemMesurableFeatureType.METROLOGY_WIDTH_ATT_NAME , new Double(item.getWidth()));
      setAttribute(ItemMesurableFeatureType.METROLOGY_LENGTH_ATT_NAME, new Double(item.getLength()));
      setAttribute(ItemMesurableFeatureType.METROLOGY_VOLUME_ATT_NAME, new Double(item.getVolume()));
      setAttribute(ItemMesurableFeatureType.METROLOGY_MASS_ATT_NAME  , new Double(item.getMass()));

    } else {
      // Creation d'attributs par defaut
      setAttribute(ItemMesurableFeatureType.GEOMETRIC_ATT_NAME       , null);
      setAttribute(ItemMesurableFeatureType.IDN_ATT_NAME             , new Integer(-1));
      setAttribute(ItemMesurableFeatureType.NAME_ATT_NAME            , null);
      setAttribute(ItemMesurableFeatureType.TIMEKEY_ATT_NAME         , new Long(0));
      setAttribute(ItemMesurableFeatureType.SUVEYID_ATT_NAME         , null);
      setAttribute(ItemMesurableFeatureType.JAVACLASS_ATT_NAME       , null);
      setAttribute(ItemMesurableFeatureType.LOC_X_ATT_NAME           , new Double(0.0d));
      setAttribute(ItemMesurableFeatureType.LOC_Y_ATT_NAME           , new Double(0.0d));
      setAttribute(ItemMesurableFeatureType.LOC_Z_ATT_NAME           , new Double(0.0d));
      setAttribute(ItemMesurableFeatureType.LOC_O_ATT_NAME           , new Double(0.0d));
      setAttribute(ItemMesurableFeatureType.LOC_P_ATT_NAME           , new Double(0.0d));
      setAttribute(ItemMesurableFeatureType.LOC_K_ATT_NAME           , new Double(0.0d));
      setAttribute(ItemMesurableFeatureType.METROLOGY_HEIGHT_ATT_NAME, new Double(0.0d));
      setAttribute(ItemMesurableFeatureType.METROLOGY_WIDTH_ATT_NAME , new Double(0.0d));
      setAttribute(ItemMesurableFeatureType.METROLOGY_LENGTH_ATT_NAME, new Double(0.0d));
      setAttribute(ItemMesurableFeatureType.METROLOGY_VOLUME_ATT_NAME, new Double(0.0d));
      setAttribute(ItemMesurableFeatureType.METROLOGY_MASS_ATT_NAME  , new Double(0.0d));
    }
   
  }

  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  //CC FIN CONSTRUCTEUR                                               CC
  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  //AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  //AA ACCESSEURS                                                     AA
  //AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  /**
   * Set the item mesurable represented by the feature.
   * @param item the item mesurable
   */
  public void setItemMesurable(ItemMesurable item) {
    this.item = item;
  }

  /**
   * Get the item mesurable represented by the feature.
   * @return the item mesurable.
   */
  public ItemMesurable getItem() {
    return this.item;
  }

  //AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  //AA FIN ACCESSEURS                                                 AA
  //AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA  
  protected Geometry[] createGeometry(Plan projectionPlane) {

    GeometryFactory factory = new GeometryFactory();
    
    Polygon2D polygon = null;
    IPoint2D point2D = null;
    Coordinate[] coordinates = null;
    LinearRing edges = null;
    Polygon geomI = null;
    Geometry[] geometries = new Geometry[1];
    ArrayList<Polygon2D> itemGeometries = null;
    if (getItem() != null) {
      itemGeometries = item.getRepresentation2D(null);
      if ((itemGeometries != null) && (itemGeometries.size() > 0)) {
        polygon = itemGeometries.get(0);
      }
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
