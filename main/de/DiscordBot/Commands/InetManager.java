package de.DiscordBot.Commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.stream.Collectors;

public class InetManager {

	public static String get(String urlString) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.connect();
		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String get = "";
		String line = "";
		while ((line = br.readLine()) != null) {
			get += line + "\n";
		}
		return get;
	}

	public static String post(String urlString, HashMap<String, String> req) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setDoOutput(true);
		con.connect();
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(con.getOutputStream()));
		pw.println(req.entrySet().stream().map((entry) -> entry.getKey() + "=" + entry.getValue())
				.collect(Collectors.joining("&")));
		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String get = "";
		String line = "";
		while ((line = br.readLine()) != null) {
			get += line + "\n";
		}
		return get;
	}

}
