package org.jorigin.jis.swing;

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
import java.util.Locale;

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

import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.LineSymbolizer;
import org.geotools.styling.PolygonSymbolizer;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.geotools.styling.Symbolizer;
import org.geotools.styling.TextSymbolizer;
import org.jorigin.jis.JIS;
import org.jorigin.jis.wrap.AWTGeometryWrap;
import org.jorigin.lang.LangResourceBundle;

/**
 * A panel dedicated to the display / edition of a {@link org.geotools.styling.Style style}.
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 * @see JStyleEditorPanel
 * @see JStyleDialog
 */
public class JStylePanel extends JPanel{
  

  private static final long serialVersionUID = JIS.BUILD;

  /**
   * The stroke color change command.
   */
  public static final String STROKE_COLOR_CHANGE_CMD = "STROKE_COLOR_CHANGE";
  
  /**
   * The stroke fill color change command.
   */
  public static final String FILL_COLOR_CHANGE_CMD   = "FILL_COLOR_CHANGE";
 
  private LangResourceBundle lres    = (LangResourceBundle) LangResourceBundle.getBundle(Locale.getDefault());
  
  private Style style             = null;
  
  private FeatureTypeStyle type   = null;
  
  private Rule rule               = null;
  
  private Symbolizer symbolizer   = null;
  
  private JLabel nameLB           = null;
  
  private JTextField nameTF       = null;
  
  private JLabel typeLB           = null;
  
  private JComboBox<FeatureTypeStyle> typeCB        = null;
  
  private JLabel ruleLB           = null;
  
  private JComboBox<Rule> ruleCB        = null;
  
  private JLabel symbolizerLB     = null;
  
  private JComboBox<Symbolizer> symbolizerCB  = null;
   
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
  
  private JPanel geometryPN       = null;
  
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
   * @param style the style that can be edited.
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
    
    this.nameLB       = new JLabel(this.lres.getString("GUI_TITLE_LB")+": ");
    this.nameTF       = new JTextField();
    this.nameTF.addCaretListener(new CaretListener(){

      @Override
      public void caretUpdate(CaretEvent e) {
	processCaretEvent(e);
      }});
    
    this.typeLB        = new JLabel(this.lres.getString("GUI_TYPE_LB")+": ");
    this.ruleLB        = new JLabel(this.lres.getString("GUI_RULE_LB")+": ");
    this.symbolizerLB  = new JLabel(this.lres.getString("GUI_GEOMETRY_LB")+": ");

