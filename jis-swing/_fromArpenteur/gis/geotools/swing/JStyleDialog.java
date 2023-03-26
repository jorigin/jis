package org.arpenteur.gis.geotools.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.arpenteur.common.ihm.icon.IconServer;

import org.geotools.styling.Style;

public class JStyleDialog extends JDialog {

  public static final String OK_CMD       = "OK";
  
  public static final String CANCEL_CMD   = "CANCEL";
  
   
  private JStyleEditorPanel styleEditorPN = null;
  
  private JPanel buttonPanel              = null;
  
  private JButton okButton                = null;
  
  private JButton cancelButton            = null;
  
  private List<Style> styles              = null;
  
  public JStyleDialog(List<Style> styles){
    this(styles, null);
  }

  public JStyleDialog(List<Style> styles, Frame owner){
    this(styles, null, false);
  }
  
  public JStyleDialog(List<Style> styles, Frame owner, boolean modal){
    super(owner, modal);
    this.styles = styles;
    initGUI();
  }
  
  protected void initGUI() {
    styleEditorPN = new JStyleEditorPanel(styles);
    
    okButton        = new JButton(IconServer.getIcon("crystalsvg/16x16/actions/adept_commit.png"));
    okButton.setText("GUI_OK_LB");
    okButton.setToolTipText("GUI_CANCEL_LB");
    okButton.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
	dispose();
      }});
 
    cancelButton    = new JButton(IconServer.getIcon("crystalsvg/16x16/actions/cancel.png"));
    cancelButton.setText("GUI_CANCEL_LB");
    cancelButton.setToolTipText("GUI_CANCEL_LB");
        
    cancelButton.addActionListener(new ActionListener(){

	@Override
	public void actionPerformed(ActionEvent e) {
	  setVisible(false);
	  dispose();
	}});
    
    buttonPanel = new JPanel();
    buttonPanel.setLayout(new BorderLayout());
    buttonPanel.add(okButton, BorderLayout.EAST);
    buttonPanel.add(cancelButton, BorderLayout.WEST);
    
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(styleEditorPN, BorderLayout.CENTER);
    getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    setSize(new Dimension(640, 480)); 
  }
  
  public void showDialog(){
    setVisible(true);
  }
  
  public void addActionListener(ActionListener l){
    okButton.addActionListener(l);
    cancelButton.addActionListener(l);
  }
  
  public void removeActionListener(ActionListener l){
    okButton.removeActionListener(l);
    cancelButton.removeActionListener(l);
  }
}
