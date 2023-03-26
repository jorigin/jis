package org.arpenteur.gis.geotools.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.arpenteur.common.ihm.icon.IconServer;
import org.arpenteur.gis.geotools.map.ExtendedMapContext;

import org.geotools.map.Layer;
import org.geotools.map.MapContent;

/**
 * A tool bar used to control the layers attached to a map context.
 * @author Julien Seinturier
 */
public class JLayerToolBar extends JToolBar {
  
  private static final long serialVersionUID = 1L;
  
  public static final String LAYER_FOSUSABLE_CMD  = "layerFocusableCMD";
  public static final String LAYER_SELECTABLE_CMD = "layerSelectableCMD";
  public static final String LAYER_UP_CMD         = "layerUpCMD";
  public static final String LAYER_DOWN_CMD       = "layerDownCMD";
  public static final String LAYER_VISIBLE_CMD    = "layerVisibleCMD";

   
  private MapContent context             = null;
  
  private Layer currentLayer          = null;
  
  private JToggleButton focusButton      = null;
  private JToggleButton selectButton     = null;
  private JToggleButton visibleButton    = null;
  
  
  private JButton upButton               = null;
  private JButton downButton             = null;
  
  private JToolBar.Separator separator1  = null;
  private JToolBar.Separator separator2  = null;
  
  
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC CONSTRUCTEUR                                                   CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  public JLayerToolBar(){
    super();
    initGUI();
  }
  
  public JLayerToolBar(MapContent context){
    super();
    this.context = context;
  }
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC FIN CONSTRUCTEUR                                               CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC

//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
//II INITIALISATION                                                 II
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
  protected void initGUI(){
    focusButton = new JToggleButton();
    focusButton.setIcon(IconServer.getIcon("crystalsvg/16x16/actions/adept_purge.png"));
    focusButton.setSelectedIcon(IconServer.getIcon("crystalsvg/16x16/actions/adept_commit.png"));
    focusButton.setActionCommand(LAYER_FOSUSABLE_CMD);
    focusButton.setToolTipText("Make the layer features focusable");
    focusButton.addActionListener(new ActionListener(){

      @Override
      public void actionPerformed(ActionEvent e) {
        if ((currentLayer != null) && (context != null) && (context instanceof ExtendedMapContext)){
          ((ExtendedMapContext)context).setFocusable(currentLayer, focusButton.isSelected());
        }
        
      }});
    
    selectButton = new JToggleButton();
    selectButton.setIcon(IconServer.getIcon("crystalsvg/16x16/actions/kimproxyoffline.png"));
    selectButton.setSelectedIcon(IconServer.getIcon("crystalsvg/16x16/actions/adept_notifier_ok.png"));
    selectButton.setActionCommand(LAYER_SELECTABLE_CMD);
    selectButton.setToolTipText("Make the layer features selectable");
    selectButton.addActionListener(new ActionListener(){

      @Override
      public void actionPerformed(ActionEvent e) {
        if ((currentLayer != null) && (context != null) && (context instanceof ExtendedMapContext)){
          ((ExtendedMapContext)context).setSelectable(currentLayer, selectButton.isSelected());
        }
        
      }});
    
    visibleButton = new JToggleButton();
    visibleButton.setIcon(IconServer.getIcon("crystalsvg/16x16/actions/jabber_offline.png"));
    visibleButton.setSelectedIcon(IconServer.getIcon("crystalsvg/16x16/actions/jabber_online.png"));
    visibleButton.setActionCommand(LAYER_VISIBLE_CMD);
    visibleButton.setToolTipText("Make the layer visible");
    visibleButton.addActionListener(new ActionListener(){

      @Override
      public void actionPerformed(ActionEvent e) {
        if (currentLayer != null){
          currentLayer.setVisible(visibleButton.isSelected());
        }
        
      }});
    
    upButton = new JButton();
    upButton.setIcon(IconServer.getIcon("crystalsvg/16x16/actions/1uparrow.png"));
    upButton.setActionCommand(LAYER_UP_CMD);
    upButton.setToolTipText("Make the layer more prioritary");
    upButton.addActionListener(new ActionListener(){

      @Override
      public void actionPerformed(ActionEvent e) {
        if ((currentLayer != null) && (context != null)){
          
          int pos = getLayerPosition(currentLayer);

          if (pos > 0){
            context.moveLayer(pos, pos-1);
          }

        }
        
      }});
    
    downButton = new JButton();
    downButton.setIcon(IconServer.getIcon("crystalsvg/16x16/actions/1downarrow.png"));
    downButton.setActionCommand(LAYER_DOWN_CMD);
    downButton.setToolTipText("Make the layer less prioritary");
    downButton.addActionListener(new ActionListener(){

      @Override
      public void actionPerformed(ActionEvent e) {
        if ((currentLayer != null) && (context != null)){
          
          int pos = getLayerPosition(currentLayer);
          
          if ((context.layers() != null) && (pos < context.layers().size() - 1)){
            context.moveLayer(pos, pos+1);
          }

        }
        
      }});
    
    separator1 = new JToolBar.Separator();
    separator2 = new JToolBar.Separator();
    
    this.setOrientation(SwingConstants.HORIZONTAL);
    this.add(visibleButton);
    this.add(focusButton);
    this.add(selectButton);
    this.add(separator1);
    this.add(upButton);
    this.add(downButton);
    this.add(separator2);
    
    init();
  }
  
