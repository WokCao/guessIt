package com.FoZ.guessIt.Services;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class IPAConvertService {
    private static final Map<String, String> ARPABET_TO_IPA = new HashMap<>();

    static {
        // Vowels
        ARPABET_TO_IPA.put("AO", "ɔ");  ARPABET_TO_IPA.put("AO0", "ɔ");  ARPABET_TO_IPA.put("AO1", "ɔ");  ARPABET_TO_IPA.put("AO2", "ɔ");
        ARPABET_TO_IPA.put("AA", "ɑ");  ARPABET_TO_IPA.put("AA0", "ɑ");  ARPABET_TO_IPA.put("AA1", "ɑ");  ARPABET_TO_IPA.put("AA2", "ɑ");
        ARPABET_TO_IPA.put("IY", "i");  ARPABET_TO_IPA.put("IY0", "i");  ARPABET_TO_IPA.put("IY1", "i");  ARPABET_TO_IPA.put("IY2", "i");
        ARPABET_TO_IPA.put("UW", "u");  ARPABET_TO_IPA.put("UW0", "u");  ARPABET_TO_IPA.put("UW1", "u");  ARPABET_TO_IPA.put("UW2", "u");
        ARPABET_TO_IPA.put("EH", "ɛ");  ARPABET_TO_IPA.put("EH0", "ɛ");  ARPABET_TO_IPA.put("EH1", "ɛ");  ARPABET_TO_IPA.put("EH2", "ɛ");
        ARPABET_TO_IPA.put("IH", "ɪ");  ARPABET_TO_IPA.put("IH0", "ɪ");  ARPABET_TO_IPA.put("IH1", "ɪ");  ARPABET_TO_IPA.put("IH2", "ɪ");
        ARPABET_TO_IPA.put("UH", "ʊ");  ARPABET_TO_IPA.put("UH0", "ʊ");  ARPABET_TO_IPA.put("UH1", "ʊ");  ARPABET_TO_IPA.put("UH2", "ʊ");
        ARPABET_TO_IPA.put("AH", "ʌ");  ARPABET_TO_IPA.put("AH0", "ə");  ARPABET_TO_IPA.put("AH1", "ʌ");  ARPABET_TO_IPA.put("AH2", "ʌ");
        ARPABET_TO_IPA.put("AE", "æ");  ARPABET_TO_IPA.put("AE0", "æ");  ARPABET_TO_IPA.put("AE1", "æ");  ARPABET_TO_IPA.put("AE2", "æ");
        ARPABET_TO_IPA.put("AX", "ə");  ARPABET_TO_IPA.put("AX0", "ə");  ARPABET_TO_IPA.put("AX1", "ə");  ARPABET_TO_IPA.put("AX2", "ə");

        // R-colored vowels
        ARPABET_TO_IPA.put("ER", "ɚ");  ARPABET_TO_IPA.put("ER0", "ɚ");  ARPABET_TO_IPA.put("ER1", "ɝ");  ARPABET_TO_IPA.put("ER2", "ɝ");
        ARPABET_TO_IPA.put("AXR", "ɚ"); ARPABET_TO_IPA.put("AXR0", "ɚ"); ARPABET_TO_IPA.put("AXR1", "ɝ"); ARPABET_TO_IPA.put("AXR2", "ɝ");

        // Diphthongs
        ARPABET_TO_IPA.put("EY", "eɪ");  ARPABET_TO_IPA.put("EY0", "eɪ");  ARPABET_TO_IPA.put("EY1", "eɪ");  ARPABET_TO_IPA.put("EY2", "eɪ");
        ARPABET_TO_IPA.put("AY", "aɪ");  ARPABET_TO_IPA.put("AY0", "aɪ");  ARPABET_TO_IPA.put("AY1", "aɪ");  ARPABET_TO_IPA.put("AY2", "aɪ");
        ARPABET_TO_IPA.put("OW", "oʊ");  ARPABET_TO_IPA.put("OW0", "oʊ");  ARPABET_TO_IPA.put("OW1", "oʊ");  ARPABET_TO_IPA.put("OW2", "oʊ");
        ARPABET_TO_IPA.put("AW", "aʊ");  ARPABET_TO_IPA.put("AW0", "aʊ");  ARPABET_TO_IPA.put("AW1", "aʊ");  ARPABET_TO_IPA.put("AW2", "aʊ");
        ARPABET_TO_IPA.put("OY", "ɔɪ");  ARPABET_TO_IPA.put("OY0", "ɔɪ");  ARPABET_TO_IPA.put("OY1", "ɔɪ");  ARPABET_TO_IPA.put("OY2", "ɔɪ");

        // Consonants
        ARPABET_TO_IPA.put("P", "p");   ARPABET_TO_IPA.put("B", "b");    ARPABET_TO_IPA.put("T", "t");    ARPABET_TO_IPA.put("D", "d");
        ARPABET_TO_IPA.put("K", "k");    ARPABET_TO_IPA.put("G", "g");    ARPABET_TO_IPA.put("CH", "tʃ");  ARPABET_TO_IPA.put("JH", "dʒ");
        ARPABET_TO_IPA.put("F", "f");    ARPABET_TO_IPA.put("V", "v");    ARPABET_TO_IPA.put("TH", "θ");   ARPABET_TO_IPA.put("DH", "ð");
        ARPABET_TO_IPA.put("S", "s");    ARPABET_TO_IPA.put("Z", "z");    ARPABET_TO_IPA.put("SH", "ʃ");    ARPABET_TO_IPA.put("ZH", "ʒ");
        ARPABET_TO_IPA.put("HH", "h");   ARPABET_TO_IPA.put("M", "m");    ARPABET_TO_IPA.put("N", "n");    ARPABET_TO_IPA.put("NG", "ŋ");
        ARPABET_TO_IPA.put("L", "l");    ARPABET_TO_IPA.put("R", "r");    ARPABET_TO_IPA.put("W", "w");    ARPABET_TO_IPA.put("Y", "j");
    }

    public static String convertArpabetToIpa(String arpabet) {
        StringBuilder ipa = new StringBuilder("/");
        String[] phonemes = arpabet.split(" ");
        boolean isFirstSyllable = true;

        for (int i = 0; i < phonemes.length; i++) {
            String phoneme = phonemes[i];
            String basePhoneme = phoneme.replaceAll("[0-2]$", "");
            String ipaChar = ARPABET_TO_IPA.get(basePhoneme);

            if (ipaChar == null) {
                ipaChar = phoneme; // Fallback
            }

            // Handle stress (1=primary ˈ, 2=secondary ˌ)
            if (phoneme.endsWith("1") && isFirstSyllable) {
                ipa.append("ˈ");
                isFirstSyllable = false;
            } else if (phoneme.endsWith("2")) {
                ipa.append("ˌ");
            }

            ipa.append(ipaChar);

            // Add syllable boundary (.) if next phoneme is a vowel or end of word
            if (i < phonemes.length - 1) {
                String nextPhoneme = phonemes[i + 1];
                boolean isNextVowel = nextPhoneme.matches("^[AEIOU].*");
                if (isNextVowel) {
                    ipa.append(".");
                }
            }
        }

        ipa.append("/");
        return ipa.toString();
    }
}
