package preprocessing;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EntityMatcherTest {
    @Test
    public void testEntityMatcher() {
        String resourcesDir = "src/test/resources/";
        String tokNaf = resourcesDir + "missive.tok";
        String entitiesXML = resourcesDir + "match.extract.xml";
        EntityMatcher entityMatcher = EntityMatcher.create(tokNaf, entitiesXML);
        entityMatcher.match();
        List<String> entityMentions = entityMatcher.getMatchedEntityMentions();
        assertEquals(entityMentions.size(), 11);
        String[] entities = {"JOAN VAN HOORN", "SWOLL", "BATAVIA", "James Cuningham", "Condoor", "Cochin-China", "Quinam", "Edam", "Guinea", "Pieter Geel", "Adolph Hage"};
        for (String e: entities) {
            assertTrue(entityMentions.contains(e));
        }
    }

    @Test
    public void testEmptyMatcher() {
        String resourcesDir = "src/test/resources/";
        String tokNaf = resourcesDir + "missive.tok";
        String entitiesXML = resourcesDir + "match.empty.xml";
        EntityMatcher entityMatcher = EntityMatcher.create(tokNaf, entitiesXML);
        entityMatcher.match();
        List<String> entityMentions = entityMatcher.getMatchedEntityMentions();
        assertTrue(entityMentions.isEmpty());
    }
}