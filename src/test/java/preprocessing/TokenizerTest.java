package preprocessing;

import net.sourceforge.argparse4j.inf.ArgumentParserException;
import org.junit.jupiter.api.Test;
import parser.NafParser;
import xjc.naf.NAF;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TokenizerTest {
    @Test
    public void testTokenizer() {
        String outFileName = "src/test/resources/missive.tok";
        try {
            Tokenizer tokenizer = Tokenizer.create();
            NAF naf = tokenizer.run("src/test/resources/missive.txt");
            NafParser parser = new NafParser(naf);
            parser.write(outFileName);
            File goal = new File(outFileName);
            assertTrue(goal.exists());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ArgumentParserException e) {
            e.printStackTrace();
        }
    }

}