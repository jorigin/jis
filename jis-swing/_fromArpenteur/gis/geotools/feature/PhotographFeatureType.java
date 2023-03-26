package org.arpenteur.gis.geotools.feature;



import java.util.List;

import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.NameImpl;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Polygon;

public class PhotographFeatureType extends ListedSimpleFeatureType{

  public static final String GEOMETRIC_ATT_NAME        = "geometry";
  public static final String IDN_ATT_NAME              = "idn";
  public static final String NAME_ATT_NAME             = "name";
  public static final String URL_ATT_NAME              = "url";

  public static final String LOC_X_ATT_NAME            = "loc_x";
  public static final String LOC_Y_ATT_NAME            = "loc_y";
  public static final String LOC_Z_ATT_NAME            = "loc_z";
  public static final String LOC_O_ATT_NAME            = "loc_o"; 
  public static final String LOC_P_ATT_NAME            = "loc_p";
  public static final String LOC_K_ATT_NAME            = "loc_k";
  
  
  /**
   * The coordinates reference system associated to this feature type.
   * This coordinates reference system is attached to the default geometry
   * @see #defaultGeometry
   */
  CoordinateReferenceSystem crs         = null;
  
  public static final String namespace              = "http://www.arpenteur.net/";
  
  public static final String typeName               = "photograph";
  
  /**
   * The first free idn used to create a new
   * unique id for a feature.
   */
  protected static int firstFreeIdn       = 0;
 
  /**
   * The geometry factory used to create geometry for feature of this type.
   */
  //protected GeometryFactory geometryFactory      = null;

  
//RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
//RR REDEFINITION                                                   RR
//RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
// -- Object ---------------------------------------------------------
  public boolean equals(SimpleFeatureType other) {
    if(other == this)
        return true;
    
    if (other == null) {
        return false;
    }

    if ((typeName == null) && (other.getTypeName() != null)) {
        return false;
    } else if (!typeName.equals(other.getTypeName())) {
        return false;
    }

    if (this.getAttributeCount() != other.getAttributeCount()) {
        return false;
    }

    for (int i = 0, ii = this.getAttributeCount(); i < ii; i++) {
        if (!this.getDescriptor(i).equals(other.getDescriptor(i))) {
            return false;
        }
    }

    return true;
}
  
  public String toString() {
    String info = "name=" + typeName;
    info += (" , abstract=" + isAbstract());

    String types1 = "types=(";

    for (int i = 0, ii = this.getAttributeCount(); i < ii; i++) {
        types1 += this.getDescriptor(i).toString();

        if (i < ii) {
            types1 += ",";
        }
    }

    types1 += ")";
    info += (" , " + types1);

    return "PhotographFeatureType [" + info + "]";
  }

  public boolean equals(Object other) {
    if (other instanceof SimpleFeatureType) 
        return equals((SimpleFeatureType) other);
        return false;
  }
//--------------------------------------------------------------------

//RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
//RR FIN REDEFINITION                                               RR
//RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR

  
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC CONSTRUCTEUR                                                   CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  
  
  
  /**
   * Construct a new feature representing an item mesurable. The coordinate system
   * correspond to the geotools coordinate system constructed from ARPENTEUR.
   * @param crs the coordinate reference system to use.
   */
  public PhotographFeatureType(CoordinateReferenceSystem crs){
    super(new NameImpl(namespace, typeName));

    List<AttributeDescriptor> attributeTypes = getAttributeDescriptors();
    AttributeTypeBuilder builder                  = new AttributeTypeBuilder();
    AttributeDescriptor descriptor                = null;
    
    
    if (crs != null){
      this.crs = crs;
    } else {
      this.crs = DefaultEngineeringCRS.CARTESIAN_2D;
    }
    
    builder.setName(GEOMETRIC_ATT_NAME);
    builder.setBinding(Polygon.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(true);
    builder.setCRS(crs);
    descriptor = builder.buildDescriptor(GEOMETRIC_ATT_NAME);
    attributeTypes.add(descriptor);
    
    builder.setName(IDN_ATT_NAME);
    builder.setBinding(Integer.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    descriptor = builder.buildDescriptor(IDN_ATT_NAME);
    attributeTypes.add(descriptor);
    
    builder.setName(NAME_ATT_NAME);
    builder.setBinding(String.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(true);
    descriptor = builder.buildDescriptor(NAME_ATT_NAME);
    attributeTypes.add(descriptor);

    builder.setName(URL_ATT_NAME);
    builder.setBinding(String.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(true);
    descriptor = builder.buildDescriptor(URL_ATT_NAME);
    attributeTypes.add(descriptor);
    
   
    builder.setName(LOC_X_ATT_NAME);
    builder.setBinding(Double.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    descriptor = builder.buildDescriptor(LOC_X_ATT_NAME);
    attributeTypes.add(descriptor);
  
    builder.setName(LOC_Y_ATT_NAME);
    builder.setBinding(Double.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    descriptor = builder.buildDescriptor(LOC_Y_ATT_NAME);
    attributeTypes.add(descriptor);
    
    builder.setName(LOC_Z_ATT_NAME);
    builder.setBinding(Double.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    descriptor = builder.buildDescriptor(LOC_Z_ATT_NAME);
    attributeTypes.add(descriptor);
    
    builder.setName(LOC_O_ATT_NAME);
    builder.setBinding(Double.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    descriptor = builder.buildDescriptor(LOC_O_ATT_NAME);
    attributeTypes.add(descriptor);
    
    builder.setName(LOC_P_ATT_NAME);
    builder.setBinding(Double.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    descriptor = builder.buildDescriptor(LOC_P_ATT_NAME);
    attributeTypes.add(descriptor);
    
    builder.setName(LOC_K_ATT_NAME);
    builder.setBinding(Double.class);
    builder.setMinOccurs(1);
    builder.setMaxOccurs(1);
    builder.setNillable(false);
    descriptor = builder.buildDescriptor(LOC_K_ATT_NAME);
    attributeTypes.add(descriptor);
    
    this.setDescriptors(attributeTypes);
  }
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC FIN CONSTRUCTEUR                                               CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC

  
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA ACCESSEURS                                                     AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  
  public static String getFreeID(){
    return typeName+"_"+firstFreeIdn;
  }
  
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA FIN ACCESSEURS                                                 AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

  
  
}
