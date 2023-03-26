package org.arpenteur.gis.geotools.swing;

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

public class JStyleEditorPanel extends JPanel {

  private List<Style> styles     = null;
  
  private Style style            = null;
  
  private JList styleLT          = null;
  
  private JScrollPane styleSP    = null;
  
  private JStylePanel stylePN    = null;
  
  private boolean isListening    = true;
  
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
//CC CONSTRUCTEUR                                             CC
//CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC
  
  /**
   * Create a new style editor affected to the given style list.
   * @param the style that can be edited.
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
    
    isListening = false;
    
    styleLT = new JList(styles.toArray());
    styleLT.addListSelectionListener(new ListSelectionListener(){

      @Override
      public void valueChanged(ListSelectionEvent e) {
	processListSelectionEvent(e);
      }});
    
    styleLT.setCellRenderer(new DefaultListCellRenderer(){
      public Component getListCellRendererComponent(
	        JList list,
		Object value,
	        int index,
	        boolean isSelecteded,
	        boolean cellHasFocus){
	
	JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelecteded, cellHasFocus);
	
	label.setText(((Style)value).getName());
	
	return label;
      } 
    });
    
    styleSP = new JScrollPane(styleLT);
    styleSP.setBorder(BorderFactory.createBevelBorder(2));
    
    if (styles.size() > 0){
      style = styles.get(0);
      stylePN = new JStylePanel(style);
    } else {
      stylePN = new JStylePanel(null);
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
    add(styleSP, c);
    
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
    add(stylePN, c);
    
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
    
    isListening = true;
  }
  
  public void refreshGUI(){
    styleLT.repaint();
    stylePN.refreshGUI();
  }
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII
//II FIN INITIALISATION                                       II
//IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII

//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
//EE EVENEMENT                                                EE
//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
  
  protected void processListSelectionEvent(ListSelectionEvent e){
    if (isListening){
      if (e.getSource() == styleLT){
	style = (Style) styleLT.getSelectedValue();
	stylePN.setStyle(style);
	refreshGUI();
      }
    }
  }
  
//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE
//EE FIN EVENEMENT                                            EE
//EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE

  
}
