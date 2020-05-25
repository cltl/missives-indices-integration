package nafutils;

import org.apache.uima.cas.CASException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class NafUimaTest {
    @Test
    public void testNafUima() {
        String resourcesDir = "src/test/resources/";
        String inNaf = resourcesDir + "missive.ner";
        String outXmi = resourcesDir + "missive.xmi";
        try {
            NafUima nafUima = NafUima.create();
            nafUima.convert(inNaf);
            nafUima.write(outXmi);
            File f = new File(outXmi);
            assertTrue(f.exists());
        } catch (IOException e) {
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