package org.arpenteur.gis.geotools.swing;

import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.arpenteur.common.Common;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.FeatureType;

public class JFeatureTable extends JTable {

  /**
   * 
   */
  private static final long serialVersionUID = Common.BUILD;
  
  public static final int STATE = 1;
  public static final int FEATUREID = 2;
  public static final int TYPE = 4;
  public static final int LAYER = 8;
  public final int STATE_COLUMN = 0;
  public final int FEATUREID_COLUMN = 1;
  public final int TYPE_COLUMN = 2;
  public final int LAYER_COLUMN = 3;
  private final int STATE_COLUMN_SIZE = 20;
  private final int FEATUREID_COLUMN_SIZE = 80;
  private final int TYPE_COLUMN_SIZE = 100;
  private final int LAYER_COLUMN_SIZE = 100;
  public static final int ALL = STATE | FEATUREID | TYPE | LAYER;
  private int visibleColumnFlags = ALL;
  private final String[] names = { "state", "featureid", "type", "layer" };
  
  private MapContent data = null;

  // CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  // CC CONSTRUCTEUR CC
  // CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  /**
   * Create a new default feature table
   */
  public JFeatureTable() {
    this(null, ALL);
  }

  /**
   * Create a new feature table displaying features of all the layer of the
   * given map context.
   * 
   * @param context the context to display.
   */
  public JFeatureTable(MapContent context) {
    this(context, ALL);
  }

  /**
   * Create a new feature table displaying features of all the layers of the
   * given context. The visible columns are given by the
   * <code>visibleColumnFlags</code> parameter.
   * 
   * @param context the context to display.
   * @param visibleColumnFlags the visible columns.
   */
  public JFeatureTable(MapContent context, int visibleColumnFlags) {
    super();
    this.visibleColumnFlags = visibleColumnFlags;
    if (context != null) {
      setData(context);
    }
  }

  // CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  // CC FIN CONSTRUCTEUR CC
  // CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  // RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
  // RR REDEFINITIONS RR
  // RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
  // >>>>>>>>>>>> JTable <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
  @Override
  public boolean isCellEditable(int row, int column) {
    return false;
  }

  // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
  // RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
  // RR FIN REDEFINITIONS RR
  // RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
  // AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  // AA ACCESSEUR AA
  // AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  /**
   * Set the data to display in the table
   * 
   * @param context the data to display as a map context.
   * @throws IOException
   */
  public void setData(MapContent context) {
    Object[][] dataArray = null;
    int featureCount = 0;
    List<Layer> layers = null;
    Layer layer = null;
    TableSorter sorter = null;
    DefaultTableModel model = null;
    FeatureCollection features = null;
    FeatureIterator iter = null;
    Feature feature = null;
    this.data = context;
    // Initialisation des donnees
    if (data != null) {
      // Recuperation des layers
      layers = data.layers();
      if (layers != null) {
        // Comptage du nombre de feature dans le contexte
        // (somme du nombre total de features dans chaque layer)
        for (int i = 0; i < layers.size(); i++) {
          layer = layers.get(i);
          try {
            featureCount += layer.getFeatureSource().getFeatures().size();
          } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
          }
        }
        layer = null;
        // Initialisation du tableau contenant toutes les feature
        dataArray = new Object[featureCount][names.length];
        // Parcours de tous les layers du contexte pour construire le tableau
        // general des features
        featureCount = 0;
        for (int i = 0; i < layers.size(); i++) {
          layer = layers.get(i);
          try {
            features = layer.getFeatureSource().getFeatures();
            iter = features.features();
            while (iter.hasNext()) {
              feature = iter.next();
              dataArray[featureCount][STATE_COLUMN] = new Integer(0);
              dataArray[featureCount][FEATUREID_COLUMN] = feature;
              dataArray[featureCount][TYPE_COLUMN] = feature.getType();
              dataArray[featureCount][LAYER_COLUMN] = layer;
              feature = null;
              featureCount++;
            }
            iter = null;
            features = null;
          } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
          }
        }
        layer = null;
        layers = null;
      }
    }
    // Initialisation des sorter
    // Attachement du modèle à la table.
    model = new DefaultTableModel(dataArray, names) {

      /*
       * JTable uses this method to determine the default renderer/ editor for
       * each cell. If we didn't implement this method, then the last column
       * would contain text ("true"/"false"), rather than a check box.
       */
      // Cette fonction doit retourner itemmesurable comme classe pour la
      // colonne de l'item
      // car sinon le comparateur spécifique ne peut être trouvé. Le sorter
      // utilise le nom
      // de la classe pour trouver un comparateur et pas l'appartenance à la
      // classe (heritage)
      @Override
      public Class getColumnClass(int c) {
        if (dataModel.getValueAt(0, c) != null) {
          return dataModel.getValueAt(0, c).getClass();
        } else {
          return Object.class;
        }
      }
    };
    sorter = new TableSorter(model);
    this.setModel(sorter);
    sorter.setTableHeader(getTableHeader());
    initModel();
    initSorter(sorter);
  }

  /**
   * Get the data associated to this table.
   * 
   * @return the data associated to this table
   */
  public MapContent getData() {
    return data;
  }

  /**
   * Get the feature displayed at the row given by <code>rowIndex</code> in the
   * table. This method take in account the order of the table.
   * 
   * @param rowIndex the row index of the feature to get.
   * @return the desired feature.
   */
  public SimpleFeature getFeatureAtRow(int rowIndex) {
    return (SimpleFeature) ((TableSorter) getModel()).getValueAt(
        ((TableSorter) getModel()).modelIndex(rowIndex), FEATUREID_COLUMN);
  }

  // AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  // AA FIN ACCESSEUR AA
  // AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  // INITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINIT
  // IN INITIALIZATION IT
  // INITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINIT
  /**
   * Init the table model
   */
  protected void initModel() {
    dataModel = this.getModel();
    if (dataModel == null) {
      return;
    }
    TableCellRenderer renderer = new TableCellRenderer() {

      @Override
      public Component getTableCellRendererComponent(JTable table,
          Object value, boolean isSelecteded, boolean hasFocus, int row, int col) {
        // Texte du label
        String text = null;
        // Texte du tooltip
        String tooltipText = null;
        Feature feature = getFeatureAtRow(row);
        Class cclass = null;
        // La premiere colonne contient les types
        JLabel label = new JLabel();
        // Impose l'affichage du background du label
        label.setOpaque(true);
        Integer integer = 0;
        if (isSelecteded) {
          label.setForeground(table.getSelectionForeground());
          label.setBackground(table.getSelectionBackground());
        } else {
          label.setForeground(table.getForeground());
          label.setBackground(table.getBackground());
        }
        label.setFont(table.getFont());
        if (hasFocus) {
          Border border = null;
          if (isSelecteded) {
            border = UIManager
                .getBorder("Table.focusSelectedCellHighlightBorder");
          }
          if (border == null) {
            border = UIManager.getBorder("Table.focusCellHighlightBorder");
          }
          label.setBorder(border);
          if (!isSelecteded && table.isCellEditable(row, col)) {
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
        switch (col) {
        case STATE_COLUMN:
          text = " ";
          tooltipText = null;
          break;
        case FEATUREID_COLUMN:
          text = " " + ((SimpleFeature) value).getID();
          tooltipText = "" + ((SimpleFeature) value).getID();
          break;
        case TYPE_COLUMN:
          text = " " + ((SimpleFeatureType) value).getName().getLocalPart();
          tooltipText = ""
              + ((SimpleFeatureType) value).getName().getLocalPart();
          break;
        case LAYER_COLUMN:
          text = " " + ((Layer) value).getTitle();
          tooltipText = "" + ((Layer) value).getTitle();
          break;
        }
        if (text != null) {
          label.setText(text);
        }
        if (tooltipText != null) {
          label.setToolTipText(tooltipText);
        }
        return label;
      }
    };
    for (int i = 0; i < getColumnModel().getColumnCount(); i++) {
      getColumnModel().getColumn(i).setCellRenderer(renderer);
    }
    initVisibleColumn();
  }

  protected void initSorter(TableSorter sorter) {
    sorter.setColumnComparator(Integer.class, new Comparator() {

      @Override
      public int compare(Object o1, Object o2) {
        if ((o1 instanceof Integer) && (o1 instanceof Integer)) {
          return ((Integer) o1).compareTo((Integer) o2);
        } else {
          return 0;
        }
      }
    });
    sorter.setColumnComparator(Feature.class, new Comparator() {

      @Override
      public int compare(Object o1, Object o2) {
        if ((o1 instanceof Feature) && (o1 instanceof Feature)) {
          if (((SimpleFeature) o1).getID() != null) {
            return ((SimpleFeature) o1).getID().compareTo(
                ((SimpleFeature) o2).getID());
          } else if (((SimpleFeature) o2).getID() == null) {
            return 0;
          } else {
            return ((SimpleFeature) o2).getID().compareTo(
                ((SimpleFeature) o1).getID());
          }
        } else {
          return 0;
        }
      }
    });
    sorter.setColumnComparator(Layer.class, new Comparator() {

      @Override
      public int compare(Object o1, Object o2) {
        if ((o1 instanceof Layer) && (o1 instanceof Layer)) {
          if (((Layer) o1).getTitle() != null) {
            return ((Layer) o1).getTitle().compareTo(
                ((Layer) o2).getTitle());
          } else if (((Layer) o2).getTitle() == null) {
            return 0;
          } else {
            return ((Layer) o2).getTitle().compareTo(
                ((Layer) o1).getTitle());
          }
        } else {
          return 0;
        }
      }
    });
    sorter.setColumnComparator(FeatureType.class, new Comparator() {

      @Override
      public int compare(Object o1, Object o2) {
        if ((o1 instanceof FeatureType) && (o1 instanceof FeatureType)) {
          if (((FeatureType) o1).getName().getLocalPart() != null) {
            return ((FeatureType) o1).getName().getLocalPart().compareTo(
                ((FeatureType) o2).getName().getLocalPart());
          } else if (((FeatureType) o2).getName().getLocalPart() == null) {
            return 0;
          } else {
            return ((FeatureType) o2).getName().getLocalPart().compareTo(
                ((FeatureType) o1).getName().getLocalPart());
          }
        } else {
          return 0;
        }
      }
    });
  }

  /**
   * Init the visible columns
   * 
   */
  protected void initVisibleColumn() {
    if ((visibleColumnFlags & STATE) == 0) {
      getColumnModel().getColumn(STATE_COLUMN).setPreferredWidth(0);
      getColumnModel().getColumn(STATE_COLUMN).setMinWidth(0);
      getColumnModel().getColumn(STATE_COLUMN).setMaxWidth(0);
    } else {
      getColumnModel().getColumn(STATE_COLUMN).setPreferredWidth(
          STATE_COLUMN_SIZE);
      getColumnModel().getColumn(STATE_COLUMN).setWidth(STATE_COLUMN_SIZE);
      getColumnModel().getColumn(STATE_COLUMN).setMinWidth(STATE_COLUMN_SIZE);
      getColumnModel().getColumn(STATE_COLUMN).setMaxWidth(STATE_COLUMN_SIZE);
      getColumnModel().getColumn(STATE_COLUMN).setResizable(false);
    }
    if ((visibleColumnFlags & FEATUREID) == 0) {
      getColumnModel().getColumn(FEATUREID_COLUMN).setPreferredWidth(0);
      getColumnModel().getColumn(FEATUREID_COLUMN).setMinWidth(0);
      getColumnModel().getColumn(FEATUREID_COLUMN).setMaxWidth(0);
    } else {
      getColumnModel().getColumn(FEATUREID_COLUMN).setPreferredWidth(
          FEATUREID_COLUMN_SIZE);
      getColumnModel().getColumn(FEATUREID_COLUMN).setWidth(
          FEATUREID_COLUMN_SIZE);
      getColumnModel().getColumn(FEATUREID_COLUMN).setMinWidth(
          FEATUREID_COLUMN_SIZE);
      getColumnModel().getColumn(FEATUREID_COLUMN).setMaxWidth(
          FEATUREID_COLUMN_SIZE);
      getColumnModel().getColumn(FEATUREID_COLUMN).setResizable(false);
    }
    if ((visibleColumnFlags & TYPE) == 0) {
      getColumnModel().getColumn(TYPE_COLUMN).setPreferredWidth(0);
      getColumnModel().getColumn(TYPE_COLUMN).setMinWidth(0);
      getColumnModel().getColumn(TYPE_COLUMN).setMaxWidth(0);
    } else {
      getColumnModel().getColumn(TYPE_COLUMN).setPreferredWidth(
          TYPE_COLUMN_SIZE);
      getColumnModel().getColumn(TYPE_COLUMN).setWidth(TYPE_COLUMN_SIZE);
      getColumnModel().getColumn(TYPE_COLUMN).setMinWidth(TYPE_COLUMN_SIZE);
      getColumnModel().getColumn(TYPE_COLUMN).setMaxWidth(TYPE_COLUMN_SIZE);
      getColumnModel().getColumn(TYPE_COLUMN).setResizable(false);
    }
    if ((visibleColumnFlags & LAYER) == 0) {
      getColumnModel().getColumn(LAYER_COLUMN).setPreferredWidth(0);
      getColumnModel().getColumn(LAYER_COLUMN).setMinWidth(0);
      getColumnModel().getColumn(LAYER_COLUMN).setMaxWidth(0);
    } else {
      getColumnModel().getColumn(LAYER_COLUMN).setPreferredWidth(
          LAYER_COLUMN_SIZE);
      getColumnModel().getColumn(LAYER_COLUMN).setWidth(LAYER_COLUMN_SIZE);
      getColumnModel().getColumn(LAYER_COLUMN).setMinWidth(LAYER_COLUMN_SIZE);
      getColumnModel().getColumn(LAYER_COLUMN).setMaxWidth(LAYER_COLUMN_SIZE);
      getColumnModel().getColumn(LAYER_COLUMN).setResizable(false);
    }
  }
  // INITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINIT
  // IN FIN INITIALIZATION IT
  // INITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINIT
}
