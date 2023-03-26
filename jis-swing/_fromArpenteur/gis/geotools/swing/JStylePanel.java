package org.arpenteur.gis.geotools.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.arpenteur.gis.geotools.wrap.AWTGeometryWrap;

import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.geotools.styling.Symbolizer;
import org.geotools.styling.TextSymbolizer;

public class JStylePanel extends JPanel{
  
  public static final String STROKE_COLOR_CHANGE_CMD = "STROKE_COLOR_CHANGE";
  
  public static final String FILL_COLOR_CHANGE_CMD   = "FILL_COLOR_CHANGE";
 
   private Style style             = null;
  
  private FeatureTypeStyle type   = null;
  
  private Rule rule               = null;
  
  private Symbolizer symbolizer   = null;
  
  private JLabel nameLB           = null;
  
  private JTextField nameTF       = null;
  
  private JLabel typeLB           = null;
  
  private JComboBox typeCB        = null;
  
  private JLabel ruleLB           = null;
  
  private JComboBox ruleCB        = null;
  
  private JLabel symbolizerLB     = null;
  
  private JComboBox symbolizerCB  = null;
   
  private JCheckBox strokeColorCH = null;
  
  private JButton strokeColorBT   = null;
  
  private JLabel strokeWidthLB    = null;
  
  private JSpinner strokeWidthTF  = null;
  
  private JLabel strokeOpacLB     = null;
  
  private JSpinner strokeOpacTF   = null;
  
  private JCheckBox fillColorCH   = null;
  
  private JButton fillColorBT     = null;
 
  private JLabel fillOpacLB       = null;
  
  private JSpinner fillOpacTF     = null;

  private JPanel typePN           = null;
  
  private JPanel labelPN          = null;
  
  private JPanel geometryPN       = null;
  
  private JPanel drawPN           = null;
  
  private BufferedImage strokeColorBI = null;
  
  private BufferedImage fillColorBI   = null;

  private int buttonImageWidth    = 16;
  
  private int buttonImageHeight   = 16;
  
  private boolean isListening     = true;
  
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC CONSTRUCTEUR                                             CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  
  /**
   * Create a new style editor for the given style.
   * @param the style that can be edited.
   */
  public JStylePanel(Style style){
    super();
    this.style = style;
    initGUI();
    refreshGUI();
  }
  
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC FIN CONSTRUCTEUR                                         CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC

//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
//II INITIALISATION                                           II
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
  
