package preprocessing;

import xjc.matchedTerms.MatchTest;
import xjc.matchedTerms.Name;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class Entities {
    // input: XML file with matched entities + naf output of TEI/tok preprocessing
    // output: UIMA or NAF file with tokens and entities

    // this class reads XML file and lists its entities
    // another class will merge the entities for this file and the parallel naf file

    private MatchTest matchTest;
    private Entities(MatchTest matchTest) {
        this.matchTest = matchTest;
    }

    public static Entities create(String xml) {
        return new Entities(load(xml));
    }

    public static MatchTest load(String xml) {
        File file = new File(xml);
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(MatchTest.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            MatchTest matchTest = (MatchTest) jaxbUnmarshaller.unmarshal(file);
            return matchTest;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Name> names() {
        return matchTest.getPages().stream()
                .map(p -> p.getContent())
                .filter(c -> c != null)
                .map(c -> c.getContent())
                .flatMap(x -> x.stream())
                .filter(x -> x instanceof Name)
                .map(n -> (Name) n)
                .collect(Collectors.toList());
    }
}
