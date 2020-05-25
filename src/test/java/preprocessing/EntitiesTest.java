package preprocessing;

import org.junit.jupiter.api.Test;
import xjc.matchedTerms.MatchTest;

import static org.junit.jupiter.api.Assertions.*;

class EntitiesTest {
    @Test
    public void testEntities() {
        String testFile = "src/test/resources/match.extract.xml";
        MatchTest matchTest = Entities.load(testFile);
        assertNotNull(matchTest);

        Entities entities =  Entities.create(testFile);
        assertEquals(entities.names().size(), 13);
    }

}