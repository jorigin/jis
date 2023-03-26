package org.jorigin.jis.swing;

import java.awt.Color;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.CitationDate;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CompoundCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.DerivedCRS;
import org.opengis.referencing.crs.EngineeringCRS;
import org.opengis.referencing.crs.GeocentricCRS;
import org.opengis.referencing.crs.GeodeticCRS;
import org.opengis.referencing.crs.GeographicCRS;
import org.opengis.referencing.crs.ImageCRS;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.crs.SingleCRS;
import org.opengis.referencing.crs.TemporalCRS;
import org.opengis.referencing.crs.VerticalCRS;
import org.opengis.referencing.cs.CoordinateSystem;
import org.opengis.referencing.datum.Datum;
import org.opengis.util.GenericName;
import org.opengis.util.InternationalString;

public class JCRSTable extends JTable{

  private static final long serialVersionUID = 1L;
  
  /** a flag that indicate the Coordinates Reference System (CRS) match. */
  public static final int MATCH       = 1;
  
  /** a flag that indicate the Coordinates Reference System (CRS) authority. */
  public static final int AUTH        = 2;
  
  /** a flag that indicate the Coordinates Reference System (CRS) code. */
  public static final int CODE        = 4;
  
  /** a flag that indicate the Coordinates Reference System (CRS) name. */
  public static final int NAME        = 8;
  
  /** a flag that indicate the Coordinates Reference System (CRS) coordinate system (CS). */
  public static final int CS          = 16;
  
  /** a flag that indicate the Coordinates Reference System (CRS) datum. */
  public static final int DATUM       = 32;
  
  /** The index of the column that display the Coordinates Reference System (CRS) match. */
  public final int MATCH_COLUMN       = 0;
  
  /** The index of the column that display the Coordinates Reference System (CRS) authority. */
  public final int AUTH_COLUMN        = 1;
  
  /** The index of the  column that display the Coordinates Reference System (CRS) code. */
  public final int CODE_COLUMN        = 2;
  
  /** The index of the  column that display the Coordinates Reference System (CRS) name. */
  public final int NAME_COLUMN        = 3;
  
  /** The index of the  column that display the Coordinates Reference System (CRS) coordinate system (CS). */
  public final int CS_COLUMN          = 4;
  
  /** The index of the  column that display the Coordinates Reference System (CRS) datum. */
  public final int DATUM_COLUMN       = 5;

  /** The default size of the column that display the Coordinates Reference System (CRS) match. */
  private final int MATCH_COLUMN_SIZE       = 50;
  
  /** The default size of the column that display the Coordinates Reference System (CRS) authority. */
  private final int AUTH_COLUMN_SIZE        = 200;
  
  /** The default size of the  column that display the Coordinates Reference System (CRS) code. */
  private final int CODE_COLUMN_SIZE        = 80;
  
  /** The default size of the  column that display the Coordinates Reference System (CRS) name. */
  private final int NAME_COLUMN_SIZE        = 150;
  
  /** The default size of the  column that display the Coordinates Reference System (CRS) coordinate system (CS). */
  private final int CS_COLUMN_SIZE          = 100;
  
  /** The default size of the  column that display the Coordinates Reference System (CRS) datum. */
  private final int DATUM_COLUMN_SIZE       = 100;
  
  /** The flag that include all the Coordinates Reference System (CRS) columns. */
  public static final int ALL =  MATCH| AUTH | CODE | NAME | CS | DATUM;
  
  private int visibleColumnFlags = ALL;
  
  private final String[] names = {"Match", "Auth.", "Code", "Name", "CS", "Datum"};
  
  private List<CoordinateReferenceSystem> data = null;
  
  private DefaultTableModel model              = null;
  
  private TableCellRenderer renderer           = null;
  
  private TableRowSorter<?> sorter             = null;
  
