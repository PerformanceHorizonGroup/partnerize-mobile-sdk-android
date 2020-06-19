package com.partnerize.tracking.Conversion;

public interface CompletableConversion {
    void complete(Conversion conversion);
    void error(ConversionException exception);
}
