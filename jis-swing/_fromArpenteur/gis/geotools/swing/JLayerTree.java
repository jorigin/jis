package org.arpenteur.gis.geotools.swing;

import java.util.List;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;

import org.arpenteur.common.Common;

import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;

public class JLayerTree extends JTree {


  private static final long serialVersionUID = Common.BUILD;
  
  /** The layers represented by this tree */
  MapContent context;
  
  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  //CC CONSTRUCTEUR                                                 CC
  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC

  /**
   * Create a new default Layer Tree
   */
  public JLayerTree(){
    super();  
    
    this.setRowHeight(20);
    
    setCellRenderer(new JLayerTreeCellRenderer());
    setCellEditor(new JLayerTreeCellEditor(this, (JLayerTreeCellRenderer)this.getCellRenderer()));
    
    setEditable(true);
  }
  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  //CC FIN CONSTRUCTEUR                                             CC
  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC

  /**
   * Create a new default Layer tree from a geotools map context.
   * This constructor list all layers in context and create a tree where
   * all nodes are attached to a layer.
   * @param MapContext the geotools map context source of the layers.
   */
  public JLayerTree(MapContent context){
    this();
    
    setData(context);

  }
  
  
  /**
   * Set the data to be displayed in the tree. The data are the layers contained in the given map context.
   * @param context the map context containing the layers to display.
   */
  public void setData(MapContent context){
    
    if ((context != null) && (context.layers().size() > 0)){
      this.context = context;
      setData(context.layers());
    }
  }

  private void setData(List<Layer> layers){
    if (layers != null){
      setData(layers.toArray(new Layer[layers.size()]));
    }
  }
  
  /**
   * Set the layers to be displayed in the tree.
   * @param layers the geotools map layer table to display.
   */
  private void setData(Layer[] layers){
    DefaultMutableTreeNode root     = new DefaultMutableTreeNode("Layers");
    DefaultMutableTreeNode node     = null;
    DefaultMutableTreeNode rnode    = null;
    Style style                     = null;
    FeatureTypeStyle ftStyle        = null;
    List<FeatureTypeStyle> ftStyles = null;
    List<Rule> rules                = null;
    
    int ii                          = 0;
    boolean found                   = false;
    
    TreeCellRenderer renderer = getCellRenderer();
    TreeCellEditor   editor   = getCellEditor();
    
    if ((layers != null) && (layers.length > 0)){

      for(int i = 0; i < layers.length; i++){
        node = new DefaultMutableTreeNode(layers[i]);
        
        // Recuperation du style associé au layer
        style = layers[i].getStyle();
        
        ftStyles = style.featureTypeStyles();
        
        if (ftStyles != null){
          ii = 0;
          ftStyle = null;
          while((!found) && (ii < ftStyles.size())){
            if (ftStyles.get(ii).featureTypeNames().contains(layers[i].getFeatureSource().getSchema().getName())){
              ftStyle = ftStyles.get(ii);
              found = true;
            }
            ii++;
          }
          found = false;
          
          if (ftStyle != null){
            rules = ftStyle.rules();
            for(ii = 0; ii < rules.size(); ii++){
              rnode = new DefaultMutableTreeNode(rules.get(ii));
              node.add(rnode);
            }
          }
          
        }
        
        root.add(node);
      }
      
      // Initialisation du modèle de l'arbre et attachement
      DefaultTreeModel model = new DefaultTreeModel(root);
      setModel(model);
      
      
      
      if (renderer != null){
        setCellRenderer(renderer);
      } else {
        setCellRenderer(new JLayerTreeCellRenderer());
      }
      
      if (editor != null){
        setCellEditor(editor);
      } else {
        setCellEditor(new JLayerTreeCellEditor(this, new JLayerTreeCellRenderer()));
      }
    }
  }
 
  /**
   * Get the map context attached to this tree.
   * @return the map context attached to this tree.
   */
  public MapContent getContext(){
    return context;
  }
}

