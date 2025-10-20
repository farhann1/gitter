package com.example.gitter.commands.checkout;

import com.example.gitter.commands.strategy.CommandStrategy;
import org.junit.jupiter.api.Test;

import static com.example.gitter.constants.Messages.ERROR_BRANCH_NAME_REQUIRED;
import static org.junit.jupiter.api.Assertions.*;

class CheckoutOptionsTest {

    @Test
    void testBuilderWithBranchName() {
        CheckoutOptions options = CheckoutOptions.builder()
                .branch("feature-branch")
                .build();

        assertEquals("feature-branch", options.getBranch());
        assertFalse(options.isCreateBranch());
    }

    @Test
    void testBuilderWithCreateBranchFlag() {
        CheckoutOptions options = CheckoutOptions.builder()
                .branch("new-branch")
                .createBranch(true)
                .build();

        assertEquals("new-branch", options.getBranch());
        assertTrue(options.isCreateBranch());
    }

    @Test
    void testBuilderWithoutCreateBranchFlag() {
        CheckoutOptions options = CheckoutOptions.builder()
                .branch("existing-branch")
                .createBranch(false)
                .build();

        assertFalse(options.isCreateBranch());
    }

    @Test
    void testBuilderDefaultCreateBranch() {
        CheckoutOptions options = CheckoutOptions.builder()
                .branch("branch")
                .build();

        assertFalse(options.isCreateBranch());
    }

    @Test
    void testBuildWithNullBranchThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            CheckoutOptions.builder()
                    .branch(null)
                    .build();
        });

        assertEquals(ERROR_BRANCH_NAME_REQUIRED, exception.getMessage());
    }

    @Test
    void testBuildWithEmptyBranchThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            CheckoutOptions.builder()
                    .branch("")
                    .build();
        });

        assertEquals(ERROR_BRANCH_NAME_REQUIRED, exception.getMessage());
    }

    @Test
    void testBuildWithoutBranchThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            CheckoutOptions.builder().build();
        });

        assertEquals(ERROR_BRANCH_NAME_REQUIRED, exception.getMessage());
    }

    @Test
    void testGetStrategyReturnsStandardCheckoutStrategy() {
        CheckoutOptions options = CheckoutOptions.builder()
                .branch("main")
                .createBranch(false)
                .build();

        CommandStrategy<CheckoutOptions> strategy = options.getStrategy();
        assertNotNull(strategy);
        assertInstanceOf(StandardCheckoutStrategy.class, strategy);
    }

    @Test
    void testGetStrategyReturnsCreateBranchCheckoutStrategy() {
        CheckoutOptions options = CheckoutOptions.builder()
                .branch("new-branch")
                .createBranch(true)
                .build();

        CommandStrategy<CheckoutOptions> strategy = options.getStrategy();
        assertNotNull(strategy);
        assertInstanceOf(CreateBranchStrategy.class, strategy);
    }
}

