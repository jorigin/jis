package org.jorigin.jis.swing;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.geotools.renderer.GTRenderer;
import org.geotools.renderer.lite.StreamingRenderer;
import org.jorigin.jis.JIS;
import org.jorigin.lang.LangResourceBundle;
import org.jorigin.swing.JRenderingHintsPanel;

/**
 * A panel dedicated to the display and the edition of {@link org.geotools.renderer.GTRenderer geotools renderer}.
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 *
 */
public class JGTRendererPanel extends JPanel {

  private static final long serialVersionUID = JIS.BUILD;

  private LangResourceBundle lres    = (LangResourceBundle) LangResourceBundle.getBundle(Locale.getDefault());
  
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
  

  private JCheckBox memoryPreLoadingCH       = null;
  private JCheckBox optimizedDataLoadingCH   = null;
  
  private JComboBox<String> scaleComputationMethodCB = null;
  private JComboBox<String> textRenderingCB          = null;
  
  
  
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
    
    this.j2dHintsPN = new JRenderingHintsPanel(this.renderer.getJava2DHints());
    this.j2dHintsPN.setBorder(BorderFactory.createTitledBorder(this.lres.getString("GUI_GENERAL_LB")));
    
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
    add(this.j2dHintsPN, c);
    
    if (this.renderer instanceof StreamingRenderer){
      this.gtHintsPN = new JPanel();
      this.gtHintsPN.setLayout(new GridBagLayout());
      this.gtHintsPN.setBorder(BorderFactory.createTitledBorder(this.lres.getString("GUI_GT_RENDERING_LB")));   
      
      this.memoryPreLoadingCH       = new JCheckBox(this.lres.getString("GUI_GT_MEMORY_PRE_LOADING_LB"));
      this.memoryPreLoadingCH.addChangeListener(changeListener);
      
      this.optimizedDataLoadingCH   = new JCheckBox(this.lres.getString("GUI_GT_OPTIMIZED_DATA_LOADING_LB"));
      this.optimizedDataLoadingCH.addChangeListener(changeListener);
      
      this.scaleComputationMethodCB = new JComboBox<String>(this.scaleComputationMethodValues);
      this.scaleComputationMethodCB.setRenderer(new DefaultListCellRenderer(){

		private static final long serialVersionUID = JIS.BUILD;

	public Component getListCellRendererComponent(JList<?> list, Object value,
	    int index, boolean isSelected,
	    boolean cellHasFocus){

	  JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);


	  if (value != null){
	    if (value.equals(StreamingRenderer.SCALE_OGC)){
	      label.setText(JGTRendererPanel.this.lres.getString("GUI_GT_SCALE_OGC_LB"));
	    } else if (value.equals(StreamingRenderer.SCALE_ACCURATE)){
	      label.setText(JGTRendererPanel.this.lres.getString("GUI_GT_SCALE_ACCURATE_LB"));
	    } 
	  }

	  return label;

	}
      });
      this.scaleComputationMethodCB.addItemListener(itemListener);    
      
      this.textRenderingCB          = new JComboBox<String>(this.textRenderingValues);
      this.textRenderingCB.setRenderer(new DefaultListCellRenderer(){
	/**
		 * 
		 */
		private static final long serialVersionUID = JIS.BUILD;

	public Component getListCellRendererComponent(JList<?> list, Object value,
	    int index, boolean isSelected,
	    boolean cellHasFocus){

	  JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);


	  if (value != null){
	    if (value.equals(StreamingRenderer.TEXT_RENDERING_STRING)){
	      label.setText(JGTRendererPanel.this.lres.getString("GUI_GT_TEXT_STRING_LB"));
	    } else if (value.equals(StreamingRenderer.TEXT_RENDERING_OUTLINE)){
	      label.setText(JGTRendererPanel.this.lres.getString("GUI_GT_TEXT_OUTLINE_LB"));
	    } 
	  }

	  return label;

	}
      });
      this.textRenderingCB.addItemListener(itemListener);
    }

  }
  
  /**
   * Refresh the Graphical User Interface.
   */
  public void refreshGUI(){
    
    this.j2dHintsPN.refreshGUI();
  }
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
//II FIN INITIALISATION                                       II
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII

//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
//AA ACCESSEURS                                               AA
//AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
  
  /**
   * Set the renderer to edit within this panel.
   * @param renderer the renderer to edit.
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
