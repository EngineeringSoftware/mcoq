package edu.utexas.ece.mcoq_toolpaper;

import de.tudresden.inf.lat.jsexp.Sexp;
import de.tudresden.inf.lat.jsexp.SexpParserException;
import edu.utexas.ece.mcoq_toolpaper.location.MutationLocation;
import edu.utexas.ece.mcoq_toolpaper.mutation.ReplaceSuccessorWithZero;
import edu.utexas.ece.mcoq_toolpaper.util.SexpUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static edu.utexas.ece.mcoq_toolpaper.util.SexpUtils.getSexpList;

/**
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Kush Jain <kjain14@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class IncDigits0Test extends MutationsTest {
    private List<String> allBeforeLines;

    @Before
    public void setUp() throws IOException {
        allBeforeLines = Files.readAllLines(Paths.get(resourceFilePath("IncDigits08_10/IncDigits_before.sexp")));
    }

    /* Tests mutation ReplaceSuccessorWithZero applying mutation to all locations one after the other and comparing .sexp files. */
    @Test
    public void testBeforeAfterRSWZ() throws IOException, SexpParserException {
        MutationLocation MutationLocation = new MutationLocation();
        int mutationCount = MutationLocation.count(allBeforeLines, new ReplaceSuccessorWithZero(), true);
        Assert.assertEquals(2, mutationCount);

        for (int i = 0; i < mutationCount; i++) {
            List<Sexp> allBeforeSexps = new ArrayList<>();
            SexpUtils.mutateLines(allBeforeLines, new ReplaceSuccessorWithZero(), i, allBeforeSexps::add, true);

            List<Sexp> allAfterSexps = getSexpList(Paths.get(resourceFilePath("IncDigits08_10/IncDigits_afterRSWZ" + i + ".sexp")));

            Assert.assertEquals(allAfterSexps.size(), allBeforeSexps.size());
            Assert.assertEquals(allBeforeSexps.stream().map(SexpUtils::stripLocs).collect(Collectors.toList()),
                    allAfterSexps.stream().map(SexpUtils::stripLocs).collect(Collectors.toList())
            );
        }
    }
}
