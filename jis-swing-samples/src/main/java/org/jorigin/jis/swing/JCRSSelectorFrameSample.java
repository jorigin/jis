package org.jorigin.jis.swing;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * A class that illustrates the use of the {@link JCRSSelectorFrame}.
 * @author Julien Seinturier
 *
 */
public class JCRSSelectorFrameSample {

	JCRSSelectorFrame frame = null;

	/**
	 * The main method.
	 * @param args the arguments.
	 */
	public static void main(String[] args){

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		}  

		JCRSSelectorFrame selector = new JCRSSelectorFrame();
		selector.showFrame();
	}
}
