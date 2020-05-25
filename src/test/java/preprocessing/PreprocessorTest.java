package preprocessing;

import net.sourceforge.argparse4j.inf.ArgumentParserException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PreprocessorTest {
    @Test
    public void testPreprocessor() {
        String resourcesDir = "src/test/resources/";
        String textFile = resourcesDir + "missive.txt";
        String entitiesXML = resourcesDir + "match.extract.xml";
        String outNaf = resourcesDir + "missive.ner";

        try {
            Preprocessor preprocessor = Preprocessor.run(textFile, entitiesXML);
            preprocessor.write(outNaf);
            File f = new File(outNaf);
            assertTrue(f.exists());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ArgumentParserException e) {
            e.printStackTrace();
        }
    }

}