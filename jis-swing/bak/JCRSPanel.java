package org.jorigin.jis.swing;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.geotools.referencing.CRS;
import org.jorigin.jis.JIS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.ReferenceIdentifier;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * This class enable to list the available CRS in geotools.
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 *
 */
public class JCRSPanel extends JPanel {

  private static final long serialVersionUID = JIS.BUILD;
  
  private DefaultTableModel crsTBModel          = null;
  private JTable crsTB                          = null;
  private JScrollPane crsSP                     = null;
  private JPanel crsPN                          = null; 
  
  private JTextArea crsWKTTA                    = null;
  private JScrollPane crsWKTSP                  = null;
  private JPanel crsWKTPN                       = null;

  private CoordinateReferenceSystem crsSelected = null;
  
  String[] crsTBHeaderNames       = new String[]{"Authority", "Code", "Name"};
  
  //IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
  //II INITIALISATION                                     II
  //IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
  /**
   * Init the graphical components.
   */
  protected void initGUI(){

    GridBagConstraints c = null;
    Insets fieldInsets   = new Insets(8,0,0,0);
    
    this.crsTBModel = new DefaultTableModel(null, this.crsTBHeaderNames);
    
    this.crsTB = new JTable(this.crsTBModel);
    TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(this.crsTB.getModel());
    List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
    sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
    sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
    sorter.setSortKeys(sortKeys); 

    this.crsTB.setRowSorter(sorter);

    this.crsTB.addMouseListener(new MouseListener(){

      @Override
      public void mouseClicked(MouseEvent e) {
        JCRSPanel.this.crsTB.getValueAt(JCRSPanel.this.crsTB.getSelectedRow(), JCRSPanel.this.crsTB.getSelectedColumn());
        
        String authorityID = ""+JCRSPanel.this.crsTB.getValueAt(JCRSPanel.this.crsTB.getSelectedRow(), 0);
        String code        = ""+JCRSPanel.this.crsTB.getValueAt(JCRSPanel.this.crsTB.getSelectedRow(), 1);
    
        selectCRS(authorityID, code);
        refreshWKT();
      }

      @Override
      public void mouseEntered(MouseEvent e) {
      }

      @Override
      public void mouseExited(MouseEvent e) {
      }

      @Override
      public void mousePressed(MouseEvent e) {
      }

      @Override
      public void mouseReleased(MouseEvent e) {
      }});
    
    this.crsSP = new JScrollPane(this.crsTB);
    this.crsTB.setFillsViewportHeight(true);

    this.crsPN = new JPanel();
    this.crsPN.setLayout(new BorderLayout());
    this.crsPN.setBorder(BorderFactory.createTitledBorder("Coordinate Reference Systems"));
    this.crsPN.add(this.crsSP, BorderLayout.CENTER);
    
    this.crsWKTTA = new JTextArea();
    this.crsWKTSP = new JScrollPane(this.crsWKTTA);
    this.crsWKTPN = new JPanel();
    this.crsWKTPN.setLayout(new BorderLayout());
    this.crsWKTPN.setBorder(BorderFactory.createTitledBorder("WKT description"));
    this.crsWKTPN.add(this.crsWKTSP, BorderLayout.CENTER);
    
    setLayout(new GridBagLayout());
    
    c           = new GridBagConstraints ();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.fill      = GridBagConstraints.BOTH;
    c.insets    = fieldInsets;
    c.weightx   = 1.0;
    c.weighty   = 0.5;
    add(this.crsPN, c);
    
    c           = new GridBagConstraints ();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.fill      = GridBagConstraints.BOTH;
    c.insets    = fieldInsets;
    c.weightx   = 1.0;
    c.weighty   = 0.5;
    add(this.crsWKTPN, c);
    
  }
  
  /**
   * Refresh the Graphical User Interface (GUI).
   */
  public void refreshGUI(){

    Set<String> authorities       = CRS.getSupportedAuthorities(false);
    String authorityID            = null;
    Iterator<String> authIter     = authorities.iterator();
    
    Set<String> codes             = null;
    String code                   = null;
    Iterator<String> codeIter     = null;
    
    CoordinateReferenceSystem crs = null;
    
    ReferenceIdentifier refID     = null;
    
    Vector<String[]> datas        = null;
     
    String crsName                = null;
    
    datas = new Vector<String[]>();
    
    while(authIter.hasNext()){
      authorityID = authIter.next();
      
      codes = CRS.getSupportedCodes(authorityID);
      
      if (codes != null){
        codeIter = codes.iterator();
        while(codeIter.hasNext()){
          code = codeIter.next();
          
          if (!code.contains(":")){
            code = authorityID+":"+code;
          }
          
          try {
            crs = CRS.decode(code);
            refID = crs.getName();

            crsName = crs.getName().toString();
            if (crsName.startsWith(authorityID+":")){
              crsName = ""+crsName.substring(refID.toString().indexOf(":")+1);
            }
            
            datas.add(new String[]{authorityID, code, crsName});

          } catch (NoSuchAuthorityCodeException e) {
            System.out.println("[JCRSPanel] [init()]   * Code: "+code+" Cannot be decoded");
            System.err.println(e.getMessage());
            e.printStackTrace();
          } catch (FactoryException e) {
            System.out.println("[JCRSPanel] [init()]   * Code: "+code+" Cannot be decoded");
            System.err.println(e.getMessage());
            e.printStackTrace();
          }
        }
       
        this.crsTBModel.setDataVector((String[][])datas.toArray(new String[datas.size()][this.crsTBHeaderNames.length]), this.crsTBHeaderNames);
        this.crsTB.repaint();
        
      }
      
    }
    
    ((TitledBorder)this.crsPN.getBorder()).setTitle("Coordinate Reference Systems ("+datas.size()+")");
    
    refreshWKT();
  }
  
  /**
   * Refresh the WKT text area.
   */
  public void refreshWKT(){
    if (this.crsSelected != null){
      this.crsWKTTA.setText(this.crsSelected.toWKT());
    } else {
      this.crsWKTTA.setText("No WKT available");
    }
    this.crsWKTTA.repaint();
  }
  //IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
  //II FIN INITIALISATION                                 II
  //IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
  
  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  //CC CONSTRUCTEUR                                       CC
  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  /**
   * Create a new default CRS panel. This panel will ask to Geotools all available CRS and display them.
   */
  public JCRSPanel(){
    super();
    initGUI();
    refreshGUI();
  }
  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  //CC FIN CONSTRUCTEUR                                   CC
  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  
  /**
   * Select a {@link org.opengis.referencing.crs.CoordinateReferenceSystem Coordinate Reference System} 
   * that matches the given <code>authority</code> and the given <code>code</code>.
   * @param authority the authority of the CRS.
   * @param code the code of the CRS.
   */
  public void selectCRS(String authority, String code){
    CoordinateReferenceSystem crs = null;
    
    try {
      crs = CRS.decode(code, true);
      this.crsSelected = crs;
    } catch (NoSuchAuthorityCodeException e) {
      System.out.println("[JCRSPanel] [init()]   * Code: "+code+" Cannot be decoded");
      System.err.println(e.getMessage());
      e.printStackTrace();
    } catch (FactoryException e) {
      System.out.println("[JCRSPanel] [init()]   * Code: "+code+" Cannot be decoded");
      System.err.println(e.getMessage());
      e.printStackTrace();
    }
  }
}
