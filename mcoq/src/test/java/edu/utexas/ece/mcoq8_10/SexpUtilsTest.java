package edu.utexas.ece.mcoq8_10;

import de.tudresden.inf.lat.jsexp.Sexp;
import de.tudresden.inf.lat.jsexp.SexpFactory;
import de.tudresden.inf.lat.jsexp.SexpParserException;
import edu.utexas.ece.mcoq8_10.util.FileUtils;
import edu.utexas.ece.mcoq8_10.util.SexpUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class SexpUtilsTest {


    @Test
    public void isMainPartOfLocSexpTest() throws IOException, SexpParserException {
        List<String> linesList = null;
        File file = null;
        try {
            file = Paths.get(Thread.currentThread().getContextClassLoader().getResource("SexpUtilsTest/MainLoc.sexp").toURI()).toFile();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        linesList = FileUtils.streamOfLines(file).collect(Collectors.toList());
        Assert.assertEquals(1, linesList.size());
        Assert.assertTrue(SexpUtils.isMainPartOfLocSexp(SexpFactory.parse(linesList.get(0))));
    }

    @Test
    public void isCApp8_9Test() throws IOException, SexpParserException {
        List<String> linesList = null;
        File file = null;
        try {
            file = Paths.get(Thread.currentThread().getContextClassLoader().getResource("SexpUtilsTest/CApp8_9.sexp").toURI()).toFile();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        linesList = FileUtils.streamOfLines(file).collect(Collectors.toList());
        Assert.assertEquals(1, linesList.size());
        Assert.assertTrue(edu.utexas.ece.mcoq8_10.util.SexpUtils.isCAppSexp8_9(SexpFactory.parse(linesList.get(0)), "alternate"));
    }

    @Test
    public void newEmptyListSexp8_9Test() {
        Sexp sexp = SexpUtils.newEmptyListSexp8_9();
        Assert.assertTrue(sexp.toString().equals("(CNotation (InConstrEntrySomeLevel \"[ ]\") (() () () ()))"));
    }
}
