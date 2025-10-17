package com.circulation.random_complement.client;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@FunctionalInterface
public interface ItemTooltipAdd {

    @NotNull
    List<String> get();
}
