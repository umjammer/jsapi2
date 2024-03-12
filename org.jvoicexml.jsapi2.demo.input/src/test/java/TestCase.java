/*
 * Copyright (c) 2024 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.jvoicexml.jsapi2.demo.input.InputDemo;


/**
 * TestCase.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2024-03-11 nsano initial version <br>
 */
@EnabledOnOs(OS.WINDOWS)
public class TestCase {

    @Test
    void test1() throws Exception {
        InputDemo.main(new String[0]);
    }
}
