package de.DiscordBot.Commands.Markov;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

public class Markov {

	// Hashmap
	public Hashtable<String, Vector<String>> markovChain = new Hashtable<String, Vector<String>>();
	Random rnd = new Random();

	public Markov() {
		markovChain.put("_start", new Vector<String>());
		markovChain.put("_end", new Vector<String>());
	}

	/*
	 * Add words
	 */
	public void addWords(String phrase) {
		if(phrase.isEmpty())return;
		// put each word into an array
		String[] words = phrase.split(" ");

		// Loop through each word, check if it's already added
		// if its added, then get the suffix vector and add the word
		// if it hasn't been added then add the word to the list
		// if its the first or last word then select the _start / _end key

		for (int i = 0; i < words.length; i++) {

			// Add the start and end words to their own
			if (i == 0) {
				Vector<String> startWords = markovChain.get("_start");
				startWords.add(words[i]);

				Vector<String> suffix = markovChain.get(words[i]);
				if (suffix == null) {
					suffix = new Vector<String>();
					suffix.add(words[i + 1]);
					markovChain.put(words[i], suffix);
				}

			} else if (i == words.length - 1) {
				Vector<String> endWords = markovChain.get("_end");
				endWords.add(words[i]);

			} else {
				Vector<String> suffix = markovChain.get(words[i]);
				if (suffix == null) {
					suffix = new Vector<String>();
					suffix.add(words[i + 1]);
					markovChain.put(words[i], suffix);
				} else {
					suffix.add(words[i + 1]);
					markovChain.put(words[i], suffix);
				}
			}
		}
	}

	public String generateSentence() {
		return generateSentence(null);
	}
	
	/*
	 * Generate a markov phrase
	 */
	public String generateSentence(String seed) {

		// Vector to hold the phrase
		Vector<String> newPhrase = new Vector<String>();

		// String for the next word
		String nextWord = "";

		if(seed==null) {
		// Select the first word
		Vector<String> startWords = markovChain.get("_start");
		int startWordsLen = startWords.size();
		nextWord = startWords.get(rnd.nextInt(startWordsLen));
		}else {
			if(seed.trim().contains(" ")) {
				for(String s : seed.trim().split(" ")) {
					if(!nextWord.isEmpty()) {
						newPhrase.add(s);
					}
					nextWord = s;
				}
			}else {
				nextWord = seed;
			}
		}
		newPhrase.add(nextWord);

		// Keep looping through the words until we've reached the end
		while (!nextWord.contains(".")&&!nextWord.contains("?")&&!nextWord.contains("!")) {
			Vector<String> wordSelection = markovChain.get(nextWord);
			int wordSelectionLen = wordSelection.size();
			nextWord = wordSelection.get(rnd.nextInt(wordSelectionLen));
			newPhrase.add(nextWord);
		}
		return newPhrase.toString();
	}
}