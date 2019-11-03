package com.nickrammos.jflux;

import com.nickrammos.jflux.annotation.Field;
import com.nickrammos.jflux.annotation.Tag;

import org.junit.Test;

public class JFluxClientAnnotationIT extends AbstractJFluxClientIT {

    @Test
    public void write_shouldWriteAnnotatedClass() {
        // Given
        TestMeasurement testPoint = new TestMeasurement();
        testPoint.testField = 4;
        testPoint.otherField = -1;
        testPoint.tag = "some tag value";

        // When
        jFluxClient.write(dbName, testPoint);

        // Then
        // No exception should be thrown.
    }

    private static class TestMeasurement {

        @Field
        private int testField;

        private int otherField;

        @Tag("test_tag")
        private String tag;
    }
}
