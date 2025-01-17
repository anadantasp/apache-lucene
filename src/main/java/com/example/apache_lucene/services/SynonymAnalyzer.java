package com.example.apache_lucene.services;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class SynonymAnalyzer extends Analyzer {

    private final SynonymMap synonymMap;
    private final CharArraySet stopWords;

    public SynonymAnalyzer(String synonymFile) throws IOException {
        this.stopWords = new CharArraySet(List.of("de", "do", "da"), true);
        SynonymMap.Builder builder = new SynonymMap.Builder(true);
        loadSynonyms(builder, synonymFile);
        synonymMap = builder.build();
    }

    private void loadSynonyms(SynonymMap.Builder builder, String synonymFile) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(synonymFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] synonyms = line.split(",");
                for (String synonym : synonyms) {
                    builder.add(new CharsRef(synonym.trim()), new CharsRef(synonyms[0].trim()), true);
                }
            }

        }
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        StandardTokenizer tokenizer = new StandardTokenizer();
        TokenStream tokenStream = new SynonymGraphFilter(tokenizer, synonymMap, true);
        tokenStream = new StopFilter(tokenStream, stopWords);
        return new TokenStreamComponents(tokenizer, tokenStream);
    }
}
