package org.jorigin.jis.swing;


import java.awt.Color;
import java.awt.Component;
import java.util.Comparator;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.jorigin.jis.JIS;

/**
 * A table dedicated to GIS feature visualization.
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 * @see FeatureTableModel
 */
public class JFeatureTable extends JTable{

  private static final long serialVersionUID = JIS.BUILD;
  
  /**
   * The state identifier.
   */
  public static final int STATE       = 1;
  
  /**
   * The feature ID identifier.
   */
  public static final int FEATUREID   = 2;
  
  /**
   * The feature type identifier.
   */
  public static final int TYPE        = 4;
  
  /**
   * The layer identifier.
   */
  public static final int LAYER       = 8;

  private int STATE_COLUMN_SIZE       = 20;
  private int FEATUREID_COLUMN_SIZE   = 80;
  private int TYPE_COLUMN_SIZE        = 100;
  private int LAYER_COLUMN_SIZE       = 100;
  
  /**
   * The combination of all identifiers.
   */
  public static final int ALL =   STATE| FEATUREID | TYPE | LAYER;
  
  private int visibleColumnFlags = ALL;
  
  private String[] names = {"state", "featureid", "type", "layer"};
  
  private MapContent data = null;
  
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC CONSTRUCTEUR                                                        CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC

  /**
   * Create a new default feature table
   */
  public JFeatureTable(){
    this(null, ALL);
  }
  
  /**
   * Create a new feature table displaying features of all the layer of the given
   * map context.
   * @param context the context to display.
   */
  public JFeatureTable(MapContent context){
    this(context, ALL);
  }
  
  /**
   * Create a new feature table displaying features of all the layers of the given context.
   * The visible columns are given by the <code>visibleColumnFlags</code> parameter.
   * @param context the context to display.
   * @param visibleColumnFlags the visible columns.
   */
  public JFeatureTable(MapContent context, int visibleColumnFlags){
    super();
    
    this.visibleColumnFlags = visibleColumnFlags;
    
    setModel(new FeatureTableModel());
    
    if (context != null){
      setData(context);
    }
  }
  
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC FIN CONSTRUCTEUR                                                    CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC

//RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
//RR REDEFINITIONS                                                       RR
//RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR

  // >>>>>>>>>>>> JTable <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
  @Override
  public boolean isCellEditable(int row, int column) {
    return false;
  }
  //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
//RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
//RR FIN REDEFINITIONS                                                   RR
//RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR

//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA ACCESSEUR                                                           AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  
  /**
   * Set the data to display in the table
   * @param context the data to display as a map context.
   */
  public void setData(MapContent context){
    
    TableRowSorter<TableModel> sorter      = null;
    
    this.data = context;

    ((FeatureTableModel)getModel()).setData(context.layers());
    
    DefaultTableColumnModel newColumnModel = new DefaultTableColumnModel();
    for(int i = 0; i < this.names.length; i++){
      newColumnModel.addColumn(new TableColumn(i));
    }
    
    setColumnModel(newColumnModel);
    
    initModel();
    
    sorter = new TableRowSorter<TableModel>(this.dataModel);
    initSorter(sorter);
  }
  
  /**
   * Get the data associated to this table.
   * @return the data associated to this table
   */
  public MapContent getData(){
    return this.data;
  }
  