    if (this.style != null){
      types = this.style.featureTypeStyles().toArray( new FeatureTypeStyle[0] );

      if ((types != null) && (types.length > 0)){
	this.type = types[0];
	this.typeCB = new JComboBox<FeatureTypeStyle>(types);
        this.typeCB.setSelectedItem(this.type);
        this.typeCB.addItemListener(new ItemListener(){

	  @Override
	  public void itemStateChanged(ItemEvent e) {
	    processItemEvent(e);
	  }});
        
	
	rules = this.type.rules().toArray( new Rule[0] );	
	if ((rules != null) && (rules.length > 0)){
	  this.rule = rules[0];
	  this.ruleCB = new JComboBox<Rule>(rules);
	  this.ruleCB.setSelectedItem(this.rule);
	  this.ruleCB.addItemListener(new ItemListener(){

		  @Override
		  public void itemStateChanged(ItemEvent e) {
		    processItemEvent(e);
		  }});
	  
	  symbolizers = this.rule.getSymbolizers();
	  if ((symbolizers != null)&&(symbolizers.length > 0)){
	    this.symbolizer = symbolizers[0];
	    
	    this.symbolizerCB = new JComboBox<Symbolizer>(symbolizers);
	    this.symbolizerCB.setSelectedItem(this.symbolizer);
	    this.symbolizerCB.addItemListener(new ItemListener(){

		  @Override
		  public void itemStateChanged(ItemEvent e) {
		    processItemEvent(e);
		  }});
	    
	  } else {
	    this.symbolizerCB = new JComboBox<Symbolizer>();
	    this.symbolizerCB.addItemListener(new ItemListener(){

		  @Override
		  public void itemStateChanged(ItemEvent e) {
		    processItemEvent(e);
		  }});
	    this.symbolizerCB.setEnabled(false);
	  }
	  
	} else {
	  this.ruleCB = new JComboBox<Rule>();
	  this.ruleCB.addItemListener(new ItemListener(){

		  @Override
		  public void itemStateChanged(ItemEvent e) {
		    processItemEvent(e);
		  }});
	  this.ruleCB.setEnabled(false);
	  this.symbolizerCB = new JComboBox<Symbolizer>();
	  this.symbolizerCB.addItemListener(new ItemListener(){

		  @Override
		  public void itemStateChanged(ItemEvent e) {
		    processItemEvent(e);
		  }});
	  this.symbolizerCB.setEnabled(false);
	}
	
      } else {
	this.typeLB.setEnabled(false);
	this.ruleLB.setEnabled(false);
	
	this.typeCB = new JComboBox<FeatureTypeStyle>();
	this.typeCB.addItemListener(new ItemListener(){

	  @Override
	  public void itemStateChanged(ItemEvent e) {
	    processItemEvent(e);
	  }});
	this.typeCB.setEnabled(false);
	this.ruleCB = new JComboBox<Rule>();
	this.ruleCB.addItemListener(new ItemListener(){

	  @Override
	  public void itemStateChanged(ItemEvent e) {
	    processItemEvent(e);
	  }});
	this.ruleCB.setEnabled(false);
	this.symbolizerCB = new JComboBox<Symbolizer>();
	this.symbolizerCB.addItemListener(new ItemListener(){

	  @Override
	  public void itemStateChanged(ItemEvent e) {
	    processItemEvent(e);
	  }});
	this.symbolizerCB.setEnabled(false);
      }
    } else {
      this.nameLB.setEnabled(false);
      this.nameTF.setEnabled(false);
      this.typeLB.setEnabled(false);
      this.ruleLB.setEnabled(false);
	
      this.typeCB = new JComboBox<FeatureTypeStyle>();
      this.typeCB.setEnabled(false);
      this.typeCB.addItemListener(new ItemListener(){

	  @Override
	  public void itemStateChanged(ItemEvent e) {
	    processItemEvent(e);
	  }});
      this.ruleCB = new JComboBox<Rule>();
      this.ruleCB.setEnabled(false);
      this.ruleCB.addItemListener(new ItemListener(){

	  @Override
	  public void itemStateChanged(ItemEvent e) {
	    processItemEvent(e);
	  }});
      this.symbolizerCB = new JComboBox<Symbolizer>();
      this.symbolizerCB.addItemListener(new ItemListener(){

	  @Override
	  public void itemStateChanged(ItemEvent e) {
	    processItemEvent(e);
	  }});
      this.symbolizerCB.setEnabled(false);
    }
    
