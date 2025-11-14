package com.circulation.random_complement.common.interfaces;

import appeng.tile.qnb.TileQuantumBridge;

public interface RCQuantumCluster {
    TileQuantumBridge[] r$getRing();

    TileQuantumBridge getCenter();

    void r$setIdlePowerUsage(RCQuantumCluster qc, double power);

    boolean r$noWork();
}