  /**
   * Get the feature displayed at the row given by <code>rowIndex</code> in the table.
   * This method take in account the order of the table.
   * @param rowIndex the row index of the feature to get.
   * @return the desired feature.
   */
  public SimpleFeature getFeatureAtRow(int rowIndex){
    return (SimpleFeature)getModel().getValueAt(convertRowIndexToModel(rowIndex), FeatureTableModel.FEATUREID_COLUMN);
  }
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA FIN ACCESSEUR                                                       AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

//INITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINIT
//IN INITIALIZATION                                                     IT
//INITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINIT
  /**
   * Init the table model
   */
  protected void initModel(){

    if (this.dataModel == null) {
      return;
    }
    
    TableCellRenderer renderer = new TableCellRenderer() {

  
      public Component getTableCellRendererComponent(JTable table,
          Object value, boolean isSelected, boolean hasFocus, int row,
          int col) {
        
        // Texte du label
        String text = null;

        // Texte du tooltip
        String tooltipText = null;

        
        // La premiere colonne contient les types
        JLabel label = new JLabel();
        
        // Impose l'affichage du background du label
        label.setOpaque(true);
        
        if (isSelected) {
          label.setForeground(table.getSelectionForeground());
          label.setBackground(table.getSelectionBackground());
        } else {
          label.setForeground(table.getForeground());
          label.setBackground(table.getBackground());
        }

        label.setFont(table.getFont());
      
          
        if (hasFocus) {
          Border border = null;
          if (isSelected) {
              border = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");
          }
          if (border == null) {
              border = UIManager.getBorder("Table.focusCellHighlightBorder");
          }
          label.setBorder(border);

            if (!isSelected && table.isCellEditable(row, col)) {
              Color color;
              color = UIManager.getColor("Table.focusCellForeground");
              if (color != null) {
                label.setForeground(color);
              }
              color = UIManager.getColor("Table.focusCellBackground");
              if (color != null) {
                label.setBackground(color);
              }
            }
        } else {
          label.setBorder(new EmptyBorder(1, 1, 1, 1));
        }
        
        switch(col){
          case FeatureTableModel.STATE_COLUMN:
            text        = " ";
            tooltipText = null;
            break;
            
          case FeatureTableModel.FEATUREID_COLUMN:
            text        = " "+((SimpleFeature)value).getID();
            tooltipText = ""+((SimpleFeature)value).getID();
            break;
            
          case FeatureTableModel.TYPE_COLUMN:
            text        = " "+((SimpleFeatureType)value).getName().getLocalPart();
            tooltipText = "" +((SimpleFeatureType)value).getName().getLocalPart();
            break;
            
          case FeatureTableModel.LAYER_COLUMN:
            text        = " "+((Layer)value).getTitle();
            tooltipText = "" +((Layer)value).getTitle();
            break;
        }
        
        if (text != null){
          label.setText(text);
        }
        
        if (tooltipText != null){
          label.setToolTipText(tooltipText);
        }         
    
        return label;
      }};
    
    
    for (int i = 0; i < getColumnModel().getColumnCount(); i++) {
      getColumnModel().getColumn(i).setCellRenderer(renderer);
    }

    initVisibleColumn();
      
  }
  
  protected void initSorter(TableRowSorter<TableModel> sorter){
	  
    sorter.setComparator(FeatureTableModel.STATE_COLUMN, new Comparator<Object>(){

    
      public int compare(Object o1, Object o2) {
        
        if ((o1 instanceof Integer) && (o1 instanceof Integer)){
          return ((Integer)o1).compareTo((Integer)o2);
        } else {
          return 0;
        }
        
      }});
    
    
    sorter.setComparator(FeatureTableModel.FEATUREID_COLUMN, new Comparator<Feature>(){

     
      public int compare(Feature o1, Feature o2) {
        
        if ((o1 instanceof Feature) && (o1 instanceof Feature)){
          
          if (((SimpleFeature)o1).getID() != null){
            return ((SimpleFeature)o1).getID().compareTo(((SimpleFeature)o2).getID());
          } else if (((SimpleFeature)o2).getID() == null){
            return 0;            
          } else {
            return ((SimpleFeature)o2).getID().compareTo(((SimpleFeature)o1).getID());
          }

        } else {
          return 0;
        }
        
      }});
    
    sorter.setComparator(FeatureTableModel.LAYER_COLUMN, new Comparator<Layer>(){


      public int compare(Layer o1, Layer o2) {

        if (o1.getTitle() != null){
          return o1.getTitle().compareTo(o2.getTitle());
        } else if (o2.getTitle() == null){
          return 0;            
        } else {
          return o2.getTitle().compareTo(o1.getTitle());
        }
      }});

    sorter.setComparator(FeatureTableModel.TYPE_COLUMN, new Comparator<FeatureType>(){

    
      public int compare(FeatureType o1, FeatureType o2) {
        
        if ((o1 instanceof FeatureType) && (o1 instanceof FeatureType)){
          
          if (((FeatureType)o1).getName().getLocalPart() != null){
            return ((FeatureType)o1).getName().getLocalPart().compareTo(((FeatureType)o2).getName().getLocalPart());
          } else if (((FeatureType)o2).getName().getLocalPart() == null){
            return 0;            
          } else {
            return ((FeatureType)o2).getName().getLocalPart().compareTo(((FeatureType)o1).getName().getLocalPart());
          }

        } else {
          return 0;
        }
        
      }});
    
  }
  
