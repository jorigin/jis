package org.jorigin.jis.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.jorigin.jis.JIS;
import org.jorigin.jis.map.ExtendedMapContent;
import org.jorigin.lang.LangResourceBundle;

/**
 * A tool bar used to control the {@link org.geotools.map.Layer layers} attached to a {@link org.geotools.map.MapContent map content}.
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 */
public class JLayerToolBar extends JToolBar {
  
  private static final long serialVersionUID = JIS.BUILD;
  
  /**
   * The layer focusable command.
   */
  public static final String LAYER_FOSUSABLE_CMD  = "layerFocusableCMD";
  
  /**
   * The layer selectable command.
   */
  public static final String LAYER_SELECTABLE_CMD = "layerSelectableCMD";
  
  /**
   * The layer up command.
   */
  public static final String LAYER_UP_CMD         = "layerUpCMD";
  
  /**
   * The layer down command.
   */
  public static final String LAYER_DOWN_CMD       = "layerDownCMD";
  
  /**
   * The layer visible command.
   */
  public static final String LAYER_VISIBLE_CMD    = "layerVisibleCMD";

  private LangResourceBundle lres    = (LangResourceBundle) LangResourceBundle.getBundle(Locale.getDefault());
  
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
  /**
   * Create a new default {@link org.geotools.map.Layer layer} tool bar.
   */
  public JLayerToolBar(){
    super();
    initGUI();
  }
  
  /**
   * Create a new {@link org.geotools.map.Layer layer} tool bar 
   * attached to the given {@link org.geotools.map.MapContent map content}
   * @param context the {@link org.geotools.map.MapContent map content} that contains the {@link org.geotools.map.Layer layers}.
   */
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
    this.focusButton = new JToggleButton();
    this.focusButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icon/layer_unfocusable.png")));
    this.focusButton.setSelectedIcon(new ImageIcon(getClass().getClassLoader().getResource("icon/layer_focusable.png")));
    this.focusButton.setActionCommand(LAYER_FOSUSABLE_CMD);
    this.focusButton.setToolTipText(this.lres.getString("Make the layer features focusable"));
    this.focusButton.addActionListener(new ActionListener(){

      @Override
      public void actionPerformed(ActionEvent e) {
        if ((JLayerToolBar.this.currentLayer != null) && (JLayerToolBar.this.context != null) && (JLayerToolBar.this.context instanceof ExtendedMapContent)){
          ((ExtendedMapContent)JLayerToolBar.this.context).setFocusable(JLayerToolBar.this.currentLayer, JLayerToolBar.this.focusButton.isSelected());
        }
        
      }});
    
    this.selectButton = new JToggleButton();
    this.selectButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icon/layer_not_selectable.png")));
    this.selectButton.setSelectedIcon(new ImageIcon(getClass().getClassLoader().getResource("icon/layer_selectable.png")));
    this.selectButton.setActionCommand(LAYER_SELECTABLE_CMD);
    this.selectButton.setToolTipText(this.lres.getString("Make the layer features selectable"));
    this.selectButton.addActionListener(new ActionListener(){

      @Override
      public void actionPerformed(ActionEvent e) {
        if ((JLayerToolBar.this.currentLayer != null) && (JLayerToolBar.this.context != null) && (JLayerToolBar.this.context instanceof ExtendedMapContent)){
          ((ExtendedMapContent)JLayerToolBar.this.context).setSelectable(JLayerToolBar.this.currentLayer, JLayerToolBar.this.selectButton.isSelected());
        }
        
      }});
    
    this.visibleButton = new JToggleButton();
    this.visibleButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icon/layer_not_visible.png")));
    this.visibleButton.setSelectedIcon(new ImageIcon(getClass().getClassLoader().getResource("icon/layer_visible.png")));
    this.visibleButton.setActionCommand(LAYER_VISIBLE_CMD);
    this.visibleButton.setToolTipText(this.lres.getString("Make the layer visible"));
    this.visibleButton.addActionListener(new ActionListener(){

      @Override
      public void actionPerformed(ActionEvent e) {
        if (JLayerToolBar.this.currentLayer != null){
          JLayerToolBar.this.currentLayer.setVisible(JLayerToolBar.this.visibleButton.isSelected());
        }
        
      }});
    
    this.upButton = new JButton();
    this.upButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icon/layer_up.png")));
    this.upButton.setActionCommand(LAYER_UP_CMD);
    this.upButton.setToolTipText(this.lres.getString("Make the layer more prioritary"));
    this.upButton.addActionListener(new ActionListener(){

      @Override
      public void actionPerformed(ActionEvent e) {
        if ((JLayerToolBar.this.currentLayer != null) && (JLayerToolBar.this.context != null)){
          
          int pos = getLayerPosition(JLayerToolBar.this.currentLayer);
          
          System.out.println("[JLayerToolBar] [actionPerformed(ActionEvent)]");
          System.out.println("  * Layer position: "+pos);
          System.out.println("  * Layer count   : "+JLayerToolBar.this.context.layers().size());
          
          
          if (pos > 0){
            JLayerToolBar.this.context.moveLayer(pos, pos-1);
          }

        }
        
      }});
    
    this.downButton = new JButton();
    this.downButton.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icon/layer_down.png")));
    this.downButton.setActionCommand(LAYER_DOWN_CMD);
    this.downButton.setToolTipText(this.lres.getString("Make the layer less prioritary"));
    this.downButton.addActionListener(new ActionListener(){

      @Override
      public void actionPerformed(ActionEvent e) {
        if ((JLayerToolBar.this.currentLayer != null) && (JLayerToolBar.this.context != null)){
          
          int pos = getLayerPosition(JLayerToolBar.this.currentLayer);
          
          if (pos < JLayerToolBar.this.context.layers().size() - 1){
            JLayerToolBar.this.context.moveLayer(pos, pos+1);
          }

        }
        
      }});
    
