package reabilitation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.swing.*;

import customcomponent.CustomDialog;
import defaults.InterfaceTextDefaults;

/**
 * 
 * 
 * @author Pokrovskaya Oksana
 */

public class Main {
	public static void main(String[] args) {

		HTTPClient.setSERVER(Utills.getAppServer());

		File f = new File(Utills.getFilePath() + "/config");
		// already registered
		if (f.exists()) {
			// check key
			String key = Utills.getLicenceKey();
			String username = Utills.getLicenceUserName();
			if (HTTPClient.checkKey(key, username)) {
				JFrame app = new Reabilitation();
				app.setVisible(true);
				if (Utills.getCheckUpdatesAuto()) {
					String location = HTTPClient.getVersion(Utills.getVersionDate());
					if (location != null) {
						// update
						// show dialog
						CustomDialog d1 = new CustomDialog(app,
								InterfaceTextDefaults.getInstance().getDefault("do_update"),
								InterfaceTextDefaults.getInstance().getDefault("yes"),
								InterfaceTextDefaults.getInstance().getDefault("no"), false);
						if (d1.getAnswer() == 1) {
							try {
								Process proc = Runtime.getRuntime().exec("java -jar updater.jar " + location);
								System.exit(0);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						} else
							return;
					}
				}
			} else {
				JOptionPane.showConfirmDialog((java.awt.Component) null,
						InterfaceTextDefaults.getInstance().getDefault("no_licence"),
						InterfaceTextDefaults.getInstance().getDefault("licence_failed"),
						javax.swing.JOptionPane.DEFAULT_OPTION);
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
				f.createNewFile();
				OutputStreamWriter bufferedWriter = new OutputStreamWriter(new FileOutputStream(f), "UTF8");
				bufferedWriter.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
				bufferedWriter.append("<config>\n");
				bufferedWriter.append("<username>" + name.getText().trim() + "</username>\n");
				bufferedWriter.append("<key>" + key.getText().trim() + "</key>\n");
				bufferedWriter.append("<checkUpdatesAuto>false</checkUpdatesAuto>");
				bufferedWriter.append("</config>");
				bufferedWriter.flush();
				bufferedWriter.close();

				if (HTTPClient.registerKey(key.getText().trim(), name.getText().trim())) {
					JFrame app = new Reabilitation();
					app.setVisible(true);
				} else {
					f.delete();
					JOptionPane.showConfirmDialog((java.awt.Component) null,
							InterfaceTextDefaults.getInstance().getDefault("no_licence"),
							InterfaceTextDefaults.getInstance().getDefault("licence_failed"),
							javax.swing.JOptionPane.DEFAULT_OPTION);
				}
			} catch (Exception e) {
				e.printStackTrace();
				f.delete();
				JOptionPane.showConfirmDialog((java.awt.Component) null,
						InterfaceTextDefaults.getInstance().getDefault("no_licence"),
						InterfaceTextDefaults.getInstance().getDefault("licence_failed"),
						javax.swing.JOptionPane.DEFAULT_OPTION);
				e.printStackTrace();
			}

		}
	}
}