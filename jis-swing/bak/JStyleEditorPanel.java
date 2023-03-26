package org.jorigin.jis.swing;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.geotools.styling.Style;
import org.jorigin.jis.JIS;

/**
 * A panel dedicated to the display / edition of {@link org.geotools.styling.Style styles}.
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 * @see JStylePanel
 * @see JStyleDialog
 */
public class JStyleEditorPanel extends JPanel {


  private static final long serialVersionUID = JIS.BUILD;

  private List<Style> styles     = null;
  
  private Style style            = null;
  
  private JList<Style> styleLT   = null;
  
  private JScrollPane styleSP    = null;
  
  private JStylePanel stylePN    = null;
  
  private boolean isListening    = true;
  
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC CONSTRUCTEUR                                             CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  
  /**
   * Create a new style editor affected to the given style list.
   * @param styles the style that can be edited.
   */
  public JStyleEditorPanel(List<Style> styles){
    super();
    if (styles != null){
      this.styles = styles;
    } else {
      this.styles = new ArrayList<Style>();
    }
    initGUI();
  }
  
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC FIN CONSTRUCTEUR                                         CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC

//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
//II INITIALISATION                                           II
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
  
  /**
   * Init the graphical user interface
   */
  protected void initGUI(){
    
    GridBagConstraints c = null;
    
    this.isListening = false;
    
    this.styleLT = new JList<Style>((Style[])this.styles.toArray(new Style[this.styles.size()]));
    this.styleLT.addListSelectionListener(new ListSelectionListener(){

      @Override
      public void valueChanged(ListSelectionEvent e) {
	processListSelectionEvent(e);
      }});
    
    this.styleLT.setCellRenderer(new DefaultListCellRenderer(){

	private static final long serialVersionUID = JIS.BUILD;

	public Component getListCellRendererComponent(
	        JList<?> list,
		Object value,
	        int index,
	        boolean isSelected,
	        boolean cellHasFocus){
	
	JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
	
	label.setText(((Style)value).getName());
	
	return label;
      } 
    });
    
    this.styleSP = new JScrollPane(this.styleLT);
    this.styleSP.setBorder(BorderFactory.createBevelBorder(2));
    
    if (this.styles.size() > 0){
      this.style = this.styles.get(0);
      this.stylePN = new JStylePanel(this.style);
    } else {
      this.stylePN = new JStylePanel(null);
    }
    
    setLayout(new GridBagLayout()); 
    
    c           = new GridBagConstraints();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 2;
    c.gridwidth = 1;
    c.fill      = GridBagConstraints.BOTH;
    c.insets    = new Insets(3, 3, 3, 3);
    c.weightx   = 1.0;
    c.weighty   = 1.0;
    c.anchor    = GridBagConstraints.EAST;
    add(this.styleSP, c);
    
    c           = new GridBagConstraints();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.fill      = GridBagConstraints.HORIZONTAL;
    c.insets    = new Insets(3, 3, 3, 3);
    c.weightx   = 1.0;
    c.weighty   = 0.0;
    c.anchor    = GridBagConstraints.EAST;
    add(this.stylePN, c);
    
    c           = new GridBagConstraints();
    c.gridx     = GridBagConstraints.RELATIVE;
    c.gridy     = GridBagConstraints.RELATIVE;
    c.gridheight= 1;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.fill      = GridBagConstraints.BOTH;
    c.insets    = new Insets(3, 3, 3, 3);
    c.weightx   = 1.0;
    c.weighty   = 1.0;
    c.anchor    = GridBagConstraints.EAST;
    add(new JPanel(), c);
    
    this.isListening = true;
  }
  
  /**
   * Refresh the Graphical User Interface of this component.
   */
  public void refreshGUI(){
    this.styleLT.repaint();
    this.stylePN.refreshGUI();
  }
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
//II FIN INITIALISATION                                       II
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII

//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
//EE EVENEMENT                                                EE
//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
  
  protected void processListSelectionEvent(ListSelectionEvent e){
    if (this.isListening){
      if (e.getSource() == this.styleLT){
	this.style = (Style) this.styleLT.getSelectedValue();
	this.stylePN.setStyle(this.style);
	refreshGUI();
      }
    }
  }
  
//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
//EE FIN EVENEMENT                                            EE
//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE

  
}
