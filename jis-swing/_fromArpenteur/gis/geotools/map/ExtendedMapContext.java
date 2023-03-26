package org.arpenteur.gis.geotools.map;

import java.awt.AWTEvent;
import java.awt.event.AWTEventListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.map.event.MapBoundsEvent;
import org.geotools.map.event.MapBoundsListener;
import org.geotools.map.event.MapLayerListEvent;
import org.geotools.map.event.MapLayerListListener;
import org.geotools.referencing.CRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 * An extended map context that enable new features for classic geotools map context. 
 * This map context enable to specify focusable layers and selectable layers.
 * <br>
 * <br>
 * This object can be used to embed a map context. In this case, the original map context
 * can be obtained by using <code>getContext()</code> method.
 * @author Julien Seinturier
 *
 */
public class ExtendedMapContext extends MapContent{

  
  private MapContent context = null;
  
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
  
//RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
//RR REDEFINITION                                                   RR
//RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR

  @Override
  public boolean addLayer(Layer layer){
    if (layer != null){
      return context.addLayer(layer);
    } else {
      return false;
    }
  }

  @Override
  public boolean removeLayer(Layer layer){
    return context.removeLayer(layer);
  }


  @Override
  public List<Layer> layers(){
    return context.layers();
  }


  @Override
  public void moveLayer(int sourcePosition, int destPosition){
    context.moveLayer(sourcePosition, destPosition);
  }


  /*
   * @Julien Seinturier
   * This is an override of org.geotools.map.MapContext#getLayerBounds() which compute
   * only bounds of "valid" layer. A valid layer is a layer for wich a bound can be computed.
   * (this method is here because getBounds() on an empty layer throws a NullPointerException)
   * @see org.geotools.map.MapContext#getLayerBounds()
   */
  @Override
  public ReferencedEnvelope getMaxBounds(){

    ReferencedEnvelope refe             = null;
    ArrayList<Layer> layerList       = null;
    Layer layer                      = null;
    ReferencedEnvelope env              = null;
    CoordinateReferenceSystem sourceCrs = null;


    try {
      refe = context.getMaxBounds();
    } catch (Exception e) {

      // Un des layer est peut être invalide car son enveloppe est null
      // On, dresse alors la liste des layers valides
      layerList = new ArrayList<Layer>();

      for(int i = 0; i < layers().size(); i++){
        try{
          refe = layers().get(i).getBounds();
          if (refe != null){
            layerList.add(layers().get(i));
          }

        } catch (Exception ex) {
        }
      }

      refe = null;

      // Calcul d'un enveloppe à partir des layers valides
      for (int i = 0; i < layerList.size(); i++) {
        layer = layerList.get(i);

        env = layer.getBounds();
        
        if (env == null) {
          continue;
        } else {
          sourceCrs = env.getCoordinateReferenceSystem();
          try {

            if (    (sourceCrs != null) 
                && (getCoordinateReferenceSystem() != null) 
                && !CRS.equalsIgnoreMetadata(sourceCrs, getCoordinateReferenceSystem())) {
              env = env.transform(getCoordinateReferenceSystem(), true);
            }
          } catch (FactoryException ex) {

          } catch (TransformException ex) {

          }

          if (refe == null) {
            refe = env;
          } else {
            refe.expandToInclude(env);
          }
        }
      }
    }

    return refe;
  }

  @Override
  public void addMapLayerListListener(MapLayerListListener listener){
    idListenerList.add(MapLayerListListener.class, listener);
  }

  @Override
  public void removeMapLayerListListener(MapLayerListListener listener){
    idListenerList.remove(MapLayerListListener.class, listener);
  }


  @Override
  public CoordinateReferenceSystem getCoordinateReferenceSystem(){
    return context.getCoordinateReferenceSystem();
  }


  @Override
  public void addMapBoundsListener(MapBoundsListener listener){
    idListenerList.add(MapBoundsListener.class, listener);
  }

  @Override
  public void removeMapBoundsListener(MapBoundsListener listener){
    idListenerList.remove(MapBoundsListener.class, listener);
  }

  @Override
  public String getTitle(){
    return context.getTitle();
  }

  @Override
  public void setTitle(final String title){
    context.setTitle(title);
  }

  @Override
  public void addPropertyChangeListener(
          java.beans.PropertyChangeListener listener){
    idListenerList.add(PropertyChangeListener.class, listener);
    
  }

