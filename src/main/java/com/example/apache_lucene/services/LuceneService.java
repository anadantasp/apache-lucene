package com.example.apache_lucene.services;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class LuceneService {

    private final Directory indexDirectory;
    private final SynonymAnalyzer analyzer;

    public LuceneService() throws IOException {
        this.indexDirectory = new ByteBuffersDirectory();
        this.analyzer = new SynonymAnalyzer("src/main/resources/synonyms/synonyms.txt");
        indexDocuments();
    }

    private void indexDocuments() throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        try (IndexWriter writer = new IndexWriter(indexDirectory, config)) {
            Files.list(Paths.get("src/main/resources/documents"))
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            Document doc = new Document();
                            String content = new String(Files.readAllBytes(path));
                            doc.add(new TextField("content", content, Field.Store.YES));
                            doc.add(new StringField("path", path.toString(), Field.Store.YES));
                            writer.addDocument(doc);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    public List<String> search(String queryString) {
        List<String> results = new ArrayList<>();
        try (DirectoryReader reader = DirectoryReader.open(indexDirectory)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            QueryParser parser = new QueryParser("content", analyzer);
            Query query = parser.parse(queryString);

            TopDocs topDocs = searcher.search(query, 10);
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                try {
                    Document doc = searcher.storedFields().document(scoreDoc.doc);
                    results.add(doc.get("path"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    public List<String> searchContent(String queryString) {
        List<String> results = new ArrayList<>();
        try (DirectoryReader reader = DirectoryReader.open(indexDirectory)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            QueryParser parser = new QueryParser("content", analyzer);
            Query query = parser.parse(queryString);

            TopDocs topDocs = searcher.search(query, 10);
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                try {
                    Document doc = searcher.storedFields().document(scoreDoc.doc);
                    results.add(doc.get("content"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

}
