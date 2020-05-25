package nafutils;

import parser.NafParser;
import xjc.naf.*;
import xjc.naf.Entities;

import java.io.BufferedWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NafWrapper {
    private NAF naf;

    private NafWrapper(NAF naf) {
        this.naf = naf;
    }

    public static NafWrapper create(String nafFile) {
        NafParser parser = new NafParser();
        parser.parse(nafFile);
        return new NafWrapper(parser.getNaf());
    }

    public static NafWrapper create(NAF naf) {
        return new NafWrapper(naf);
    }

    public void write(String file) {
        NafParser parser = new NafParser(naf);
        parser.write(file);
    }

    public List<Wf> getWordForms() {
        return naf.getText().getWves();
    }

    public List<Term> getTerms() {
        if (naf.getTerms() == null)
            return Collections.EMPTY_LIST;
        return naf.getTerms().getTerms();
    }

    public void addTermsFromText() {
        if (naf.getTerms() != null)
            throw new IllegalArgumentException("Expected null Terms layer");
        Terms terms = new Terms();
        terms.getTerms().addAll(naf.getText().getWves().stream().map(wf -> createTerm(wf)).collect(Collectors.toList()));
        naf.setTerms(terms);
    }

    private String getTermID(Wf wf) {
        return "t" + wf.getId().substring(1);
    }

    private String getWfId(String termId) {
        return "w" + termId.substring(1);
    }

    private Target createTarget(String id) {
        Target target = new Target();
        target.setId(id);
        return target;
    }

    private Term createTerm(Wf wf) {
        Term t = new Term();
        t.setId(getTermID(wf));
        t.setSpan(new Span());
        Target target = createTarget(wf.getId());
        t.getSpan().getTargets().add(target);
        return t;
    }


    public void addLP(String layer, String processor, String version) {
        Calendar cal = Calendar.getInstance();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
        String time = new SimpleDateFormat("HH:mm:ss").format(cal.getTime());
        String host = "";
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Lp lp = new Lp();
        lp.setTimestamp(date + "_" + time);
        lp.setName(processor);
        lp.setVersion(version);
        lp.setHostname(host);
        LinguisticProcessors lps = naf.getNafHeader().getLinguisticProcessors().stream().filter(p -> p.getLayer().equals(layer)).findFirst().orElse(null);
        if (lps == null) {
            lps = new LinguisticProcessors();
            lps.setLayer(layer);
        }
        lps.getLps().add(lp);
        naf.getNafHeader().getLinguisticProcessors().add(lps);
    }

    public Entity createEntity(int i, String type, List<Wf> wordSpan) {
        Entity e = new Entity();
        e.setId("e" + i);
        e.setType(type);
        References refs = new References();
        Span s = new Span();
        List<Target> targets = wordSpan.stream().map(w -> getTermID(w)).map(tid -> createTarget(tid)).collect(Collectors.toList());
        s.getTargets().addAll(targets);
        refs.setSpan(s);
        e.setReferences(refs);
        return e;
    }

    public void addEntities(List<Entity> entities) {
        if (naf.getEntities() != null)
            throw new IllegalArgumentException("Expected null Entities Layer");
        Entities es = new Entities();
        es.getEntities().addAll(entities);
        naf.setEntities(es);
    }

    private Wf getWf(String wfId) {
        return naf.getText().getWves().stream().filter(w -> w.getId().equals(wfId)).findFirst().orElse(null);
    }

    public List<String> getEntityMentions() {
        return getEntities().stream().map(e -> wordForm(e)).collect(Collectors.toList());
    }

    public List<Entity> getEntities() {
        if (naf.getEntities() == null)
            return Collections.EMPTY_LIST;
        return naf.getEntities().getEntities();
    }

    private String wordForm(Entity e) {
        return e.getReferences().getSpan().getTargets().stream().map(t -> t.getId()).map(t -> getWfId(t)).map(i -> getWf(i).getValue()).collect(Collectors.joining(" "));
    }

    public String getRaw() {
        return naf.getRaw();
    }

    public int[] beginAndEndIndices(Entity e) {
        List<Target> targets = e.getReferences().getSpan().getTargets();
        int begin = getWf(getWfId(targets.get(0).getId())).getOffset();
        Wf t1 = getWf(getWfId(targets.get(targets.size() - 1).getId()));
        return new int[]{begin, t1.getOffset() + t1.getLength()};
    }
}