  @Override
  public void removePropertyChangeListener(
          java.beans.PropertyChangeListener listener){
    idListenerList.remove(PropertyChangeListener.class, listener);
  }
  
//RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
//RR FIN REDEFINITION                                               RR
//RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
  
  
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC CONSTRUCTEUR                                                   CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC  
  /**
   * Creates a default empty map context
   * @param crs the coordinate reference system of the map.
   */
  public ExtendedMapContext(final CoordinateReferenceSystem crs) {
    context = new MapContent();
    context.getViewport().setCoordinateReferenceSystem(crs);
    selectableLayers = new ArrayList<Layer>();
    focusableLayers = new ArrayList<Layer>();
    selectedLayers = new ArrayList<Layer>();
    initContext();
  }
  
  /**
   * Creates a map context with the provided layers and title
   * 
   * @param layers the layers composing the map
   * @param crs the coordinate reference system of the map.
   */
  public ExtendedMapContext(Layer[] layers, final CoordinateReferenceSystem crs) {
      context = new MapContent();
      
      context.getViewport().setCoordinateReferenceSystem(crs);
      
      if (layers != null){
        for(int i = 0; i < layers.length; i++){
          context.addLayer(layers[i]);
        }
      }
      
      selectableLayers = new ArrayList<Layer>();
      focusableLayers = new ArrayList<Layer>();
      selectedLayers = new ArrayList<Layer>();
      initContext();
  }
  
  /**
   * Creates a map context.
   * @param layers the layers composing the map.
   * @param title the title of the map.
   * @param contextAbstract an abstract describing the context.
   * @param contactInformation informations on the contact of the map maintainer.
   * @param keywords keywords associated to the map.
   */
  public ExtendedMapContext(Layer[] layers, String title,
          String contextAbstract, String contactInformation,
          String[] keywords, final CoordinateReferenceSystem crs) {

      this(layers, crs);
    
      setTitle(title);
      
      context.getUserData().put("abstract", contextAbstract);
      context.getUserData().put("contact", contactInformation);
      context.getUserData().put("keywords", keywords);

      selectableLayers = new ArrayList<Layer>();
      focusableLayers = new ArrayList<Layer>();
      selectedLayers = new ArrayList<Layer>();
      initContext();
  }
  
  /**
   * Create a new Extended map context that embed a map context. 
   * @param context the edxisting context to embed.
   */
  public ExtendedMapContext(MapContent context){
    this.context = context;
    selectableLayers = new ArrayList<Layer>();
    focusableLayers = new ArrayList<Layer>();
    selectedLayers = new ArrayList<Layer>();
    initContext();
  }
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC FIN CONSTRUCTEUR                                               CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC  

//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
//II INITIALISATION                                                 II
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
  
  /**
   * This method is used to redirect inner map context event to the extended context.
   */
  private void initContext(){
    
    // Conservation d'une reference vers le contexte étendu
    final ExtendedMapContext emc = this;
    
    context.addMapLayerListListener(new MapLayerListListener(){

      @Override
      public void layerAdded(MapLayerListEvent event) {
        
        // Construction d'un nouvel évènement
        MapLayerListEvent nevent = null; 
        
        if (event.getFromIndex() == event.getToIndex()){
          nevent = new MapLayerListEvent(emc, event.getElement(), event.getFromIndex(), event.getMapLayerEvent());
        } else {
          nevent = new MapLayerListEvent(emc, event.getElement(), event.getFromIndex(), event.getToIndex());
        }
        
        Object[] listeners = idListenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
          if (listeners[i] == MapLayerListListener.class) {
            ( (MapLayerListListener) listeners[i + 1]).layerAdded(nevent);
          }
        }  
      }

      @Override
      public void layerChanged(MapLayerListEvent event) {
        // Construction d'un nouvel évènement
        MapLayerListEvent nevent = null; 
        
        if (event.getFromIndex() == event.getToIndex()){
          nevent = new MapLayerListEvent(emc, event.getElement(), event.getFromIndex(), event.getMapLayerEvent());
        } else {
          nevent = new MapLayerListEvent(emc, event.getElement(), event.getFromIndex(), event.getToIndex());
        }
        
        Object[] listeners = idListenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
          if (listeners[i] == MapLayerListListener.class) {
            ( (MapLayerListListener) listeners[i + 1]).layerChanged(nevent);
          }
        }  

      }

      @Override
      public void layerMoved(MapLayerListEvent event) {
        // Construction d'un nouvel évènement
        MapLayerListEvent nevent = null; 
        
        if (event.getFromIndex() == event.getToIndex()){
          nevent = new MapLayerListEvent(emc, event.getElement(), event.getFromIndex(), event.getMapLayerEvent());
        } else {
          nevent = new MapLayerListEvent(emc, event.getElement(), event.getFromIndex(),  event.getToIndex());
        }
        
        Object[] listeners = idListenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
          if (listeners[i] == MapLayerListListener.class) {
            ( (MapLayerListListener) listeners[i + 1]).layerMoved(nevent);
          }
        }  
      }

