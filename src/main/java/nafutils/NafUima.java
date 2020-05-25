package nafutils;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.*;
import org.apache.uima.UIMAFramework;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.InvalidXMLException;
import org.apache.uima.util.XMLInputSource;
import org.apache.uima.util.XMLParser;
import org.apache.uima.util.XMLSerializer;
import org.xml.sax.SAXException;
import xjc.naf.Entity;
import xjc.naf.Wf;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class NafUima {
    JCas jCas;
    TypeSystemDescription tsd;
    private static final String TSD_FILE = "src/main/resources/dkproTypeSystem.xml";


    private NafUima(JCas jCas, TypeSystemDescription tsd) {
        this.jCas = jCas;
        this.tsd = tsd;
    }

    public static NafUima create() throws IOException, InvalidXMLException, CASException, ResourceInitializationException {
        XMLParser xmlp = UIMAFramework.getXMLParser();
        XMLInputSource fis = new XMLInputSource(TSD_FILE);
        TypeSystemDescription tsd = xmlp.parseTypeSystemDescription(fis);
        fis.close();
        return new NafUima(JCasFactory.createJCas(tsd), tsd);
    }

    public void write(String outXmi) throws IOException, SAXException {
        FileOutputStream fos = new FileOutputStream(outXmi);
        XMLSerializer sax2xml = new XMLSerializer(fos, true);
        XmiCasSerializer xmiCasSerializer = new XmiCasSerializer(jCas.getTypeSystem());
        xmiCasSerializer.serialize(jCas.getCas(), sax2xml.getContentHandler());
        fos.close();
    }

    public void convert(String nafFile) {
        NafWrapper naf = NafWrapper.create(nafFile);
        jCas.setDocumentLanguage("nl");
        jCas.setDocumentText(naf.getRaw());
        addParagraphs(jCas, naf);
        addSentences(jCas, naf);
        addTokens(jCas, naf);
        addEntities(jCas, naf);
    }

    private void addEntities(JCas jCas, NafWrapper naf) {
        for (Entity e: naf.getEntities()) {
            int[] beginEnd = naf.beginAndEndIndices(e);
            Annotation a = AnnotationFactory.createAnnotation(jCas, beginEnd[0], beginEnd[1], NamedEntity.class);
            ((NamedEntity) a).setValue(e.getType());
            a.addToIndexes(jCas);
        }
    }

    private void addTokens(JCas jCas, NafWrapper naf) {
        for (Wf w: naf.getWordForms()) {
            Annotation af = AnnotationFactory.createAnnotation(jCas, w.getOffset(), w.getOffset() + w.getLength(), SurfaceForm.class);
            ((SurfaceForm) af).setValue(w.getValue());
            af.addToIndexes(jCas);
            Annotation a = AnnotationFactory.createAnnotation(jCas, w.getOffset(), w.getOffset() + w.getLength(), Token.class);
            ((Token) a).setId(w.getId());
            a.addToIndexes(jCas);
        }
    }

    private void addParagraphs(JCas jCas, NafWrapper naf) {
        List<Wf> ws = naf.getWordForms();
        if (ws.isEmpty())
            return;
        int i = ws.get(0).getPara();
        int begin = ws.get(0).getOffset();
        int end = ws.get(0).getOffset() + ws.get(0).getLength();
        for (Wf w: ws) {
            if (w.getPara() != i) {
                Annotation a = AnnotationFactory.createAnnotation(jCas, begin, end, Paragraph.class);
                a.addToIndexes(jCas);
                i = w.getPara();
                begin = w.getOffset();
            }
            end = w.getOffset() + w.getLength();
        }
    }

    private void addSentences(JCas jCas, NafWrapper naf) {
        List<Wf> ws = naf.getWordForms();
        if (ws.isEmpty())
            return;
        int i = ws.get(0).getSent();
        int begin = ws.get(0).getOffset();
        int end = ws.get(0).getOffset() + ws.get(0).getLength();
        for (Wf w: ws) {
            if (w.getSent() != i) {
                Annotation a = AnnotationFactory.createAnnotation(jCas, begin, end, Sentence.class);
                ((Sentence) a).setId("" + i);
                a.addToIndexes(jCas);
                i = w.getSent();
                begin = w.getOffset();
            }
            end = w.getOffset() + w.getLength();
        }
    }

}