  protected void initTypePanel(){
    FeatureTypeStyle[] types = null;
    Rule[] rules             = null;
    Symbolizer[] symbolizers = null;
    GridBagConstraints c     = null;
    Insets labelInsets       = new Insets(3, 3, 3, 3);
    
    nameLB       = new JLabel("GUI_TITLE_LB: ");
    nameTF       = new JTextField();
    nameTF.addCaretListener(new CaretListener(){

      @Override
      public void caretUpdate(CaretEvent e) {
	      processCaretEvent(e);
      }});
    
    typeLB        = new JLabel("GUI_TYPE_LB: ");
    ruleLB        = new JLabel("GUI_RULE_LB: ");
    symbolizerLB  = new JLabel("GUI_GEOMETRY_LB: ");

    if (style != null){
      types = style.getFeatureTypeStyles();

      if ((types != null) && (types.length > 0)){
	type = types[0];
	typeCB = new JComboBox(types);
        typeCB.setSelectedItem(type);
        typeCB.addItemListener(new ItemListener(){

	  @Override
	  public void itemStateChanged(ItemEvent e) {
	    processItemEvent(e);
	  }});
        
	
	rules = type.getRules();	
	if ((rules != null) && (rules.length > 0)){
	  rule = rules[0];
	  ruleCB = new JComboBox(rules);
	  ruleCB.setSelectedItem(rule);
	  ruleCB.addItemListener(new ItemListener(){

		  @Override
		  public void itemStateChanged(ItemEvent e) {
		    processItemEvent(e);
		  }});
	  
	  symbolizers = rule.getSymbolizers();
	  if ((symbolizers != null)&&(symbolizers.length > 0)){
	    symbolizer = symbolizers[0];
	    
	    symbolizerCB = new JComboBox(symbolizers);
	    symbolizerCB.setSelectedItem(symbolizer);
	    symbolizerCB.addItemListener(new ItemListener(){

		  @Override
		  public void itemStateChanged(ItemEvent e) {
		    processItemEvent(e);
		  }});
	    
	  } else {
	    symbolizerCB = new JComboBox();
	    symbolizerCB.addItemListener(new ItemListener(){

		  @Override
		  public void itemStateChanged(ItemEvent e) {
		    processItemEvent(e);
		  }});
	    symbolizerCB.setEnabled(false);
	  }
	  
	} else {
	  ruleCB = new JComboBox();
	  ruleCB.addItemListener(new ItemListener(){

		  @Override
		  public void itemStateChanged(ItemEvent e) {
		    processItemEvent(e);
		  }});
	  ruleCB.setEnabled(false);
	  symbolizerCB = new JComboBox();
	  symbolizerCB.addItemListener(new ItemListener(){

		  @Override
		  public void itemStateChanged(ItemEvent e) {
		    processItemEvent(e);
		  }});
	  symbolizerCB.setEnabled(false);
	}
	
      } else {
	typeLB.setEnabled(false);
	ruleLB.setEnabled(false);
	
	typeCB = new JComboBox();
	typeCB.addItemListener(new ItemListener(){

	  @Override
	  public void itemStateChanged(ItemEvent e) {
	    processItemEvent(e);
	  }});
	typeCB.setEnabled(false);
	ruleCB = new JComboBox();
	ruleCB.addItemListener(new ItemListener(){

	  @Override
	  public void itemStateChanged(ItemEvent e) {
	    processItemEvent(e);
	  }});
	ruleCB.setEnabled(false);
	symbolizerCB = new JComboBox();
	symbolizerCB.addItemListener(new ItemListener(){

	  @Override
	  public void itemStateChanged(ItemEvent e) {
	    processItemEvent(e);
	  }});
	symbolizerCB.setEnabled(false);
      }
    } else {
      nameLB.setEnabled(false);
      nameTF.setEnabled(false);
      typeLB.setEnabled(false);
      ruleLB.setEnabled(false);
	
      typeCB = new JComboBox();
      typeCB.setEnabled(false);
      typeCB.addItemListener(new ItemListener(){

	  @Override
	  public void itemStateChanged(ItemEvent e) {
	    processItemEvent(e);
	  }});
      ruleCB = new JComboBox();
      ruleCB.setEnabled(false);
      ruleCB.addItemListener(new ItemListener(){

	  @Override
	  public void itemStateChanged(ItemEvent e) {
	    processItemEvent(e);
	  }});
      symbolizerCB = new JComboBox();
      symbolizerCB.addItemListener(new ItemListener(){

	  @Override
	  public void itemStateChanged(ItemEvent e) {
	    processItemEvent(e);
	  }});
      symbolizerCB.setEnabled(false);
    }
    
    // Initialisation des renderers
    typeCB.setRenderer(new DefaultListCellRenderer(){
      public Component getListCellRendererComponent(
	        JList list,
		Object value,
	        int index,
	        boolean isSelecteded,
	        boolean cellHasFocus){
	
	JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelecteded, cellHasFocus);
	
	if (label != null){
	  if (value != null){
	    label.setText(((FeatureTypeStyle)value).getName());
	  } else {
	    label.setText("No feature type");
	  }
	}
	
	
	
	return label;
      } 
    });
    