      @Override
      public void layerRemoved(MapLayerListEvent event) {
        // Construction d'un nouvel évènement
        MapLayerListEvent nevent = null; 
        
        if (event.getFromIndex() == event.getToIndex()){
          nevent = new MapLayerListEvent(emc, event.getElement(), event.getFromIndex(), event.getMapLayerEvent());
        } else {
          nevent = new MapLayerListEvent(emc, event.getElement(), event.getFromIndex(), event.getToIndex());
        }
        
        Object[] listeners = idListenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
          if (listeners[i] == MapLayerListListener.class) {
            ( (MapLayerListListener) listeners[i + 1]).layerRemoved(nevent);
          }
        }  
      }

      @Override
      public void layerPreDispose(MapLayerListEvent event) {
        // TODO Auto-generated method stub
        
      }});
    
    context.addMapBoundsListener(new MapBoundsListener(){

      @Override
      public void mapBoundsChanged(MapBoundsEvent event) {
        
        MapBoundsEvent nevent = new MapBoundsEvent(emc.getViewport(), event.getEventType(), event.getOldAreaOfInterest(), event.getNewAreaOfInterest());
        
        Object[] listeners = idListenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
          if (listeners[i] == MapBoundsListener.class) {
            ( (MapBoundsListener) listeners[i + 1]).mapBoundsChanged(nevent);
          }
        }  
        
      }});
    
    context.addPropertyChangeListener(new PropertyChangeListener(){

      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        PropertyChangeEvent nevent = new PropertyChangeEvent(evt.getSource(), 
                                                             evt.getPropertyName(),
                                                             evt.getOldValue(), 
                                                             evt.getNewValue());
        
        Object[] listeners = idListenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
          if (listeners[i] == PropertyChangeListener.class) {
            ( (PropertyChangeListener) listeners[i + 1]).propertyChange(nevent);
          }
        }  
        
      }});
    
      
  }
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
//II FIN INITIALISATION                                             II
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
  
