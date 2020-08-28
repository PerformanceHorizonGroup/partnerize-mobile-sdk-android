package com.partnerize.tracking.ClickManager;

import com.partnerize.tracking.TestClickConsts;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class VirtualClickTest {

    @Test
    public void testVirtualClickBuildWithInvalidJSON() {
        final String response = TestClickConsts.invalidJsonResponse;
        VirtualClick.VirtualClickBuilder builder = new VirtualClick.VirtualClickBuilder();

        try {
            builder.buildWithJSON(response);
            fail("exoected to throw ClickException");
        } catch (ClickException ex) {
            assertEquals("Invalid JSON.", ex.getMessage());
        }
    }

    @Test
    public void testVirtualClickBuildWithValidJSON() throws Throwable {
        final String response = TestClickConsts.validJsonResponse;
        VirtualClick.VirtualClickBuilder builder = new VirtualClick.VirtualClickBuilder();
        VirtualClick click = builder.buildWithJSON(response);

        assertEquals("9g9g9g9g9g9g", click.getClickref());
        assertEquals("https://molimo.partnerize.com/product/999999999", click.getDestination());
    }
}