  /**
   * Create a new default {@link org.opengis.referencing.crs.CoordinateReferenceSystem Coordinate Reference System (CRS)} 
   * table. by default, all the columns are visible.
   * @see #JCRSTable(List)
   * @see #JCRSTable(List, int)
   */
  public JCRSTable() {
    super();

    //  Attachement du modèle à la table.
    this.model = new DefaultTableModel(this.names, 0){
      /**
       * 
       */
      private static final long serialVersionUID = 1L;

      /*
       * JTable uses this method to determine the default renderer/
       * editor for each cell.  If we didn't implement this method,
       * then the last column would contain text ("true"/"false"),
       * rather than a check box.
       */
      // Cette fonction doit retourner itemmesurable comme classe pour la colonne de l'item
      // car sinon le comparateur spécifique ne peut être trouvé. Le sorter utilise le nom
      // de la classe pour trouver un comparateur et pas l'appartenance à la classe (heritage)
      @Override
      public Class<?> getColumnClass(int c) {
        switch(c){
          case MATCH_COLUMN:
            return Double.class;
          case AUTH_COLUMN:
            return Citation.class;
          case CODE_COLUMN:
            return String.class;
          case NAME_COLUMN:
            return ReferenceIdentifier.class;
          case CS_COLUMN:
            return CoordinateSystem.class;
          case DATUM_COLUMN:
            return Datum.class;
          default:
            return Object.class;
        }
      }
      
    };    
    
    this.renderer = new TableCellRenderer() {

      @Override
      public Component getTableCellRendererComponent(JTable table,
          Object value, boolean isSelecteded, boolean hasFocus, int row, int col) {

        // Texte du label
        String text = null;

        // Texte du tooltip
        String tooltipText = null;
        
        //CoordinateReferenceSystem item = getValueAtRow(row);
        
        // La premiere colonne contient les types
        JLabel label = new JLabel();
        
        // Impose l'affichage du background du label
        label.setOpaque(true);
        
        
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
              border = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");
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

        case MATCH_COLUMN:
          text        = " "+((Double)value).intValue();
          tooltipText = ""+value;
          break;

        
        // Colonne de l'identifiant de l'item
        case AUTH_COLUMN:
          
          if ((value != null)&&(value instanceof Citation)){
            
            Citation citation = (Citation)value;
            
            text = ""+citation.getTitle();
            
            Collection<? extends InternationalString> c = citation.getAlternateTitles();
            if ((c != null)&&(c.size() > 0)){
              Iterator<? extends InternationalString> iter = c.iterator();
              tooltipText = "<html><head></head><body>";
              tooltipText +="<b>Aliases:&nbsp;</b>"+iter.next().toString();
              while(iter.hasNext()){
                tooltipText +=", "+iter.next().toString();
              }
              tooltipText +="</br>";
              tooltipText += "</body></html>";
            } else {
              tooltipText = "No additional information.";
            }

            tooltipText = ""+value;
          } else {
            text = "";
          }
          break;
        
        // Colonne de l'identifiant de l'item
        case CODE_COLUMN:

          if ((value != null)){
            text = value.toString();
          } else {
            text = "No identifier";
          }
          
          tooltipText = ""+value;
          break;
          
        // Colonne de la classe de l'item (si non null);
        case NAME_COLUMN:
          
          if ((value != null)&&(value instanceof ReferenceIdentifier)){
            ReferenceIdentifier identifier = (ReferenceIdentifier) value;
            text = identifier.toString();
            
            CoordinateReferenceSystem crs = getValueAtRow(row);
            
            Collection<GenericName> c = crs.getAlias();
            if ((c != null)&&(c.size() > 0)){
              Iterator<GenericName> iter = c.iterator();
              tooltipText = "<html><head></head><body>";
              tooltipText +="<b>Aliases:&nbsp;</b>"+iter.next().toString();
              while(iter.hasNext()){
                tooltipText +=", "+iter.next().toString();
              }
              tooltipText +="</br>";
              tooltipText += "</body></html>";
            } else {
              tooltipText = "No additional information.";
            }
          }

          break;

        // Colonne de l'identifiant de l'item
        case CS_COLUMN:
  
          CoordinateReferenceSystem crs = getValueAtRow(row);
          
          if (crs instanceof CompoundCRS){
            text = "Compound";
          } else if (crs instanceof ProjectedCRS){
            text = "Projected";
          } else if (crs instanceof EngineeringCRS){
            text = "Engineering";
          } else if (crs instanceof GeocentricCRS){
            text = "Geocentric";
          } else if (crs instanceof GeographicCRS){
            text = "Geographic";
          } else if (crs instanceof GeodeticCRS){
            text = "Geodetic";
          } else if (crs instanceof ImageCRS){
            text = "Image";
          } else if (crs instanceof DerivedCRS){
            text = "Derived";
          } else if (crs instanceof TemporalCRS){
            text = "Temporal";
          } else if (crs instanceof VerticalCRS){
            text = "Vertical";
          } else if (crs instanceof SingleCRS){
            text = "Single";
          } else {
            text = "Unknown ("+crs.getCoordinateSystem().getClass().getName()+")";
          }
          break;
             
        case DATUM_COLUMN:
          
          Datum datum = (Datum)value;
 
          if (datum != null){
            if (datum.getName() != null){
              text = datum.getName().getCode();
              
              tooltipText = "<html><head></head><body>";
              
              if (datum.getName().getCode() != null){
                tooltipText += "<b>Code:&nbsp;</b>"+datum.getName().getCode()+"<br/>";
              }
              
              if (datum.getName().getCodeSpace() != null){
                tooltipText += "<b>Code Space:&nbsp;</b>"+datum.getName().getCodeSpace()+"<br/>";
              }
              
              if (datum.getName().getVersion() != null){
                tooltipText += "<b>Version:&nbsp;</b>"+datum.getName().getVersion()+"<br/>";
              }
              
              Collection<GenericName> c2 = datum.getAlias();
              if ((c2 != null) && (c2.size() > 0)){
                Iterator<GenericName> nameIter = c2.iterator();
                tooltipText +="<b>Aliases:&nbsp;</b>"+nameIter.next().toString();
                while(nameIter.hasNext()){
                  tooltipText +=", "+nameIter.next().tip();
                }
                tooltipText += "<br/><br/>";
              }
              
              if (datum.getName().getAuthority() != null){
                Citation citation = datum.getName().getAuthority();
                
                tooltipText += "<b>Authority&nbsp;:</b>"+citation.getTitle()+"<br/>";
  
                Collection<? extends InternationalString> altTitles = citation.getAlternateTitles();
                if ((altTitles != null)&&(altTitles.size() > 0)){
                  Iterator<? extends InternationalString> iter = altTitles.iterator();
                  tooltipText += "&nbsp;&nbsp;<b>Alternate titles:&nbsp;</b>"+iter.next();
                  while(iter.hasNext()){
                    tooltipText += ", "+iter.next();
                  }
                  tooltipText += "<br/>";
                }
                
                Collection<? extends ResponsibleParty> respParties = citation.getCitedResponsibleParties();
                ResponsibleParty respParty = null;
                if ((respParties != null)&&(respParties.size() >0)){
                  
                  Iterator<? extends ResponsibleParty> iter = respParties.iterator();
                  respParty = iter.next();
                  tooltipText += "&nbsp;&nbsp;<b>Responsible parties:&nbsp;</b>";
                  
                  if (respParty.getIndividualName() != null){
                    tooltipText += respParty.getIndividualName()+"(<i>"+respParty.getOrganisationName()+"</i>)";
                  } else {
                    if (respParty.getOrganisationName() != null){
                      tooltipText += "<i>"+respParty.getOrganisationName()+"</i>";
                    }
                  }
                  
                  while(iter.hasNext()){
                    respParty = iter.next();
                    tooltipText += ", "+respParty.getIndividualName()+"("+respParty.getOrganisationName()+")";
                  }
                
                  tooltipText += "<br/>";
                }
                
                if (citation.getCollectiveTitle() != null){
                  tooltipText += "&nbsp;&nbsp;<b>Collective title:&nbsp;</b>"+citation.getCollectiveTitle();
                }
                
                Collection<? extends CitationDate> dates = citation.getDates();
                if ((dates != null)&&(dates.size() > 0)){
                  SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                  Iterator<? extends CitationDate> iter = dates.iterator();
                  tooltipText += "&nbsp;&nbsp;<b>Dates:&nbsp;</b>"+sdf.format(iter.next().getDate());
                  while(iter.hasNext()){
                    tooltipText += ", "+sdf.format(iter.next().getDate());
                  }
                  tooltipText += "<br/>";
                }
                
                if (citation.getEdition() != null){
                  tooltipText += "&nbsp;&nbsp;<b>Edition:&nbsp;</b>"+citation.getEdition()+"<br/>";
                }
                
                if (citation.getEditionDate() != null){
                  tooltipText += "&nbsp;&nbsp;<b>Edition date:&nbsp;</b>"+citation.getEditionDate()+"<br/>";
                }
                
                if (citation.getISBN() != null){
                  tooltipText += "&nbsp;&nbsp;<b>ISBN:&nbsp;</b>"+citation.getISBN()+"<br/>";
                }
                
                if (citation.getISSN() != null){
                  tooltipText += "&nbsp;&nbsp;<b>ISSN:&nbsp;</b>"+citation.getISSN()+"<br/>";
                }
              }

              tooltipText += "</body></html>";
              
            } else {
              text = ""+datum.hashCode();
              tooltipText = "No additional information.";
            }
 
          } else {
            text = "No datum available";
            tooltipText = "No additional information.";
          }
          
          break;
        }
        
        if (text != null){
          label.setText(text);
        }
        
        if (tooltipText != null){
          label.setToolTipText(tooltipText);
        }       
        
        return label;
      }
    };
    
    

