package org.jorigin.jis.swing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import javax.swing.event.EventListenerList;
import javax.swing.table.AbstractTableModel;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.geotools.data.FeatureEvent;
import org.geotools.data.FeatureListener;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.map.MapLayerEvent;
import org.geotools.map.MapLayerListEvent;
import org.geotools.map.MapLayerListListener;
import org.geotools.map.MapLayerListener;
import org.jorigin.jis.JIS;

/**
 * A model dedicated to feature presentation within a {@link org.jorigin.jis.swing.JFeatureTable feature table}.
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 */
public class FeatureTableModel extends AbstractTableModel
                               implements MapLayerListener, FeatureListener, MapLayerListListener{

  private static final long serialVersionUID = JIS.BUILD;
  
  /**
   * State column identifier.
   */
  public static final int STATE_COLUMN     = 0;
  
  /**
   * Feature ID column identifier.
   */
  public static final int FEATUREID_COLUMN = 1;
  
  /**
   * Feature type column identifier.
   */
  public static final int TYPE_COLUMN      = 2;
  
  /**
   * Feature layer column identifier.
   */
  public static final int LAYER_COLUMN     = 3;
  
  private String[] names            = {"FEAT_TABLE_STATE_COL", "FEAT_TABLE_ID_COL", "FEAT_TABLE_TYPE_COL", "FEAT_TABLE_LAYER_COL"};
  
  /** List of listeners */
  protected EventListenerList listenerList = new EventListenerList();
  
  List<Layer> layers                 = null; 
  
  MapContent mapContext             = null;
  
  ArrayList<Feature> features                 = null;
       
  HashMap<Feature, Layer> featureToLayerMap = null;
  
  @Override
  public int getRowCount() {
    if (this.features != null){
      return this.features.size();
    } else {
      return 0;
    }
  }

  @Override
  public int getColumnCount() {
    if (this.names != null){
      return this.names.length;
    } else {
      return 0;
    }
  }

  @Override
  public String getColumnName(int columnIndex) {
    return this.names[columnIndex];
  }

  @Override
  public Class<?> getColumnClass(int columnIndex) {
    switch(columnIndex){
      case STATE_COLUMN:
        return Integer.class;
      case FEATUREID_COLUMN:
        return SimpleFeature.class;
      case TYPE_COLUMN:
        return SimpleFeatureType.class;
      case LAYER_COLUMN: 
        return Layer.class;
      default:
        return Object.class;
    }
  }

  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return false;
  }

  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    
    Feature feature = null;
    
    if (this.features == null){
      JIS.logger.log(Level.WARNING,"No features available");
      return null;
    }
    
    switch(columnIndex){
    case STATE_COLUMN:
      return 0;
    case FEATUREID_COLUMN:
      
      try {
        return this.features.get(rowIndex);
      } catch (Exception e) {
    	  JIS.logger.log(Level.SEVERE, "Cannot access feature at row "+rowIndex, e);
        throw new IndexOutOfBoundsException("Index: "+rowIndex+", Size: "+this.features.size());
      }
      
    case TYPE_COLUMN:
      
      try {
        feature = this.features.get(rowIndex);
      } catch (Exception e) {
    	  JIS.logger.log(Level.SEVERE, "Cannot access feature at row "+rowIndex, e);
        throw new IndexOutOfBoundsException("Index: "+rowIndex+", Size: "+this.features.size());
      }
      
      if (feature != null){
        return this.features.get(rowIndex).getType();
      } else {
    	  JIS.logger.severe("Cannot access feature at row "+rowIndex);
        return null;
      }
      
    case LAYER_COLUMN:

      try {
        feature = this.features.get(rowIndex);
      } catch (Exception e) {
    	  JIS.logger.log(Level.SEVERE, "Cannot access feature at row "+rowIndex, e);
        throw new IndexOutOfBoundsException("Index: "+rowIndex+", Size: "+this.features.size());
      }
      
      if (feature != null){
        return this.featureToLayerMap.get(this.features.get(rowIndex));
      } else {
    	  JIS.logger.severe("Cannot access feature at row "+rowIndex);
        return null;
      }
      
    default:
      return null;
    }
  }

  @Override
  public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	  JIS.logger.log(Level.WARNING,"Not yet Implemented");
  }

  // -----------------------------------------------------------------------------
  // - IMPLEMENTATION OF MapLayerListener START ----------------------------------
  @Override
  public void layerChanged(MapLayerEvent event) {
	  JIS.logger.log(Level.FINEST, "Layer changed event dispatched: "+event.getSource());
  }

  @Override
  public void layerShown(MapLayerEvent event) {
	  JIS.logger.log(Level.FINEST, "Layer shown event dispatched: "+event.getSource()); 
  }

  @Override
  public void layerHidden(MapLayerEvent event) {
	  JIS.logger.log(Level.FINEST, "Layer hidden event dispatched: "+event.getSource());
  }

  @Override
  public void layerSelected(MapLayerEvent event) {
	  JIS.logger.log(Level.FINEST, "Layer selected event dispatched: "+event.getSource());
  }

  @Override
  public void layerDeselected(MapLayerEvent event) {
	  JIS.logger.log(Level.FINEST, "Layer deselected event dispatched: "+event.getSource());
  }
  
  @Override
  public void layerPreDispose(MapLayerListEvent event) {
	  JIS.logger.log(Level.FINEST, "Layer pre dispose event dispatched: "+event.getSource());
  }

  @Override
  public void layerPreDispose(MapLayerEvent event) {
	  JIS.logger.log(Level.FINEST, "Layer pre dispose event dispatched: "+event.getSource());
  }
  
  // - IMPLEMENTATION OF MapLayerListener END ------------------------------------
  // -----------------------------------------------------------------------------

  // -----------------------------------------------------------------------------  
  // - IMPLEMENTATION OF FeatureListener START -----------------------------------
  @Override
  public void changed(FeatureEvent featureEvent) {
    if (featureEvent.getType().equals(FeatureEvent.Type.ADDED)){
    	JIS.logger.log(Level.FINEST, "Feature changed event dispatched FEATURE_ADDED: "+featureEvent.getSource());
    } else if (featureEvent.getType().equals(FeatureEvent.Type.CHANGED)){
    	JIS.logger.log(Level.FINEST, "Feature changed event dispatched FEATURE_CHANGED: "+featureEvent.getSource());
    } else if (featureEvent.getType().equals(FeatureEvent.Type.COMMIT)){
    	JIS.logger.log(Level.FINEST, "Feature changed event dispatched FEATURE_COMMIT: "+featureEvent.getSource());
    } else if (featureEvent.getType().equals(FeatureEvent.Type.REMOVED)){
    	JIS.logger.log(Level.FINEST, "Feature changed event dispatched FEATURE_REMOVED: "+featureEvent.getSource());
    } else if (featureEvent.getType().equals(FeatureEvent.Type.ROLLBACK)){
    	JIS.logger.log(Level.FINEST, "Feature changed event dispatched FEATURE_ROLLBACK: "+featureEvent.getSource());
    } 
  }
  // - IMPLEMENTATION OF FeatureListener END -------------------------------------
  // -----------------------------------------------------------------------------  
  
  // -----------------------------------------------------------------------------  
  // - IMPLEMENTATION OF MapLayerListListener START ------------------------------
  @Override
  public void layerAdded(MapLayerListEvent event) {
	  JIS.logger.log(Level.FINEST, "Layer added event dispatched: "+event.getSource());
  }

  @Override
  public void layerRemoved(MapLayerListEvent event) {
	  JIS.logger.log(Level.FINEST, "Layer removed event dispatched: "+event.getSource());
  }

  @Override
  public void layerChanged(MapLayerListEvent event) {
	  JIS.logger.log(Level.FINEST, "Layer "+event.getLayer().getTitle()+" changed event dispatched: "+event.getSource());
  }

  @Override
  public void layerMoved(MapLayerListEvent event) {
    JIS.logger.log(Level.FINEST, "Layer moved event dispatched: "+event.getSource());
  }
  // - IMPLEMENTATION OF MapLayerListListener END --------------------------------
  // -----------------------------------------------------------------------------
  
  /**
   * Constructs a new default feature table model.
   */
  public FeatureTableModel(){
    this((MapContent)null);
  }
  
  /**
   * Create a new feature table model from a {@link org.geotools.map.MapContext map context}.
   * @param context the context containing the features.
   */
  public FeatureTableModel(MapContent context){
    if (context != null){
      setData(context.layers());
      
      context.addMapLayerListListener(this);
      
    }
  }
  
  /**
   * Create a new feature table model from an array of {@link org.geotools.map.MapLayer map layers}.
   * @param layers the layers containing the features.
   */
  public FeatureTableModel(List<Layer> layers){
    setData(layers);
  }
  
  /**
   * Set the data to handle with this model.
   * @param layers the {@link org.geotools.map.Layer layers} that contain the features to handle.
   */
  public void setData(List<Layer> layers){
    
    Layer layer                      = null;
    FeatureIterator<? extends Feature> iter        = null;
    FeatureCollection<? extends FeatureType,? extends Feature> featureCollection = null;
    Feature feature = null;
    
    this.layers = layers;
    
    this.features = new ArrayList<Feature>();
    this.featureToLayerMap = new HashMap<Feature, Layer>();
    
    if (layers != null){
      
      // Parcours de tous les layers du contexte pour construire le tableau
      // general des features
    	JIS.logger.log(Level.FINEST, "Processing "+layers.size()+" layer(s) from context");
      for(int i = 0; i < layers.size(); i++){
        layer = layers.get(i);
        
        
        if (layer != null){
          if (layer.getFeatureSource() != null){
            try {
              featureCollection = layer.getFeatureSource().getFeatures();
            } catch (IOException e) {
            	JIS.logger.log(Level.SEVERE, "Cannot access layer "+layer.getTitle()+" data source", e);
              featureCollection = null;
            }  
              
            if (featureCollection != null){
              iter = featureCollection.features();
              
              if (iter != null){
                while(iter.hasNext()){
                  feature = (Feature)iter.next();

                  if (feature != null){
                    this.features.add(feature);
                    
                    this.featureToLayerMap.put(feature, layer);
                    
                    JIS.logger.log(Level.FINEST, "Adding feature to table: ["+feature.getIdentifier()+", "
                                                                                +layer.getTitle()
                                                                                +"]");
                  } else {
                	  JIS.logger.log(Level.WARNING, "Invalid feature");
                  }

                  feature = null;
                }
                
                iter     = null;
              } else {
            	  JIS.logger.log(Level.WARNING, "No feature in source"+layer.getFeatureSource());
              }
            } else {
            	JIS.logger.log(Level.WARNING, "Cannot access feature source for layer "+layer.getTitle());
            }
          }

          registerMapLayer(layer);
        } else {
        	JIS.logger.log(Level.WARNING, "Layer "+i+" is null, ignoring");
        }
        
        layer  = null;
      }
     
      layers = null;
    }
  }
  
  private void registerMapLayer(Layer layer){
    
    if (layer != null){
      layer.addMapLayerListener(this);
    
      if (layer.getFeatureSource() != null){
        layer.getFeatureSource().addFeatureListener(this);
      } else if (layer.getFeatureSource() != null){
    	  JIS.logger.log(Level.FINEST, "Cannot register layer data source "+layer.getFeatureSource().getClass());
      }
    }
  }

}
