package com.circulation.random_complement.client;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

@FunctionalInterface
public interface ItemTooltipAdd extends Supplier<List<String>> {

    @NotNull
    List<String> get();
}
