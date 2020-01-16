package edu.utexas.ece.mcoq8_10;

import de.tudresden.inf.lat.jsexp.Sexp;
import de.tudresden.inf.lat.jsexp.SexpParserException;
import edu.utexas.ece.mcoq8_10.location.MutationLocation;
import edu.utexas.ece.mcoq8_10.mutation.ReplacePlusWithMinus;
import edu.utexas.ece.mcoq8_10.util.SexpUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static edu.utexas.ece.mcoq8_10.util.SexpUtils.getSexpList;

/**
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class DoubleDigitsTest extends MutationsTest {

    private List<String> allBeforeLines;

    @Before
    public void setUp() throws IOException {
        allBeforeLines = Files.readAllLines(Paths.get(resourceFilePath("DoubleDigits/Digits_before.sexp")));
    }

    /* Tests mutation ReplacePlusWithMinus applying mutation to first location and comparing .sexp files. */
    @Test
    public void testBeforeAfterRPWM() throws IOException, SexpParserException {
        MutationLocation MutationLocation = new MutationLocation();
        int mutationCount = MutationLocation.count(allBeforeLines, new ReplacePlusWithMinus(), true);
        Assert.assertEquals(3, mutationCount);

        List<Sexp> allBeforeSexps = new ArrayList<>();
        SexpUtils.mutateLines(allBeforeLines, new ReplacePlusWithMinus(), 0, allBeforeSexps::add, true);

        List<Sexp> allAfterSexps = getSexpList(Paths.get(resourceFilePath("DoubleDigits/Digits_afterRPWM0.sexp")));

        Assert.assertEquals(allAfterSexps.size(), allBeforeSexps.size());
        Assert.assertEquals(allBeforeSexps, allAfterSexps);
    }
}
