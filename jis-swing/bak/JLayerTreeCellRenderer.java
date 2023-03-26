package org.jorigin.jis.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PointSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.RasterSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Symbolizer;
import org.geotools.styling.TextSymbolizer;
import org.jorigin.jis.JIS;
import org.jorigin.jis.map.ExtendedMapContent;
import org.jorigin.jis.wrap.AWTGeometryWrap;
import org.jorigin.lang.LangResourceBundle;



/**
 * The default layer tree cell renderer.
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 *
 */
public class JLayerTreeCellRenderer extends DefaultTreeCellRenderer {

  /**
	 * 
	 */
	private static final long serialVersionUID = JIS.BUILD;

private LangResourceBundle lres    = (LangResourceBundle) LangResourceBundle.getBundle(Locale.getDefault());
  
  /** The width of the style icon */
  int styleIconWidth = 16;
  
  /** The height of the style icon */
  int styleIconHeight = 16;
  
  public Component getTreeCellRendererComponent(JTree tree, Object value,
      boolean selected, boolean expanded, boolean leaf, int row,
      boolean hasFocus) {

    DefaultMutableTreeNode dmtcr = (DefaultMutableTreeNode)value;
    Layer layer               = null;
    
    JPanel panel                 = null;
    JLabel label                 = null;
    JCheckBox focusableCB        = null;
    JCheckBox selectableCB       = null;
   
    
    JCheckBox checkBox           = null;
    BufferedImage image          = null;
    Graphics2D graphics          = null;

    Rule  rule                   = null;
    
    JPanel labelPanel            = null;
    
    MapContent context           = null;
    
    String str                   = null;
    
    final ExtendedMapContent econtext;

    // Recuperation du contexte (et détermination de la presence d'un contexte etendu 
    // ou normal )
    if (tree instanceof JLayerTree){
      context = ((JLayerTree)tree).getContext();
    }
    
    if (context instanceof ExtendedMapContent){
      econtext = (ExtendedMapContent)context;

    } else {
      econtext = null;
    }
    
    context = null;
    
    
    // Affichage de la raciine
    if (dmtcr == tree.getModel().getRoot()){
      label =  new JLabel(this.lres.getString("Layers"));
      label.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icon/layers_root.png")));
      return  label;
    } 
    
    label = new JLabel();
    labelPanel = new JPanel();
    
    if (selected){
      label.setForeground(UIManager.getColor("Tree.selectionForeground"));
      label.setBackground(UIManager.getColor("Tree.selectionBackground"));
      labelPanel.setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
      labelPanel.setBackground(UIManager.getColor("Tree.selectionBackground"));
    } else {
      label.setForeground(UIManager.getColor("Tree.textForeground"));
      label.setBackground(UIManager.getColor("Tree.textBackground"));
      labelPanel.setBackground(UIManager.getColor("Tree.textBackground"));
    }
    
    
    // Affichage d'un layer avec un style simple (style avec une seule règle)
    if (dmtcr.getUserObject() instanceof Layer){
      
      JPanel comandPanel  = null;
      
      layer               = (Layer)dmtcr.getUserObject();
      
      final Layer fLayer = layer;
      
      checkBox            = new JCheckBox();
      checkBox.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icon/layer_not_visible.png")));
      checkBox.setSelectedIcon(new ImageIcon(getClass().getClassLoader().getResource("icon/layer_visible.png")));
      checkBox.addItemListener(new ItemListener(){

        public void itemStateChanged(ItemEvent e) {
          fLayer.setVisible(((JCheckBox)e.getSource()).isSelected());
          
        }});
      
      
      // Le layer est il visible
      if (layer.isVisible()){
        checkBox.setSelected(true);
      } else {
        checkBox.setSelected(false);
      }
     
      checkBox.setToolTipText(this.lres.getString("Set the visibility of the layer"));
      
      // Le layer est il focusable
      if (econtext != null){
        focusableCB = new JCheckBox();
        focusableCB.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icon/layer_unfocusable.png")));
        focusableCB.setSelectedIcon(new ImageIcon(getClass().getClassLoader().getResource("icon/layer_focusable.png")));
        focusableCB.setSelected(econtext.isFocusable(layer));
        focusableCB.addItemListener(new ItemListener(){

          public void itemStateChanged(ItemEvent e) {
            econtext.setFocusable(fLayer, ((JCheckBox)e.getSource()).isSelected());
          }});
        
        focusableCB.setToolTipText(this.lres.getString("Focusability of the layer"));
        
      } else {
        focusableCB = null;
      }
      
      // Le layer est il selectionnable
      if (econtext != null){
        selectableCB = new JCheckBox();
        selectableCB.setIcon(new ImageIcon(getClass().getClassLoader().getResource("icon/layer_not_selectable.png")));
        selectableCB.setSelectedIcon(new ImageIcon(getClass().getClassLoader().getResource("icon/layer_selectable.png")));
        selectableCB.setSelected(econtext.isSelectable(layer));
        selectableCB.addItemListener(new ItemListener(){

          public void itemStateChanged(ItemEvent e) {
            econtext.setSelectable(fLayer, ((JCheckBox)e.getSource()).isSelected());
          }});
        
        selectableCB.setToolTipText(this.lres.getString("Selectability of the layer"));
        
      } else {
        selectableCB = null;
      }
      
      
      //label = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);   

      str = layer.getTitle();
      
      if (layer.getFeatureSource() != null){
	
	if (layer.getFeatureSource().getSchema() != null){
	  
	  str +=" - "+layer.getFeatureSource().getSchema().getName().getLocalPart();
	  
	  if (layer.getFeatureSource().getSchema().getGeometryDescriptor() != null){
	  
	    if (layer.getFeatureSource().getSchema().getGeometryDescriptor().getType() != null){
	    
	      str += " ("
                     +layer.getFeatureSource().getSchema().getGeometryDescriptor().getType().getName().getLocalPart()
                     +")";	     
	    }
	  }
	}
      } 
	  
      label.setText(str);
      
      //label.setIcon(IconServer.getIcon("crystalsvg/16x16/mimetypes/html.png"));
      label.setIcon(null);
      label.setToolTipText("<html><head></head><body>"+"<b>"+layer.getTitle()+"</b><br>"+layer.getQuery()+"</body></html>");
      
      comandPanel = new JPanel();
      comandPanel.setBackground(UIManager.getColor("Tree.textBackground"));
      comandPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
      ((FlowLayout)comandPanel.getLayout()).setHgap(1);
      ((FlowLayout)comandPanel.getLayout()).setVgap(0);
      
      comandPanel.add(checkBox);
      
      if (focusableCB != null){
        comandPanel.add(focusableCB);
      }
      
      if (selectableCB != null){
        comandPanel.add(selectableCB);
      }
      
      labelPanel.setBackground(label.getBackground());
      labelPanel.setLayout(new BorderLayout());
      ((BorderLayout)labelPanel.getLayout()).setVgap(0);
      
      labelPanel.add(label, BorderLayout.CENTER);
      
      panel = new JPanel();
      panel.setLayout(new BorderLayout());
      panel.add(comandPanel, BorderLayout.WEST);
      panel.add(labelPanel, BorderLayout.CENTER);

    // Affichage d'un style
    } else if (dmtcr.getUserObject() instanceof Rule){
      
      rule  = (Rule) dmtcr.getUserObject();
      
      if (rule != null){
        
        // Creation d'une image représentant le style.
        image = new BufferedImage(this.styleIconWidth, this.styleIconHeight, BufferedImage.TYPE_INT_ARGB);
        graphics = (Graphics2D) image.getGraphics();
        
        // Recuperation des symbolizers pour la règle associée au style du layer
        Symbolizer symbolizer =  rule.getSymbolizers()[0];
        
        if (symbolizer instanceof LineSymbolizer){
          LineSymbolizer lineSymbolizer = (LineSymbolizer) symbolizer;
          
          Color strokeColor = AWTGeometryWrap.wrapColor(lineSymbolizer.getStroke().getColor());
          Stroke stroke     = null;
          
          
          if ((lineSymbolizer.getStroke() != null) &&(lineSymbolizer.getStroke().getColor() != null)){
            strokeColor = AWTGeometryWrap.wrapColor(lineSymbolizer.getStroke().getColor());
            stroke      = AWTGeometryWrap.wrapStroke(lineSymbolizer.getStroke());
          } else {
            strokeColor = graphics.getColor();
            stroke      = graphics.getStroke();
          }
          
          graphics.setColor(strokeColor);
          graphics.setStroke(stroke);
          graphics.drawLine(0, 0, this.styleIconWidth/3, this.styleIconHeight);
          graphics.drawLine(this.styleIconWidth/3, this.styleIconHeight, 2*this.styleIconWidth/3, 0);
          graphics.drawLine(2*this.styleIconWidth/3, 0, this.styleIconWidth, this.styleIconHeight);
 
        } else if (symbolizer instanceof PointSymbolizer){
          
        } else if (symbolizer instanceof PolygonSymbolizer){
          PolygonSymbolizer polygonSymbolizer = (PolygonSymbolizer) symbolizer;
          
          Color fillColor   = null;
          Color strokeColor = null;
          
          if ((polygonSymbolizer.getFill() != null) && (polygonSymbolizer.getFill().getColor() != null)){
            fillColor   = AWTGeometryWrap.wrapColor(polygonSymbolizer.getFill().getColor());
          } else {
            fillColor = graphics.getColor();
          }
          
          if ((polygonSymbolizer.getStroke() != null) && (polygonSymbolizer.getStroke().getColor() != null)){
            strokeColor = AWTGeometryWrap.wrapColor(polygonSymbolizer.getStroke().getColor());
          } else {
            strokeColor = graphics.getColor();
          }

          Stroke stroke     = AWTGeometryWrap.wrapStroke(polygonSymbolizer.getStroke());
          
          graphics.setColor(fillColor);
          graphics.fillRect(0, 0, this.styleIconWidth - 1, this.styleIconHeight - 1);
          graphics.setColor(strokeColor);
          graphics.setStroke(stroke);
          graphics.drawRect(0, 0, this.styleIconWidth - 1, this.styleIconHeight - 1);
    
        } else if (symbolizer instanceof RasterSymbolizer){
          
        } else if (symbolizer instanceof TextSymbolizer){
          
        }

        //label = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        
        if ((rule.getDescription().getTitle() != null) && (rule.getDescription().getTitle().toString().length() > 0)){
          label.setText(rule.getDescription().getTitle().toString());
        } else {
          label.setText(this.lres.getString("No title"));
        }
        label.setIcon(new ImageIcon(image));
        label.setToolTipText("<html><head></head><body>"+"<b>"+rule.getDescription().getTitle().toString()+"</b><br>"+rule.getDescription().getAbstract().toString()+"</body></html>");
        
        labelPanel.add(label);
        labelPanel.setLayout(new BorderLayout());
        labelPanel.add(label, BorderLayout.CENTER);
        
        panel = labelPanel;
      }
      
      
      
    } else {
      label          = new JLabel(dmtcr.getUserObject().toString());
      label.setEnabled(false);
      
      panel = new JPanel();
      panel.setLayout(new BorderLayout());
      panel.add(label, BorderLayout.CENTER);
    }

    dmtcr    = null;
    layer    = null;
    
    return panel; 
  }
 
}
