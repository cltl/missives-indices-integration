package preprocessing;


import nafutils.NafWrapper;
import xjc.matchedTerms.Name;
import xjc.naf.*;

import java.util.*;
import java.util.stream.Collectors;


public class EntityMatcher {
    private final static String PROCESSOR = "EntityMatcher";
    private final static String VERSION = "1.0";

    private NafWrapper nafWrapper;
    private Entities entities;
    private List<Name> foundEntities;

    private EntityMatcher(NafWrapper nafWrapper, Entities entities) {
        this.nafWrapper = nafWrapper;
        this.entities = entities;
        this.foundEntities = new LinkedList<>();
    }

    public static EntityMatcher create(String nafFile, String matchedEntitiesFile) {
        return new EntityMatcher(NafWrapper.create(nafFile), Entities.create(matchedEntitiesFile));
    }

    public static EntityMatcher create(NAF naf, String matchedEntitiesFile) {
        return new EntityMatcher(NafWrapper.create(naf), Entities.create(matchedEntitiesFile));
    }

    public NafWrapper getNafWrapper() {
        return nafWrapper;
    }
    public void match() {
        nafWrapper.addLP("terms", PROCESSOR, VERSION);
        nafWrapper.addTermsFromText();
        nafWrapper.addLP("entities", PROCESSOR, VERSION);
        nafWrapper.addEntities(matchingEntities(nafWrapper, entities.names()));
    }

    private List<Entity> matchingEntities(NafWrapper nafWrapper, List<Name> entityNames) {
        List<Entity> toAdd = new LinkedList<>();
        int i = 1;
        Iterator<Wf> wfIterator = nafWrapper.getWordForms().iterator();
        boolean doIterate = true;
        Wf wf = null;
        while (wfIterator.hasNext()) {
            if (doIterate)
                wf = wfIterator.next();
            doIterate = true;
            String currentWf = wf.getValue();
            String matchedWf = currentWf;
            List<Wf> wordSpan = new LinkedList<>();
            Name longestMatch = null;
            List<Name> matchedEntities = entityNames.stream().filter(e -> e.getValue().startsWith(currentWf)).collect(Collectors.toList());

            while (! matchedEntities.isEmpty()) {
                longestMatch = matchedEntities.get(0);
                wordSpan.add(wf);
                wf = wfIterator.next();
                doIterate = false;
                String matchedWf2 = matchedWf + " " + wf.getValue();
                matchedEntities = matchedEntities.stream().filter(e -> e.getValue().startsWith(matchedWf2)).collect(Collectors.toList());
                matchedWf = matchedWf2;
            }
            if (longestMatch != null) {
                foundEntities.add(longestMatch);
                toAdd.add(nafWrapper.createEntity(i, type(longestMatch), wordSpan));
                i += 1;
            }
        }
        return toAdd;
    }

    private String type(Name entity) {
        if (entity.getType().equals("person"))
            return "PER";
        if (entity.getType().equals("location"))
            return "LOC";
        if (entity.getType().equals("ship"))
            return "SHP";
        System.out.println("Unregistered matched entity type: " + entity.getType());
        return "OTH";
    }

    private void testUnmatchedEntities(List<Name> entityNames, Set<Name> foundEntities) {
        List<Name> unmatched = entityNames.stream().filter(e -> !foundEntities.contains(e)).collect(Collectors.toList());
        if (! unmatched.isEmpty()) {
            System.out.println("Unmatched entities: " + unmatched.stream().map(n -> n.getValue()).collect(Collectors.joining("; ")));
        }
    }

    public List<String> getMatchedEntityMentions() {
        return nafWrapper.getEntityMentions();
    }

}