  protected void init(){
    if (currentLayer == null){
      visibleButton.setEnabled(false);
      visibleButton.setSelected(false);
      focusButton.setEnabled(false);
      focusButton.setSelected(false);
      selectButton.setEnabled(false);
      selectButton.setSelected(false);
      upButton.setEnabled(false);
      upButton.setSelected(false);
      downButton.setEnabled(false);
      downButton.setSelected(false);
    } else {
      
      if (context == null){
        visibleButton.setEnabled(true);
        visibleButton.setSelected(currentLayer.isVisible());
        
        focusButton.setEnabled(false);
        focusButton.setSelected(false);
        selectButton.setEnabled(false);
        selectButton.setSelected(false);
        upButton.setEnabled(false);
        upButton.setSelected(false);
        downButton.setEnabled(false);
        downButton.setSelected(false);
        
        if (this.getComponentIndex(focusButton) > -1){
          this.remove(focusButton);
        }
        
        
        if (this.getComponentIndex(selectButton) > -1){
          this.remove(selectButton);
        }
  
        this.remove(separator1);
        
        if (this.getComponentIndex(upButton) > -1){
          this.remove(upButton);
        }
        
  
        if (this.getComponentIndex(downButton) > -1){
          this.remove(downButton);
        }

      } else {
        if (context instanceof ExtendedMapContext){
          
          visibleButton.setEnabled(true);
          visibleButton.setSelected(currentLayer.isVisible());
          focusButton.setEnabled(true);
          focusButton.setSelected(((ExtendedMapContext)context).isFocusable(currentLayer));
          selectButton.setEnabled(true);
          selectButton.setSelected(((ExtendedMapContext)context).isSelectedable(currentLayer));
          
          if ((context.layers() != null) && (getLayerPosition(currentLayer) < context.layers().size() -1)){
            downButton.setEnabled(true);
          } else {
            downButton.setEnabled(false);
          }
          
          if (getLayerPosition(currentLayer) > 0){
            upButton.setEnabled(true);
          } else {
            
            upButton.setEnabled(false);
          }

          if (this.getComponentIndex(separator1) > -1){
            remove(separator1);
          }
          
          if (this.getComponentIndex(separator2) > -1){
            remove(separator2);
          }
          
          if (this.getComponentIndex(focusButton) < 0){
            this.add(focusButton);
          }
          
          if (this.getComponentIndex(selectButton) < 0){
            this.add(selectButton);
          }
          
          add(separator1);
          
          if (this.getComponentIndex(upButton) < 0){
            this.add(upButton);
          }
          
          if (this.getComponentIndex(downButton) < 0){
            this.add(downButton);
          }
          
          add(separator2);
          
        } else {
          visibleButton.setEnabled(true);
          visibleButton.setSelected(currentLayer.isVisible());
          focusButton.setEnabled(false);
          focusButton.setSelected(false);
          selectButton.setEnabled(false);
          selectButton.setSelected(false);
          
          if ((context.layers() != null) && (getLayerPosition(currentLayer) < context.layers().size() -1)){
            downButton.setEnabled(true);
          } else {
            downButton.setEnabled(false);
          }
          
          if (getLayerPosition(currentLayer) > 0){
            upButton.setEnabled(true);
          } else {
            
            upButton.setEnabled(false);
          }

          
          if (this.getComponentIndex(separator1) > -1){
            remove(separator1);
          }
          
          if (this.getComponentIndex(separator2) > -1){
            remove(separator2);
          }
          
          if (this.getComponentIndex(focusButton) > -1){
            this.remove(focusButton);
          }
          
          if (this.getComponentIndex(selectButton) > -1){
            this.remove(selectButton);
          }
          
          add(separator1);
          
          if (this.getComponentIndex(upButton) < 0){
            this.add(upButton);
          }
          
          if (this.getComponentIndex(downButton) < 0){
            this.add(downButton);
          }
          
          add(separator2);
        }
      }

    }
  }
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
//II FIN INITIALISATION                                             II
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII

  
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA ACCESSEURS                                                     AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  
  /**
   * Set the context of the layers controlled by this tool bar.
   * @param MapContext context the map context.
   */
  public void setContext(MapContent context){
    this.context = context;
    init();
  }
  
  /**
   * Set the current layer attached to the control tool bar.
   * @param layer the current layer.
   */
  public void setCurrentLayer(Layer layer){
    currentLayer = layer;
    
//    System.out.println("[JLayerToolBar] [setCurrentLayer(MapLayer)]");
//    System.out.println("  * Layer position: "+getLayerPosition(currentLayer));
    
    init();
  }
  
  /**
   * Get the current layer attached to the control tool bar.
   * @return the current layer.
   */
  public Layer getCurrentLayer(){
    return currentLayer;
  }
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA FIN ACCESSEURS                                                 AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  
  /**
   * Get the position of a layer in the given context.
   * @param the layer.
   */
  private int getLayerPosition(Layer layer){
    List<Layer> layers = null;
    int position      = -1;
    
//    System.out.println("[JLayerToolBar] [getLayerPosition(MapLayer)]");
//    System.out.println("  * Layer:   "+layer.getTitle());
    
    layers = context.layers();
    if ((layers != null)&&(layers.size() > 0)){
      for(int i = 0; i < layers.size(); i++){
        
//        System.out.println("  * Layer "+i+": "+layers[i].getTitle());
        if (layers.get(i).equals(layer)){
          position = i;
        }
      }
    }
//    System.out.println("  + Position:   "+position);
    return position;
  }

}
