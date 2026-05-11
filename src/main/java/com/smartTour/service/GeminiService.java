package com.smartTour.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

	@Value("${gemini.api.key}")
	private String apiKey;

	public String getResponse(String prompt) {

		try {
//            String urlString =
//                    "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;
//			String urlString = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key="
//					+ apiKey;
//			String urlString =
//					"https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + apiKey;
//			  String urlString =
//			            "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=" + apiKey;
//			String urlString =
//					"https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + apiKey;
//			String urlString =
//					"https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + apiKey;
			String urlString =
					"https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-001:generateContent?key=" + apiKey;
			
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);

			// 🔥 Request Body
			String requestBody = """
					{
					  "contents": [
					    {
					      "parts": [
					        {"text": "%s"}
					      ]
					    }
					  ]
					}
					""".formatted(prompt);

			// 🔥 Send request
			OutputStream os = conn.getOutputStream();
			os.write(requestBody.getBytes());
			os.flush();

			// 🔥 IMPORTANT FIX
			int responseCode = conn.getResponseCode();
			System.out.println("Response Code: " + responseCode);

			BufferedReader br;

			if (responseCode == 200) {
				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}

			// 🔥 Read response
			StringBuilder result = new StringBuilder();
			String line;

			while ((line = br.readLine()) != null) {
				result.append(line);
			}

			String response = result.toString();
			System.out.println("Full API Response: " + response);
			return extractText(response);

		} catch (Exception e) {
			e.printStackTrace();
			return "Error calling Gemini API";
		}
	}

	// ✅ Extract only AI text (important 🔥)
	private String extractText(String json) {
		try {
			// Simple extraction without library
			int start = json.indexOf("\"text\":\"");
			if (start == -1)
				return "No response from AI";

			start += 8;
			int end = json.indexOf("\"", start);

			return json.substring(start, end);

		} catch (Exception e) {
			return "Error parsing AI response";
		}
	}
}
//@Service
//public class GeminiService {
//
//    @Value("${AIzaSyDV7UL7TgAqSf7n6yrB-CUX2v_hbW5gT8c}")
//    private String apiKey;
//
//    public String getResponse(String prompt) {
//
//        try {
//            URL url = new URL(
//                "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=" + apiKey
//            );
//
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Type", "application/json");
//            conn.setDoOutput(true);
//
//            String requestBody = """
//            {
//              "contents": [{
//                "parts": [{
//                  "text": "%s"
//                }]
//              }]
//            }
//            """.formatted(prompt);
//
//            OutputStream os = conn.getOutputStream();
//            os.write(requestBody.getBytes());
//            os.flush();
//
//            BufferedReader br = new BufferedReader(
//                new InputStreamReader(conn.getInputStream())
//            );
//
////            String response = br.readLine();
//
////            return response;
//            StringBuilder result = new StringBuilder();
//            String line;
//
//            while ((line = br.readLine()) != null) {
//                result.append(line);
//            }
//
//            return result.toString();
//            
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Error connecting to AI";
//        }
//    }
//}