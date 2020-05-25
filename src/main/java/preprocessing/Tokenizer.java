package preprocessing;

import eus.ixa.ixa.pipe.cli.CLIArgumentsParser;
import eus.ixa.ixa.pipe.cli.Parameters;
import eus.ixa.ixa.pipe.tok.Annotate;
import ixa.kaflib.KAFDocument;
import ixa.kaflib.WF;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import xjc.naf.*;

import java.io.*;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;


public class Tokenizer {
    private String lang;
    private String version;
    private Parameters parameters;
    private static final String NAFVERSION = "3.0";

    private Tokenizer(Parameters parameters, String lang, String version) {
        this.parameters = parameters;
        this.lang = lang;
        this.version = version;
    }

    private String getRawText(String file) throws IOException {
        BufferedReader breader = new BufferedReader(new FileReader(file));
        String rawText = breader.lines().collect(Collectors.joining("\n"));
        breader.close();
        return rawText;
    }

    public NAF run(String textFile) throws IOException {
        BufferedReader breader = new BufferedReader(new FileReader(textFile));
        final Properties properties = parameters.getAnnotateProperties();
        final Annotate annotator = new Annotate(breader, properties);

        breader.close();

        KAFDocument kaf = new KAFDocument(lang, version);
        final KAFDocument.LinguisticProcessor newLp = kaf
                .addLinguisticProcessor("text", "ixa-pipe-tok-" + lang,
                        version);
        newLp.setBeginTimestamp();
        annotator.tokenizeToKAF(kaf);
        newLp.setEndTimestamp();
        NAF naf = toNaf(kaf);
        naf.setRaw(getRawText(textFile));
        return naf;
    }

    public static Tokenizer create() throws ArgumentParserException {
        String[] args = {"tok", "-l", "nl"};
        String version = "2.0.0";
        CLIArgumentsParser argumentsParser = new CLIArgumentsParser(version);
        Parameters parameters = argumentsParser.parse(args);
        String lang = "nl";
        return new Tokenizer(parameters, lang, version);
    }


    public static NAF toNaf(KAFDocument kafDocument) {
        NAF naf = new NAF();
        naf.setVersion(NAFVERSION);
        NafHeader nafHeader = new NafHeader();
        KAFDocument.LinguisticProcessor lpIxa = kafDocument.getLinguisticProcessors().get("text").get(0);
        Lp lpNaf = new Lp();
        lpNaf.setHostname(lpIxa.getHostname());
        lpNaf.setVersion(lpIxa.getVersion());
        lpNaf.setName(lpIxa.getName());
        lpNaf.setBeginTimestamp(lpIxa.getBeginTimestamp());
        lpNaf.setEndTimestamp(lpIxa.getEndTimestamp());
        LinguisticProcessors textLp = new LinguisticProcessors();
        textLp.setLayer("text");
        textLp.getLps().add(lpNaf);
        nafHeader.getLinguisticProcessors().add(textLp);
        naf.setNafHeader(nafHeader);

        List<Wf> wfs = kafDocument.getWFs().stream().map(w -> createWF(w)).collect(Collectors.toList());
        Text text = new Text();
        text.getWves().addAll(wfs);
        naf.setText(text);
        return naf;
    }


    private static Wf createWF(WF w) {
        Wf wf = new Wf();
        wf.setId(w.getId());
        wf.setValue(w.getForm());
        wf.setLength(w.getLength());
        wf.setOffset(w.getOffset());
        wf.setPara(w.getPara());
        wf.setSent(w.getSent());
        return wf;
    }
}
