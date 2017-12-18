package lucene;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.ChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class LucenTest {

	@Test
	public void testbuild() throws Exception {
		FSDirectory directory = FSDirectory.open(new File("D:/development/upload"));
		Analyzer standardAnalyzer = new StandardAnalyzer();
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LATEST, standardAnalyzer);
		IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
		File files = new File("D:/program files/feiq/Recv Files/lucene&solr/00.参考资料/searchsource/searchsource");
		File[] listFiles = files.listFiles();
		for (File file : listFiles) {
			String fileName = file.getName();
			String fileContent = FileUtils.readFileToString(file);
			String filePath = file.getPath();
			long fileSize = FileUtils.sizeOf(file);
			Document document = new Document();
			Field fieldName = new TextField("name", fileName, Store.YES);
			Field fieldContent = new TextField("content", fileContent, Store.YES);
			Field fieldPath = new TextField("path", filePath, Store.YES);
			Field fieldSize = new TextField("size", fileSize + "", Store.YES);
			document.add(fieldName);
			document.add(fieldContent);
			document.add(fieldPath);
			document.add(fieldSize);
			indexWriter.addDocument(document);
		}
		indexWriter.commit();
		indexWriter.close();
	}

	@Test	
	public void testRead() throws Exception {
		FSDirectory directory = FSDirectory.open(new File("D:/development/upload"));
		DirectoryReader directoryReader = DirectoryReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
		Query query = new TermQuery(new Term("content", "spring"));
		TopDocs topDocs = indexSearcher.search(query, 10);
		System.out.println("记录总条数:" + topDocs.totalHits);
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			int docId = scoreDoc.doc;
			Document document = indexSearcher.doc(docId);
			System.out.println(document.getField("name"));
			System.out.println(document.getField("path"));
			System.out.println(document.getField("size"));
		}
		directoryReader.close();
	}

	@Test
	public void testTokenStream() throws Exception {
		// StandardAnalyzer analyzer = new StandardAnalyzer();
		Analyzer analyzer = new IKAnalyzer();
		TokenStream tokenStream = analyzer.tokenStream(null,
				"Lucene是apache共产党传智播客软件基金会4 jakarta项目组的一个子项目，是一个开放源代码的全文检索引擎工具包");
		tokenStream.reset();
		CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
		while (tokenStream.incrementToken()) {
			System.out.println(charTermAttribute);
		}
		tokenStream.close();
	}

}
