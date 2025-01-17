package com.example.apache_lucene.services;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SynonymAnalyzer extends Analyzer {

    private final SynonymMap synonymMap;

    public SynonymAnalyzer(String synonymFile) throws IOException {
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
        return new TokenStreamComponents(tokenizer, tokenStream);
    }
}