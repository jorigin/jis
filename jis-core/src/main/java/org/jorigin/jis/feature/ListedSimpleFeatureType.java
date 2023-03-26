package org.jorigin.jis.feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.logging.Level;

import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.feature.type.PropertyType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.util.InternationalString;
import org.geotools.feature.NameImpl;
import org.jorigin.Common;

/**
 * An implementation of a {@link org.opengis.feature.simple.SimpleFeatureType SimpleFeatureType} 
 * with an underlying {@link java.util.List List} of attributes.
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 */
public class ListedSimpleFeatureType implements SimpleFeatureType{

  private static final List<Filter> NO_RESTRICTIONS =  Collections.emptyList();

  /**
   * The list of attributes types
   */
  List<AttributeType> types = null;

  /**
   * The list of attributes descriptors
   */
  List<AttributeDescriptor> descriptors;

  protected Map<Name, PropertyDescriptor> propertyMap = null;

  Map<String, Integer> index            = null;


  private boolean identified            = true;

  protected final Class<Collection<Property>> binding;

  protected final PropertyType superType;

  protected final InternationalString description;

  protected final Name name;

  protected final List<Filter> restrictions;

  protected final Map<Object,Object> userData;

  protected final boolean isAbstract;



  //IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
  //II IMPLANTATION                                                     II
  //IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII

  // org.opengis.feature.simple.SimpleFeatureType ------------------------
  @Override
  public int getAttributeCount() {
    if (getTypes() != null){
      return getTypes().size();
    } else {
      return -1;
    }  
  }

  @Override
  public List<AttributeDescriptor> getAttributeDescriptors() {
    //return Collections.unmodifiableList(descriptors);
    return this.descriptors;
  }

  @Override
  public AttributeDescriptor getDescriptor(String name) {
    return getDescriptor(new NameImpl( name ) );
  }

  @Override
  public AttributeDescriptor getDescriptor(Name name) {
    return (AttributeDescriptor) this.propertyMap.get(name);
  }

  @Override
  public AttributeDescriptor getDescriptor(int index) throws IndexOutOfBoundsException {
    return this.descriptors.get(index);
  }

  @Override
  public AttributeType getType(String name) {
    AttributeDescriptor attribute = (AttributeDescriptor) getDescriptor(name);
    if (attribute != null) {
      return attribute.getType();
    }

    return null;
  }

  @Override
  public AttributeType getType(Name name) {
    AttributeDescriptor attribute = (AttributeDescriptor) getDescriptor(name);
    if (attribute != null) {
      return attribute.getType();
    }

    return null;
  }

  @Override
  public AttributeType getType(int index) throws IndexOutOfBoundsException {
    return getTypes().get(index);
  }

  @Override
  public String getTypeName() {
    return getName().getLocalPart();
  }

  @Override
  public List<AttributeType> getTypes() {
    if (this.types == null) {
      synchronized (this) {
        if (this.types == null) {
          this.types = new ArrayList<AttributeType>();
          for (Iterator<AttributeDescriptor> itr = this.descriptors
              .iterator(); itr.hasNext();) {
            AttributeDescriptor ad = itr.next();
            this.types.add(ad.getType());
          }
        }
      }
    }

    return this.types;
  }

  @Override
  public int indexOf(String name) {
    Integer idx = this.index.get(name);
    if(idx != null)
      return idx.intValue();
    else
      return -1;
  }

  @Override
  public int indexOf(Name name) {
    if(name.getNamespaceURI() == null)
      return indexOf(name.getLocalPart());

    // otherwise do a full scan
    int index = 0;
    for (Iterator<AttributeDescriptor> itr = getAttributeDescriptors().iterator(); itr.hasNext(); index++) {
      AttributeDescriptor descriptor = (AttributeDescriptor) itr.next();
      if (descriptor.getName().equals(name)) {
        return index;
      }
    }
    return -1;
  }

  @Override
  public CoordinateReferenceSystem getCoordinateReferenceSystem() {
    CoordinateReferenceSystem crs         = null;
    if ( getGeometryDescriptor() != null && getGeometryDescriptor().getType().getCoordinateReferenceSystem() != null) {
      crs = getGeometryDescriptor().getType().getCoordinateReferenceSystem();
    }
    if(crs == null) {
      for (PropertyDescriptor property : this.propertyMap.values()) {
        if ( property instanceof GeometryDescriptor ) {
          GeometryDescriptor geometry = (GeometryDescriptor) property;
          if ( geometry.getType().getCoordinateReferenceSystem() != null ) {
            crs = geometry.getType().getCoordinateReferenceSystem();
            break;
          }
        }
      }
    }

    return crs;
  }

