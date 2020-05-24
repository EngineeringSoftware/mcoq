package edu.utexas.ece.mcoq_toolpaper;

import edu.utexas.ece.mcoq_toolpaper.mutation.Mutations;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Ahmet Celik <ahmetcelik@utexas.edu>
 * @author Kush Jain <kjain14@utexas.edu>
 * @author Marinela Parovic <marinelaparovic@gmail.com>
 */
public class AlternateCountTest {

    private String alternate, output;

    @Before
    public void setUp() throws IOException {
        alternate = Thread.currentThread().getContextClassLoader().getResource("Alternate8_9/Alternate_before.sexp").getFile();
        output = Files.createTempFile("output", ".sexp").toString();
    }

    @Test
    public void testAlternateReplaceFunctionWithIdentityCount() throws IOException {
        MCoq.main("--file", alternate, "-m", Mutations.REPLACE_FUNCTION_WITH_IDENTITY.name(), "-o", output, "-c", "--all");
        List<String> lines = Files.readAllLines(Paths.get(output));
        Assert.assertEquals(1, lines.size());
        Assert.assertEquals("0", lines.get(0));
    }

    @Test
    public void testAlternateReplaceMatchExpressionCount() throws IOException {
        MCoq.main("--file", alternate, "-m", Mutations.REPLACE_MATCH_EXPRESSION.name(), "-o", output, "-c", "--all");
        List<String> lines = Files.readAllLines(Paths.get(output));
        Assert.assertEquals(1, lines.size());
        Assert.assertEquals("2", lines.get(0));
    }

    @Test
    public void testAlternateRightEmptyConcatCount() throws IOException {
        MCoq.main("--file", alternate, "-m", Mutations.RIGHT_EMPTY_CONCAT_LISTS.name(), "-o", output, "-c", "--all");
        List<String> lines = Files.readAllLines(Paths.get(output));
        Assert.assertEquals(1, lines.size());
        Assert.assertEquals("0", lines.get(0));
    }

    @Test
    public void testAlternateLeftEmptyConcatCount() throws IOException {
        MCoq.main("--file", alternate, "-m", Mutations.LEFT_EMPTY_CONCAT_LISTS.name(), "-o", output, "-c", "--all");
        List<String> lines = Files.readAllLines(Paths.get(output));
        Assert.assertEquals(1, lines.size());
        Assert.assertEquals("0", lines.get(0));
    }

    @Test
    public void testAlternateReorderConcatCount() throws IOException {
        MCoq.main("--file", alternate, "-m", Mutations.REORDER_CONCAT_LISTS.name(), "-o", output, "-c", "--all");
        List<String> lines = Files.readAllLines(Paths.get(output));
        Assert.assertEquals(1, lines.size());
        Assert.assertEquals("0", lines.get(0));
    }

    @Test
    public void testAlternateReplaceListWithEmptyListCount() throws IOException {
        MCoq.main("--file", alternate, "-m", Mutations.REPLACE_LIST_WITH_EMPTY_LIST.name(), "-o", output, "-c", "--all");
        List<String> lines = Files.readAllLines(Paths.get(output));
        Assert.assertEquals(1, lines.size());
        Assert.assertEquals("5", lines.get(0));
    }

    @Test
    public void testAlternateReplaceListWithTailCount() throws IOException {
        MCoq.main("--file", alternate, "-m", Mutations.REPLACE_LIST_WITH_TAIL.name(), "-o", output, "-c", "--all");
        List<String> lines = Files.readAllLines(Paths.get(output));
        Assert.assertEquals(1, lines.size());
        Assert.assertEquals("5", lines.get(0));
    }

    @Test
    public void testAlternateReplaceListWithHeadCount() throws IOException {
        MCoq.main("--file", alternate, "-m", Mutations.REPLACE_LIST_WITH_HEAD.name(), "-o", output, "-c", "--all");
        List<String> lines = Files.readAllLines(Paths.get(output));
        Assert.assertEquals(1, lines.size());
        Assert.assertEquals("5", lines.get(0));
    }

    @Test
    public void testAlternateReverseInductiveCasesCount() throws IOException {
        MCoq.main("--file", alternate, "-m", Mutations.REVERSE_INDUCTIVE_CASES.name(), "-o", output, "-c", "--all");
        List<String> lines = Files.readAllLines(Paths.get(output));
        Assert.assertEquals(1,  lines.size());
        Assert.assertEquals("1", lines.get(0));
    }

    @Test
    public void testAlternateReverseMatchCasesCount() throws IOException {
        MCoq.main("--file", alternate, "-m", Mutations.REVERSE_MATCH_CASES.name(), "-o", output, "-c", "--all");
        List<String> lines = Files.readAllLines(Paths.get(output));
        Assert.assertEquals(1,  lines.size());
        Assert.assertEquals("2", lines.get(0));
    }

    @After
    public void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(output));
    }
}