    ruleCB.setRenderer(new DefaultListCellRenderer(){
      public Component getListCellRendererComponent(
	        JList list,
		Object value,
	        int index,
	        boolean isSelecteded,
	        boolean cellHasFocus){
	
	JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelecteded, cellHasFocus);
	
	if (label != null){
	 if (value != null){
	   label.setText(((Rule)value).getName());
	 } else {
	   label.setText("Unnamed rule");
	 }
	}
	
	return label;
      } 
    });
    
    symbolizerCB.setRenderer(new DefaultListCellRenderer(){
      public Component getListCellRendererComponent(
	        JList list,
		Object value,
	        int index,
	        boolean isSelecteded,
	        boolean cellHasFocus){
	
	JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelecteded, cellHasFocus);
	
	if (label != null){
	  
	  if (value != null){
	    label.setText(((Symbolizer)value).getClass().getSimpleName());
	  } else {
	    label.setText("No symbolizer");
	  }
	} else {
	  label = new JLabel("");
	}

	return label;
      } 
    });
    
    typePN = new JPanel();
    typePN.setBorder(BorderFactory.createTitledBorder("GUI_STYLE_LB"));
    typePN.setLayout(new GridBagLayout());
    
    c           = new GridBagConstraints();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = 1;
    c.fill      = GridBagConstraints.NONE;
    c.insets    = labelInsets;
    c.weightx   = 0.0;
    c.weighty   = 0.0;
    c.anchor    = GridBagConstraints.EAST;
    typePN.add(nameLB, c);
    
    c           = new GridBagConstraints();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = 1;
    c.fill      = GridBagConstraints.HORIZONTAL;
    c.insets    = labelInsets;
    c.weightx   = 1.0;
    c.weighty   = 0.0;
    c.anchor    = GridBagConstraints.WEST;
    typePN.add(nameTF, c);
    
    c           = new GridBagConstraints();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = 1;
    c.fill      = GridBagConstraints.NONE;
    c.insets    = labelInsets;
    c.weightx   = 0.0;
    c.weighty   = 0.0;
    c.anchor    = GridBagConstraints.EAST;
    typePN.add(typeLB, c);
    
    c           = new GridBagConstraints();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.fill      = GridBagConstraints.HORIZONTAL;
    c.insets    = labelInsets;
    c.weightx   = 1.0;
    c.weighty   = 0.0;
    c.anchor    = GridBagConstraints.WEST;
    typePN.add(typeCB, c);
    
    c           = new GridBagConstraints();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = 1;
    c.fill      = GridBagConstraints.NONE;
    c.insets    = labelInsets;
    c.weightx   = 0.0;
    c.weighty   = 0.0;
    c.anchor    = GridBagConstraints.EAST;
    typePN.add(ruleLB, c);
    
    c           = new GridBagConstraints();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = 1;
    c.fill      = GridBagConstraints.HORIZONTAL;
    c.insets    = labelInsets;
    c.weightx   = 1.0;
    c.weighty   = 0.0;
    c.anchor    = GridBagConstraints.WEST;
    typePN.add(ruleCB, c);
    
    c           = new GridBagConstraints();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = 1;
    c.fill      = GridBagConstraints.NONE;
    c.insets    = labelInsets;
    c.weightx   = 0.0;
    c.weighty   = 0.0;
    c.anchor    = GridBagConstraints.EAST;
    typePN.add(symbolizerLB, c);
    
    c           = new GridBagConstraints();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.fill      = GridBagConstraints.HORIZONTAL;
    c.insets    = labelInsets;
    c.weightx   = 1.0;
    c.weighty   = 0.0;
    c.anchor    = GridBagConstraints.WEST;
    typePN.add(symbolizerCB, c);
  }
  
  protected void initGeometryPanel(){

    GridBagConstraints c = null;
    Insets labelInsets   = new Insets(3, 3, 3, 3);
    
    strokeColorCH = new JCheckBox("GUI_STROKE_COLOR_LB");
    strokeColorCH.addItemListener(new ItemListener(){

      @Override
      public void itemStateChanged(ItemEvent e) {
	processItemEvent(e);
      }});
    
    strokeColorBI   = new BufferedImage(buttonImageWidth, buttonImageHeight, BufferedImage.TYPE_INT_ARGB);
    
    strokeColorBT = new JButton(new ImageIcon(strokeColorBI));
    strokeColorBT.setActionCommand(STROKE_COLOR_CHANGE_CMD);
    strokeColorBT.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e) {
	processActionEvent(e);
      }});
    
    strokeWidthLB = new JLabel("GUI_STROKE_WIDTH_LB");
    
    SpinnerModel swidthmodel  = new SpinnerNumberModel(1, //initial value
                                                       0, //min
                                                      10, //max
                                                       1);//step
    strokeWidthTF = new JSpinner(swidthmodel);
    strokeWidthTF.addChangeListener(new ChangeListener(){
      @Override
      public void stateChanged(ChangeEvent e) {
	processChangeEvent(e);
      }});
    
    strokeOpacLB  = new JLabel("GUI_STROKE_OPACITY_LB");
    
    SpinnerModel sopacmodel  = new SpinnerNumberModel(1, //initial value
                                                      0, //min
                                                      1, //max
                                                      0.1);//step
    strokeOpacTF  = new JSpinner(sopacmodel);
    strokeOpacTF.addChangeListener(new ChangeListener(){
      @Override
      public void stateChanged(ChangeEvent e) {
	processChangeEvent(e);
      }});
    
    fillColorCH   = new JCheckBox("GUI_FILL_COLOR_LB");
    
    fillColorBI   = new BufferedImage(buttonImageWidth, buttonImageHeight, BufferedImage.TYPE_INT_ARGB);
    
    fillColorBT   = new JButton(new ImageIcon(fillColorBI));
    fillColorBT.setActionCommand(FILL_COLOR_CHANGE_CMD);
    fillColorBT.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e) {
	processActionEvent(e);
      }});
    
    fillOpacLB    = new JLabel("GUI_FILL_OPACITY_LB");
    
    SpinnerModel fopacmodel  = new SpinnerNumberModel(1, //initial value
        0, //min
        1, //max
        0.1);//step
    
    fillOpacTF    = new JSpinner(fopacmodel);
    fillOpacTF.addChangeListener(new ChangeListener(){
      @Override
      public void stateChanged(ChangeEvent e) {
	processChangeEvent(e);
      }});
    
    geometryPN = new JPanel();
    geometryPN.setBorder(BorderFactory.createTitledBorder("GUI_GEOMETRY_LB"));
    geometryPN.setLayout(new GridBagLayout());
    
    c           = new GridBagConstraints();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = 1;
    c.fill      = GridBagConstraints.NONE;
    c.insets    = labelInsets;
    c.weightx   = 0.0;
    c.weighty   = 0.0;
    c.anchor    = GridBagConstraints.WEST;
    geometryPN.add(strokeColorCH, c);
    
    c           = new GridBagConstraints();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = 1;
    c.fill      = GridBagConstraints.HORIZONTAL;
    c.insets    = labelInsets;
    c.weightx   = 0.5;
    c.weighty   = 0.0;
    c.anchor    = GridBagConstraints.CENTER;
    geometryPN.add(strokeColorBT, c);
    
    c           = new GridBagConstraints();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = 1;
    c.fill      = GridBagConstraints.NONE;
    c.insets    = labelInsets;
    c.weightx   = 0.0;
    c.weighty   = 0.0;
    c.anchor    = GridBagConstraints.EAST;
    geometryPN.add(strokeWidthLB, c); 
    
    c           = new GridBagConstraints();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = 1;
    c.fill      = GridBagConstraints.HORIZONTAL;
    c.insets    = labelInsets;
    c.weightx   = 1.0;
    c.weighty   = 0.0;
    c.anchor    = GridBagConstraints.WEST;
    geometryPN.add(strokeWidthTF, c); 
    
    c           = new GridBagConstraints();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = 1;
    c.fill      = GridBagConstraints.NONE;
    c.insets    = labelInsets;
    c.weightx   = 0.0;
    c.weighty   = 0.0;
    c.anchor    = GridBagConstraints.EAST;
    geometryPN.add(strokeOpacLB, c); 
    
    c           = new GridBagConstraints();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.fill      = GridBagConstraints.HORIZONTAL;
    c.insets    = labelInsets;
    c.weightx   = 1.0;
    c.weighty   = 0.0;
    c.anchor    = GridBagConstraints.WEST;
    geometryPN.add(strokeOpacTF, c); 
    
    
    c           = new GridBagConstraints();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = 1;
    c.fill      = GridBagConstraints.NONE;
    c.insets    = labelInsets;
    c.weightx   = 0.0;
    c.weighty   = 0.0;
    c.anchor    = GridBagConstraints.WEST;
    geometryPN.add(fillColorCH, c);
    
    c           = new GridBagConstraints();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = 1;
    c.fill      = GridBagConstraints.HORIZONTAL;
    c.insets    = labelInsets;
    c.weightx   = 0.5;
    c.weighty   = 0.0;
    c.anchor    = GridBagConstraints.CENTER;
    geometryPN.add(fillColorBT, c);
    
    c           = new GridBagConstraints();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = 1;
    c.fill      = GridBagConstraints.NONE;
    c.insets    = labelInsets;
    c.weightx   = 0.0;
    c.weighty   = 0.0;
    c.anchor    = GridBagConstraints.EAST;
    geometryPN.add(fillOpacLB, c); 
    
    c           = new GridBagConstraints();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = 1;
    c.fill      = GridBagConstraints.HORIZONTAL;
    c.insets    = labelInsets;
    c.weightx   = 1.0;
    c.weighty   = 0.0;
    c.anchor    = GridBagConstraints.WEST;
    geometryPN.add(fillOpacTF, c); 
    
    c           = new GridBagConstraints();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.fill      = GridBagConstraints.HORIZONTAL;
    c.insets    = labelInsets;
    c.weightx   = 1.0;
    c.weighty   = 0.0;
    c.anchor    = GridBagConstraints.WEST;
    geometryPN.add(new JPanel(), c); 
  }
  
  protected void initTextPanel(){
    GridBagConstraints c     = null;
    Insets labelInsets       = new Insets(3, 3, 3, 3);
    
    
  }
  
  /**
   * Init the graphical user interface
   */
  protected void initGUI(){
    
    FeatureTypeStyle[] types = null;
    Rule[] rules             = null;
    Symbolizer[] symbolizers = null;
    GridBagConstraints c     = null;
    Insets labelInsets       = new Insets(3, 3, 3, 3);
    
    isListening = false;

    initTypePanel();
    initGeometryPanel();
    initTextPanel();
    
    setLayout(new GridBagLayout());
    
    c           = new GridBagConstraints();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.fill      = GridBagConstraints.BOTH;
    c.insets    = labelInsets;
    c.weightx   = 1.0;
    c.weighty   = 1.0;
    c.anchor    = GridBagConstraints.EAST;
    add(typePN, c);
    
    c           = new GridBagConstraints();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.fill      = GridBagConstraints.BOTH;
    c.insets    = labelInsets;
    c.weightx   = 1.0;
    c.weighty   = 1.0;
    c.anchor    = GridBagConstraints.EAST;
    add(geometryPN, c);
    
    isListening = true;
  }
  
  public void refreshGUI(){
    
    FeatureTypeStyle[] types = null;
    Rule[] rules             = null;
    Symbolizer[] symbolizers = null;   
    
    isListening = false;
    
    if (style != null){
      nameLB.setEnabled(true);
      
      nameTF.setText(style.getName());
      
      types = style.getFeatureTypeStyles();
      
      if ((types != null) && (types.length > 0)){
	typeLB.setEnabled(true);
	
	if (type == null){
	  type = types[0];
	}
	
	((DefaultComboBoxModel)typeCB.getModel()).removeAllElements();
	for(int i = 0; i < types.length; i++){
	  ((DefaultComboBoxModel)typeCB.getModel()).addElement(types[i]);
	}
	typeCB.setSelectedItem(type);
	typeCB.setEnabled(true);

	rules = type.getRules();	
	if ((rules != null) && (rules.length > 0)){
	  ruleLB.setEnabled(true);
	  
	  if (rule == null){
	    rule = rules[0];  
	  }
	  
	  ((DefaultComboBoxModel)ruleCB.getModel()).removeAllElements();
          for(int i = 0; i < rules.length; i++){
            ((DefaultComboBoxModel)ruleCB.getModel()).addElement(rules[i]);
	  }
	  ruleCB.setSelectedItem(rule);
	  ruleCB.setEnabled(true);
  
	  symbolizers = rule.getSymbolizers();
	  if ((symbolizers != null)&&(symbolizers.length > 0)){
	    symbolizerLB.setEnabled(true);
	    
	    if (symbolizer == null){
	      symbolizer = symbolizers[0];
	    }
	    
	    ((DefaultComboBoxModel)symbolizerCB.getModel()).removeAllElements();
	    for(int i = 0; i < symbolizers.length; i++){
	      ((DefaultComboBoxModel)symbolizerCB.getModel()).addElement(symbolizers[i]);
            }
	    symbolizerCB.setSelectedItem(symbolizer);
	    symbolizerCB.setEnabled(true);
	    
	    initSymbolizer();
	    
	  } else {
	    ((DefaultComboBoxModel)symbolizerCB.getModel()).removeAllElements();
	    symbolizerCB.setEnabled(false);
	  }
	  
	} else {
	  ((DefaultComboBoxModel)ruleCB.getModel()).removeAllElements();
	  ruleCB.setEnabled(false);
	  ((DefaultComboBoxModel)symbolizerCB.getModel()).removeAllElements();
	  symbolizerCB.setEnabled(false);
	}
	
      } else {
	nameLB.setEnabled(false);
	typeLB.setEnabled(false);
	ruleLB.setEnabled(false);
	symbolizerLB.setEnabled(false);
	
	((DefaultComboBoxModel)typeCB.getModel()).removeAllElements();
	typeCB.setEnabled(false);
	((DefaultComboBoxModel)ruleCB.getModel()).removeAllElements();
	ruleCB.setEnabled(false);
	((DefaultComboBoxModel)symbolizerCB.getModel()).removeAllElements();
	symbolizerCB.setEnabled(false);
      }
    } else {
      nameLB.setEnabled(false);
      nameTF.setText("");
      typeLB.setEnabled(false);
      ruleLB.setEnabled(false);
      symbolizerLB.setEnabled(false);
	
      ((DefaultComboBoxModel)typeCB.getModel()).removeAllElements();
      typeCB.setEnabled(false);
      ((DefaultComboBoxModel)ruleCB.getModel()).removeAllElements();
      ruleCB.setEnabled(false);
      ((DefaultComboBoxModel)symbolizerCB.getModel()).removeAllElements();
      symbolizerCB.setEnabled(false);

      strokeWidthTF.setValue(new Double(1));
      strokeOpacTF.setValue(new Double(1));
      fillOpacTF.setValue(new Double(1));  
    }
    
    initSymbolizer();
    
    isListening = true;
  }
  
  protected void initSymbolizer(){
    
    Graphics2D graphics = null;
    Color strokeColor   = null;
    Stroke stroke       = null;
    Color fillColor     = null;
    
    if (symbolizer instanceof PolygonSymbolizer){

      PolygonSymbolizer psymbolizer = (PolygonSymbolizer) symbolizer;
      
      strokeColorCH.setEnabled(true);
      fillColorCH.setEnabled(true);
      
      if (psymbolizer.getStroke() != null){
	strokeColorCH.setSelected(true);
	strokeColorBT.setEnabled(true);
	strokeWidthLB.setEnabled(true);
	strokeWidthTF.setEnabled(true);
	strokeOpacLB.setEnabled(true);
	strokeOpacTF.setEnabled(true);

        if (psymbolizer.getStroke().getColor() != null){
          strokeColor = AWTGeometryWrap.wrapColor(psymbolizer.getStroke().getColor());
          stroke      = AWTGeometryWrap.wrapStroke(psymbolizer.getStroke());
        } else {
          strokeColor = graphics.getColor();
          stroke      = graphics.getStroke();
        }
        
        graphics = (Graphics2D) strokeColorBI.getGraphics();
        
        graphics.setColor(strokeColor);
        graphics.setStroke(stroke);
        graphics.drawLine(0, 0, buttonImageWidth/3, buttonImageHeight);
        graphics.drawLine(buttonImageWidth/3, buttonImageHeight, 2*buttonImageWidth/3, 0);
        graphics.drawLine(2*buttonImageWidth/3, 0, buttonImageWidth, buttonImageHeight);

        strokeColorBT.repaint();
        
        strokeWidthTF.setValue(Double.parseDouble(psymbolizer.getStroke().getWidth().toString()));
        strokeOpacTF.setValue(Double.parseDouble(psymbolizer.getStroke().getOpacity().toString()));
        
      } else {
	strokeColorCH.setSelected(false);
	strokeColorBT.setEnabled(false);
	strokeWidthLB.setEnabled(false);
	strokeWidthTF.setEnabled(false);
	strokeOpacLB.setEnabled(false);
	strokeOpacTF.setEnabled(false);
      }
      
      if (psymbolizer.getFill() != null){
        if (psymbolizer.getFill().getColor() != null){
          fillColorCH.setSelected(true);
          fillColorBT.setEnabled(true);
          fillOpacLB.setEnabled(true);
          fillOpacTF.setEnabled(true);  
          
          fillColor   = AWTGeometryWrap.wrapColor(psymbolizer.getFill().getColor());

          graphics = (Graphics2D) fillColorBI.getGraphics();
          graphics.setColor(fillColor);
          graphics.fillRect(0, 0, buttonImageWidth - 1, buttonImageHeight - 1);
                
          fillOpacTF.setValue(Double.parseDouble(psymbolizer.getFill().getOpacity().toString()));
                
        } else {
          fillColorCH.setSelected(false);
          fillColorBT.setEnabled(false);
          fillOpacLB.setEnabled(false);
          fillOpacTF.setEnabled(false);    
        }
      }
            
      geometryPN.setEnabled(true);
      
      graphics    = null;
      strokeColor = null;
      stroke      = null;
      fillColor   = null;
      
    } else if (symbolizer instanceof LineSymbolizer){
      
      LineSymbolizer lsymbolizer = (LineSymbolizer) symbolizer;
      
      strokeColorCH.setEnabled(true);
      
      
      if (lsymbolizer.getStroke() != null){
	strokeColorCH.setSelected(true);
	strokeColorBT.setEnabled(true);
	strokeWidthLB.setEnabled(true);
	strokeWidthTF.setEnabled(true);
	strokeOpacLB.setEnabled(true);
	strokeOpacTF.setEnabled(true);
	
	if (lsymbolizer.getStroke().getColor() != null){
          strokeColor = AWTGeometryWrap.wrapColor(lsymbolizer.getStroke().getColor());
          stroke      = AWTGeometryWrap.wrapStroke(lsymbolizer.getStroke());
        } else {
          strokeColor = graphics.getColor();
          stroke      = graphics.getStroke();
        }
        
        graphics = (Graphics2D) strokeColorBI.getGraphics();
        
        graphics.setColor(strokeColor);
        graphics.setStroke(stroke);
        graphics.drawLine(0, 0, buttonImageWidth/3, buttonImageHeight);
        graphics.drawLine(buttonImageWidth/3, buttonImageHeight, 2*buttonImageWidth/3, 0);
        graphics.drawLine(2*buttonImageWidth/3, 0, buttonImageWidth, buttonImageHeight);

        strokeColorBT.repaint();
        
        strokeWidthTF.setValue(Integer.parseInt(lsymbolizer.getStroke().getWidth().toString()));
        strokeOpacTF.setValue(Double.parseDouble(lsymbolizer.getStroke().getOpacity().toString()));
        
	
      } else {
	strokeColorCH.setSelected(false);
	strokeColorBT.setEnabled(false);
	strokeWidthLB.setEnabled(false);
	strokeWidthTF.setEnabled(false);
	strokeWidthTF.setValue(0);
	strokeOpacLB.setEnabled(false);
	strokeOpacTF.setEnabled(false);
	strokeOpacTF.setValue(1.0d);
      }

      fillColorCH.setSelected(false);
      fillColorCH.setEnabled(false);
      fillColorBT.setEnabled(false);
      fillOpacLB.setEnabled(false);
      fillOpacTF.setEnabled(false); 
      fillOpacTF.setValue(1.0d);
      geometryPN.setEnabled(true);
      
    } else if (symbolizer instanceof TextSymbolizer){
      strokeColorCH.setSelected(false);
      strokeColorCH.setEnabled(false);
      strokeColorBT.setEnabled(false);
      strokeWidthLB.setEnabled(false);
      strokeWidthTF.setEnabled(false);
      strokeWidthTF.setValue(0);
      strokeOpacLB.setEnabled(false);
      strokeOpacTF.setEnabled(false);
      strokeOpacTF.setValue(1.0d);
      fillColorCH.setSelected(false);
      fillColorCH.setEnabled(false);
      fillColorCH.setEnabled(false);
      fillColorBT.setEnabled(false);
      fillOpacLB.setEnabled(false);
      fillOpacTF.setEnabled(false);  
      fillOpacTF.setValue(1.0d);
      geometryPN.setEnabled(false);
    } else {
      strokeColorCH.setSelected(false);
      strokeColorCH.setEnabled(false);
      strokeColorBT.setEnabled(false);
      strokeWidthLB.setEnabled(false);
      strokeWidthTF.setEnabled(false);
      strokeWidthTF.setValue(0);
      strokeOpacLB.setEnabled(false);
      strokeOpacTF.setEnabled(false);
      strokeOpacTF.setValue(1.0d);
      fillColorCH.setSelected(false);
      fillColorCH.setEnabled(false);
      fillColorBT.setEnabled(false);
      fillOpacLB.setEnabled(false);
      fillOpacTF.setEnabled(false); 
      fillOpacTF.setValue(1.0d);
      geometryPN.setEnabled(false);
    }
  }
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
//II FIN INITIALISATION                                       II
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII

//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
//EE EVENEMENT                                                EE
//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
  protected void processActionEvent(ActionEvent e){
    
    Color color                   = null;
    LineSymbolizer lsymbolizer    = null;
    PolygonSymbolizer psymbolizer = null;
    
    if (isListening){
      if (e.getActionCommand().equals(STROKE_COLOR_CHANGE_CMD)){

	if (symbolizer instanceof LineSymbolizer){
	  lsymbolizer = (LineSymbolizer)symbolizer;
	  color = JColorChooser.showDialog(null, null, AWTGeometryWrap.wrapColor(lsymbolizer.getStroke().getColor()));
	  lsymbolizer.getStroke().setColor(AWTGeometryWrap.wrapColor(color));
	  refreshGUI();
	  lsymbolizer = null;
	  color       = null;
	} else if (symbolizer instanceof PolygonSymbolizer){
	  psymbolizer = (PolygonSymbolizer)symbolizer;
	  color = JColorChooser.showDialog(null, null, AWTGeometryWrap.wrapColor(psymbolizer.getStroke().getColor()));
	  psymbolizer.getStroke().setColor(AWTGeometryWrap.wrapColor(color));
	  refreshGUI();
	  psymbolizer = null;
	  color       = null;
	}
      }
    }
  }
  
  protected void processChangeEvent(ChangeEvent e){
    
    LineSymbolizer lsymbolizer    = null;
    PolygonSymbolizer psymbolizer = null;
    
    if (isListening){
      if (e.getSource() == strokeWidthTF){
	if (symbolizer instanceof PolygonSymbolizer){
	  psymbolizer = (PolygonSymbolizer) symbolizer;
	  psymbolizer.getStroke().setWidth(AWTGeometryWrap.createExpression((Integer) strokeWidthTF.getValue()));
	  
	} else if (symbolizer instanceof LineSymbolizer){
	  
	}
      } else if (e.getSource() == strokeOpacTF){
        if (symbolizer instanceof PolygonSymbolizer){
          psymbolizer = (PolygonSymbolizer) symbolizer;
	  psymbolizer.getStroke().setOpacity(AWTGeometryWrap.createExpression((Double) strokeOpacTF.getValue()));
	  
	} else if (symbolizer instanceof LineSymbolizer){
	  
	}
      } else if (e.getSource() == fillOpacTF){
        if (symbolizer instanceof PolygonSymbolizer){
          psymbolizer = (PolygonSymbolizer) symbolizer;
	  psymbolizer.getFill().setOpacity(AWTGeometryWrap.createExpression((Double) fillOpacTF.getValue()));
	 
	}
      }
    }
  }
  
  protected void processItemEvent(ItemEvent e){
    if (isListening){
      if (e.getSource() == typeCB){
	type = (FeatureTypeStyle) typeCB.getSelectedItem();
	
	if (type != null){
	  
	  if ((type.getRules() != null) && (type.getRules().length > 0)){
	    rule  = type.getRules()[0];
	  } else {
	    rule  = null;
	  }
	  
	  if ((rule.getSymbolizers() != null) && (rule.getSymbolizers().length > 0)){
	    symbolizer = rule.getSymbolizers()[0];
	  } else {
	    symbolizer = null;
	  }
	  
	} else {
	  rule       = null;
	  symbolizer = null;
	}
	refreshGUI();
	
      } else if (e.getSource() == ruleCB){
	
	rule = (Rule)ruleCB.getSelectedItem();
	
	if (rule != null){
	  if ((rule.getSymbolizers() != null) && (rule.getSymbolizers().length > 0)){
	    symbolizer = rule.getSymbolizers()[0];
	  } else {
	    symbolizer = null;
	  }
	} else {
	  symbolizer = null;
	}
	
	refreshGUI();
	
      } else if (e.getSource() == symbolizerCB){
	symbolizer = (Symbolizer)symbolizerCB.getSelectedItem();
	refreshGUI();
      }
    }
  }
  
  protected void processCaretEvent(CaretEvent e){
    if (isListening){
      if (e.getSource() == nameTF){
	
      }
    }
  }
//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
//EE FIN EVENEMENT                                            EE
//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE

//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA ACCESSEURS                                               AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

  /**
   * Get the style edited within the panel.
   * @return the style edited.
   */
  public Style getStyle(){
    return style;
  }
  
  /**
   * Set the style to edit within the panel.
   * @param style the style to edit.
   */
  public void setStyle(Style style){
    this.style = style;
    
    if ((style.getFeatureTypeStyles() != null) && (style.getFeatureTypeStyles().length > 0)){
      type = style.getFeatureTypeStyles()[0];
    } else {
      type = null;
    }
    
    if (type != null){
      if ((type.getRules() != null) && (type.getRules().length > 0)){
	rule = type.getRules()[0];
      } else {
	rule = null;
      }
    } else {
      rule  = null;
    }
    
    if (rule != null){
      if ((rule.getSymbolizers() != null) && (rule.getSymbolizers().length > 0)){
	symbolizer = rule.getSymbolizers()[0];
      } else {
	symbolizer = null;
      }
    } else {
      symbolizer = null;
    }

    refreshGUI();
  }
  
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA FIN ACCESSEURS                                           AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

}