  @Override
  public GeometryDescriptor getGeometryDescriptor() {

    AttributeDescriptor defaultGeometry = null;

    int idx = -1;
    try {
      idx = this.index.get(null);
    } catch (Exception e) {
      Common.logger.log(Level.SEVERE, "Cannot get geometry descriptor for "+getName(), e);
    }

    if (idx > -1){
      defaultGeometry = this.descriptors.get(idx);
    }

    if (defaultGeometry == null) {
      for (PropertyDescriptor property : this.propertyMap.values()) {
        if (property instanceof GeometryDescriptor ) {
          return (GeometryDescriptor) property; 
        }
      }
    } else if (defaultGeometry instanceof GeometryDescriptor){
      return (GeometryDescriptor) defaultGeometry;
    }

    return null;
  }

  @Override
  public boolean isIdentified() {
    return this.identified;
  }

  @Override
  public Class<Collection<Property>> getBinding() {
    return (Class<Collection<Property>>) this.binding;
  }

  @Override
  public Collection<PropertyDescriptor> getDescriptors() {
    return unmodifiable(this.propertyMap.values());
  }

  @Override
  public boolean isInline() {
    return false;
  }

  @Override
  public AttributeType getSuper() {
    return (AttributeType) this.superType;
  }

  @Override
  public InternationalString getDescription() {
    return this.description;
  }

  @Override
  public Name getName() {
    return this.name;
  }

  @Override
  public List<Filter> getRestrictions() {
    return this.restrictions;
  }

  @Override
  public Map<Object, Object> getUserData() {
    return this.userData;
  }

  @Override
  public boolean isAbstract() {
    return this.isAbstract;
  }
  // ---------------------------------------------------------------------	
  //IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
  //II FIN IMPLANTATION                                                 II
  //IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII

  //RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
  //RR REDEFINITION                                                     RR
  //RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
  // Object --------------------------------------------------------------
  @Override
  public String toString(){
    String info = "name=" + getName().getLocalPart();
    info += (" , namespace=" + getName().getNamespaceURI());
    info += (" , abstract=" + isAbstract());

    String types1 = "types=(";

    for (int i = 0, ii = getAttributeCount(); i < ii; i++) {
      types1 += getDescriptor(i).toString();

      if (i < ii) {
        types1 += ",";
      }
    }

    types1 += ")";
    info += (" , " + types1);

    return getClass().getSimpleName()+" [" + info + "]";
  }

  @Override
  public boolean equals(Object obj){

    if(obj == this){
      return true;
    } else if ((obj instanceof SimpleFeatureType) && (obj != null)){

      SimpleFeatureType other = (SimpleFeatureType)obj;

      if ((getName().getLocalPart() == null) && (other.getName().getLocalPart() != null)) {
        return false;
      } else if (!getTypeName().equals(other.getName().getLocalPart())) {
        return false;
      }

      if ((getName().getNamespaceURI() == null) && (other.getName().getNamespaceURI() != null)) {
        return false;
      } else if (!getName().getNamespaceURI().equals(other.getName().getNamespaceURI())) {
        return false;
      }

      if (getAttributeCount() != other.getDescriptors().size()) {
        return false;
      }

      for (int i = 0, ii = getAttributeCount(); i < ii; i++) {
        if (!getDescriptor(i).equals(other.getDescriptor(i))) {
          return false;
        }
      }
    } else {
      return false;
    }

    return true;
  }
  // ---------------------------------------------------------------------


  //RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
  //RR FIN REDEFINITION                                                 RR
  //RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR


  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  //CC CONSTRUCTEUR                                                     CC
  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC

  /**
   * Constructs a new feature type with the given {@link org.opengis.feature.type.Name name}.
   * @param name the {@link org.opengis.feature.type.Name name} of the feature.
   */
  public ListedSimpleFeatureType(Name name){
    this(name, new ArrayList<AttributeDescriptor>(), null, false, NO_RESTRICTIONS, null, null, null);
  }

  /**
   * Constructs a new feature type with the given {@link org.opengis.feature.type.Name name} 
   * and the given <code>binding</code>
   * @param name the name of the type.
   * @param binding the properties binding. By default <code>Class&lt;Collection&lt;Property&gt;&gt;</code>
   */
  public ListedSimpleFeatureType(Name name, Class<Collection<Property>> binding){
    this(name, new ArrayList<AttributeDescriptor>(), null, false, NO_RESTRICTIONS, null, null, binding);
  }