//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
//EE EVENEMENT                                                      EE
//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
  
  /**
   * Add a listener to the extended map context. The listener must implements interface
   * java.awt.event.AWTEventListener because the interface java.util.EventListener
   * is empty and does not determine a dispatch method.
   * @param listener the listener to add.
   */
  public void addExtendedMapContextListener(AWTEventListener listener){
    idListenerList.add(AWTEventListener.class, listener);
  }
  
  /**
   * Remove a listener from the extended map context. The listener must implements interface
   * java.awt.event.AWTEventListener because the interface java.util.EventListener
   * is empty and does not determine a dispatch method.
   * @param listener the listener to remove.
   */
  public void removeExtendedMapContextListener(AWTEventListener listener){
    idListenerList.remove(AWTEventListener.class, listener);
  }
  
  /**
   * Dispatch a new event to all registered listeners. The event must be a subclass of 
   * java.awt.AWTEvent because the listener are AWTEventListener.
   * @param event the event to dispatch.
   */
  public void dispatchEvent(AWTEvent event){
    Object[] listeners = idListenerList.getListenerList();
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
   * Get the Map Context embedded by yhe extended map context.
   * @return the map context.
   */
  public MapContent getContent(){
    return this.context;
  }
  
  /**
   * Set the feature to be marked as selected in the current map panel.
   * @param features the features to select.
   */
  public void setSelectedFeatures(Collection features){
    
	  SimpleFeature[] featuresArray = null;

    if ((features != null) && (features.size() > 0)){

      featuresArray = new SimpleFeature[features.size()];
      featuresArray = (SimpleFeature[])features.toArray(featuresArray);
    }
    
    setSelectedFeatures(featuresArray); 

  }
  
  /**
   * Set the features to be marked as selected in the map panel.
   * @param features features to select
   */
  public void setSelectedFeatures(SimpleFeature[] features){

    if (selectedFeatures == null){
      this.selectedFeatures = new ArrayList<SimpleFeature>();
    } else {
      this.selectedFeatures.clear();
    }
    
    if (features != null){
      for(int i = 0; i < features.length; i++){
        this.selectedFeatures.add(features[i]);
      }
    }
    
    dispatchEvent(new ExtendedMapContextEvent(this, ExtendedMapContextEvent.FEATURE_SELECTED));
  }
  
  /**
   * Get the features selected in this panel.
   * @return the selected features.
   */
  public ArrayList<SimpleFeature> getSelectedFeatures(){
    return selectedFeatures;
  }
  
  /**
   * Set the layers that can be focused.
   * @param layers the layer to be focused. These layers have to be handled
   * by the map context.
   * @see #setFocusableLayers(Layer[])
   * @see #getFocusableLayers()
   */
  public void setFocusableLayers(List<Layer> layers){
    
    Layer[] alayers = null;
    
    if ((layers != null) && (layers.size() > 0)){
      alayers = new Layer[layers.size()];
      alayers = layers.toArray(alayers);
    } else {
      alayers = null;
    }
    
    setFocusableLayers(alayers);
  }
  
  /**
   * Set the layers that can be focused.
   * @param layers the layer to be focused. The layers have to be handled by the map
   * context.
   * @see #setFocusableLayers(List)
   * @see #getFocusableLayers()
   */
  public void setFocusableLayers(Layer[] layers){
    
    if (focusableLayers == null){
      focusableLayers = new ArrayList<Layer>();
    } else {
      focusableLayers.clear();
    }
    
    if (layers != null){
      for(int i = 0; i < layers.length; i++){
        focusableLayers.add(layers[i]);
      }
    } 
    
    dispatchEvent(new ExtendedMapContextEvent(this, ExtendedMapContextEvent.LAYER_FOCUSABILITY_CHANGED));
  }
  
  /**
   * Get the layer that can be focused.
   * @return the layer to be focused.
   * @see #setFocusableLayers(List)
   * @see #setFocusableLayers(Layer[])
   */
  public List<Layer> getFocusableLayers(){
    return focusableLayers;
  }
  
  /**
   * Set the layers that can be selected.
   * @param layers the layer to be selected. These layers have to be handled
   * by the map context.
   * @see #setFocusableLayers(Layer[])
   * @see #getSelectableLayers()
   */
  public void setSelectableLayers(List<Layer> layers){
    
    Layer[] alayers = null;
    
    if ((layers != null) && (layers.size() > 0)){
      alayers = new Layer[layers.size()];
      alayers = layers.toArray(alayers);
    } else {
      alayers = null;
    }
    
    setSelectableLayers(alayers);
    
  }
  
  /**
   * Set the layers that can be selected.
   * @param layers the layer to be selected. The layers have to be handled by the map
   * context.
   * @see #setSelectableLayers(List)
   * @see #getSelectableLayers()
   */
  public void setSelectableLayers(Layer[] layers){
    
    if (selectableLayers == null){
      selectableLayers = new ArrayList<Layer>();
    } else {
      selectableLayers.clear();
    }
    
    if (layers != null){
      for(int i = 0; i < layers.length; i++){
        selectableLayers.add(layers[i]);
      }
    } 
    
    dispatchEvent(new ExtendedMapContextEvent(this, ExtendedMapContextEvent.LAYER_SELECTABLILITY_CHANGED));
  }
  
  /**
   * Get the layer that can be selected.
   * @return the layer to be selected.
   * @see #setSelectableLayers(List)
   * @see #setSelectableLayers(Layer[])
   */
  public List<Layer> getSelectableLayers(){
    return selectableLayers;
  }

  /**
   * Set the selected layers.
   * @param layers the selected layers. These layers have to be handled
   * by the map context.
   * @see #setSelectedLayers(Layer[])
   * @see #getSelectedLayers()
   */
  public void setSelectedLayers(List<Layer> layers){
    
    Layer[] alayers = null;
    
    if ((layers != null) && (layers.size() > 0)){
      alayers = new Layer[layers.size()];
      alayers = layers.toArray(alayers);
    } else {
      alayers = null;
    }
    
    setSelectedLayers(alayers);
  }
  
  /**
   * Set the selected layers.
   * @param layers the selected layers. The layers have to be handled by the map
   * context.
   * @see #setSelectedLayers(List)
   * @see #getSelectedLayers()
   */
  public void setSelectedLayers(Layer[] layers){
    
    if (selectedLayers == null){
      selectedLayers = new ArrayList<Layer>();
    } else {
      selectedLayers.clear();
    }
    
    if (layers != null){
      for(int i = 0; i < layers.length; i++){
        selectedLayers.add(layers[i]);
      }
    } 
    
    dispatchEvent(new ExtendedMapContextEvent(this, ExtendedMapContextEvent.LAYER_SELECTED));
  }
  
  /**
   * Get the selected layers.
   * @return the selected layers.
   * @see #setSelectedLayers(List)
   * @see #setSelectedLayers(Layer[])
   */
  public List<Layer> getSelectedLayers(){
    return selectedLayers;
  }

   
  /**
   * Set if the layer given in parameter is focusable or not.
   * @param layer the layer to mark.
   * @param focusable <code>true</code> if the layer is focusable, 
   * <code>false</code> otherwise.
   * @see #isFocusable(Layer)
   */
  public void setFocusable(Layer layer, boolean focusable){
    if (layer != null){
      if (focusable == false){
        focusableLayers.remove(layer);
      } else {
        if (! focusableLayers.contains(layer)){
          focusableLayers.add(layer);
        }
      }
    }

    dispatchEvent(new ExtendedMapContextEvent(this, ExtendedMapContextEvent.LAYER_FOCUSABILITY_CHANGED));
  }
  
  /**
   * Get if a layer is focusable. This method return <code>true</code> if the layer 
   * is focusable and <code>false</code> otherwise.
   * @param layer the layer to query
   * @return <code>true</code> if the layer 
   * is focusable and <code>false</code> otherwise.
   * @see #setFocusable(Layer, boolean)
   */
  public boolean isFocusable(Layer layer){
    return focusableLayers.contains(layer);
  }
  
  /**
   * Set if the layer given in parameter is selectable or not.
   * @param layer the layer to mark.
   * @param selectable <code>true</code> if the layer is selectable, 
   * <code>false</code> otherwise.
   * @see #isSelectedable(Layer)
   */
  public void setSelectable(Layer layer, boolean selectable){
    if (layer != null){
      if (selectable == false){
        selectableLayers.remove(layer);
      } else {
        if (! selectableLayers.contains(layer)){
          selectableLayers.add(layer);
        }
      }
    }

    dispatchEvent(new ExtendedMapContextEvent(this, ExtendedMapContextEvent.LAYER_SELECTABLILITY_CHANGED));
  }
  
  /**
   * Get if a layer is selectable. This method return <code>true</code> if the layer 
   * is selectable and <code>false</code> otherwise.
   * @param layer the layer to query
   * @return <code>true</code> if the layer 
   * is selectable and <code>false</code> otherwise.
   * @see #setSelectable(Layer, boolean)
   */
  public boolean isSelectedable(Layer layer){
    return selectableLayers.contains(layer);
  }
  
  /**
   * Set if the layer given in parameter is selected or not.
   * @param layer the layer to mark.
   * @param selected <code>true</code> if the layer is selected, 
   * <code>false</code> otherwise.
   * @see #isSelecteded(Layer)
   */
  public void setSelected(Layer layer, boolean selected){
    if (layer != null){
      if (selected == false){
        selectedLayers.remove(layer);
      } else {
        if (! selectedLayers.contains(layer)){
          selectedLayers.add(layer);
        }
      }
    }
    
    dispatchEvent(new ExtendedMapContextEvent(this, ExtendedMapContextEvent.LAYER_SELECTED));
  }
  
  /**
   * Get if a layer is selected. This method return <code>true</code> if the layer 
   * is selected and <code>false</code> otherwise.
   * @param layer the layer to query
   * @return <code>true</code> if the layer 
   * is selected and <code>false</code> otherwise.
   * @see #setSelected(Layer, boolean)
   */
  public boolean isSelecteded(Layer layer){
    return selectedLayers.contains(layer);
  }
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA FIN ACCESSEURS                                                 AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  
  /**
   * Clear the current selection of layer.
   */
  public void clearLayerSelection(){
    if (selectedLayers == null){
      selectedLayers = new ArrayList<Layer>();
    } else {
      selectedLayers.clear();
    }
    
    dispatchEvent(new ExtendedMapContextEvent(this, ExtendedMapContextEvent.LAYER_SELECTED));
  }
}
