// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.schedule.ext.workdays.core;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import org.junit.Test;

/**
 *
 * @author Zqq
 */
public class ParserTest {

    @Test
    public void test() throws IOException {
        String path = "src/main/resources/ParserTest.txt";

        Reader reader = new FileReader(path);

        new Parser().parse(2014, reader);
        reader.close();
    }
}
