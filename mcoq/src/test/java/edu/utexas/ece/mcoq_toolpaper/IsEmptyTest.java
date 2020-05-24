package edu.utexas.ece.mcoq_toolpaper;

import de.tudresden.inf.lat.jsexp.Sexp;
import de.tudresden.inf.lat.jsexp.SexpParserException;
import edu.utexas.ece.mcoq_toolpaper.location.MutationLocation;
import edu.utexas.ece.mcoq_toolpaper.mutation.ReplaceTrueWithFalse;
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
public class IsEmptyTest extends MutationsTest {
    private List<String> allBeforeLines;

    @Before
    public void setUp() throws IOException {
        allBeforeLines = Files.readAllLines(Paths.get(resourceFilePath("IsEmpty/IsEmpty_before.sexp")));
    }

    /* Tests mutation ReplaceTrueWithFalse applying mutation to first location and comparing .sexp files. */
    @Test
    public void testBeforeAfterRPWM() throws IOException, SexpParserException {
        MutationLocation MutationLocation = new MutationLocation();
        int mutationCount = MutationLocation.count(allBeforeLines, new ReplaceTrueWithFalse(), true);
        Assert.assertEquals(1, mutationCount);

        List<Sexp> allBeforeSexps = new ArrayList<>();
        SexpUtils.mutateLines(allBeforeLines, new ReplaceTrueWithFalse(), 0, allBeforeSexps::add, true);

        List<Sexp> allAfterSexps = getSexpList(Paths.get(resourceFilePath("IsEmpty/IsEmpty_afterRTWF0.sexp")));

        Assert.assertEquals(allAfterSexps.size(), allBeforeSexps.size());
        Assert.assertEquals(allBeforeSexps, allAfterSexps);
    }
}
