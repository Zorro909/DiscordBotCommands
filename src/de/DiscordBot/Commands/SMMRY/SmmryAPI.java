package de.DiscordBot.Commands.SMMRY;

import java.io.IOException;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javautils.HTTPManager.Connection;
import javautils.HTTPManager.InetManager;

public class SmmryAPI {

	private String apiKey;
	
	public SmmryAPI(String apiKey) {
		this.apiKey = apiKey;
	}
	
	public SummaryBuilder newSummaryBuilder() {
		return new SummaryBuilder();
	}	
	
class SummaryBuilder{
	
	private String url, text;
	private boolean extern = false;
	
	private int sentences = 7;
	private int keywords = 0;
	private boolean withBreak = false;
	private boolean withEncode = false;
	private boolean avoidQuotes = false, avoidQuestion = false, avoidExclamation = false;
	
	private SummaryBuilder() {
		
	}
	
	public SummaryBuilder website(String url) {
		this.url = url;
		extern = true;
		return this;
	}
	
	public SummaryBuilder text(String text) {
		this.text = text;
		extern = false;
		return this;
	}
	
	public SummaryBuilder keywords(int keywords) {
		this.keywords = keywords;
		return this;
	}
	
	public SummaryBuilder withBreak() {
		return withBreak(true);
	}

	public SummaryBuilder withBreak(boolean withBreak) {
		this.withBreak = withBreak;
		return this;
	}
	
	public SummaryBuilder avoidQuotes() {
		return avoidQuotes(true);
	}

	public SummaryBuilder avoidQuotes(boolean avoidQuotes) {
		this.avoidQuotes = avoidQuotes;
		return this;
	}
	
	public SummaryBuilder avoidQuestion() {
		return avoidQuestion(true);
	}

	public SummaryBuilder avoidQuestion(boolean avoidQuestion) {
		this.avoidQuestion = avoidQuestion;
		return this;
	}
	
	public SummaryBuilder avoidExclamation() {
		return avoidExclamation(true);
	}

	public SummaryBuilder avoidExclamation(boolean avoidExclamation) {
		this.avoidExclamation = avoidExclamation;
		return this;
	}
	
	public SummaryBuilder sentences(int sentences) {
		this.sentences = sentences;
		return this;		
	}
	
	public Summary build() {
		if(url==null&&text==null) {
			return null;
		}
		Summary s = null;
		try {
			String url = "http://api.smmry.com/?SM_API_KEY=" + apiKey;
			url+="&SM_LENGTH=" + sentences;
			url+="&SM_KEYWORD_COUNT=" + keywords;
			if(withBreak)url+="&SM_WITH_BREAK=" + withBreak;
			if(withEncode)url+="&SM_WITH_ENCODE=" + withEncode;
			if(avoidQuotes)url+="&SM_QUOTE_AVOID=" + avoidQuotes;
			if(avoidQuestion)url+="&SM_QUESTION_AVOID=" + avoidQuestion;
			if(avoidExclamation)url+="&SM_EXCLAMATION_AVOID=" + avoidExclamation;
			if(extern) {
				url+="&SM_URL=" + this.url;
			}
			Connection con = InetManager.openConnection(url);
			String result = "";
			if(!extern) {
				HashMap<String, String> req = new HashMap<String, String>();
				req.put("sm_api_input", text);
				con.initPost(req);
				result = con.post();
			} else {
				con.initGet(false, null);
				result = con.get();
			}
			Gson gson = new Gson();
			JsonObject jo = gson.fromJson(result, JsonObject.class);
			s = new Summary();
			if(jo.has("sm_api_message")) {
				s.message = jo.get("sm_api_message").getAsString();
			}
			if(jo.has("sm_api_character_count")) {
				s.characterCount = jo.get("sm_api_character_count").getAsInt();
			}
			if(jo.has("sm_api_title")) {
				s.title = jo.get("sm_api_title").getAsString();
			}
			if(jo.has("sm_api_content")) {
				s.content = jo.get("sm_api_content").getAsString();
			}
			if(jo.has("sm_api_keyword_array")) {
				s.keywords = gson.fromJson(jo.get("sm_api_keyword_array").getAsJsonArray(), String[].class);
			}
			if(jo.has("sm_api_error")) {
				s.error = jo.get("sm_api_error").getAsInt();
			}
			return s;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}

class Summary {
	private Summary() {}
	
	private String message;
	private int characterCount;
	private String title;
	private String content;
	private String[] keywords;
	private int error = -1;
	
	public boolean hasError() {
		return error != -1;
	}
	
	public String getMessage() {
		return message;
	}
	
	public int getError() {
		return error;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getSummary() {
		return content;
	}
	
	public String[] getKeywords() {
		return keywords;
	}
	
	public int getCharacterCount() {
		return characterCount;
	}
	
}
	
}
