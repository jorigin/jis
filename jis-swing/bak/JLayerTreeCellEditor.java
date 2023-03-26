package org.jorigin.jis.swing;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JTree;
import javax.swing.event.CellEditorListener;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;



/**
 * The default tree cell editor.
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 *
 */
public class JLayerTreeCellEditor extends DefaultTreeCellEditor {

  /**
   * Construct a new JLayerTreeCellEditor
   * @param tree the tree to edit
   * @param renderer the renderer of the tree.
   */
  public JLayerTreeCellEditor(JTree tree, DefaultTreeCellRenderer renderer){
    super(tree, renderer);
  }
  
  public Component getTreeCellEditorComponent(JTree tree, Object value,
      boolean isSelected, boolean expanded, boolean leaf, int row) {
 
    
    return this.renderer.getTreeCellRendererComponent(tree, value, true, expanded, leaf, row, true);
/*    
   
    int styleIconWidth = 16;
    
    
    int styleIconHeight = 16;
    
    DefaultMutableTreeNode dmtcr = (DefaultMutableTreeNode)value;
    MapLayer layer               = null;
    
    JPanel panel                 = null;
    JLabel label                 = null;
    
    JCheckBox checkBox           = null;
    BufferedImage image          = null;
    Graphics2D graphics          = null;
    Style style                  = null;
    
    if (dmtcr == tree.getModel().getRoot()){
      return  new JLabel(LangResourceBundle.getString("Layers"));
    }
    
    if (dmtcr.getUserObject() instanceof MapLayer){
      layer                    = (MapLayer)dmtcr.getUserObject();
      final MapLayer currLayer = layer;
      
      
      checkBox            = new JCheckBox();
      
      checkBox.addItemListener(new ItemListener(){

        public void itemStateChanged(ItemEvent e) {
          currLayer.setVisible(((JCheckBox)e.getSource()).isSelected());  
        }});
      
      if (layer.isVisible()){
        checkBox.setSelected(true);
      } else {
        checkBox.setSelected(false);
      }
      
      // Création d'un icone d'apercu de style si un layer
      // possède un style.
      style = layer.getStyle();
      
      if (style != null){

        // Creation d'une image représentant le style.
        image = new BufferedImage(styleIconWidth, styleIconHeight, BufferedImage.TYPE_INT_ARGB);
        graphics = (Graphics2D) image.getGraphics();
        
        // Recuperation des styles pour les types de features associés à ce layer
        // Par defaut il s'agit du style du premier type de feature et pour la règle
        // la plus generale.
        Symbolizer symbolizer =  style.getFeatureTypeStyles()[0].getRules()[0].getSymbolizers()[0];
        
        if (symbolizer instanceof LineSymbolizer){
          
        } else if (symbolizer instanceof PointSymbolizer){
          
        } else if (symbolizer instanceof PolygonSymbolizer){
          PolygonSymbolizer polygonSymbolizer = (PolygonSymbolizer) symbolizer;
          Color fillColor   = AWTGeometryWrap.wrapColor(polygonSymbolizer.getFill().getColor());
          Color strokeColor = AWTGeometryWrap.wrapColor(polygonSymbolizer.getStroke().getColor());
          Stroke stroke     = AWTGeometryWrap.wrapStroke(polygonSymbolizer.getStroke());
          
          graphics.setColor(fillColor);
          graphics.fillRect(0, 0, styleIconWidth - 1, styleIconHeight - 1);
          graphics.setColor(strokeColor);
          graphics.setStroke(stroke);
          graphics.drawRect(0, 0, styleIconWidth - 1, styleIconHeight - 1);
          
          
          
        } else if (symbolizer instanceof RasterSymbolizer){
          
        } else if (symbolizer instanceof TextSymbolizer){
          
        }
        panel = (JPanel) super.renderer.getTreeCellRendererComponent(tree, value, isSelected, expanded, leaf, row, true);
      }

    } else {
      label          = new JLabel(dmtcr.getUserObject().toString());
      label.setEnabled(false);
      
      panel = new JPanel();
      panel.setLayout(new BorderLayout());
      panel.add(label, BorderLayout.CENTER);
      
    }

    dmtcr = null;
    layer = null;
    
    return panel; 
    
*/    
  }

  public void addCellEditorListener(CellEditorListener l) {
    // TODO Auto-generated method stub
    
  }

  public void removeCellEditorListener(CellEditorListener l) {
    // TODO Auto-generated method stub
    
  }
  
  public void cancelCellEditing() {
    // TODO Auto-generated method stub
    
  }

  public Object getCellEditorValue() {
    return this;
  }

  public boolean isCellEditable(EventObject anEvent) {
    if(anEvent instanceof MouseEvent){
      MouseEvent mevt = (MouseEvent) anEvent;
      if (mevt.getClickCount() == 1){
        return true;
      }
    }
    
    return false; 
  }

  public boolean shouldSelectCell(EventObject anEvent) {
    return true;
  }

  public boolean stopCellEditing() {
    return false;
  }
  
}
