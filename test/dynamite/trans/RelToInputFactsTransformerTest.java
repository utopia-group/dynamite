package dynamite.trans;

import java.io.StringReader;
import java.nio.file.Paths;

import org.junit.Assert;
import org.junit.Test;

import dynamite.reldb.RelSchemaParser;
import dynamite.reldb.ast.RelationalSchema;
import dynamite.util.FileUtils;

public class RelToInputFactsTransformerTest {

    @Test
    public void test1() {
        String dir = "tmp/Souffle_input/RelToInputFactsTransformerTest/";
        String schemaString = "Reviews(id: Int, country: String, description: String, designation: String, points: Int, price: Float, province: String, region_1: String, region_2: String, taster_name: String, taster_twitter_handle: String, title: String, variety: String, winery: String)\n";
        String relString = "Reviews(id: Int, country: String, description: String, designation: String, points: Int, price: Float, province: String, region_1: String, region_2: String, taster_name: String, taster_twitter_handle: String, title: String, variety: String, winery: String)\n" +
                "0, \"Italy\", \"Aromas include tropical fruit, broom, brimstone and dried herb. The palate isn't overly expressive, offering unripened apple, citrus and dried sage alongside brisk acidity.\", \"Vulka Bianco\", 87, null, \"Sicily & Sardinia\", \"Etna\", null, \"Kerin O'Keefe\", \"@kerinokeefe\", \"Nicosia 2013 Vulka Bianco  (Etna)\", \"White Blend\", \"Nicosia\"\n";
        RelationalSchema schema = RelSchemaParser.parse(schemaString);
        RelToInputFactsTransformer.parseToFactsCsv(schema.tables.get(0), new StringReader(relString), dir);
        String actual = FileUtils.readFromFile(dir + "Reviews.facts");
        String expected = "0\tItaly\tAromas include tropical fruit, broom, brimstone and dried herb. The palate isn't overly expressive, offering unripened apple, citrus and dried sage alongside brisk acidity.\tVulka Bianco\t87\tnull\tSicily & Sardinia\tEtna\tnull\tKerin O'Keefe\t@kerinokeefe\tNicosia 2013 Vulka Bianco  (Etna)\tWhite Blend\tNicosia";
        Assert.assertEquals(expected, actual);
        FileUtils.deleteDirAndFiles(Paths.get(dir));
    }
}
