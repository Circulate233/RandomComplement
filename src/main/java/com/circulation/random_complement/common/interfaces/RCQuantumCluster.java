package com.circulation.random_complement.common.interfaces;

import appeng.me.cache.helpers.ConnectionWrapper;
import appeng.tile.qnb.TileQuantumBridge;

public interface RCQuantumCluster {
    ConnectionWrapper r$getConnection();
    TileQuantumBridge[] r$getRing();
    TileQuantumBridge getCenter();
    void r$setIdlePowerUsage(Object obj, double power);
    boolean r$noWork();
}
