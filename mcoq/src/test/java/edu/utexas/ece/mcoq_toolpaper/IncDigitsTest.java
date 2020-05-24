package edu.utexas.ece.mcoq_toolpaper;

import de.tudresden.inf.lat.jsexp.Sexp;
import de.tudresden.inf.lat.jsexp.SexpParserException;
import edu.utexas.ece.mcoq_toolpaper.location.MutationLocation;
import edu.utexas.ece.mcoq_toolpaper.mutation.RemoveSuccessorApplication;
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
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class IncDigitsTest extends MutationsTest {
    private List<String> allBeforeLines;

    @Before
    public void setUp() throws IOException {
        allBeforeLines = Files.readAllLines(Paths.get(resourceFilePath("IncDigits/IncDigits_before.sexp")));
    }

    /* Tests mutation RemoveSuccessorApplication applying mutation to all locations one after the other and comparing .sexp files. */
    @Test
    public void testBeforeAfterRSA() throws IOException, SexpParserException {
        MutationLocation MutationLocation = new MutationLocation();
        int mutationCount = MutationLocation.count(allBeforeLines, new RemoveSuccessorApplication(), true);
        Assert.assertEquals(2, mutationCount);

        for (int i = 0; i < mutationCount; i++) {
            List<Sexp> allBeforeSexps = new ArrayList<>();
            SexpUtils.mutateLines(allBeforeLines, new RemoveSuccessorApplication(), i, allBeforeSexps::add, true);

            List<Sexp> allAfterSexps = getSexpList(Paths.get(resourceFilePath("IncDigits/IncDigits_afterRSA" + i + ".sexp")));

            Assert.assertEquals(allAfterSexps.size(), allBeforeSexps.size());
            Assert.assertEquals(allBeforeSexps, allAfterSexps);
        }
    }
}