  /**
   * Init the visible columns
   *
   */
  protected void initVisibleColumn(){
    if ((this.visibleColumnFlags & STATE) == 0) {
      getColumnModel().getColumn(FeatureTableModel.STATE_COLUMN).setPreferredWidth(0);
      getColumnModel().getColumn(FeatureTableModel.STATE_COLUMN).setMinWidth(0);
      getColumnModel().getColumn(FeatureTableModel.STATE_COLUMN).setMaxWidth(0);
    } else {
      getColumnModel().getColumn(FeatureTableModel.STATE_COLUMN).setPreferredWidth(this.STATE_COLUMN_SIZE);
      getColumnModel().getColumn(FeatureTableModel.STATE_COLUMN).setWidth(this.STATE_COLUMN_SIZE);
      getColumnModel().getColumn(FeatureTableModel.STATE_COLUMN).setMinWidth(this.STATE_COLUMN_SIZE);
      getColumnModel().getColumn(FeatureTableModel.STATE_COLUMN).setMaxWidth(this.STATE_COLUMN_SIZE);
      getColumnModel().getColumn(FeatureTableModel.STATE_COLUMN).setResizable(false);
    }  
    
    if ((this.visibleColumnFlags & FEATUREID) == 0) {
      getColumnModel().getColumn(FeatureTableModel.FEATUREID_COLUMN).setPreferredWidth(0);
      getColumnModel().getColumn(FeatureTableModel.FEATUREID_COLUMN).setMinWidth(0);
      getColumnModel().getColumn(FeatureTableModel.FEATUREID_COLUMN).setMaxWidth(0);
    } else {
      getColumnModel().getColumn(FeatureTableModel.FEATUREID_COLUMN).setPreferredWidth(this.FEATUREID_COLUMN_SIZE);
      getColumnModel().getColumn(FeatureTableModel.FEATUREID_COLUMN).setWidth(this.FEATUREID_COLUMN_SIZE);
      getColumnModel().getColumn(FeatureTableModel.FEATUREID_COLUMN).setMinWidth(this.FEATUREID_COLUMN_SIZE);
      getColumnModel().getColumn(FeatureTableModel.FEATUREID_COLUMN).setMaxWidth(this.FEATUREID_COLUMN_SIZE);
      getColumnModel().getColumn(FeatureTableModel.FEATUREID_COLUMN).setResizable(false);
    }  
    
    if ((this.visibleColumnFlags & TYPE) == 0) {
      getColumnModel().getColumn(FeatureTableModel.TYPE_COLUMN).setPreferredWidth(0);
      getColumnModel().getColumn(FeatureTableModel.TYPE_COLUMN).setMinWidth(0);
      getColumnModel().getColumn(FeatureTableModel.TYPE_COLUMN).setMaxWidth(0);
    } else {
      getColumnModel().getColumn(FeatureTableModel.TYPE_COLUMN).setPreferredWidth(this.TYPE_COLUMN_SIZE);
      getColumnModel().getColumn(FeatureTableModel.TYPE_COLUMN).setWidth(this.TYPE_COLUMN_SIZE);
      getColumnModel().getColumn(FeatureTableModel.TYPE_COLUMN).setMinWidth(this.TYPE_COLUMN_SIZE);
      getColumnModel().getColumn(FeatureTableModel.TYPE_COLUMN).setMaxWidth(this.TYPE_COLUMN_SIZE);
      getColumnModel().getColumn(FeatureTableModel.TYPE_COLUMN).setResizable(false);
    }  
    
    if ((this.visibleColumnFlags & LAYER) == 0) {
      getColumnModel().getColumn(FeatureTableModel.LAYER_COLUMN).setPreferredWidth(0);
      getColumnModel().getColumn(FeatureTableModel.LAYER_COLUMN).setMinWidth(0);
      getColumnModel().getColumn(FeatureTableModel.LAYER_COLUMN).setMaxWidth(0);
    } else {
      getColumnModel().getColumn(FeatureTableModel.LAYER_COLUMN).setPreferredWidth(this.LAYER_COLUMN_SIZE);
      getColumnModel().getColumn(FeatureTableModel.LAYER_COLUMN).setWidth(this.LAYER_COLUMN_SIZE);
      getColumnModel().getColumn(FeatureTableModel.LAYER_COLUMN).setMinWidth(this.LAYER_COLUMN_SIZE);
      getColumnModel().getColumn(FeatureTableModel.LAYER_COLUMN).setMaxWidth(this.LAYER_COLUMN_SIZE);
      getColumnModel().getColumn(FeatureTableModel.LAYER_COLUMN).setResizable(false);
    }  
    
  }
  
//INITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINIT
//IN FIN INITIALIZATION                                                 IT
//INITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINIT

  
}