    this.sorter = new TableRowSorter<TableModel>(this.model){
      @Override
      public Comparator<?> getComparator(int column) {
        return super.getComparator(column);
    }
    };
    
    this.sorter.setComparator(this.MATCH_COLUMN, new Comparator<Double>(){

      @Override
      public int compare(Double o1, Double o2) {
        if (o1 == null){
          if (o2 == null){
            return 0;
          } else {
            return -1;
          }
        } else {
          if (o2 == null){
            return 1;
          } else {
            return o1.compareTo(o2);
          }
        }
      }});
    
    this.sorter.setComparator(this.AUTH_COLUMN, new Comparator<Citation>(){

      @Override
      public int compare(Citation o1, Citation o2) {
        if (o1 == null){
          if (o2 == null){
            return 0;
          } else {
            return -1;
          }
        } else {
          if (o2 == null){
            return 1;
          } else {
            if (o1.getTitle() != null){
              return o1.getTitle().compareTo(o2.getTitle());
            } else {
              if (o2.getTitle() != null){
                return -1;
              } else {
                return 0;
              }
            }
          }
        }
      }});
    
    this.sorter.setComparator(this.CODE_COLUMN, new Comparator<String>(){

      @Override
      public int compare(String o1, String o2) {
        if (o1 == null){
          if (o2 == null){
            return 0;
          } else {
            return -1;
          }
        } else {
          if (o2 == null){
            return 1;
          } else {
            return o1.compareTo(o2);
          }
        }
      }});
    
