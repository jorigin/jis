package org.arpenteur.gis.geotools.swing;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.arpenteur.common.ihm.JRenderingHintsPanel;

import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;

public class JGTRendererPanel extends JPanel {

  
  private String[] scaleComputationMethodValues = new String[]{StreamingRenderer.SCALE_OGC, StreamingRenderer.SCALE_ACCURATE};
  
  private String[] textRenderingValues          = new String[]{StreamingRenderer.TEXT_RENDERING_STRING, StreamingRenderer.TEXT_RENDERING_OUTLINE};
  
  private GTRenderer renderer                   = null; 
 
/*  
  StreamingRenderer.SCALE_COMPUTATION_METHOD_KEY
  StreamingRenderer.DPI_KEY, 90);
  StreamingRenderer.MEMORY_PRE_LOADING_KEY, Boolean.FALSE);   
  StreamingRenderer.OPTIMIZED_DATA_LOADING_KEY, Boolean.TRUE); 
  StreamingRenderer.TEXT_RENDERING_KEY, StreamingRenderer.TEXT_RENDERING_STRING);
*/  
  
  private JRenderingHintsPanel j2dHintsPN       = null;
  
  private JPanel gtHintsPN                      = null;
  
  private JLabel scaleComputationMethodLB    = null;
  private JLabel dpiLB                       = null;
  private JCheckBox memoryPreLoadingCH       = null;
  private JCheckBox optimizedDataLoadingCH   = null;
  private JLabel textRenderingLB             = null;
  
  private JComboBox scaleComputationMethodCB = null;
  private JSpinner dpiSI                     = null;
  private JComboBox textRenderingCB          = null;
  
  
  
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC CONSTRUCTEUR                                             CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  
  /**
   * Create a new renderer editor panel.
   * @param renderer the renderer to edit.
   */
  public JGTRendererPanel(GTRenderer renderer){
    super();
    if (renderer != null){
      setRenderer(renderer);
    } else {
      
      StreamingRenderer r = new StreamingRenderer();
      r.setRendererHints(new HashMap<String, Object>());
      r.setJava2DHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT));
      
      setRenderer(r);
    }
  }
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC FIN CONSTRUCTEUR                                         CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC

//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
//II INITIALISATION                                           II
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
  protected void initGUI(){
    
    GridBagConstraints c = null;
    
    removeAll();
    
    ItemListener itemListener = new ItemListener(){
      public void itemStateChanged(ItemEvent e) {
	processItemEvent(e);
      }};
      
    ChangeListener changeListener = new ChangeListener(){

      @Override
      public void stateChanged(ChangeEvent e) {
	processChangeEvent(e);
      } 
    };
    
    j2dHintsPN = new JRenderingHintsPanel(renderer.getJava2DHints());
    j2dHintsPN.setBorder(BorderFactory.createTitledBorder("GUI_GENERAL_LB"));
    
    setLayout(new GridBagLayout());
    
    c           = new GridBagConstraints();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.fill      = GridBagConstraints.HORIZONTAL;
    c.insets    = new Insets(3, 3, 3, 3);
    c.weightx   = 1.0;
    c.weighty   = 0.0;
    c.anchor    = GridBagConstraints.CENTER;
    add(j2dHintsPN, c);
    
    if (renderer instanceof StreamingRenderer){
      gtHintsPN = new JPanel();
      gtHintsPN.setLayout(new GridBagLayout());
      gtHintsPN.setBorder(BorderFactory.createTitledBorder("GUI_GT_RENDERING_LB"));   
      
      
      scaleComputationMethodLB = new JLabel("GUI_GT_SCALE_COMPUTATION_METHOD_LB");
      dpiLB                    = new JLabel("GUI_GT_DPI_LB");
      
      memoryPreLoadingCH       = new JCheckBox("GUI_GT_MEMORY_PRE_LOADING_LB");
      memoryPreLoadingCH.addChangeListener(changeListener);
      
      optimizedDataLoadingCH   = new JCheckBox("GUI_GT_OPTIMIZED_DATA_LOADING_LB");
      optimizedDataLoadingCH.addChangeListener(changeListener);
      
      textRenderingLB          = new JLabel("GUI_GT_TEXT_RENDERING_LB");
      
      scaleComputationMethodCB = new JComboBox(scaleComputationMethodValues);
      scaleComputationMethodCB.setRenderer(new DefaultListCellRenderer(){
	public Component getListCellRendererComponent(JList list, Object value,
	    int index, boolean isSelecteded,
	    boolean cellHasFocus){

	  JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelecteded, cellHasFocus);


	  if (value != null){
	    if (value.equals(StreamingRenderer.SCALE_OGC)){
	      label.setText("GUI_GT_SCALE_OGC_LB");
	    } else if (value.equals(StreamingRenderer.SCALE_ACCURATE)){
	      label.setText("GUI_GT_SCALE_ACCURATE_LB");
	    } 
	  }

	  return label;

	}
      });
      scaleComputationMethodCB.addItemListener(itemListener);
      
      
      dpiSI                    = new JSpinner();
      
      
      textRenderingCB          = new JComboBox(textRenderingValues);
      textRenderingCB.setRenderer(new DefaultListCellRenderer(){
	public Component getListCellRendererComponent(JList list, Object value,
	    int index, boolean isSelecteded,
	    boolean cellHasFocus){

	  JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelecteded, cellHasFocus);


	  if (value != null){
	    if (value.equals(StreamingRenderer.TEXT_RENDERING_STRING)){
	      label.setText("GUI_GT_TEXT_STRING_LB");
	    } else if (value.equals(StreamingRenderer.TEXT_RENDERING_OUTLINE)){
	      label.setText("GUI_GT_TEXT_OUTLINE_LB");
	    } 
	  }

	  return label;

	}
      });
      textRenderingCB.addItemListener(itemListener);
    }

  }
  
  public void refreshGUI(){
    
    j2dHintsPN.refreshGUI();
  }
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
//II FIN INITIALISATION                                       II
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII

//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA ACCESSEURS                                               AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  
  /**
   * Set the renderer to edit within this panel.
   * @param the renderer to edit.
   */
  public void setRenderer(GTRenderer renderer){
    if (renderer != null){
      
      if (renderer.getRendererHints() == null){
	renderer.setRendererHints(new HashMap<Object, Object>());
      }
      
      if (renderer.getJava2DHints() == null){
	renderer.setJava2DHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT));
      }
      
      this.renderer = renderer;
      initGUI();
    }
  }
  
  /**
   * Get the renderer edited within this panel.
   * @return the renderer edited.
   */
  public GTRenderer getRenderer(){
    return this.renderer;
  }
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA FIN ACCESSEURS                                           AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  
//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
//EE EVENEMENT                                                EE
//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
  protected void processItemEvent(ItemEvent e){
    
  }
  
  protected void processChangeEvent(ChangeEvent e){
    
  }
//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
//EE FIN EVENEMENT                                            EE
//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
  
  
}
