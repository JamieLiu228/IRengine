package com.yliu.search;

import com.trinity.utils.ConfigUtil;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class IndexSearch {

    private static final Properties PROPS = ConfigUtil.getProps("conf.properties");

    /**
     * Calculation result path
     */
    private static final String RESULT_FILE_PATH = PROPS.getProperty("result_file_path");
    /**
     * Query text path
     */
    private static final String QUERY_FILE_PATH = PROPS.getProperty("query_file_path");
    /**
     * Index directory
     */
    private static final String INDEX_DIRECTORY = PROPS.getProperty("index_directory");
    /**
     * Maximum query returns
     */
    private static final int MAX_RESULTS = 1400;

    public static void main(String[] args) throws IOException {
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

        // create objects to read and search across the index
        DirectoryReader iReader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(iReader);
        searcher.setSimilarity(new BM25Similarity());

//        QueryParser parser = new QueryParser("content", new SynonymAnalyzer(new SimpleSynonymEngine()));
        StandardAnalyzer standardAnalyzer = new StandardAnalyzer();
        String line;
        try {
            FileReader fileReader = new FileReader(new File(QUERY_FILE_PATH));
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + " ");
            }

            String[] split = stringBuilder.toString().split("\\.I");

            Map<String, String> map = new LinkedHashMap<>();
            for (String queryText : split) {
                if ("".equals(queryText)) {
                    continue;
                }
                String[] split1 = queryText.split("\\.W");
                if (split1.length == 2) {
                    map.put(split1[0], split1[1]);
                }
            }
            int queryNum = 1;

            deleteFile(new File(RESULT_FILE_PATH));
            for (String key : map.keySet()) {
                System.out.println("index:" + queryNum);
                String query = QueryParser.escape(map.get(key)).trim();

                Query boostedTermQuery1 = new QueryParser("title", standardAnalyzer).parse(query);
                Query boostedTermQuery2 = new QueryParser("author", standardAnalyzer).parse(query);
                Query boostedTermQuery3 = new QueryParser("content", standardAnalyzer).parse(query);
                Query boostedTermQuery4 = new QueryParser("bibliography", standardAnalyzer).parse(query);

                BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
                booleanQuery.add(boostedTermQuery1, BooleanClause.Occur.SHOULD);
                booleanQuery.add(boostedTermQuery2, BooleanClause.Occur.SHOULD);
                booleanQuery.add(boostedTermQuery3, BooleanClause.Occur.SHOULD);
                booleanQuery.add(boostedTermQuery4, BooleanClause.Occur.SHOULD);

                ScoreDoc[] scoreDocs = searcher.search(booleanQuery.build(), MAX_RESULTS).scoreDocs;
                for (int i = 0; i < scoreDocs.length; i++) {
                    Document hitDoc = searcher.doc(scoreDocs[i].doc);
                    saveDocument(String.valueOf(queryNum) + " Q0 " + hitDoc.get("id") + " 0 " + scoreDocs[i].score + " STANDARD");
                }
                queryNum++;
            }
            bufferedReader.close();
        } catch (Exception e) {
            System.err.println("ERROR:" + e.toString());
        } finally {
            if (iReader != null) {
                iReader.close();
            }
            if (directory != null) {
                directory.close();
            }
        }
    }

    public static boolean deleteFile(File file) {
        boolean result = false;
        if (file.exists()) {
            file.delete();
            result = true;
        }
        return result;
    }

    /**
     * Persistent data
     *
     * @param data
     */
    private static void saveDocument(String data) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(RESULT_FILE_PATH, true)));
            out.write(data + "\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
