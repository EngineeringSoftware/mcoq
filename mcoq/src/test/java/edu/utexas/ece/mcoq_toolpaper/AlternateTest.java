package edu.utexas.ece.mcoq_toolpaper;

import de.tudresden.inf.lat.jsexp.Sexp;
import de.tudresden.inf.lat.jsexp.SexpParserException;
import edu.utexas.ece.mcoq_toolpaper.location.MutationLocation;
import edu.utexas.ece.mcoq_toolpaper.mutation.*;
import edu.utexas.ece.mcoq_toolpaper.util.SexpUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static edu.utexas.ece.mcoq_toolpaper.util.SexpUtils.getSexpList;

/**
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Kush Jain <kjain14@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class AlternateTest extends MutationsTest {

    private List<String> allBeforeLines;

    @Before
    public void setUp() throws IOException {
        allBeforeLines = Files.readAllLines(Paths.get(resourceFilePath("Alternate8_9/Alternate_before.sexp")));
    }

    /* Tests mutation ReplaceListWithHead applying mutation on all possible locations (one after the other) and comparing .sexp files. */
    @Test
    public void testBeforeAfterRLWH() throws IOException, SexpParserException {
        MutationLocation MutationLocation = new MutationLocation();
        int mutationCount = MutationLocation.count(allBeforeLines, new ReplaceListWithHead(), true);
        Assert.assertEquals(5, mutationCount);

        for (int i=0; i<mutationCount; i++) {
            List<Sexp> allBeforeSexps = new ArrayList<>();
            SexpUtils.mutateLines(allBeforeLines, new ReplaceListWithHead(), i, allBeforeSexps::add, true);

            List<Sexp> allAfterSexps = getSexpList(Paths.get(resourceFilePath("Alternate8_9/Alternate_afterRLWH"+i+".sexp")));

            Assert.assertEquals(allAfterSexps.size(), allBeforeSexps.size());
            Assert.assertEquals(allBeforeSexps, allAfterSexps);
        }
    }

    /* Tests mutation ReplaceListWithTail applying mutation on all possible locations (one after the other) and comparing .sexp files. */
    @Test
    public void testBeforeAfterRLWT() throws IOException, SexpParserException {
        MutationLocation MutationLocation = new MutationLocation();
        int mutationCount = MutationLocation.count(allBeforeLines, new ReplaceListWithTail(), true);
        Assert.assertEquals(5, mutationCount);

        for (int i=0; i<mutationCount; i++) {
            List<Sexp> allBeforeSexps = new ArrayList<>();
            SexpUtils.mutateLines(allBeforeLines, new ReplaceListWithTail(), i, allBeforeSexps::add, true);

            List<Sexp> allAfterSexps = getSexpList(Paths.get(resourceFilePath("Alternate8_9/Alternate_afterRLWT"+i+".sexp")));

            Assert.assertEquals(allAfterSexps.size(), allBeforeSexps.size());
            Assert.assertEquals(allBeforeSexps, allAfterSexps);
        }
    }

    /* Tests mutation ReplaceListWithEmptyList applying mutation on all possible locations (one after the other) and comparing .sexp files. */
    @Test
    public void testBeforeAfterRLWE() throws IOException, SexpParserException {
        MutationLocation MutationLocation = new MutationLocation();
        int mutationCount = MutationLocation.count(allBeforeLines, new ReplaceListWithEmptyList(), true);
        Assert.assertEquals(5, mutationCount);

        for (int i=0; i<mutationCount; i++) {
            List<Sexp> allBeforeSexps = new ArrayList<>();
            SexpUtils.mutateLines(allBeforeLines, new ReplaceListWithEmptyList(), i, allBeforeSexps::add, true);

            List<Sexp> allAfterSexps = getSexpList(Paths.get(resourceFilePath("Alternate8_9/Alternate_afterRLWE"+i+".sexp")));

            Assert.assertEquals(allAfterSexps.size(), allBeforeSexps.size());
            Assert.assertEquals(allBeforeSexps, allAfterSexps);
        }
    }

    /* Tests mutation ReplaceMatchExpression applying mutation on all possible locations (one after the other) and comparing .sexp files. */
    @Test
    public void testBeforeAfterRME() throws IOException, SexpParserException {
        MutationLocation MutationLocation = new MutationLocation();
        int mutationCount = MutationLocation.count(allBeforeLines, new ReplaceMatchExpression(), true);
        Assert.assertEquals(2, mutationCount);

        for (int i=0; i<mutationCount; i++) {
            List<Sexp> allBeforeSexps = new ArrayList<>();
            SexpUtils.mutateLines(allBeforeLines, new ReplaceMatchExpression(), i, allBeforeSexps::add, true);

            List<Sexp> allAfterSexps = getSexpList(Paths.get(resourceFilePath("Alternate8_9/Alternate_afterRME"+i+".sexp")));

            Assert.assertEquals(allAfterSexps.size(), allBeforeSexps.size());
            Assert.assertEquals(allBeforeSexps, allAfterSexps);
        }
    }

    /* Tests mutation ReverseMatchCases applying mutation on all possible locations (one after the other) and comparing .sexp files. */
    @Test
    public void testBeforeAfterRMC() throws IOException, SexpParserException {
        MutationLocation MutationLocation = new MutationLocation();
        int mutationCount = MutationLocation.count(allBeforeLines, new ReverseMatchCases(), true);
        Assert.assertEquals(2, mutationCount);

        for (int i=0; i<mutationCount; i++) {
            List<Sexp> allBeforeSexps = new ArrayList<>();
            SexpUtils.mutateLines(allBeforeLines, new ReverseMatchCases(), i, allBeforeSexps::add, true);

            List<Sexp> allAfterSexps = getSexpList(Paths.get(resourceFilePath("Alternate8_9/Alternate_afterRMC"+i+".sexp")));

            Assert.assertEquals(allAfterSexps.size(), allBeforeSexps.size());
            Assert.assertEquals(allBeforeSexps, allAfterSexps);
        }
    }

    /* Tests mutation ReverseInductiveCases applying mutation on all possible locations (one after the other) and comparing .sexp files. */
    @Test
    public void testBeforeAfterRIC() throws IOException, SexpParserException {
        MutationLocation MutationLocation = new MutationLocation();
        int mutationCount = MutationLocation.count(allBeforeLines, new ReverseInductiveCases(), true);
        Assert.assertEquals(1, mutationCount);

        for (int i=0; i<mutationCount; i++) {
            List<Sexp> allBeforeSexps = new ArrayList<>();
            SexpUtils.mutateLines(allBeforeLines, new ReverseInductiveCases(), i, allBeforeSexps::add, true);

            List<Sexp> allAfterSexps = getSexpList(Paths.get(resourceFilePath("Alternate8_9/Alternate_afterRIC"+i+".sexp")));

            Assert.assertEquals(allAfterSexps.size(), allBeforeSexps.size());
            Assert.assertEquals(allBeforeSexps, allAfterSexps);
        }
    }
}
