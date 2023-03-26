package org.jorigin.jis.swing;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.ItemSelectable;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultRowSorter;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jorigin.jis.CRSDefaults;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * This class enable to list the available CRS in geotools.
 * @author Julien Seinturier
 *
 */
public class JCRSPanel extends JPanel implements ItemSelectable{
  
  private static final long serialVersionUID = 1L;
  
  private boolean isListening                   = true;
  
  private boolean filterByPosition              = false;

  private boolean filterByName                  = false;
  
  private JCheckBox positionFilterCH            = null;
  
  private JLabel wsg84LongAngleLB               = null;
  private JSpinner wsg84LongAngleTF             = null;
  private double lonAngle                       = 0.0d;
  
  private JLabel wsg84LatAngleLB                = null;
  private JSpinner wsg84LatAngleTF              = null;
  private double latAngle                       = 0.0d;
  
  private JPanel positionFilterPanel            = null;
  
  private JCheckBox nameFilterCH                = null;
  private JTextField nameTF                     = null;
  private String name                           = null;
  private JPanel nameFilterPanel                = null;
  
  private JButton filterBT                      = null;
  
  private JPanel filterPN                       = null;
  
  private RowFilter<Object, Object> crsTBFilter = null;
  private JCRSTable crsTB                       = null;
  private JScrollPane crsSP                     = null;
  private JPanel crsPN                          = null; 
  
  private JTextArea crsWKTTA                    = null;
  private JScrollPane crsWKTSP                  = null;
  private JPanel crsWKTPN                       = null;

  private JPanel upPanel                        = null;
  private JPanel downPanel                      = null;
  
  private JSplitPane splitPane                  = null;
  
  private List<CoordinateReferenceSystem> crsList         = null;
  
  private List<CoordinateReferenceSystem> crsSelectedList = null;

  private List<EventListener> listeners = null;
  
  String[] crsTBHeaderNames       = new String[]{"Match", "Authority", "Code", "Name", "CS"};
  
