package org.jorigin.jis.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.geotools.styling.Style;
import org.jorigin.jis.JIS;
import org.jorigin.lang.LangResourceBundle;

/**
 * A dialog dedicated to the display / edition of {@link org.geotools.styling.Style styles}.
 * @author Julien Seinturier - (c) 2010 - JOrigin project - <a href="http://www.jorigin.org">http:/www.jorigin.org</a>
 * @since 1.0.0
 * @see JStyleEditorPanel
 * @see JStylePanel
 */
public class JStyleDialog extends JDialog {

  private static final long serialVersionUID = JIS.BUILD;

  /**
   * The ok command.
   */
  public static final String OK_CMD       = "OK";
  
  /**
   * The cancel command.
   */
  public static final String CANCEL_CMD   = "CANCEL";
  
  private LangResourceBundle lres    = (LangResourceBundle) LangResourceBundle.getBundle(Locale.getDefault());
  
  private JStyleEditorPanel styleEditorPN = null;
  
  private JPanel buttonPanel              = null;
  
  private JButton okButton                = null;
  
  private JButton cancelButton            = null;
  
  private List<Style> styles              = null;
  
  /**
   * Create a new style dialog that display the given {@link org.geotools.styling.Style styles}.
   * @param styles the {@link org.geotools.styling.Style styles} to display.
   */
  public JStyleDialog(List<Style> styles){
    this(styles, null);
  }

  /**
   * Create a new style dialog that display the given {@link org.geotools.styling.Style styles}.
   * @param styles the {@link org.geotools.styling.Style styles} to display.
   * @param owner the owner of this dialog.
   */
  public JStyleDialog(List<Style> styles, Frame owner){
    this(styles, null, false);
  }

  /**
   * Create a new style dialog that display the given {@link org.geotools.styling.Style styles}.
   * @param styles the {@link org.geotools.styling.Style styles} to display.
   * @param owner the owner of this dialog.
   * @param modal <code>true</code> if this dialog is modal and <code>false</code> otherwise.
   */
  public JStyleDialog(List<Style> styles, Frame owner, boolean modal){
    super(owner, modal);
    this.styles = styles;
    initGUI();
  }
  
  protected void initGUI() {
    this.styleEditorPN = new JStyleEditorPanel(this.styles);
    
    this.okButton        = new JButton(new ImageIcon(getClass().getClassLoader().getResource("icon/button_ok.png")));
    this.okButton.setText(this.lres.getString("GUI_OK_LB"));
    this.okButton.setToolTipText(this.lres.getString("GUI_CANCEL_LB"));
    this.okButton.addActionListener(new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e) {
        setVisible(false);
	dispose();
      }});
 
    this.cancelButton    = new JButton(new ImageIcon(getClass().getClassLoader().getResource("icon/button_cancel.png")));
    this.cancelButton.setText(this.lres.getString("GUI_CANCEL_LB"));
    this.cancelButton.setToolTipText(this.lres.getString("GUI_CANCEL_LB"));
        
    this.cancelButton.addActionListener(new ActionListener(){

	@Override
	public void actionPerformed(ActionEvent e) {
	  setVisible(false);
	  dispose();
	}});
    
    this.buttonPanel = new JPanel();
    this.buttonPanel.setLayout(new BorderLayout());
    this.buttonPanel.add(this.okButton, BorderLayout.EAST);
    this.buttonPanel.add(this.cancelButton, BorderLayout.WEST);
    
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(this.styleEditorPN, BorderLayout.CENTER);
    getContentPane().add(this.buttonPanel, BorderLayout.SOUTH);
    setSize(new Dimension(640, 480)); 
  }
  
  /**
   * Add an {@link java.awt.event.ActionListener action listener} to this component.
   * @param l the {@link java.awt.event.ActionListener action listener} to add.
   * @see #removeActionListener(ActionListener)
   */
  public void addActionListener(ActionListener l){
    this.okButton.addActionListener(l);
    this.cancelButton.addActionListener(l);
  }
  
  /**
   * Remove the given {@link java.awt.event.ActionListener action listener} from this component.
   * @param l the {@link java.awt.event.ActionListener action listener} to remove.
   * @see #addActionListener(ActionListener)
   */
  public void removeActionListener(ActionListener l){
    this.okButton.removeActionListener(l);
    this.cancelButton.removeActionListener(l);
  }
}
