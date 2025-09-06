package com.circulation.random_complement.client;

import com.circulation.random_complement.client.buttonsetting.InscriberAutoOutput;
import com.circulation.random_complement.client.buttonsetting.InscriberBlockMode;
import com.circulation.random_complement.client.buttonsetting.InscriberMaxStackLimit;
import com.circulation.random_complement.client.buttonsetting.PatternTermAutoFillPattern;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.EnumSet;

public enum RCSettings {
    InscriberBlockMode(EnumSet.allOf(InscriberBlockMode.class)),
    InscriberAutoOutput(EnumSet.allOf(InscriberAutoOutput.class)),
    InscriberMaxStackLimit(EnumSet.allOf(InscriberMaxStackLimit.class)),
    PatternTermAutoFillPattern(EnumSet.allOf(PatternTermAutoFillPattern.class));

    @Getter
    private final EnumSet<? extends Enum<?>> values;

    RCSettings(@Nonnull EnumSet<? extends Enum<?>> possibleOptions) {
        if (possibleOptions.isEmpty()) {
            throw new IllegalArgumentException("Tried to instantiate an empty setting.");
        } else {
            this.values = possibleOptions;
        }
    }

}
