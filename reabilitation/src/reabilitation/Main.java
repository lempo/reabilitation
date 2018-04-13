package reabilitation;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.*;

import defaults.InterfaceTextDefaults;
import dialogs.Dialogs;
import exception.DiskPermissionsException;
import exception.HddSerialScriptException;
import exception.KeyAlreadyRegisteredException;
import exception.KeyNotExistException;
import exception.KeyNotRegisteredException;
import exception.LisenceExpiredException;
import exception.ProgramFilesBrokenException;
import exception.ServerConnectionException;
import reabilitation.utils.Utils;

/**
 * 
 * 
 * @author Pokrovskaya Oksana
 */

public class Main {
	public static void main(String[] args) {

		try {
			HTTPClient.setSERVER(Utils.getAppServer());
		} catch (ProgramFilesBrokenException e1) {
			e1.printStackTrace();
			Dialogs.showFilesBrokenErrorDialog(e1);
			return;
		}
		File f = new File(Utils.getFilePath() + "/config");
		
		// that means already registered because config exists
		if (f.exists()) {
			// check key
			try {
				String key = Utils.getLicenceKey();
				String username = Utils.getLicenceUserName();
				HTTPClient.checkKey(key, username);
				JFrame app = new Reabilitation();
				app.setVisible(true);
			} catch (DiskPermissionsException e) {
				e.printStackTrace();
				Dialogs.showDiskPermissionsErrorDialog(e);
			} catch (ProgramFilesBrokenException e) {
				e.printStackTrace();
				Dialogs.showFilesBrokenErrorDialog(e);
			} catch (HddSerialScriptException e) {
				e.printStackTrace();
				Dialogs.showHddSerialErrorDialog(e);
			} catch (ServerConnectionException e) {
				e.printStackTrace();
				Dialogs.showServerConnectionErrorDialog(e);
			} catch (KeyNotRegisteredException e) {
				e.printStackTrace();
				Dialogs.showNoLicenseErrorDialog(e);
			} catch (LisenceExpiredException e) {
				e.printStackTrace();
				Dialogs.showLisenceExpiredErrorDialog();
			}
			
		}
		// not registered yet
		else {
			// licence dialog
			KeyListener letterNumberSpace = new KeyListener() {
				@Override
				public void keyPressed(KeyEvent e) {}
				@Override
				public void keyReleased(KeyEvent e) {}
				@Override
				public void keyTyped(KeyEvent e) {
					if (!(e.getKeyCode() == KeyEvent.VK_BACK_SPACE || 
							Character.isLetterOrDigit(e.getKeyChar()) ||
							Character.isWhitespace(e.getKeyChar()))) {  
				        e.consume();
				    }
				}
			};
			KeyListener letterNumber = new KeyListener() {
				@Override
				public void keyPressed(KeyEvent e) {}
				@Override
				public void keyReleased(KeyEvent e) {}
				@Override
				public void keyTyped(KeyEvent e) {
					if (!(e.getKeyCode() == KeyEvent.VK_BACK_SPACE || 
							Character.isLetterOrDigit(e.getKeyChar()))) {  
				        e.consume();
				    }
				}
			};
			UIManager.put("OptionPane.cancelButtonText", InterfaceTextDefaults.getInstance().getDefault("cancel"));
			UIManager.put("OptionPane.okButtonText", InterfaceTextDefaults.getInstance().getDefault("ok"));
			JTextField name = new JTextField();
			name.addKeyListener(letterNumberSpace);
			JTextField key = new JTextField();
			JTextField login = new JTextField();
			login.addKeyListener(letterNumber);
			JTextField pass = new JTextField();
			pass.addKeyListener(letterNumber);
			final JComponent[] inputs = new JComponent[] {
					new JLabel(InterfaceTextDefaults.getInstance().getDefault("name_surname_patronymic") + ":"), name,
					new JLabel(InterfaceTextDefaults.getInstance().getDefault("key") + ":"), key,
					new JLabel(InterfaceTextDefaults.getInstance().getDefault("create_login") + ":"), login,
					new JLabel(InterfaceTextDefaults.getInstance().getDefault("create_pass") + ":"), pass };
			int reply = JOptionPane.showConfirmDialog(null, inputs,
					InterfaceTextDefaults.getInstance().getDefault("enter_licence_data"), JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE);
			if (reply != JOptionPane.OK_OPTION)
				return;
			
			if (name.getText().trim().equals("") || key.getText().trim().equals("") || login.getText().trim().equals("")
					|| pass.getText().trim().equals("")) {
				Dialogs.showFillAllFieldsDialog();
				return;
			}

			// create file
			try {
				Utils.createConfigFile(f, name.getText().trim(), key.getText().trim());
				HTTPClient.registerKey(key.getText().trim(), name.getText().trim(), login.getText().trim(), pass.getText().trim());
				JFrame app = new Reabilitation();
				app.setVisible(true);
			} catch (DiskPermissionsException e) {
				e.printStackTrace();
				f.delete();
				Dialogs.showDiskPermissionsErrorDialog(e);
			} catch (ProgramFilesBrokenException e) {
				e.printStackTrace();
				f.delete();
				Dialogs.showFilesBrokenErrorDialog(e);
			} catch (ServerConnectionException e) {
				e.printStackTrace();
				f.delete();
				Dialogs.showServerConnectionErrorDialog(e);
			} catch (HddSerialScriptException e) {
				e.printStackTrace();
				f.delete();
				Dialogs.showHddSerialErrorDialog(e);
			} catch (KeyAlreadyRegisteredException e) {
				e.printStackTrace();
				f.delete();
				Dialogs.showKeyRegisteredErrorDialog(e);
			} catch (KeyNotExistException e) {
				e.printStackTrace();
				f.delete();
				Dialogs.showKeyNotExistErrorDialog(e);
			} catch (Exception e) {
				e.printStackTrace();
				f.delete();
				Dialogs.showNoLicenseErrorDialog(e);
			}
		}
	}
}