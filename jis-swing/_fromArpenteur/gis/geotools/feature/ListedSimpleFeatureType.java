package org.arpenteur.gis.geotools.feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.geotools.feature.FeatureImplUtils;
import org.geotools.feature.NameImpl;
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
	
	protected final Class<?> binding;
	
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
	  return descriptors;
	}

	@Override
	public AttributeDescriptor getDescriptor(String name) {
		return getDescriptor(new NameImpl( name ) );
	}

	@Override
	public AttributeDescriptor getDescriptor(Name name) {
		return (AttributeDescriptor) propertyMap.get(name);
	}

	@Override
	public AttributeDescriptor getDescriptor(int index) throws IndexOutOfBoundsException {
		return descriptors.get(index);
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
		if (types == null) {
            synchronized (this) {
                if (types == null) {
                    types = new ArrayList<AttributeType>();
                    for (Iterator<AttributeDescriptor> itr = descriptors
                            .iterator(); itr.hasNext();) {
                        AttributeDescriptor ad = itr.next();
                        types.add(ad.getType());
                    }
                }
            }
        }

        return types;
	}

	@Override
	public int indexOf(String name) {
		Integer idx = index.get(name);
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
        	    for (PropertyDescriptor property : propertyMap.values()) {
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
	  //System.out.println("[ListedSimpleFeature] [getGeometryDescriptor()] index: "+index);
		// By convention the default geometry descriptor is at the index mapped with null
	  int idx = -1;
    try {
      idx = index.get(null);
    } catch (Exception e) {
      System.err.println("Exception in getGeometryDescriptor()");
      System.err.println("Message: "+e.getMessage());
      System.err.println("Index: "+index);
      e.printStackTrace(System.err);
    }
	  
	  if (idx > -1){
		  defaultGeometry = descriptors.get(idx);
	  }
	  
		if (defaultGeometry == null) {
            for (PropertyDescriptor property : propertyMap.values()) {
                if (property instanceof GeometryDescriptor ) {
                    //System.out.println("[ListedSimpleFeature] [getGeometryDescriptor()] Found geometry descriptor"+property);
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
		return identified;
	}

	@Override
	public Class<Collection<Property>> getBinding() {
		return (Class<Collection<Property>>) binding;
	}

	@Override
	public Collection<PropertyDescriptor> getDescriptors() {
		return FeatureImplUtils.unmodifiable(propertyMap.values());
	}

	@Override
	public boolean isInline() {
		return false;
	}

	@Override
	public AttributeType getSuper() {
		return (AttributeType) superType;
	}

	@Override
	public InternationalString getDescription() {
		return description;
	}

	@Override
	public Name getName() {
		return name;
	}

	@Override
	public List<Filter> getRestrictions() {
		return restrictions;
	}

	@Override
	public Map<Object, Object> getUserData() {
		return userData;
	}

	@Override
	public boolean isAbstract() {
		return isAbstract;
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

	public ListedSimpleFeatureType(Name name){
	  this(name, new ArrayList<AttributeDescriptor>(), null, false, NO_RESTRICTIONS, null, null, null);
	}
	
	public ListedSimpleFeatureType(Name name, Class<?> binding){
	  this(name, new ArrayList<AttributeDescriptor>(), null, false, NO_RESTRICTIONS, null, null, binding);
	}

	public ListedSimpleFeatureType(Name name, List<AttributeDescriptor> schema,
            GeometryDescriptor defaultGeometry, boolean isAbstract,
            List<Filter> restrictions, AttributeType superType,
            InternationalString description, Class<?> binding){
		
		if(name== null){
			throw new NullPointerException("Name is required for PropertyType");
		}
		
		this.name = name;
		
		if (binding != null){
		  this.binding = Collection.class;
		} else {
		  this.binding = binding;
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
		
		descriptors = schema;
		
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
   * Set the default geometry descriptor attached to the feature.
   * @param GeometryDescriptor the geometry descriptor to use
   */
  public void setGeometryDescriptor(GeometryDescriptor geometry) throws IllegalArgumentException{
    	
	if ( geometry != null && !(geometry.getType() instanceof GeometryType ) )  {
	  throw new IllegalArgumentException( "defaultGeometry must have a GeometryType");
	} else {
	
	  GeometryDescriptor defaultGeometry = getGeometryDescriptor();	
		
	  if ((defaultGeometry != null) && (defaultGeometry != geometry)){
	    descriptors.remove(defaultGeometry);
	    descriptors.add(geometry);
	    updateIndex();
	  }
	}
  }
  
  /**
   * Set the descriptors describing this feature type. If the descriptor list contains at least one <code>GeometryDescriptor</code>, the first
   * geometry descriptor is marked as the default geometry descriptor.
   * @param schema the list of all the descriptors.
   */
  public void setDescriptors(List<AttributeDescriptor> schema){
    
    //System.out.println("[ListedSimpleFeatureType] [setDescriptors()] #setting: "+schema.size());
	  if (schema != null){
      descriptors = schema;
      updatePropertyMap();
      updateIndex();
	  }
  }
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA FIN ACCESSEURS                                                   AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

	
	
	/**
     * Builds the name -> position index used by simple features for fast attribute lookup
     * @param featureType
     * @return
     */
    static Map<String, Integer> buildIndex(SimpleFeatureType featureType) {
        // build an index of attribute name to index
      
        //System.out.println("[ListedSimpleFeatureType] [buildIndex] building index for "+featureType.getName());
        Map<String, Integer> index = new HashMap<String, Integer>();
        int i = 0;
        
        if ((featureType.getAttributeDescriptors() != null)&&(featureType.getAttributeCount() > 0)){
          for (AttributeDescriptor ad : featureType.getAttributeDescriptors()) {
            index.put(ad.getLocalName(), i++);
            //System.out.println("[ListedSimpleFeatureType] [buildIndex] "+i+": "+ad.getLocalName());
          }
          if (featureType.getGeometryDescriptor() != null) {
            index.put(null, index.get(featureType.getGeometryDescriptor().getLocalName()));
          }
        }
        return index;
    }
    
    private void updatePropertyMap(){
      if(descriptors == null){
	    this.propertyMap = Collections.emptyMap();
      } else {
		this.propertyMap = new HashMap<Name, PropertyDescriptor>();
		for (PropertyDescriptor pd : descriptors) {
          this.propertyMap.put(pd.getName(), pd);
        }
      }
    }
    
    private void updateIndex(){
      //System.out.println("[ListedSimpleFeatureType] [buildIndex] building index for "+getName());
      index = new HashMap<String, Integer>();
      index.put(null, new Integer(-1));
      int i = 0;
      
      if ((descriptors != null)&&(descriptors.size() > 0)){
        for (AttributeDescriptor ad : descriptors) {
          index.put(ad.getLocalName(), i++);
          //System.out.println("[ListedSimpleFeatureType] [buildIndex] "+i+": "+ad.getLocalName());
        }
        if (getGeometryDescriptor() != null) {
          index.put(null, index.get(getGeometryDescriptor().getLocalName()));
        }
      }
      //System.out.println("[ListedSimpleFeatureType] [buildIndex] Build index: "+index);
      //System.out.println("[ListedSimpleFeatureType] [buildIndex]");
      
    }
}