  //IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
  //II INITIALISATION                                     II
  //IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
  /**
   * Init the graphical components.
   */
  protected void initGUI(){

    GridBagConstraints c = null;
    Insets labelInsets   = new Insets(8,8,0,8);
    Insets fieldInsets   = new Insets(8,0,0,0);
    
    positionFilterCH = new JCheckBox("Position");
    positionFilterCH.addChangeListener(new ChangeListener(){

      @Override
      public void stateChanged(ChangeEvent e) {
        if (isListening){
          filterByPosition = positionFilterCH.isSelected();
          refreshGUI();
        }
      }
      
    });
    
    wsg84LatAngleLB = new JLabel("Lat.");
    SpinnerModel latmodel  = new SpinnerNumberModel(45.0d, //initial value
        -90.0d,   //min
        90.0d,    //max
        0.5d);    //step
    
    wsg84LatAngleTF     = new JSpinner(latmodel);
    wsg84LatAngleTF.addChangeListener(new ChangeListener(){

      @Override
      public void stateChanged(ChangeEvent e) {
        processChangeEvent(e);
      }});
    
    wsg84LatAngleTF.setEditor(new JSpinner.NumberEditor(wsg84LatAngleTF){
      @Override
      public void stateChanged(ChangeEvent e) {
        JSpinner spinner = (JSpinner)(e.getSource());
        
        double value = ((Double)spinner.getValue()).doubleValue();
        
        getTextField().setText(CRSDefaults.toDegreeMinuteSecondsLon(value));
    }
    });
    
    wsg84LongAngleLB = new JLabel("Lon.");    
    SpinnerModel lonmodel  = new SpinnerNumberModel(0.0d, //initial value
        -180.0d,   //min
        180.0d,    //max
        0.5d);    //step
    
    wsg84LongAngleTF     = new JSpinner(lonmodel);
    wsg84LongAngleTF.addChangeListener(new ChangeListener(){

      @Override
      public void stateChanged(ChangeEvent e) {
        processChangeEvent(e);
      }});
    
    JSpinner.NumberEditor e = null;
    
    wsg84LongAngleTF.setEditor(new JSpinner.NumberEditor(wsg84LongAngleTF){
      @Override
      public void stateChanged(ChangeEvent e) {
        JSpinner spinner = (JSpinner)(e.getSource());
        
        double value = ((Double)spinner.getValue()).doubleValue();
        
        getTextField().setText(CRSDefaults.toDegreeMinuteSecondsLon(value));
    }
    });
    
    positionFilterPanel = new JPanel();
    positionFilterPanel.setLayout(new GridBagLayout());
    
    c           = new GridBagConstraints ();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = 1;
    c.fill      = GridBagConstraints.NONE;
    c.insets    = labelInsets;
    c.weightx   = 0.0;
    c.weighty   = 0.0;
    positionFilterPanel.add(positionFilterCH, c);
    
    c           = new GridBagConstraints ();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = 1;
    c.fill      = GridBagConstraints.NONE;
    c.insets    = labelInsets;
    c.weightx   = 0.0;
    c.weighty   = 0.0;
    positionFilterPanel.add(wsg84LongAngleLB, c);
    
    c           = new GridBagConstraints ();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = 1;
    c.fill      = GridBagConstraints.HORIZONTAL;
    c.insets    = fieldInsets;
    c.weightx   = 1.0;
    c.weighty   = 0.0;
    positionFilterPanel.add(wsg84LongAngleTF, c);
    
    c           = new GridBagConstraints ();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = 1;
    c.fill      = GridBagConstraints.NONE;
    c.insets    = labelInsets;
    c.weightx   = 0.0;
    c.weighty   = 0.0;
    positionFilterPanel.add(wsg84LatAngleLB, c);
    
    c           = new GridBagConstraints ();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.fill      = GridBagConstraints.HORIZONTAL;
    c.insets    = fieldInsets;
    c.weightx   = 1.0;
    c.weighty   = 0.0;
    positionFilterPanel.add(wsg84LatAngleTF, c);
    
    nameFilterCH = new JCheckBox("Name ");
    nameFilterCH.addChangeListener(new ChangeListener(){

      @Override
      public void stateChanged(ChangeEvent e) {
        if (isListening){
          filterByName = nameFilterCH.isSelected();
          refreshGUI();
        }
      }
      
    });
    
    nameTF       = new JTextField();
    nameTF.addCaretListener(new CaretListener(){

      @Override
      public void caretUpdate(CaretEvent e) {
        name = nameTF.getText();       
      }});
    nameFilterPanel = new JPanel();
    nameFilterPanel.setLayout(new GridBagLayout());
    
    c           = new GridBagConstraints ();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = 1;
    c.fill      = GridBagConstraints.NONE;
    c.insets    = labelInsets;
    c.weightx   = 0.0;
    c.weighty   = 0.0;
    nameFilterPanel.add(nameFilterCH, c);
    
    c           = new GridBagConstraints ();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.fill      = GridBagConstraints.HORIZONTAL;
    c.insets    = fieldInsets;
    c.weightx   = 1.0;
    c.weighty   = 0.0;
    nameFilterPanel.add(nameTF, c);
    
    filterBT = new JButton("Filter");
    filterBT.addActionListener(new ActionListener(){

      @Override
      public void actionPerformed(ActionEvent e) {
        processActionEvent(e);
      }});
    
    filterPN = new JPanel();
    filterPN.setBorder(BorderFactory.createTitledBorder("Filters"));
    filterPN.setLayout(new GridBagLayout());
    
    c           = new GridBagConstraints ();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.fill      = GridBagConstraints.HORIZONTAL;
    c.insets    = labelInsets;
    c.weightx   = 1.0;
    c.weighty   = 0.0;
    filterPN.add(positionFilterPanel, c);
    
    c           = new GridBagConstraints ();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.fill      = GridBagConstraints.HORIZONTAL;
    c.insets    = labelInsets;
    c.weightx   = 1.0;
    c.weighty   = 0.0;
    filterPN.add(nameFilterPanel, c);
    
    c           = new GridBagConstraints ();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.fill      = GridBagConstraints.HORIZONTAL;
    c.insets    = fieldInsets;
    c.weightx   = 1.0;
    c.weighty   = 0.0;
    filterPN.add(filterBT, c);

    crsTB = new JCRSTable();
    
    crsTBFilter = new RowFilter<Object, Object>() {
      @Override
      public boolean include(Entry<?, ?> entry) {
        boolean isInclude = true;
        
        if (crsList != null){
          
          if (entry != null){
            CoordinateReferenceSystem crs = crsList.get(((Integer) entry.getIdentifier()).intValue());
            
            if (filterByPosition){
              isInclude &= CRSDefaults.isBounded(crs, latAngle, lonAngle);
            }

            if (filterByName){
              isInclude &= crs.getName().toString().contains(name);
            }
          }
        }
        
        return isInclude;
        
      }
    };
    
    //((DefaultRowSorter<?, ?>)crsTB.getRowSorter()).setRowFilter(crsTBFilter);
    
    crsTB.addMouseListener(new MouseListener(){

      @Override
      public void mouseClicked(MouseEvent e) {
        setSelectedCRS(crsTB.getValueAtRow(crsTB.getSelectedRow()));
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
    
    crsSP = new JScrollPane(crsTB);
    crsTB.setFillsViewportHeight(true);

    crsPN = new JPanel();
    crsPN.setLayout(new BorderLayout());
    crsPN.setBorder(BorderFactory.createTitledBorder("Coordinate Reference Systems"));
    crsPN.add(crsSP, BorderLayout.CENTER);
    
    crsWKTTA = new JTextArea();
    crsWKTSP = new JScrollPane(crsWKTTA);
    crsWKTPN = new JPanel();
    crsWKTPN.setLayout(new BorderLayout());
    crsWKTPN.setBorder(BorderFactory.createTitledBorder("WKT description"));
    crsWKTPN.setPreferredSize(new Dimension(200, 150));
    crsWKTPN.setMinimumSize(new Dimension(50, 150));
    
    crsWKTPN.add(crsWKTSP, BorderLayout.CENTER);
    
    upPanel   = new JPanel();
    upPanel.setLayout(new GridBagLayout());
    upPanel.setPreferredSize(new Dimension(320, 200));
    upPanel.setSize(new Dimension(320, 200));
    
    c           = new GridBagConstraints ();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.fill      = GridBagConstraints.HORIZONTAL;
    c.insets    = fieldInsets;
    c.weightx   = 1.0;
    c.weighty   = 0.0;
    upPanel.add(filterPN, c);
    
    c           = new GridBagConstraints ();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.fill      = GridBagConstraints.BOTH;
    c.insets    = fieldInsets;
    c.weightx   = 1.0;
    c.weighty   = 0.5;
    upPanel.add(crsPN, c);
    
    downPanel = new JPanel();
    downPanel.setLayout(new GridBagLayout());
    downPanel.setPreferredSize(new Dimension(320, 100));
    downPanel.setSize(new Dimension(320, 100));
    
    c           = new GridBagConstraints ();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.fill      = GridBagConstraints.BOTH;
    c.insets    = fieldInsets;
    c.weightx   = 1.0;
    c.weighty   = 0.5;
    downPanel.add(crsWKTPN, c);
    
    splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    splitPane.add(upPanel, JSplitPane.TOP);
    splitPane.add(downPanel, JSplitPane.BOTTOM);
    splitPane.setDividerLocation(350);
    
    setLayout(new BorderLayout());
    add(splitPane, BorderLayout.CENTER);
  }
  
  public void refreshGUI(){ 

    isListening = false;
    
    positionFilterCH.setEnabled(true);
    positionFilterCH.setSelected(filterByPosition);
    
    wsg84LongAngleLB.setEnabled(filterByPosition);
    wsg84LongAngleTF.setEnabled(filterByPosition);
    wsg84LatAngleLB.setEnabled(filterByPosition);
    wsg84LatAngleTF.setEnabled(filterByPosition);
    
    positionFilterPanel.setEnabled(filterByPosition);
    nameTF.setEnabled(filterByName);

    wsg84LongAngleTF.setValue(lonAngle);
    wsg84LatAngleTF.setValue(latAngle);
    
    crsTB.clearSelection();
    if ((crsSelectedList != null)&&(crsSelectedList.size() > 0)){
      
      int index = -1;
      CoordinateReferenceSystem crs = null;
      
      for(int i = 0; i < crsSelectedList.size(); i++){
        crs = crsSelectedList.get(i);
        
        index = crsList.indexOf(crs);
        
        if (index > -1){
          crsTB.getSelectionModel().addSelectionInterval(crsTB.convertRowIndexToView(index), crsTB.convertRowIndexToView(index));
        }
      }
      
      crsTB.scrollRectToVisible(new Rectangle(crsTB.getCellRect(crsTB.convertRowIndexToView(index), 0, true)));

    }
     
    crsTB.repaint();
    refreshWKT();
    
    isListening = true;
  }
  
  public void refreshWKT(){
    if (getSelectedCRS() != null){
      try {
        crsWKTTA.setText(getSelectedCRS().toWKT());
      } catch (Exception e) {
        crsWKTTA.setText("No WKT available (Object too complex)");
      }
    } else {
      crsWKTTA.setText("No WKT available");
    }
    crsWKTTA.repaint();
  }
  //IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
  //II FIN INITIALISATION                                 II
  //IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
  
  /**
   * Set the geographic coordinates used by the position filter.
   * @param longitudeAngle the longitude angle (between -180 and 180)
   * @param latitudeAngle the latitude angle (between -90 and 90)
   */
  public void setGeographicCoordinates(double longitudeAngle, double latitudeAngle){
    latAngle = latitudeAngle;
    lonAngle = longitudeAngle;
  }
  
  /**
   * Get the geographic coordinates used by the position filter as {longitudeAngle, latitudeAngle}.
   * @return the geographic coordinates used by the position filter.
   */
  public double[] getGeographicsCoordinates(){
    return new double[]{lonAngle, latAngle};
  }
  
  /**
   * Set if the position filter is active.
   * @param active <code>true</code> if the position filter is activated, <code>false</code> otherwise.
   */
  public void setPositionFilterActive(boolean active){
    filterByPosition = active;
  }
  
  /**
   * Get if the position filter is active.
   * @return <code>true</code> if the position filter is activated, <code>false</code> otherwise.
   */
  public boolean isPositionFilterActive(){
    return filterByPosition;
  }
  
  /**
   * Set the filter on the name of the CRS. If the <code>filter</code> is set to <code>null</code>, the name filter is desactivated.
   * @param filter the filter on the name of the CRS.
   */
  public void setNameFilter(String filter){
    this.name = filter;
    
    if (filter == null){
      filterByName = false;
    }
    
  }
  
  /**
   * Get the filter on the name of the CRS.
   * @return the filter on the name of the CRS.
   */
  public String getNameFilter(){
    return name;
  }
  
  /**
   * Set if the name filter is active.
   * @param active <code>true</code> if the name filter is activated, <code>false</code> otherwise.
   */
  public void setNameFilterActive(boolean active){
    filterByName = active;
  }
  
  /**
   * Get if the name filter is active.
   * @return <code>true</code> if the name filter is activated, <code>false</code> otherwise.
   */
  public boolean isNameFilterActive(){
    return filterByName;
  }
  
  /**
   * Set the list of CRS to display within the panel.
   * @param list the list of CRS to display.
   */
  public void setCRSList(List<CoordinateReferenceSystem> list){
    
    crsTB.setData(list);
    crsList = list;
    crsTB.repaint();
    if (list != null){
      ((TitledBorder)crsPN.getBorder()).setTitle("Coordinate Reference Systems ("+crsList.size()+")");
    } else {
      ((TitledBorder)crsPN.getBorder()).setTitle("Coordinate Reference Systems ("+0+")");
    }
    refreshGUI();
  }
  
  /**
   * Check if any filter is active within this panel.
   * @return <code>true</code> is a filter is active within the panel, <code>false</code> otherwise.
   */
  public boolean isFiltered(){
    return filterByPosition || filterByName;
  }
  
  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  //CC CONSTRUCTEUR                                       CC
  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  /**
   * Create a new default CRS panel. This panel will ask to Geotools all available CRS and display them.
   */
  public JCRSPanel(){
    super();
    initGUI();
    setCRSList(CRSDefaults.getAvailablesCRS());
    refreshGUI();
  }
  
  /**
   * Create a new CRS panel showing CRS given in parameters. The vector must contain CRS description triplets as: 
   * <code>{authorityID, code, crsName}</code>.<br>
   * 
   */
  public JCRSPanel(List<CoordinateReferenceSystem> crsList){
    super();
    initGUI();
    setCRSList(crsList);
    refreshGUI();
  }
  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  //CC FIN CONSTRUCTEUR                                   CC
  //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  
  public void filter(){
    ((DefaultRowSorter<?, ?>)crsTB.getRowSorter()).setRowFilter(crsTBFilter);
    
    double distanceMin  = Double.POSITIVE_INFINITY;
    double distanceMax = 0.0d;
    double distance    = 0.0d;
    
    Iterator<CoordinateReferenceSystem> iter = crsList.iterator();
    CoordinateReferenceSystem crs = null;
    while(iter.hasNext()){
      crs = iter.next();
      
      distance = CRSDefaults.distanceFromCRSCenter(crs, lonAngle, latAngle);
      
      if (distance < distanceMin){
        distanceMin = distance;
      }
      
      if (distance > distanceMax){
        distanceMax = distance;
      }
    }
    
    iter = crsList.iterator();
    crs = null;
    int i = 0;
    while(iter.hasNext()){
      crs = iter.next();
      
      distance = CRSDefaults.distanceFromCRSCenter(crs, lonAngle, latAngle);
      crsTB.getModel().setValueAt(new Double(100-((distance-distanceMin)*100/(distanceMax-distanceMin))), i, 0);
      i++;
    }
   
    refreshGUI();
  }
  
  /**
   * Process an action event.
   * @param e the event to process.
   */
  protected void processActionEvent(ActionEvent e){
    if (e.getSource() == filterBT){
      filter();
    }
  }
  
  /**
   * Process a change event.
   * the event to process.
   */
  protected void processChangeEvent(ChangeEvent e){
    
    if (isListening){
      if (e.getSource() == wsg84LongAngleTF){
        lonAngle = (Double)wsg84LongAngleTF.getValue();
        refreshGUI();
      } else if (e.getSource() == wsg84LatAngleTF){
        latAngle = (Double)wsg84LatAngleTF.getValue();
        refreshGUI();
      }
    }
  }
  
  /**
   * Get the {@link CoordinateReferenceSystem coordinate reference systems} that are currently selected within the panel.
   * @return the {@link CoordinateReferenceSystem coordinate reference systems} that are currently selected within the panel.
   * @see #setSelectedCRS(List)
   */
  public List<CoordinateReferenceSystem> getSelectedCRSList(){
    return this.crsSelectedList;
  }
  
  /**
   * Set the {@link CoordinateReferenceSystem coordinate reference systems} that have to be selected within the panel.
   * @param selection the {@link CoordinateReferenceSystem coordinate reference systems} that have to be selected within the panel.
   * @see #getSelectedCRS()
   */
  public void setSelectedCRSList(List<CoordinateReferenceSystem> selection){
    this.crsSelectedList = selection;
    refreshGUI();
  }

  /**
   * Get the {@link CoordinateReferenceSystem coordinate reference systems (CRS)} that is currently selected within the panel. 
   * If different CRS are selected at the same time, this method return the first selected whithin the list given by {@link #getSelectedCRSList()}.
   * @return the {@link CoordinateReferenceSystem coordinate reference systems (CRS)} that is currently selected within the panel. 
   * @see #getSelectedCRSList()
   */
  public CoordinateReferenceSystem getSelectedCRS(){
    if ((crsSelectedList != null)&&(crsSelectedList.size() > 0)){
      return crsSelectedList.get(0);
    } else {
      return null;
    }
  }
  
  /**
   * Set the {@link CoordinateReferenceSystem coordinate reference systems (CRS)} that is currently selected within the panel. 
   * @param crs the {@link CoordinateReferenceSystem coordinate reference systems} that has to be selected within the panel.
   * @see #getSelectedCRS()
   * @see #setSelectedCRSList(List)
   */
  public void setSelectedCRS(CoordinateReferenceSystem crs){
    
    if (crsSelectedList == null){
      crsSelectedList = new ArrayList<CoordinateReferenceSystem>();
    } else {
      crsSelectedList.clear();
    }
    
    if (crs != null){
      crsSelectedList.add(crs);
      
      if (!crsList.contains(crs)){
        crsList.add(crs);
      }
    }
    refreshGUI();
    fireEvent(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, null, ItemEvent.SELECTED));
  }
  
  /**
   * Attach an {@link java.awt.event.ItemListener item listener} to this panel. the listener will describe 
   * {@link CoordinateReferenceSystem coordinate reference systems (CRS)} selection within this component.
   * @param listener the {@link java.awt.event.ItemListener item listener} to attach to this component.
   * @see #removeItemListener(ItemListener)
   */
  @Override
  public void addItemListener(ItemListener listener){
    if (listeners == null){
      listeners = new ArrayList<EventListener>();
    }
    
    if ((listener != null)&&(!listeners.contains(listener))){
      listeners.add(listener);
    }
  }
  
  /**
   * Detach the given {@link java.awt.event.ItemListener item listener} from this panel.
   * @param listener the {@link java.awt.event.ItemListener item listener} to detach from this component.
   * @see #addItemListener(ItemListener)
   */
  @Override
  public void removeItemListener(ItemListener listener){
    if ((listener != null)&&(listeners != null)){
      listeners.remove(listener);
    }
  }
  
  /**
   * This method is an implementation 
   * of method {@link java.awt.ItemSelectable#getSelectedObjects() getSelectedObjects()} 
   * from {@link java.awt.ItemSelectable ItemSelectable} interface.<br/>
   * However, is is recommended to use {@link #getSelectedCRSList()} method instead.
   * @return the selected objects.
   */
  @Override
  public Object[] getSelectedObjects(){
    Object[] objects = null;
    
    if (getSelectedCRSList() != null){
      objects = getSelectedCRSList().toArray(new Object[getSelectedCRSList().size()]);
    }
    
    return objects;
  }
  
  /**
   * Dispatches an {@link java.awt.event.ItemEvent item event} to all registered {@link java.awt.event.ItemListener item listeners}.
   * @param event the {@link java.awt.event.ItemEvent item event} to dispatch.
   */
  protected void fireEvent(ItemEvent event){
    
    EventListener listener = null;
    if ((event != null)&&(listeners.size() > 0)){
      for(int i = 0; i < listeners.size(); i++){
        listener = listeners.get(i);
        if (listener instanceof ItemListener){
          ((ItemListener)listener).itemStateChanged(event);
        }
      }
    }
  }
  public static void main(String[] args){
    
    JFrame frame = new JFrame();
    frame.getContentPane().setLayout(new BorderLayout());
    frame.setSize(new Dimension(640, 480));
    frame.setPreferredSize(new Dimension(640, 480));
    frame.getContentPane().add(new JCRSPanel(), BorderLayout.CENTER);
    frame.setVisible(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
}
