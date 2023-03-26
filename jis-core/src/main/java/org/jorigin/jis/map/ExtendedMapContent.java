package org.jorigin.jis.map;

import java.awt.AWTEvent;
import java.awt.event.AWTEventListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * An extended map content that enable new features for classic geotools map context. 
 * This map context enable to specify focusable layers and selectable layers.
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 *
 */
public class ExtendedMapContent extends MapContent {
  
  /** The feature currently selected in this panel */
  private ArrayList<SimpleFeature> selectedFeatures = null;
  
  /**
   * The list of the layers that can interact. These layers can be
   * processed to find selected features.
   */
  private List<Layer> selectableLayers = null;
  
  /**
   * The list of the layers that can interact. These layers can be
   * processed to find focused features.
   */
  private List<Layer> focusableLayers  = null;
  
  /**
   * The list of selected layers
   */
  private List<Layer> selectedLayers   = null;
  
  //Liste des écouteurs informés des evenements du panneau
  protected EventListenerList idListenerList = new EventListenerList();
  
  
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC CONSTRUCTEUR                                                   CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC  
  /**
   * Creates a default empty map context
   * @param crs the coordinate reference system of the map.
   */
  public ExtendedMapContent(final CoordinateReferenceSystem crs) {
    super();
    getViewport().setCoordinateReferenceSystem(crs);
    this.selectableLayers = new ArrayList<Layer>();
    this.focusableLayers  = new ArrayList<Layer>();
    this.selectedLayers   = new ArrayList<Layer>();
  }
  
  /**
   * Creates a map context with the provided layers and title
   * 
   * @param layers the layers composing the map
   * @param crs the coordinate reference system of the map.
   */
  public ExtendedMapContent(Collection<? extends Layer> layers, final CoordinateReferenceSystem crs) {
    super();
    getViewport().setCoordinateReferenceSystem(crs);
    addLayers(layers);
    this.selectableLayers = new ArrayList<Layer>();
    this.focusableLayers = new ArrayList<Layer>();
    this.selectedLayers = new ArrayList<Layer>();
  }
  
  /**
   * Creates a map context.
   * @param layers the layers composing the map.
   * @param title the title of the map.
   * @param contextAbstract an abstract describing the context.
   * @param contactInformation informations on the contact of the map maintainer.
   * @param keywords keywords associated to the map.
   * @param crs the coordinate reference system of the context.
   */
  public ExtendedMapContent(Collection<? extends Layer> layers, String title,
          String contextAbstract, String contactInformation,
          String[] keywords, final CoordinateReferenceSystem crs) {

    super();
    getViewport().setCoordinateReferenceSystem(crs);
    addLayers(layers);
    setTitle(title);
    if (contextAbstract != null) {
      getUserData().put("abstract", contextAbstract);
    }
    if (contactInformation != null) {
      getUserData().put("contact", contactInformation);
    }
    if (keywords != null) {
      getUserData().put("keywords", keywords);
    }
    
    this.selectableLayers = new ArrayList<Layer>();
    this.focusableLayers = new ArrayList<Layer>();
    this.selectedLayers = new ArrayList<Layer>();
  }
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC FIN CONSTRUCTEUR                                               CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC  

//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
//EE EVENEMENT                                                      EE
//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
  
  /**
   * Add a listener to the extended map content. The listener must implements interface
   * {@link java.awt.event.AWTEventListener AWTEventListener} because the interface {@link java.util.EventListener EventListener}
   * is empty and does not determine a dispatch method.
   * @param listener the listener to add.
   */
  public void addExtendedMapContentListener(AWTEventListener listener){
    this.idListenerList.add(AWTEventListener.class, listener);
  }
  
  /**
   * Remove a listener from the extended map content. The listener must implements interface
   * {@link java.awt.event.AWTEventListener AWTEventListener} because the interface {@link java.util.EventListener EventListener}
   * is empty and does not determine a dispatch method.
   * @param listener the listener to remove.
   */
  public void removeExtendedMapContentListener(AWTEventListener listener){
    this.idListenerList.remove(AWTEventListener.class, listener);
  }
  
  /**
   * Dispatch a new event to all registered listeners. The event must be a subclass of 
   * java.awt.AWTEvent because the listener are AWTEventListener.
   * @param event the event to dispatch.
   */
  public void dispatchEvent(AWTEvent event){
    Object[] listeners = this.idListenerList.getListenerList();
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == AWTEventListener.class) {
        ( (AWTEventListener) listeners[i + 1]).eventDispatched(event);
      }
    }  
  }