    this.sorter.setComparator(this.NAME_COLUMN, new Comparator<ReferenceIdentifier>(){

      @Override
      public int compare(ReferenceIdentifier o1, ReferenceIdentifier o2) {
        if (o1 == null){
          if (o2 == null){
            return 0;
          } else {
            return -1;
          }
        } else {
          if (o2 == null){
            return 1;
          } else {
            if (o1.toString() != null){
              return o1.toString().compareTo(o2.toString());
            } else {
              if (o2.toString() != null){
                return -1;
              } else {
                return 0;
              }
            }
          }
        }
      }});
    
    this.sorter.setComparator(this.CS_COLUMN, new Comparator<CoordinateSystem>(){

      @Override
      public int compare(CoordinateSystem o1, CoordinateSystem o2) {
        if (o1 == null){
          if (o2 == null){
            return 0;
          } else {
            return -1;
          }
        } else {
          if (o2 == null){
            return 1;
          } else {
            if (o1.getName() != null){
              return o1.getName().getCode().compareTo(o2.getName().getCode());
            } else {
              if (o2.getName().getCode() != null){
                return -1;
              } else {
                return 0;
              }
            }
          }
        }
      }});
    
    this.sorter.setComparator(this.DATUM_COLUMN, new Comparator<Datum>(){

      @Override
      public int compare(Datum o1, Datum o2) {
        if (o1 == null){
          if (o2 == null){
            return 0;
          } else {
            return -1;
          }
        } else {
          if (o2 == null){
            return 1;
          } else {
            if (o1.getName() != null){
              return o1.getName().toString().compareTo(o2.getName().toString());
            } else {
              if (o2.getName().toString() != null){
                return -1;
              } else {
                return 0;
              }
            }
          }
        }
      }});
    