    // Initialisation des renderers
    this.typeCB.setRenderer(new DefaultListCellRenderer(){

	  private static final long serialVersionUID = JIS.BUILD;

	  public Component getListCellRendererComponent(
	        JList<?> list,
		Object value,
	        int index,
	        boolean isSelected,
	        boolean cellHasFocus){
	
	JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	
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
    
    this.ruleCB.setRenderer(new DefaultListCellRenderer(){
      /**
		 * 
		 */
	  private static final long serialVersionUID = JIS.BUILD;

	  public Component getListCellRendererComponent(
	        JList<?> list,
		Object value,
	        int index,
	        boolean isSelected,
	        boolean cellHasFocus){
	
	JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	
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
    
    this.symbolizerCB.setRenderer(new DefaultListCellRenderer(){

	  private static final long serialVersionUID = JIS.BUILD;

	  public Component getListCellRendererComponent(
	        JList<?> list,
		Object value,
	        int index,
	        boolean isSelected,
	        boolean cellHasFocus){
	
	JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	
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
    
    this.typePN = new JPanel();
    this.typePN.setBorder(BorderFactory.createTitledBorder(this.lres.getString("GUI_STYLE_LB")));
    this.typePN.setLayout(new GridBagLayout());
    
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
    this.typePN.add(this.nameLB, c);
    
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
    this.typePN.add(this.nameTF, c);
    
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
    this.typePN.add(this.typeLB, c);
    
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
    this.typePN.add(this.typeCB, c);
    
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
    this.typePN.add(this.ruleLB, c);
    
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
    this.typePN.add(this.ruleCB, c);
    
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
    this.typePN.add(this.symbolizerLB, c);
    
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
    this.typePN.add(this.symbolizerCB, c);
  }
  
  protected void initGeometryPanel(){

    GridBagConstraints c = null;
    Insets labelInsets   = new Insets(3, 3, 3, 3);
    
    this.strokeColorCH = new JCheckBox(this.lres.getString("GUI_STROKE_COLOR_LB"));
    this.strokeColorCH.addItemListener(new ItemListener(){

      @Override
      public void itemStateChanged(ItemEvent e) {
	processItemEvent(e);
      }});
    
    this.strokeColorBI   = new BufferedImage(this.buttonImageWidth, this.buttonImageHeight, BufferedImage.TYPE_INT_ARGB);
    
    this.strokeColorBT = new JButton(new ImageIcon(this.strokeColorBI));
    this.strokeColorBT.setActionCommand(STROKE_COLOR_CHANGE_CMD);
    this.strokeColorBT.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e) {
	processActionEvent(e);
      }});
    
    this.strokeWidthLB = new JLabel(this.lres.getString("GUI_STROKE_WIDTH_LB"));
    
    SpinnerModel swidthmodel  = new SpinnerNumberModel(1, //initial value
                                                       0, //min
                                                      10, //max
                                                       1);//step
    this.strokeWidthTF = new JSpinner(swidthmodel);
    this.strokeWidthTF.addChangeListener(new ChangeListener(){
      @Override
      public void stateChanged(ChangeEvent e) {
	processChangeEvent(e);
      }});
    
    this.strokeOpacLB  = new JLabel(this.lres.getString("GUI_STROKE_OPACITY_LB"));
    
    SpinnerModel sopacmodel  = new SpinnerNumberModel(1, //initial value
                                                      0, //min
                                                      1, //max
                                                      0.1);//step
    this.strokeOpacTF  = new JSpinner(sopacmodel);
    this.strokeOpacTF.addChangeListener(new ChangeListener(){
      @Override
      public void stateChanged(ChangeEvent e) {
	processChangeEvent(e);
      }});
    
    this.fillColorCH   = new JCheckBox(this.lres.getString("GUI_FILL_COLOR_LB"));
    
    this.fillColorBI   = new BufferedImage(this.buttonImageWidth, this.buttonImageHeight, BufferedImage.TYPE_INT_ARGB);
    
    this.fillColorBT   = new JButton(new ImageIcon(this.fillColorBI));
    this.fillColorBT.setActionCommand(FILL_COLOR_CHANGE_CMD);
    this.fillColorBT.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e) {
	processActionEvent(e);
      }});
    
    this.fillOpacLB    = new JLabel(this.lres.getString("GUI_FILL_OPACITY_LB"));
    
    SpinnerModel fopacmodel  = new SpinnerNumberModel(1, //initial value
        0, //min
        1, //max
        0.1);//step
    
    this.fillOpacTF    = new JSpinner(fopacmodel);
    this.fillOpacTF.addChangeListener(new ChangeListener(){
      @Override
      public void stateChanged(ChangeEvent e) {
	processChangeEvent(e);
      }});
    
    this.geometryPN = new JPanel();
    this.geometryPN.setBorder(BorderFactory.createTitledBorder(this.lres.getString("GUI_GEOMETRY_LB")));
    this.geometryPN.setLayout(new GridBagLayout());
    
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
    this.geometryPN.add(this.strokeColorCH, c);
    
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
    this.geometryPN.add(this.strokeColorBT, c);
    
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
    this.geometryPN.add(this.strokeWidthLB, c); 
    
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
    this.geometryPN.add(this.strokeWidthTF, c); 
    
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
    this.geometryPN.add(this.strokeOpacLB, c); 
    
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
    this.geometryPN.add(this.strokeOpacTF, c); 
    
    
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
    this.geometryPN.add(this.fillColorCH, c);
    
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
    this.geometryPN.add(this.fillColorBT, c);
    
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
    this.geometryPN.add(this.fillOpacLB, c); 
    
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
    this.geometryPN.add(this.fillOpacTF, c); 
    
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
    this.geometryPN.add(new JPanel(), c); 
  }
  
  protected void initTextPanel(){
    
  }
  
  /**
   * Init the graphical user interface
   */
  protected void initGUI(){
    

    GridBagConstraints c     = null;
    Insets labelInsets       = new Insets(3, 3, 3, 3);
    
    this.isListening = false;

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
    add(this.typePN, c);
    
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
    add(this.geometryPN, c);
    
    this.isListening = true;
  }
  
  /**
   * Refresh the Graphical User Interface of the component.
   */
  public void refreshGUI(){
    
    FeatureTypeStyle[] types = null;
    Rule[] rules             = null;
    Symbolizer[] symbolizers = null;   
    
    this.isListening = false;
    
    if (this.style != null){
      this.nameLB.setEnabled(true);
      
      this.nameTF.setText(this.style.getName());
      
      types = this.style.featureTypeStyles().toArray( new FeatureTypeStyle[0] );
      
      if ((types != null) && (types.length > 0)){
	this.typeLB.setEnabled(true);
	
	if (this.type == null){
	  this.type = types[0];
	}
	
	((DefaultComboBoxModel<FeatureTypeStyle>)this.typeCB.getModel()).removeAllElements();
	for(int i = 0; i < types.length; i++){
	  ((DefaultComboBoxModel<FeatureTypeStyle>)this.typeCB.getModel()).addElement(types[i]);
	}
	this.typeCB.setSelectedItem(this.type);
	this.typeCB.setEnabled(true);

	rules = this.type.rules().toArray( new Rule[0] );	
	if ((rules != null) && (rules.length > 0)){
	  this.ruleLB.setEnabled(true);
	  
	  if (this.rule == null){
	    this.rule = rules[0];  
	  }
	  
	  ((DefaultComboBoxModel<Rule>)this.ruleCB.getModel()).removeAllElements();
          for(int i = 0; i < rules.length; i++){
            ((DefaultComboBoxModel<Rule>)this.ruleCB.getModel()).addElement(rules[i]);
	  }
	  this.ruleCB.setSelectedItem(this.rule);
	  this.ruleCB.setEnabled(true);
  
	  symbolizers = this.rule.getSymbolizers();
	  if ((symbolizers != null)&&(symbolizers.length > 0)){
	    this.symbolizerLB.setEnabled(true);
	    
	    if (this.symbolizer == null){
	      this.symbolizer = symbolizers[0];
	    }
	    
	    ((DefaultComboBoxModel<Symbolizer>)this.symbolizerCB.getModel()).removeAllElements();
	    for(int i = 0; i < symbolizers.length; i++){
	      ((DefaultComboBoxModel<Symbolizer>)this.symbolizerCB.getModel()).addElement(symbolizers[i]);
            }
	    this.symbolizerCB.setSelectedItem(this.symbolizer);
	    this.symbolizerCB.setEnabled(true);
	    
	    initSymbolizer();
	    
	  } else {
	    ((DefaultComboBoxModel<Symbolizer>)this.symbolizerCB.getModel()).removeAllElements();
	    this.symbolizerCB.setEnabled(false);
	  }
	  
	} else {
	  ((DefaultComboBoxModel<Rule>)this.ruleCB.getModel()).removeAllElements();
	  this.ruleCB.setEnabled(false);
	  ((DefaultComboBoxModel<Symbolizer>)this.symbolizerCB.getModel()).removeAllElements();
	  this.symbolizerCB.setEnabled(false);
	}
	
      } else {
	this.nameLB.setEnabled(false);
	this.typeLB.setEnabled(false);
	this.ruleLB.setEnabled(false);
	this.symbolizerLB.setEnabled(false);
	
	((DefaultComboBoxModel<FeatureTypeStyle>)this.typeCB.getModel()).removeAllElements();
	this.typeCB.setEnabled(false);
	((DefaultComboBoxModel<Rule>)this.ruleCB.getModel()).removeAllElements();
	this.ruleCB.setEnabled(false);
	((DefaultComboBoxModel<Symbolizer>)this.symbolizerCB.getModel()).removeAllElements();
	this.symbolizerCB.setEnabled(false);
      }
    } else {
      this.nameLB.setEnabled(false);
      this.nameTF.setText("");
      this.typeLB.setEnabled(false);
      this.ruleLB.setEnabled(false);
      this.symbolizerLB.setEnabled(false);
	
      ((DefaultComboBoxModel<FeatureTypeStyle>)this.typeCB.getModel()).removeAllElements();
      this.typeCB.setEnabled(false);
      ((DefaultComboBoxModel<Rule>)this.ruleCB.getModel()).removeAllElements();
      this.ruleCB.setEnabled(false);
      ((DefaultComboBoxModel<Symbolizer>)this.symbolizerCB.getModel()).removeAllElements();
      this.symbolizerCB.setEnabled(false);

      this.strokeWidthTF.setValue(Double.valueOf(1));
      this.strokeOpacTF.setValue(Double.valueOf(1));
      this.fillOpacTF.setValue(Double.valueOf(1));  
    }
    
    initSymbolizer();
    
    this.isListening = true;
  }
  
  protected void initSymbolizer(){
    
    Graphics2D graphics = null;
    Color strokeColor   = null;
    Stroke stroke       = null;
    Color fillColor     = null;
    
    if (this.symbolizer instanceof PolygonSymbolizer){

      PolygonSymbolizer psymbolizer = (PolygonSymbolizer) this.symbolizer;
      
      this.strokeColorCH.setEnabled(true);
      this.fillColorCH.setEnabled(true);
      
      if (psymbolizer.getStroke() != null){
	this.strokeColorCH.setSelected(true);
	this.strokeColorBT.setEnabled(true);
	this.strokeWidthLB.setEnabled(true);
	this.strokeWidthTF.setEnabled(true);
	this.strokeOpacLB.setEnabled(true);
	this.strokeOpacTF.setEnabled(true);

	graphics = (Graphics2D) this.strokeColorBI.getGraphics();
	
    if (psymbolizer.getStroke().getColor() != null){
      strokeColor = AWTGeometryWrap.wrapColor(psymbolizer.getStroke().getColor());
      stroke      = AWTGeometryWrap.wrapStroke(psymbolizer.getStroke());
    } else {
      strokeColor = graphics.getColor();
      stroke      = graphics.getStroke();
    }
        
        
        
        graphics.setColor(strokeColor);
        graphics.setStroke(stroke);
        graphics.drawLine(0, 0, this.buttonImageWidth/3, this.buttonImageHeight);
        graphics.drawLine(this.buttonImageWidth/3, this.buttonImageHeight, 2*this.buttonImageWidth/3, 0);
        graphics.drawLine(2*this.buttonImageWidth/3, 0, this.buttonImageWidth, this.buttonImageHeight);

        this.strokeColorBT.repaint();
        
        this.strokeWidthTF.setValue(Double.parseDouble(psymbolizer.getStroke().getWidth().toString()));
        this.strokeOpacTF.setValue(Double.parseDouble(psymbolizer.getStroke().getOpacity().toString()));
        
      } else {
	this.strokeColorCH.setSelected(false);
	this.strokeColorBT.setEnabled(false);
	this.strokeWidthLB.setEnabled(false);
	this.strokeWidthTF.setEnabled(false);
	this.strokeOpacLB.setEnabled(false);
	this.strokeOpacTF.setEnabled(false);
      }
      
      if (psymbolizer.getFill().getColor() != null){
	this.fillColorCH.setSelected(true);
	this.fillColorBT.setEnabled(true);
	this.fillOpacLB.setEnabled(true);
	this.fillOpacTF.setEnabled(true);  
	
	fillColor   = AWTGeometryWrap.wrapColor(psymbolizer.getFill().getColor());

	graphics = (Graphics2D) this.fillColorBI.getGraphics();
	graphics.setColor(fillColor);
        graphics.fillRect(0, 0, this.buttonImageWidth - 1, this.buttonImageHeight - 1);
        
        this.fillOpacTF.setValue(Double.parseDouble(psymbolizer.getFill().getOpacity().toString()));
        
      } else {
	this.fillColorCH.setSelected(false);
	this.fillColorBT.setEnabled(false);
	this.fillOpacLB.setEnabled(false);
	this.fillOpacTF.setEnabled(false);    
      }
      
      this.geometryPN.setEnabled(true);
      
      graphics    = null;
      strokeColor = null;
      stroke      = null;
      fillColor   = null;
      
    } else if (this.symbolizer instanceof LineSymbolizer){
      
      LineSymbolizer lsymbolizer = (LineSymbolizer) this.symbolizer;
      
      this.strokeColorCH.setEnabled(true);
      
      
      if (lsymbolizer.getStroke() != null){
	this.strokeColorCH.setSelected(true);
	this.strokeColorBT.setEnabled(true);
	this.strokeWidthLB.setEnabled(true);
	this.strokeWidthTF.setEnabled(true);
	this.strokeOpacLB.setEnabled(true);
	this.strokeOpacTF.setEnabled(true);
	
	graphics = (Graphics2D) this.strokeColorBI.getGraphics();
	
	if (lsymbolizer.getStroke().getColor() != null){
          strokeColor = AWTGeometryWrap.wrapColor(lsymbolizer.getStroke().getColor());
          stroke      = AWTGeometryWrap.wrapStroke(lsymbolizer.getStroke());
        } else {
          strokeColor = graphics.getColor();
          stroke      = graphics.getStroke();
        }
        

        graphics.setColor(strokeColor);
        graphics.setStroke(stroke);
        graphics.drawLine(0, 0, this.buttonImageWidth/3, this.buttonImageHeight);
        graphics.drawLine(this.buttonImageWidth/3, this.buttonImageHeight, 2*this.buttonImageWidth/3, 0);
        graphics.drawLine(2*this.buttonImageWidth/3, 0, this.buttonImageWidth, this.buttonImageHeight);

        this.strokeColorBT.repaint();
        
        this.strokeWidthTF.setValue(Integer.parseInt(lsymbolizer.getStroke().getWidth().toString()));
        this.strokeOpacTF.setValue(Double.parseDouble(lsymbolizer.getStroke().getOpacity().toString()));
        
	
      } else {
	this.strokeColorCH.setSelected(false);
	this.strokeColorBT.setEnabled(false);
	this.strokeWidthLB.setEnabled(false);
	this.strokeWidthTF.setEnabled(false);
	this.strokeWidthTF.setValue(0);
	this.strokeOpacLB.setEnabled(false);
	this.strokeOpacTF.setEnabled(false);
	this.strokeOpacTF.setValue(1.0d);
      }

      this.fillColorCH.setSelected(false);
      this.fillColorCH.setEnabled(false);
      this.fillColorBT.setEnabled(false);
      this.fillOpacLB.setEnabled(false);
      this.fillOpacTF.setEnabled(false); 
      this.fillOpacTF.setValue(1.0d);
      this.geometryPN.setEnabled(true);
      
    } else if (this.symbolizer instanceof TextSymbolizer){
      this.strokeColorCH.setSelected(false);
      this.strokeColorCH.setEnabled(false);
      this.strokeColorBT.setEnabled(false);
      this.strokeWidthLB.setEnabled(false);
      this.strokeWidthTF.setEnabled(false);
      this.strokeWidthTF.setValue(0);
      this.strokeOpacLB.setEnabled(false);
      this.strokeOpacTF.setEnabled(false);
      this.strokeOpacTF.setValue(1.0d);
      this.fillColorCH.setSelected(false);
      this.fillColorCH.setEnabled(false);
      this.fillColorCH.setEnabled(false);
      this.fillColorBT.setEnabled(false);
      this.fillOpacLB.setEnabled(false);
      this.fillOpacTF.setEnabled(false);  
      this.fillOpacTF.setValue(1.0d);
      this.geometryPN.setEnabled(false);
    } else {
      this.strokeColorCH.setSelected(false);
      this.strokeColorCH.setEnabled(false);
      this.strokeColorBT.setEnabled(false);
      this.strokeWidthLB.setEnabled(false);
      this.strokeWidthTF.setEnabled(false);
      this.strokeWidthTF.setValue(0);
      this.strokeOpacLB.setEnabled(false);
      this.strokeOpacTF.setEnabled(false);
      this.strokeOpacTF.setValue(1.0d);
      this.fillColorCH.setSelected(false);
      this.fillColorCH.setEnabled(false);
      this.fillColorBT.setEnabled(false);
      this.fillOpacLB.setEnabled(false);
      this.fillOpacTF.setEnabled(false); 
      this.fillOpacTF.setValue(1.0d);
      this.geometryPN.setEnabled(false);
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
    
    if (this.isListening){
      if (e.getActionCommand().equals(STROKE_COLOR_CHANGE_CMD)){

	if (this.symbolizer instanceof LineSymbolizer){
	  lsymbolizer = (LineSymbolizer)this.symbolizer;
	  color = JColorChooser.showDialog(null, null, AWTGeometryWrap.wrapColor(lsymbolizer.getStroke().getColor()));
	  lsymbolizer.getStroke().setColor(AWTGeometryWrap.wrapColor(color));
	  refreshGUI();
	  lsymbolizer = null;
	  color       = null;
	} else if (this.symbolizer instanceof PolygonSymbolizer){
	  psymbolizer = (PolygonSymbolizer)this.symbolizer;
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
    
    PolygonSymbolizer psymbolizer = null;
    
    if (this.isListening){
      if (e.getSource() == this.strokeWidthTF){
	if (this.symbolizer instanceof PolygonSymbolizer){
	  psymbolizer = (PolygonSymbolizer) this.symbolizer;
	  psymbolizer.getStroke().setWidth(AWTGeometryWrap.createExpression((Integer) this.strokeWidthTF.getValue()));
	  
	} else if (this.symbolizer instanceof LineSymbolizer){
	  
	}
      } else if (e.getSource() == this.strokeOpacTF){
        if (this.symbolizer instanceof PolygonSymbolizer){
          psymbolizer = (PolygonSymbolizer) this.symbolizer;
	  psymbolizer.getStroke().setOpacity(AWTGeometryWrap.createExpression((Double) this.strokeOpacTF.getValue()));
	  
	} else if (this.symbolizer instanceof LineSymbolizer){
	  
	}
      } else if (e.getSource() == this.fillOpacTF){
        if (this.symbolizer instanceof PolygonSymbolizer){
          psymbolizer = (PolygonSymbolizer) this.symbolizer;
	  psymbolizer.getFill().setOpacity(AWTGeometryWrap.createExpression((Double) this.fillOpacTF.getValue()));
	 
	}
      }
    }
  }
  
  protected void processItemEvent(ItemEvent e){
    if (this.isListening){
      if (e.getSource() == this.typeCB){
	this.type = (FeatureTypeStyle) this.typeCB.getSelectedItem();
	
	if (this.type != null){
	  
	  if ((this.type.rules().toArray( new Rule[0] ) != null) && (this.type.rules().toArray( new Rule[0] ).length > 0)){
	    this.rule  = this.type.rules().toArray( new Rule[0] )[0];
	  } else {
	    this.rule  = null;
	  }
	  
	  if ((this.rule.getSymbolizers() != null) && (this.rule.getSymbolizers().length > 0)){
	    this.symbolizer = this.rule.getSymbolizers()[0];
	  } else {
	    this.symbolizer = null;
	  }
	  
	} else {
	  this.rule       = null;
	  this.symbolizer = null;
	}
	refreshGUI();
	
      } else if (e.getSource() == this.ruleCB){
	
	this.rule = (Rule)this.ruleCB.getSelectedItem();
	
	if (this.rule != null){
	  if ((this.rule.getSymbolizers() != null) && (this.rule.getSymbolizers().length > 0)){
	    this.symbolizer = this.rule.getSymbolizers()[0];
	  } else {
	    this.symbolizer = null;
	  }
	} else {
	  this.symbolizer = null;
	}
	
	refreshGUI();
	
      } else if (e.getSource() == this.symbolizerCB){
	this.symbolizer = (Symbolizer)this.symbolizerCB.getSelectedItem();
	refreshGUI();
      }
    }
  }
  
  protected void processCaretEvent(CaretEvent e){
    if (this.isListening){
      if (e.getSource() == this.nameTF){
	
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
    return this.style;
  }
  
  /**
   * Set the style to edit within the panel.
   * @param style the style to edit.
   */
  public void setStyle(Style style){
    this.style = style;
    
    if ((style.featureTypeStyles().toArray( new FeatureTypeStyle[0] ) != null) && (style.featureTypeStyles().toArray( new FeatureTypeStyle[0] ).length > 0)){
      this.type = style.featureTypeStyles().toArray( new FeatureTypeStyle[0] )[0];
    } else {
      this.type = null;
    }
    
    if (this.type != null){
      if ((this.type.rules().toArray( new Rule[0] ) != null) && (this.type.rules().toArray( new Rule[0] ).length > 0)){
	this.rule = this.type.rules().toArray( new Rule[0] )[0];
      } else {
	this.rule = null;
      }
    } else {
      this.rule  = null;
    }
    
    if (this.rule != null){
      if ((this.rule.getSymbolizers() != null) && (this.rule.getSymbolizers().length > 0)){
	this.symbolizer = this.rule.getSymbolizers()[0];
      } else {
	this.symbolizer = null;
      }
    } else {
      this.symbolizer = null;
    }

    refreshGUI();
  }
  
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA FIN ACCESSEURS                                           AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA

}
