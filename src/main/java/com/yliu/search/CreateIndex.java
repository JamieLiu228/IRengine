package com.yliu.search;

import com.trinity.utils.ConfigUtil;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CreateIndex {

    private static final Properties PROPS = ConfigUtil.getProps("conf.properties");

    /**
     * Index directory
     */
    private static final String INDEX_DIRECTORY = PROPS.getProperty("index_directory");
    /**
     * Document path
     */
    private static final String DOC_FILE_PATH = PROPS.getProperty("doc_file_path");

    public static void main(String[] args) throws IOException {
        List<Document> documents = new ArrayList<>();

        IndexWriter iWriter = null;
        Directory directory = null;

        try {
            directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

            IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            iWriter = new IndexWriter(directory, config);

            BufferedReader br = new BufferedReader(new FileReader(new File(DOC_FILE_PATH)));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line + " ");
            }

            String[] split = stringBuilder.toString().split("\\.I");

            for (String article : split) {
                if ("".equals(article)) {
                    continue;
                }
                String id = article.substring(0, article.indexOf(".")).replace(" ", "");
                System.out.println("Start writing to index! Current id [" + id + "]");
                String title = article.substring(article.indexOf(".T") + 2, article.indexOf(".A")).trim();
                String author = article.substring(article.indexOf(".A") + 2, article.indexOf(".B")).trim();
                String bibliographic = article.substring(article.indexOf(".B") + 2, article.indexOf(".W")).trim();
                String content = article.substring(article.indexOf(".W") + 2).trim();
                Document doc = new Document();
                doc.add(new StringField("id", id, Field.Store.YES));
                doc.add(new TextField("title", title, Field.Store.YES));
                doc.add(new StringField("author", author, Field.Store.YES));
                doc.add(new TextField("bibliographic", bibliographic, Field.Store.YES));
                doc.add(new TextField("content", content, Field.Store.YES));
                documents.add(doc);
            }
            iWriter.addDocuments(documents);
        } catch (Exception e) {
            System.err.println("error:" + e.toString());
        } finally {
            System.out.println("Index successfully written!!");
            if (iWriter != null) {
                iWriter.close();
            } else if (directory != null) {
                directory.close();
            }
        }
    }
}