    List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
    sortKeys.add(new RowSorter.SortKey(4, SortOrder.ASCENDING));
    sortKeys.add(new RowSorter.SortKey(3, SortOrder.ASCENDING));
    sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
    sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
    sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
    this.sorter.setSortKeys(sortKeys); 
    
    super.setModel(this.model);
    super.setRowSorter(this.sorter);

    for (int i = 0; i < getColumnModel().getColumnCount(); i++) {
      super.getColumnModel().getColumn(i).setCellRenderer(this.renderer);
    }
     
    initVisibleColumn();
  }

  /**
   * Create a {@link org.opengis.referencing.crs.CoordinateReferenceSystem Coordinate Reference System (CRS)} 
   * table that display the CRS contained
   * in the {@link java.util.List list} given in parameter. By default, all the columns are shown.
   * @param data the list of CRS to display within the table.
   * @see #JCRSTable()
   * @see #JCRSTable(List, int)
   */
  public JCRSTable(List<CoordinateReferenceSystem> data){
    this(data, ALL);  
  }
    
  /**
   * Create a {@link org.opengis.referencing.crs.CoordinateReferenceSystem Coordinate Reference System (CRS)} 
   * table that display the CRS contained
   * in the {@link java.util.List list} given in parameter. The parameter <code>visibleColumnFlags</code>
   * list the columns to show.
   * @param data the list of CRS to display within the table.
   * @param visibleColumnFlags the column to set visible.
   * @see #JCRSTable()
   * @see #JCRSTable(List)
   */
  public JCRSTable(List<CoordinateReferenceSystem> data, int visibleColumnFlags){
    this();
    
    if (data == null){
      return;
    }
    
    this.data = data;
    
    this.visibleColumnFlags = visibleColumnFlags;
    
    setData(data);
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
  
  @Override
  public void setModel(TableModel model){
    
  }
  //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
//RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR
//RR FIN REDEFINITIONS                                                   RR
//RRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR

  
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA ACCESSEUR                                                           AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  
  /**
   * Set the {@link java.util.List list} of 
   * {@link org.opengis.referencing.crs.CoordinateReferenceSystem Coordinate Reference System (CRS)} 
   * to display in the table. 
   * @param the list of CRS to display to display.
   * @see #getData()
   */
  public void setData(List<CoordinateReferenceSystem> data){
    Object[][] dataArray = null;

    Iterator<CoordinateReferenceSystem> itemIter = null;
    CoordinateReferenceSystem item = null;
    int i = 0;  
    
    
    if (data == null){
      return;
    }
    
    this.data = data;
    
    dataArray = new Object[data.size()][this.names.length];
    
    itemIter = data.iterator();
    i = 0;
    while(itemIter.hasNext()){
      item = itemIter.next();
      
      dataArray[i][this.MATCH_COLUMN] = new Double(100.0d);
      
      if (item.getName() != null){
        dataArray[i][this.AUTH_COLUMN]  = item.getName().getAuthority();
        dataArray[i][this.CODE_COLUMN]  = item.getIdentifiers().iterator().next().getCodeSpace()+":"+item.getIdentifiers().iterator().next().getCode();
      } else {
        dataArray[i][this.AUTH_COLUMN] = null;
        dataArray[i][this.CODE_COLUMN] = null;
      }
      
      dataArray[i][this.NAME_COLUMN]  = item.getName();

      dataArray[i][this.CS_COLUMN]    = item.getCoordinateSystem();
       
      if (item instanceof SingleCRS){
        dataArray[i][this.DATUM_COLUMN] = ((SingleCRS)item).getDatum();
      } else {
        dataArray[i][this.DATUM_COLUMN] = null;
      }

      i++;
    }

    // Sauvegarde des sorter et renderer car ils sont effaces par le setDataVector
    TableRowSorter<? extends TableModel> sorter   = (TableRowSorter<? extends TableModel>) getRowSorter();
    
    Comparator<?>[] comparators = new Comparator[getColumnCount()];
    for(i = 0; i < comparators.length; i++){
      comparators[i] = sorter.getComparator(i);
    }
    
    TableCellRenderer[] renderers = new TableCellRenderer[getColumnModel().getColumnCount()];
    for (i = 0; i < getColumnModel().getColumnCount(); i++) {
      renderers[i] = super.getColumnModel().getColumn(i).getCellRenderer();
    }
    
    ((DefaultTableModel)getModel()).setDataVector(dataArray, this.names);    
    
    super.setRowSorter(sorter);
    for (i = 0; i < getColumnModel().getColumnCount(); i++) {
      super.getColumnModel().getColumn(i).setCellRenderer(renderers[i]);
      ((TableRowSorter<?>)super.getRowSorter()).setComparator(i, comparators[i]);
    }
    
    initVisibleColumn();
  }
  
  /**
   * Return the data displayed in  the table as a {@link java.util.List list} of 
   * {@link org.opengis.referencing.crs.CoordinateReferenceSystem Coordinate Reference System (CRS)} 
   * @return the list of CRS displayed within the table.
   * @see #setData(List)
   */
  public List<CoordinateReferenceSystem> getData(){
    return this.data;
  }
  
  /**
   * Set the visible columns for this table.
   * @param visibleColumns the visible columns
   */
  public void setVisibleColumn(int visibleColumns){
    this.visibleColumnFlags = visibleColumns;
  }
  
  /**
   * Get the visible columns for this table.
   * @return the visible columns
   */
  public int getVisibleColumn(){
    return this.visibleColumnFlags;
  }
  
  /**
   * Return the column index of the flag given in parameter.
   * @param flag the flag of the column
   * @return the column index corresponding to the flag.
   */
  public int getColumnIndex(int flag){
    int index = -1;
    
    if (flag == MATCH){
      index = this.MATCH_COLUMN;
    } else if (flag == AUTH){
      index = this.AUTH_COLUMN;
    } else if (flag == CODE){
      index = this.CODE_COLUMN;
    }else if (flag == NAME){
      index = this.NAME_COLUMN;
    }else if (flag == CS){
      index = this.CS_COLUMN;
    } else if (flag == DATUM){
      index = this.DATUM_COLUMN;
    } 
    
    return index;
  }
  
  /**
   * Get the item mesurable at the desired index. If the index does 
   * not match an item mesurable, <code>null</code> is returned
   * @param rowIndex the index of the item mesurable
   * @return the item mesurable at the index, or <code>null</code>
   */
  public CoordinateReferenceSystem getValueAtRow(int rowIndex){
    return this.data.get(getRowSorter().convertRowIndexToModel(rowIndex));
  }
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA FIN ACCESSEUR                                                       AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

  
//INITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINIT
//IN INITIALIZATION                                                     IT
//INITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINIT

  /**
   * Init the visible columns
   *
   */
  public void initVisibleColumn(){
    

    if ((this.visibleColumnFlags & MATCH) == 0) {
      getColumnModel().getColumn(this.MATCH_COLUMN).setPreferredWidth(0);
      getColumnModel().getColumn(this.MATCH_COLUMN).setMinWidth(0);
      getColumnModel().getColumn(this.MATCH_COLUMN).setMaxWidth(0);
    } else {
      getColumnModel().getColumn(this.MATCH_COLUMN).setPreferredWidth(this.MATCH_COLUMN_SIZE);
      getColumnModel().getColumn(this.MATCH_COLUMN).setWidth(this.MATCH_COLUMN_SIZE);
      getColumnModel().getColumn(this.MATCH_COLUMN).setMinWidth(this.MATCH_COLUMN_SIZE);
      getColumnModel().getColumn(this.MATCH_COLUMN).setMaxWidth(this.MATCH_COLUMN_SIZE);
      getColumnModel().getColumn(this.MATCH_COLUMN).setResizable(false);
    }  

    if ((this.visibleColumnFlags & AUTH) == 0) {
      getColumnModel().getColumn(this.AUTH_COLUMN).setPreferredWidth(0);
      getColumnModel().getColumn(this.AUTH_COLUMN).setMinWidth(0);
      getColumnModel().getColumn(this.AUTH_COLUMN).setMaxWidth(0);
    } else {
      getColumnModel().getColumn(this.AUTH_COLUMN).setPreferredWidth(this.AUTH_COLUMN_SIZE);
      getColumnModel().getColumn(this.AUTH_COLUMN).setWidth(this.AUTH_COLUMN_SIZE);
      //getColumnModel().getColumn(AUTH_COLUMN).setMinWidth(AUTH_COLUMN_SIZE);
      //getColumnModel().getColumn(AUTH_COLUMN).setMaxWidth(AUTH_COLUMN_SIZE);
      getColumnModel().getColumn(this.AUTH_COLUMN).setResizable(true);
    }  

    if ((this.visibleColumnFlags & CODE)  == 0){
      getColumnModel().getColumn(this.CODE_COLUMN).setPreferredWidth(0);
      getColumnModel().getColumn(this.CODE_COLUMN).setMinWidth(0);
      getColumnModel().getColumn(this.CODE_COLUMN).setMaxWidth(0);
    } else {
      getColumnModel().getColumn(this.CODE_COLUMN).setPreferredWidth(this.CODE_COLUMN_SIZE);
      getColumnModel().getColumn(this.CODE_COLUMN).setWidth(this.CODE_COLUMN_SIZE);
      getColumnModel().getColumn(this.CODE_COLUMN).setResizable(true);
    }
    
    if ((this.visibleColumnFlags & NAME) == 0) {
      getColumnModel().getColumn(this.NAME_COLUMN).setPreferredWidth(0);
      getColumnModel().getColumn(this.NAME_COLUMN).setMinWidth(0);
      getColumnModel().getColumn(this.NAME_COLUMN).setMaxWidth(0);
    } else {
      getColumnModel().getColumn(this.NAME_COLUMN).setPreferredWidth(this.NAME_COLUMN_SIZE);
      getColumnModel().getColumn(this.NAME_COLUMN).setWidth(this.NAME_COLUMN_SIZE);
      getColumnModel().getColumn(this.NAME_COLUMN).setResizable(true);
    }  

    
    
    if ((this.visibleColumnFlags & CS)  == 0){
      getColumnModel().getColumn(this.CS_COLUMN).setPreferredWidth(0);
      getColumnModel().getColumn(this.CS_COLUMN).setMinWidth(0);
      getColumnModel().getColumn(this.CS_COLUMN).setMaxWidth(0);
    }else {
      getColumnModel().getColumn(this.CS_COLUMN).setPreferredWidth(this.CS_COLUMN_SIZE);
      getColumnModel().getColumn(this.CS_COLUMN).setWidth(this.CS_COLUMN_SIZE);
      getColumnModel().getColumn(this.CS_COLUMN).setResizable(true);
    }
    
    if ((this.visibleColumnFlags & DATUM)  == 0){
      getColumnModel().getColumn(this.DATUM_COLUMN).setPreferredWidth(0);
      getColumnModel().getColumn(this.DATUM_COLUMN).setMinWidth(0);
      getColumnModel().getColumn(this.DATUM_COLUMN).setMaxWidth(0);
    } else {
      getColumnModel().getColumn(this.DATUM_COLUMN).setPreferredWidth(this.DATUM_COLUMN_SIZE);
      getColumnModel().getColumn(this.DATUM_COLUMN).setWidth(this.DATUM_COLUMN_SIZE);
      getColumnModel().getColumn(this.DATUM_COLUMN).setResizable(true);
    }
  }
//INITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINIT
//IN FIN INITIALIZATION                                                 IT
//INITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINITINIT


}
