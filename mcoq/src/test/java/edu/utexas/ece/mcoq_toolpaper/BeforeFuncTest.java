package edu.utexas.ece.mcoq_toolpaper;

import de.tudresden.inf.lat.jsexp.Sexp;
import de.tudresden.inf.lat.jsexp.SexpParserException;
import edu.utexas.ece.mcoq_toolpaper.location.MutationLocation;
import edu.utexas.ece.mcoq_toolpaper.mutation.ReplaceFalseWithTrue;
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
public class BeforeFuncTest extends MutationsTest {
    private List<String> allBeforeLines;

    @Before
    public void setUp() throws IOException {
        allBeforeLines = Files.readAllLines(Paths.get(resourceFilePath("BeforeFunc/Before_before.sexp")));
    }

    /* Tests mutation ReplaceFalseWithTrue applying mutation to first location and comparing .sexp files. */
    @Test
    public void testBeforeAfterRSA() throws IOException, SexpParserException {
        MutationLocation MutationLocation = new MutationLocation();
        int allMutationCount = MutationLocation.count(allBeforeLines, new ReplaceFalseWithTrue(), true);
        Assert.assertEquals(2, allMutationCount);
        int mutationCount = MutationLocation.count(allBeforeLines, new ReplaceFalseWithTrue(), false);
        Assert.assertEquals(1, mutationCount);

        for (int i = 0; i < allMutationCount; i++) {
            List<Sexp> allBeforeSexps = new ArrayList<>();
            SexpUtils.mutateLines(allBeforeLines, new ReplaceFalseWithTrue(), i, allBeforeSexps::add, true);

            List<Sexp> allAfterSexps = getSexpList(Paths.get(resourceFilePath("BeforeFunc/Before_afterRFWT" + i + ".sexp")));

            Assert.assertEquals(allAfterSexps.size(), allBeforeSexps.size());
            Assert.assertEquals(allBeforeSexps, allAfterSexps);
        }
    }
}
