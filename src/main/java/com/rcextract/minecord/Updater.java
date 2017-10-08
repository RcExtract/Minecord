package com.rcextract.minecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Updater {

	private static final String API_KEY = "98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4";
	private static final String REQUEST_METHOD = "POST";
	private static final String LINK = "http://www.spigotmc.org/api/general.php";
	
	public static enum UpdaterResult {
		CONNECTION_FAILURE, DATA_ACCESSED, UP_TO_DATE, UPDATE_AVAILABLE;
	}
	
	private Minecord minecord;
	public Updater(Minecord minecord) {
		this.minecord = minecord;
	}
	public UpdaterResult check() {
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) new URL(LINK).openConnection();
		} catch (IOException e) {
			return UpdaterResult.CONNECTION_FAILURE;
		}
		connection.setDoOutput(true);
		try {
			connection.setRequestMethod(REQUEST_METHOD);
			connection.getOutputStream().write(("key=" + API_KEY + "&resource=44055").getBytes("UTF-8"));
		} catch (IOException e) {
			return UpdaterResult.CONNECTION_FAILURE;
		}
		String label = null;
		try {
			label = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
		} catch (IOException e) {
			//This exception is never thrown.
			return UpdaterResult.DATA_ACCESSED;
		}
		String current = minecord.getDescription().getVersion();
		if (!(label.startsWith("Beta ") || current.startsWith("Beta "))) {
			if (Double.parseDouble(label) > Double.parseDouble(current)) {
				return UpdaterResult.UPDATE_AVAILABLE;
			}
			return UpdaterResult.UP_TO_DATE;
		}
		if (!(label.startsWith("Beta ")) && current.startsWith("Beta ")) {
			return UpdaterResult.UPDATE_AVAILABLE;
		}
		if (Double.parseDouble(label.substring(5)) > Double.parseDouble(current.substring(5))) {
			return UpdaterResult.UPDATE_AVAILABLE;
		}
		return UpdaterResult.UP_TO_DATE;
	}
}
