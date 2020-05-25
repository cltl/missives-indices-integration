package preprocessing;

import nafutils.NafWrapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NafWrapperTest {
    @Test
    public void testTermsFromText() {
        String resourcesDir = "src/test/resources/";
        String tokNaf = resourcesDir + "missive.tok";
        NafWrapper nafWrapper = NafWrapper.create(tokNaf);
        assertTrue(nafWrapper.getTerms().isEmpty());

        nafWrapper.addTermsFromText();
        assertEquals(nafWrapper.getTerms().size(), nafWrapper.getWordForms().size());

    }

}