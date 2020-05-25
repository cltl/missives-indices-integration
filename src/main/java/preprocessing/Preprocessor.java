package preprocessing;

import nafutils.NafUima;
import nafutils.NafWrapper;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import org.apache.uima.cas.CASException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.xml.sax.SAXException;
import xjc.naf.NAF;

import java.io.File;
import java.io.IOException;

public class Preprocessor {
    NafWrapper naf;
    private Preprocessor(NafWrapper naf) {
        this.naf = naf;
    }

    public static Preprocessor run(String rawTextFile, String matchedEntitiesFile) throws IOException, ArgumentParserException {
        Tokenizer tokenizer = Tokenizer.create();
        NAF naf = tokenizer.run(rawTextFile);
        File entitiesFile = new File(matchedEntitiesFile);
        if (entitiesFile.exists()) {
            EntityMatcher entityMatcher = EntityMatcher.create(naf, matchedEntitiesFile);
            entityMatcher.match();
            return new Preprocessor(entityMatcher.getNafWrapper());
        } else
            return new Preprocessor(NafWrapper.create(naf));
    }

    public void write(String file) {
        naf.write(file);
    }

    public static void main(String[] args) {
        String textFile = args[0];
        String matchedEntities = args[1];
        String outNaf = args[2];
        String outXmi = args[3];
        try {
            Preprocessor preprocessor = Preprocessor.run(textFile, matchedEntities);
            preprocessor.write(outNaf);
            NafUima nafUima = NafUima.create();
            nafUima.convert(outNaf);
            nafUima.write(outXmi);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ArgumentParserException e) {
            e.printStackTrace();
        } catch (InvalidXMLException e) {
            e.printStackTrace();
        } catch (CASException e) {
            e.printStackTrace();
        } catch (ResourceInitializationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }
}