//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
//EE FIN EVENEMENT                                                  EE
//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE

  
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA ACCESSEURS                                                     AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  

  /**
   * Set the {@link org.opengis.feature.simple.SimpleFeature feature} to be marked as selected in the current map panel.
   * @param features the features to select.
   */
  public void setSelectedFeatures(Collection<? extends SimpleFeature> features){
    
	  SimpleFeature[] featuresArray = null;

    if ((features != null) && (features.size() > 0)){

      featuresArray = new SimpleFeature[features.size()];
      featuresArray = (SimpleFeature[])features.toArray(featuresArray);
    }
    
    setSelectedFeatures(featuresArray); 

  }
  
  /**
   * Set the {@link org.opengis.feature.simple.SimpleFeature features} to be marked as selected in the map panel.
   * @param features features to select.
   */
  public void setSelectedFeatures(SimpleFeature[] features){

    if (this.selectedFeatures == null){
      this.selectedFeatures = new ArrayList<SimpleFeature>();
    } else {
      this.selectedFeatures.clear();
    }
    
    if (features != null){
      for(int i = 0; i < features.length; i++){
        this.selectedFeatures.add(features[i]);
      }
    }
    
    dispatchEvent(new ExtendedMapContentEvent(this, ExtendedMapContentEvent.FEATURE_SELECTED));
  }
  
  /**
   * Get the {@link org.opengis.feature.simple.SimpleFeature features} selected in this panel.
   * @return the selected features.
   */
  public ArrayList<SimpleFeature> getSelectedFeatures(){
    return this.selectedFeatures;
  }
  
  /**
   * Set the {@link org.geotools.map.Layer layers} that can be focused.
   * @param layers the layer to be focused. These layers have to be handled
   * by the underlying {@link org.geotools.map.MapContent map content}.
   * @see #setFocusableLayers(Layer[])
   * @see #getFocusableLayers()
   */
  public void setFocusableLayers(List<? extends Layer> layers){
    
    Layer[] alayers = null;
    
    if ((layers != null) && (layers.size() > 0)){
      alayers = new Layer[layers.size()];
      alayers = (Layer[])layers.toArray(alayers);
    } else {
      alayers = null;
    }
    
    setFocusableLayers(alayers);
  }
  
  /**
   * Set the {@link org.geotools.map.Layer layers} that can be focused.
   * @param layers the layer to be focused. The layers have to be handled by the
   * underlying {@link org.geotools.map.MapContent map content}.
   * @see #setFocusableLayers(List)
   * @see #getFocusableLayers()
   */
  public void setFocusableLayers(Layer[] layers){
    
    if (this.focusableLayers == null){
      this.focusableLayers = new ArrayList<Layer>();
    } else {
      this.focusableLayers.clear();
    }
    
    if (layers != null){
      for(int i = 0; i < layers.length; i++){
        this.focusableLayers.add(layers[i]);
      }
    } 
    
    dispatchEvent(new ExtendedMapContentEvent(this, ExtendedMapContentEvent.LAYER_FOCUSABILITY_CHANGED));
  }
  
  /**
   * Get the {@link org.geotools.map.Layer layers} that can be focused.
   * @return the layer to be focused.
   * @see #setFocusableLayers(List)
   * @see #setFocusableLayers(Layer[])
   */
  public List<Layer> getFocusableLayers(){
    return this.focusableLayers;
  }
  
  /**
   * Set the {@link org.geotools.map.Layer layers} that can be selected.
   * @param layers the layer to be selected. These layers have to be handled
   * by the underlying {@link org.geotools.map.MapContent map content}.
   * @see #setSelectableLayers(Layer[])
   * @see #getSelectableLayers()
   */
  public void setSelectableLayers(List<Layer> layers){
    
    Layer[] alayers = null;
    
    if ((layers != null) && (layers.size() > 0)){
      alayers = new Layer[layers.size()];
      alayers = (Layer[])layers.toArray(alayers);
    } else {
      alayers = null;
    }
    
    setSelectableLayers(alayers);
    
  }
  
  /**
   * Set the {@link org.geotools.map.Layer layers} that can be selected.
   * @param layers the layer to be selected. The layers have to be handled by the
   * underlying {@link org.geotools.map.MapContent map content}.
   * @see #setSelectableLayers(List)
   * @see #getSelectableLayers()
   */
  public void setSelectableLayers(Layer[] layers){
    
    if (this.selectableLayers == null){
      this.selectableLayers = new ArrayList<Layer>();
    } else {
      this.selectableLayers.clear();
    }
    
    if (layers != null){
      for(int i = 0; i < layers.length; i++){
        this.selectableLayers.add(layers[i]);
      }
    } 
    
    dispatchEvent(new ExtendedMapContentEvent(this, ExtendedMapContentEvent.LAYER_SELECTABLILITY_CHANGED));
  }
  
  /**
   * Get the {@link org.geotools.map.Layer layers} that can be selected.
   * @return the layer to be selected.
   * @see #setSelectableLayers(List)
   * @see #setSelectableLayers(Layer[])
   */
  public List<Layer> getSelectableLayers(){
    return this.selectableLayers;
  }


  /**
   * Set the selected {@link org.geotools.map.Layer layers}.
   * @param layers the selected layers. These layers have to be handled
   * by the underlying {@link org.geotools.map.MapContent map content}.
   * @see #setSelectedLayers(Layer[])
   * @see #getSelectedLayers()
   */
  public void setSelectedLayers(List<Layer> layers){
    
    Layer[] alayers = null;
    
    if ((layers != null) && (layers.size() > 0)){
      alayers = new Layer[layers.size()];
      alayers = (Layer[])layers.toArray(alayers);
    } else {
      alayers = null;
    }
    
    setSelectedLayers(alayers);
  }
  
  /**
   * Set the selected {@link org.geotools.map.Layer layers}.
   * @param layers the selected layers. The layers have to be handled by
   * the underlying {@link org.geotools.map.MapContent map content}.
   * @see #setSelectedLayers(List)
   * @see #getSelectedLayers()
   */
  public void setSelectedLayers(Layer[] layers){
    
    if (this.selectedLayers == null){
      this.selectedLayers = new ArrayList<Layer>();
    } else {
      this.selectedLayers.clear();
    }
    
    if (layers != null){
      for(int i = 0; i < layers.length; i++){
        this.selectedLayers.add(layers[i]);
      }
    } 
    
    dispatchEvent(new ExtendedMapContentEvent(this, ExtendedMapContentEvent.LAYER_SELECTED));
  }
  
  /**
   * Get the selected {@link org.geotools.map.Layer layers}.
   * @return the selected layers.
   * @see #setSelectedLayers(List)
   * @see #setSelectedLayers(Layer[])
   */
  public List<Layer> getSelectedLayers(){
    return this.selectedLayers;
  }

   

  /**
   * Set if the {@link org.geotools.map.Layer layer} given in parameter is focusable or not.
   * @param layer the layer to mark.
   * @param focusable <code>true</code> if the layer is focusable, 
   * <code>false</code> otherwise.
   * @see #isFocusable(Layer)
   */
  public void setFocusable(Layer layer, boolean focusable){
    if (layer != null){
      if (focusable == false){
        this.focusableLayers.remove(layer);
      } else {
        if (! this.focusableLayers.contains(layer)){
          this.focusableLayers.add(layer);
        }
      }
    }

    dispatchEvent(new ExtendedMapContentEvent(this, ExtendedMapContentEvent.LAYER_FOCUSABILITY_CHANGED));
  }
  
  /**
   * Get if a {@link org.geotools.map.Layer layer} is focusable. This method return <code>true</code> if the layer 
   * is focusable and <code>false</code> otherwise.
   * @param layer the layer to query
   * @return <code>true</code> if the layer 
   * is focusable and <code>false</code> otherwise.
   * @see #setFocusable(Layer, boolean)
   */
  public boolean isFocusable(Layer layer){
    return this.focusableLayers.contains(layer);
  }
  
  /**
   * Set if the {@link org.geotools.map.Layer layer} given in parameter is selectable or not.
   * @param layer the layer to mark.
   * @param selectable <code>true</code> if the layer is selectable, 
   * <code>false</code> otherwise.
   * @see #isSelectable(Layer)
   */
  public void setSelectable(Layer layer, boolean selectable){
    if (layer != null){
      if (selectable == false){
        this.selectableLayers.remove(layer);
      } else {
        if (! this.selectableLayers.contains(layer)){
          this.selectableLayers.add(layer);
        }
      }
    }

    dispatchEvent(new ExtendedMapContentEvent(this, ExtendedMapContentEvent.LAYER_SELECTABLILITY_CHANGED));
  }
  
  /**
   * Get if a {@link org.geotools.map.Layer layer} is selectable. This method return <code>true</code> if the layer 
   * is selectable and <code>false</code> otherwise.
   * @param layer the layer to query
   * @return <code>true</code> if the layer 
   * is selectable and <code>false</code> otherwise.
   * @see #setSelectable(Layer, boolean)
   */
  public boolean isSelectable(Layer layer){
    return this.selectableLayers.contains(layer);
  }
  
  /**
   * Set if the {@link org.geotools.map.Layer layer} given in parameter is selected or not.
   * @param layer the layer to mark.
   * @param selected <code>true</code> if the layer is selected, 
   * <code>false</code> otherwise.
   * @see #isSelected(Layer)
   */
  public void setSelected(Layer layer, boolean selected){
    if (layer != null){
      if (selected == false){
        this.selectedLayers.remove(layer);
      } else {
        if (! this.selectedLayers.contains(layer)){
          this.selectedLayers.add(layer);
        }
      }
    }
    
    dispatchEvent(new ExtendedMapContentEvent(this, ExtendedMapContentEvent.LAYER_SELECTED));
  }
  
  /**
   * Get if a {@link org.geotools.map.Layer layer} is selected. This method return <code>true</code> if the layer 
   * is selected and <code>false</code> otherwise.
   * @param layer the layer to query
   * @return <code>true</code> if the layer 
   * is selected and <code>false</code> otherwise.
   * @see #setSelected(Layer, boolean)
   */
  public boolean isSelected(Layer layer){
    return this.selectedLayers.contains(layer);
  }
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA FIN ACCESSEURS                                                 AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  
  /**
   * Clear the current selection of {@link org.geotools.map.Layer layers}.
   */
  public void clearLayerSelection(){
    if (this.selectedLayers == null){
      this.selectedLayers = new ArrayList<Layer>();
    } else {
      this.selectedLayers.clear();
    }
    
    dispatchEvent(new ExtendedMapContentEvent(this, ExtendedMapContentEvent.LAYER_SELECTED));
  }
}
