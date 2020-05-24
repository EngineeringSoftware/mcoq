package edu.utexas.ece.mcoq_toolpaper;

import edu.utexas.ece.mcoq_toolpaper.util.ExecUtils;
import org.junit.Assert;

import java.io.File;
import java.util.Arrays;

/**
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Kush Jain <kjain14@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class MutationsTest {

    protected void checkSexpFile(String filename, String... flags) {
        ExecUtils.ExecResult result = ExecUtils.compser(new File("."), filename, Arrays.asList(flags));
        System.out.println(result.stdErr);
        Assert.assertEquals(0, result.code);
    }

    protected String resourceFilePath(String resourceName) {
        return Thread.currentThread().getContextClassLoader().getResource(resourceName).getFile();
    }
}
