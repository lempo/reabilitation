package dialogs;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JOptionPane;
import defaults.InterfaceTextDefaults;

public class Dialogs {
	
	private static void showErrorDialog(String mainContent, Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		JOptionPane.showConfirmDialog((java.awt.Component) null,
				mainContent + "\n" + InterfaceTextDefaults.getInstance().getDefault("error_dailog_content")
				+ "\n\n" + sw.toString(),
				InterfaceTextDefaults.getInstance().getDefault("error"),
				javax.swing.JOptionPane.DEFAULT_OPTION);
	}
	
	public static void showLisenceExpiredErrorDialog() {
		JOptionPane.showConfirmDialog((java.awt.Component) null,
				InterfaceTextDefaults.getInstance().getDefault("lisence_expired"),
				InterfaceTextDefaults.getInstance().getDefault("error"),
				javax.swing.JOptionPane.DEFAULT_OPTION);
	}
	
	public static void showHddSerialErrorDialog(Exception e) {
		showErrorDialog(InterfaceTextDefaults.getInstance().getDefault("hdd_serial_err"), e);
	}
	
	public static void showKeyNotExistErrorDialog(Exception e) {
		showErrorDialog(InterfaceTextDefaults.getInstance().getDefault("key_not_exist"), e);
	}
	
	public static void showKeyRegisteredErrorDialog(Exception e) {
		showErrorDialog(InterfaceTextDefaults.getInstance().getDefault("key_already_registered"), e);
	}
	
	public static void showNoLicenseErrorDialog() {
		JOptionPane.showConfirmDialog((java.awt.Component) null,
				InterfaceTextDefaults.getInstance().getDefault("no_licence"),
				InterfaceTextDefaults.getInstance().getDefault("error"),
				javax.swing.JOptionPane.DEFAULT_OPTION);
	}
	
	public static void showNoLicenseErrorDialog(Exception e) {
		showErrorDialog(InterfaceTextDefaults.getInstance().getDefault("no_licence"), e);
	}
	
	public static void showFillAllFieldsDialog() {
		JOptionPane.showConfirmDialog((java.awt.Component) null,
				InterfaceTextDefaults.getInstance().getDefault("fill_all_fields"),
				InterfaceTextDefaults.getInstance().getDefault("error"),
				javax.swing.JOptionPane.DEFAULT_OPTION);
	}
	
	public static void showDiskPermissionsErrorDialog(Exception e) {
		showErrorDialog(InterfaceTextDefaults.getInstance().getDefault("disk_permissions_err"), e);
	}
	
	public static void showFilesBrokenErrorDialog(Exception e) {
		showErrorDialog(InterfaceTextDefaults.getInstance().getDefault("files_broken_err"), e);
	}
	
	public static void showServerConnectionErrorDialog(Exception e) {
		showErrorDialog(InterfaceTextDefaults.getInstance().getDefault("server_connecion_err"), e);
	}
}
