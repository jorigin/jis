package org.jorigin.jis.swing;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;

import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.jorigin.jis.JIS;
import org.jorigin.lang.LangResourceBundle;

/**
 * 
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 *
 */
public class JLayerTree extends JTree {

  private static final long serialVersionUID = JIS.BUILD;

  private LangResourceBundle lres    = (LangResourceBundle) LangResourceBundle.getBundle(Locale.getDefault());
  
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
   * @param context the geotools map context source of the layers.
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

  
  /**
   * Set the layers to be displayed in the tree.
   * @param layers the geotools map layer table to display.
   */
  private void setData(List<Layer> layers){
    DefaultMutableTreeNode root  = new DefaultMutableTreeNode(this.lres.getString("Layers"));
    DefaultMutableTreeNode node  = null;
    DefaultMutableTreeNode rnode = null;
    Style style                  = null;
    FeatureTypeStyle ftStyle     = null;
    FeatureTypeStyle[] ftStyles  = null;
    Rule[] rules                 = null;
    
    int ii                       = 0;
    boolean found                = false;
    
    TreeCellRenderer renderer = getCellRenderer();
    TreeCellEditor   editor   = getCellEditor();
    
    if ((layers != null) && (layers.size() > 0)){

      Iterator<Layer> iter = layers.iterator();
      Layer layer          = null;
      while(iter.hasNext()){
        layer = iter.next();

        node = new DefaultMutableTreeNode(layer);
        
        // Recuperation du style associé au layer
        style = layer.getStyle();
        
        ftStyles = style.featureTypeStyles().toArray(new FeatureTypeStyle[0]);
       
        if (ftStyles != null){
          ii = 0;
          ftStyle = null;
          while((!found) && (ii < ftStyles.length)){
            if (ftStyles[ii].featureTypeNames().equals(layer.getFeatureSource().getSchema().getName().getLocalPart())){
              ftStyle = ftStyles[ii];
              found = true;
            }
            ii++;
          }
          found = false;
          
          if (ftStyle != null){
            rules = ftStyle.rules().toArray( new Rule[0] );
            for(ii = 0; ii < rules.length; ii++){
              rnode = new DefaultMutableTreeNode(rules[ii]);
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
    return this.context;
  }
}

