package reabilitation;

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

/**
 * 
 * 
 * @author Pokrovskaya Oksana
 */

public class Main {
	public static void main(String[] args) {

		try {
			HTTPClient.setSERVER(Utills.getAppServer());
		} catch (ProgramFilesBrokenException e1) {
			e1.printStackTrace();
			Dialogs.showFilesBrokenErrorDialog(e1);
			return;
		}
		File f = new File(Utills.getFilePath() + "/config");
		
		// that means already registered because config exists
		if (f.exists()) {
			// check key
			try {
				String key = Utills.getLicenceKey();
				String username = Utills.getLicenceUserName();
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
			JTextField name = new JTextField();
			JTextField key = new JTextField();
			final JComponent[] inputs = new JComponent[] {
					new JLabel(InterfaceTextDefaults.getInstance().getDefault("name_surname_patronymic") + ":"), name,
					new JLabel(InterfaceTextDefaults.getInstance().getDefault("key") + ":"), key };
			int reply = JOptionPane.showConfirmDialog(null, inputs,
					InterfaceTextDefaults.getInstance().getDefault("enter_licence_data"), JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.PLAIN_MESSAGE);
			if (reply != JOptionPane.OK_OPTION)
				return;

			// create file
			try {
				Utills.createConfigFile(f, name.getText().trim(), key.getText().trim());
				HTTPClient.registerKey(key.getText().trim(), name.getText().trim());
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