    this.separator1 = new JToolBar.Separator();
    this.separator2 = new JToolBar.Separator();
    
    this.setOrientation(SwingConstants.HORIZONTAL);
    this.add(this.visibleButton);
    this.add(this.focusButton);
    this.add(this.selectButton);
    this.add(this.separator1);
    this.add(this.upButton);
    this.add(this.downButton);
    this.add(this.separator2);
    
    init();
  }
  
  protected void init(){
    if (this.currentLayer == null){
      this.visibleButton.setEnabled(false);
      this.visibleButton.setSelected(false);
      this.focusButton.setEnabled(false);
      this.focusButton.setSelected(false);
      this.selectButton.setEnabled(false);
      this.selectButton.setSelected(false);
      this.upButton.setEnabled(false);
      this.upButton.setSelected(false);
      this.downButton.setEnabled(false);
      this.downButton.setSelected(false);
    } else {
      
      if (this.context == null){
        this.visibleButton.setEnabled(true);
        this.visibleButton.setSelected(this.currentLayer.isVisible());
        
        this.focusButton.setEnabled(false);
        this.focusButton.setSelected(false);
        this.selectButton.setEnabled(false);
        this.selectButton.setSelected(false);
        this.upButton.setEnabled(false);
        this.upButton.setSelected(false);
        this.downButton.setEnabled(false);
        this.downButton.setSelected(false);
        
        if (this.getComponentIndex(this.focusButton) > -1){
          this.remove(this.focusButton);
        }
        
        
        if (this.getComponentIndex(this.selectButton) > -1){
          this.remove(this.selectButton);
        }
  
        this.remove(this.separator1);
        
        if (this.getComponentIndex(this.upButton) > -1){
          this.remove(this.upButton);
        }
        
  
        if (this.getComponentIndex(this.downButton) > -1){
          this.remove(this.downButton);
        }

      } else {
        if (this.context instanceof ExtendedMapContent){
          
          this.visibleButton.setEnabled(true);
          this.visibleButton.setSelected(this.currentLayer.isVisible());
          this.focusButton.setEnabled(true);
          this.focusButton.setSelected(((ExtendedMapContent)this.context).isFocusable(this.currentLayer));
          this.selectButton.setEnabled(true);
          this.selectButton.setSelected(((ExtendedMapContent)this.context).isSelectable(this.currentLayer));
          
          if (getLayerPosition(this.currentLayer) < this.context.layers().size() -1){
            this.downButton.setEnabled(true);
          } else {
            this.downButton.setEnabled(false);
          }
          
          if (getLayerPosition(this.currentLayer) > 0){
            this.upButton.setEnabled(true);
          } else {
            
            this.upButton.setEnabled(false);
          }

          if (this.getComponentIndex(this.separator1) > -1){
            remove(this.separator1);
          }
          
          if (this.getComponentIndex(this.separator2) > -1){
            remove(this.separator2);
          }
          
          if (this.getComponentIndex(this.focusButton) < 0){
            this.add(this.focusButton);
          }
          
          if (this.getComponentIndex(this.selectButton) < 0){
            this.add(this.selectButton);
          }
          
          add(this.separator1);
          
          if (this.getComponentIndex(this.upButton) < 0){
            this.add(this.upButton);
          }
          
          if (this.getComponentIndex(this.downButton) < 0){
            this.add(this.downButton);
          }
          
          add(this.separator2);
          
        } else {
          this.visibleButton.setEnabled(true);
          this.visibleButton.setSelected(this.currentLayer.isVisible());
          this.focusButton.setEnabled(false);
          this.focusButton.setSelected(false);
          this.selectButton.setEnabled(false);
          this.selectButton.setSelected(false);
          
          if (getLayerPosition(this.currentLayer) < this.context.layers().size() -1){
            this.downButton.setEnabled(true);
          } else {
            this.downButton.setEnabled(false);
          }
          
          if (getLayerPosition(this.currentLayer) > 0){
            this.upButton.setEnabled(true);
          } else {
            
            this.upButton.setEnabled(false);
          }

          
          if (this.getComponentIndex(this.separator1) > -1){
            remove(this.separator1);
          }
          
          if (this.getComponentIndex(this.separator2) > -1){
            remove(this.separator2);
          }
          
          if (this.getComponentIndex(this.focusButton) > -1){
            this.remove(this.focusButton);
          }
          
          if (this.getComponentIndex(this.selectButton) > -1){
            this.remove(this.selectButton);
          }
          
          add(this.separator1);
          
          if (this.getComponentIndex(this.upButton) < 0){
            this.add(this.upButton);
          }
          
          if (this.getComponentIndex(this.downButton) < 0){
            this.add(this.downButton);
          }
          
          add(this.separator2);
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
   * @param context context the map context.
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
    this.currentLayer = layer;
    
//    System.out.println("[JLayerToolBar] [setCurrentLayer(MapLayer)]");
//    System.out.println("  * Layer position: "+getLayerPosition(currentLayer));
    
    init();
  }
  
  /**
   * Get the current layer attached to the control tool bar.
   * @return the current layer.
   */
  public Layer getCurrentLayer(){
    return this.currentLayer;
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
    
    layers = this.context.layers();
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
