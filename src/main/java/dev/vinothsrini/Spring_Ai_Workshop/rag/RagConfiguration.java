package dev.vinothsrini.Spring_Ai_Workshop.rag;

import com.openai.models.vectorstores.VectorStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Configuration
public class RagConfiguration {
    
    private static final Logger log = LoggerFactory.getLogger(RagConfiguration.class);
    private final String vectorStoreName= "vectorstore.json";

    @Value("classpath:/data/models.json")
    private Resource models;

    @Bean
    VectorStore vectorStore(EmbeddingModel embeddingModel) throws IOException {

        VectorStore vectorStore1 = VectorStore.builder().build();
        var vectoreStoreFile= getVectorStoreFile();
        if(vectoreStoreFile.exists()){
            log.info("Vector store already exists at {}", vectoreStoreFile.getAbsolutePath());
//            vectorStore1.load(vectoreStoreFile);
        }else{
            log.info("Creating new Vector store at {}", vectoreStoreFile.getAbsolutePath());
            TextReader textReader=new TextReader(models);
            textReader.getCustomMetadata().put("fileName", "models.text");
            List<Document> documents= textReader.get();
//            TokenTextSplitter tokenTextSplitter=new TokenTextSplitter();
//            List<Document> splitDocuments=tokenTextSplitter.apply(documents);

//            vectorStore1.add(splitDocuments);
//            vectorStore1.save(vectoreStoreFile);

        }
        return vectorStore1;
    }

    private File getVectorStoreFile() throws IOException {
      Path path = Paths.get("src","main","resources","data");
      String absoultePath = path.toFile().getAbsolutePath()+"/"+vectorStoreName;
      return new File(absoultePath);
    }



}
