package com.christianoette;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BootstrapTest {

    @BeforeEach
    public void init() {

    }

    @Test
    public void testFailWithoutException() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.setRunEndless(false);
        bootstrap.execute();
    }

    @Test
    public void testSuccessfullHandlerInvocation() {

    }
}
