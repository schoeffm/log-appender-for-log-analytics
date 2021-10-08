package de.bender.logging.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static de.bender.logging.common.Signature.RFC_1123_DATE;
import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.OrderingComparison.greaterThan;

class SignatureTest {

    Signature underTest;

    @BeforeEach
    void setUp() throws Exception {
        underTest = new Signature("This is the payload", "workspaceId", "secret");
    }

    @Test
    void getSignatureAsClearString_returnsAClearTestViewOfTheSignature() {

        // when - then
        assertThat(underTest.getSignatureAsClearString(), startsWith("POST\n19\napplication/json\nx-ms-date:"));
        assertThat(underTest.getSignatureAsClearString(), endsWith("\n/api/logs"));
    }

    @Test
    void getXmsDateAsString_returnsAValidFormattedDateString() throws ParseException {
        // given
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(RFC_1123_DATE, Locale.US);

        // when
        Date parsed = simpleDateFormat.parse(this.underTest.getXmsDateAsString());

        // then
        assertThat(new Date(), greaterThan(parsed));
    }
}