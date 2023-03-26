package org.jorigin.jis.feature;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.geotools.data.FeatureEvent;
import org.geotools.data.FeatureListener;
import org.geotools.data.memory.MemoryFeatureCollection;
import org.geotools.feature.collection.RandomFeatureAccess;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

/**
 * A default implementation of the {@link org.geotools.feature.collection.AbstractFeatureCollection AbstractFeatureCollection}.
 * This implementation enable to dynamically add and remove {@link org.opengis.feature.simple.SimpleFeature features}.
 * The underlying data representation is a {@link java.util.TreeMap TreeMap}.
 * This implementation also implements {@link org.geotools.feature.collection.RandomFeatureAccess RandomFeatureAccess} interface 
 * and enables to access randomly features members.
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 *
 */
public class ListenedFeatureCollection extends MemoryFeatureCollection implements
                                      RandomFeatureAccess {

  List<FeatureListener> listeners;
  
  /**
   * Create a new listened feature collection that relies on the given {@link org.opengis.feature.simple.SimpleFeatureType schema}.
   * @param schema the {@link org.opengis.feature.simple.SimpleFeatureType schema} of the features.
   */
  public ListenedFeatureCollection(SimpleFeatureType schema) {
    super(schema);
    this.listeners = new ArrayList<FeatureListener>();
  }

  /**
   * Create a new listened feature collection that relies on the given {@link org.opengis.feature.simple.SimpleFeatureType schema}.
   * @param id the identifier of the collection.
   * @param schema the {@link org.opengis.feature.simple.SimpleFeatureType schema} of the features.
   */
  public ListenedFeatureCollection(String id, SimpleFeatureType schema) {
    super(schema);
    this.id = id;
    this.listeners = new ArrayList<FeatureListener>();
  }
  
  @Override
  public boolean add(SimpleFeature o) {

    boolean ok = super.add(o);
    
    if (ok){
      fireCollectionEvent(new FeatureEvent(this, FeatureEvent.Type.ADDED, new ReferencedEnvelope(o.getBounds()), null));
    }
    
    return ok;
  }

  @Override
  public boolean remove(Object o) {

    boolean ok = super.remove(o);
    
    if (ok && (o instanceof SimpleFeature)){
      fireCollectionEvent(new FeatureEvent(this, FeatureEvent.Type.REMOVED, new ReferencedEnvelope(((SimpleFeature)o).getBounds()), null));
    }

    return ok;
  }

  //
  // RandomFeatureAccess
  //

  @Override
  public SimpleFeature removeFeatureMember(String id) {
    
    SimpleFeature feature = super.removeFeatureMember(id);
    
    if (feature != null){
      fireCollectionEvent(new FeatureEvent(this, FeatureEvent.Type.REMOVED, new ReferencedEnvelope(feature.getBounds()), null));
    }

    return feature;
  };

  boolean addFeatureListener(FeatureListener listener){
    if (listener != null){
	  if (this.listeners == null){
	    this.listeners = new LinkedList<FeatureListener>();
	  }
	  
	  return this.listeners.add(listener);
    } else {
      return false;
    }
  }
  
  boolean removeFeatureListener(FeatureListener listener){
    if (listener != null){
      if (this.listeners != null){
        return this.listeners.remove(listener);
      } else {
        return false;
      }
    } else {
      return false;
    }
  }
  
  synchronized protected void fireCollectionEvent(FeatureEvent e){

    Object next = null;

    if (this.listeners != null){
      Iterator<FeatureListener> iter = this.listeners.iterator();
      while(iter.hasNext()){
        next = iter.next();
        if (next instanceof FeatureListener){
          ((FeatureListener)next).changed(e);
        }
      }
    }
  }
}