  /**
   * Constructs a new feature type with given parameters.
   * @param name the name of the type
   * @param schema the schema of the type (a {@link java.util.List list} of 
   * {@link org.opengis.feature.type.AttributeDescriptor AttributeDescriptors} composing the schema of the type).
   * @param defaultGeometry the {@link org.opengis.feature.type.GeometryDescriptor geometry descriptor} of the type.
   * @param isAbstract is the type is abstract (<code>true</code>) or not (<code>false</code>)
   * @param restrictions the restrictions of the type as a  {@link java.util.List list} 
   * of {@link org.opengis.filter.Filter filters}.
   * @param superType the {@link org.opengis.feature.type.AttributeType super type} of this type.
   * @param description the description of the type.
   * @param binding the properties binding. By default <code>Class&lt;Collection&lt;Property&gt;&gt;</code>
   */
  public ListedSimpleFeatureType(Name name, List<AttributeDescriptor> schema,
      GeometryDescriptor defaultGeometry, boolean isAbstract,
      List<Filter> restrictions, AttributeType superType,
      InternationalString description, Class<Collection<Property>> binding){
    if(name== null){
      throw new NullPointerException("Name is required for PropertyType");
    }

    this.name = name;

    if (binding != null){
      this.binding = binding;
    } else {
      this.binding = null;
    }
    this.isAbstract = isAbstract;

    if (restrictions == null) {
      this.restrictions = NO_RESTRICTIONS;
    } else {
      this.restrictions = Collections.unmodifiableList(restrictions);
    }

    this.superType = superType;
    this.description = description;
    this.userData = new HashMap<Object,Object>();		

    this.identified = true;

    this.descriptors = schema;

    if (defaultGeometry != null){
      setGeometryDescriptor(defaultGeometry);
    }

    updatePropertyMap();
    updateIndex();

  }
  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  //CC FIN CONSTRUCTEUR                                                 CC
  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC

  //AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  //AA ACCESSEURS                                                       AA
  //AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

  /**
   * Set the default {@link org.opengis.feature.type.GeometryDescriptor geometry descriptor} attached to the feature type.
   * @param geometry the geometry descriptor to use.
   * @throws IllegalArgumentException if an error occurs.
   */
  public void setGeometryDescriptor(GeometryDescriptor geometry) throws IllegalArgumentException{

    if ( geometry != null && !(geometry.getType() instanceof GeometryType ) )  {
      throw new IllegalArgumentException( "defaultGeometry must have a GeometryType");
    } else {

      GeometryDescriptor defaultGeometry = getGeometryDescriptor();	

      if ((defaultGeometry != null) && (defaultGeometry != geometry)){
        this.descriptors.remove(defaultGeometry);
        this.descriptors.add(geometry);
        updateIndex();
      }
    }
  }

  /**
   * Set the {@link org.opengis.feature.type.AttributeDescriptor attribute descriptors} for this feature type. 
   * If the descriptor list contains at least one {@link org.opengis.feature.type.GeometryDescriptor geometry descriptor}, the first
   * geometry descriptor is marked as the default geometry descriptor.
   * @param schema the list of all the descriptors.
   */
  public void setDescriptors(List<AttributeDescriptor> schema){

    if (schema != null){
      this.descriptors = schema;
      updatePropertyMap();
      updateIndex();
    }
  }
  //AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  //AA FIN ACCESSEURS                                                   AA
  //AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA



  /**
   * Builds the name -> position index used by simple features for fast attribute lookup
   * @param featureType the feature.
   * @return the feature properties.
   */
  static Map<String, Integer> buildIndex(SimpleFeatureType featureType) {
    // build an index of attribute name to index

    Map<String, Integer> index = new HashMap<String, Integer>();
    int i = 0;

    if ((featureType.getAttributeDescriptors() != null)&&(featureType.getAttributeCount() > 0)){
      for (AttributeDescriptor ad : featureType.getAttributeDescriptors()) {
        index.put(ad.getLocalName(), i++);
      }
      if (featureType.getGeometryDescriptor() != null) {
        index.put(null, index.get(featureType.getGeometryDescriptor().getLocalName()));
      }
    }
    return index;
  }

  private void updatePropertyMap(){
    if(this.descriptors == null){
      this.propertyMap = Collections.emptyMap();
    } else {
      this.propertyMap = new HashMap<Name, PropertyDescriptor>();
      for (PropertyDescriptor pd : this.descriptors) {
        this.propertyMap.put(pd.getName(), pd);
      }
    }
  }

  private void updateIndex(){
    this.index = new HashMap<String, Integer>();
    this.index.put(null, Integer.valueOf(-1));
    int i = 0;

    if ((this.descriptors != null)&&(this.descriptors.size() > 0)){
      for (AttributeDescriptor ad : this.descriptors) {
        this.index.put(ad.getLocalName(), i++);
      }
      if (getGeometryDescriptor() != null) {
        this.index.put(null, this.index.get(getGeometryDescriptor().getLocalName()));
      }
    }

  }

  private static Collection<PropertyDescriptor> unmodifiable( Collection<PropertyDescriptor> original ) {

    if ( original instanceof Set ) {
      if ( original instanceof SortedSet ) {
        return Collections.unmodifiableSortedSet((SortedSet<PropertyDescriptor>) original);
      }

      return Collections.unmodifiableSet((Set<PropertyDescriptor>)original);
    }
    else if ( original instanceof List ) {
      return Collections.unmodifiableList((List<PropertyDescriptor>)original);
    }

    return Collections.unmodifiableCollection(original);
  }
}
