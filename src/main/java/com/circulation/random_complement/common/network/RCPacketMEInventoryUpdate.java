package com.circulation.random_complement.common.network;

import appeng.api.storage.data.IAEItemStack;
import appeng.client.gui.implementations.GuiMEMonitorable;
import appeng.core.AELog;
import appeng.core.sync.AppEngPacket;
import appeng.core.sync.network.INetworkInfo;
import appeng.util.item.AEItemStack;
import com.circulation.random_complement.common.handler.MEHandler;
import com.circulation.random_complement.common.interfaces.SpecialLogic;
import com.circulation.random_complement.common.util.SimpleItem;
import com.glodblock.github.client.GuiUltimateEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class RCPacketMEInventoryUpdate extends AppEngPacket {

    private static final int UNCOMPRESSED_PACKET_BYTE_LIMIT = 16777216;
    private static final int OPERATION_BYTE_LIMIT = 2048;
    private static final int TEMP_BUFFER_SIZE = 1024;
    private static final int STREAM_MASK = 255;
    @Nullable
    private final List<IAEItemStack> list;
    private final byte ref;
    @Nullable
    private final ByteBuf data;
    @Nullable
    private final GZIPOutputStream compressFrame;
    private int writtenBytes;
    private boolean empty;

    private final short id;

    public RCPacketMEInventoryUpdate(final ByteBuf stream) throws IOException {
        this.writtenBytes = 0;
        this.empty = true;
        this.data = null;
        this.compressFrame = null;
        this.list = new ArrayList<>();
        this.ref = stream.readByte();
        this.id = stream.readShort();
        GZIPInputStream gzReader = new GZIPInputStream(new InputStream() {
            public int read() {
                return stream.readableBytes() <= 0 ? -1 : stream.readByte() & STREAM_MASK;
            }
        });

        try {
            ByteBuf uncompressed = Unpooled.buffer(stream.readableBytes());
            byte[] tmp = new byte[TEMP_BUFFER_SIZE];

            while(gzReader.available() != 0) {
                int bytes = gzReader.read(tmp);
                if (bytes > 0) {
                    uncompressed.writeBytes(tmp, 0, bytes);
                }
            }

            while(uncompressed.readableBytes() > 0) {
                this.list.add(AEItemStack.fromPacket(uncompressed));
            }
        } catch (Throwable var7) {
            try {
                gzReader.close();
            } catch (Throwable var6) {
                var7.addSuppressed(var6);
            }

            throw var7;
        }

        gzReader.close();
        this.empty = this.list.isEmpty();
    }

    public RCPacketMEInventoryUpdate(short id) throws IOException {
        this((byte) 0,id);
    }

    public RCPacketMEInventoryUpdate(byte ref,short id) throws IOException {
        this.writtenBytes = 0;
        this.empty = true;
        this.ref = ref;
        this.id = id;
        this.data = Unpooled.buffer(OPERATION_BYTE_LIMIT);
        this.data.writeInt(1000);
        this.data.writeByte(this.ref);
        this.data.writeShort(this.id);
        this.compressFrame = new GZIPOutputStream(new OutputStream() {
            public void write(int value) {
                RCPacketMEInventoryUpdate.this.data.writeByte(value);
            }
        });
        this.list = null;
    }

    @Nullable
    public FMLProxyPacket getProxy() {
        try {
            this.compressFrame.close();
            this.configureWrite(this.data);
            return super.getProxy();
        } catch (IOException e) {
            AELog.debug(e);
            return null;
        }
    }

    public void appendItem(IAEItemStack is) throws IOException, BufferOverflowException {
        ByteBuf tmp = Unpooled.buffer(OPERATION_BYTE_LIMIT);
        is.writeToPacket(tmp);
        this.compressFrame.flush();
        if (this.writtenBytes + tmp.readableBytes() > UNCOMPRESSED_PACKET_BYTE_LIMIT) {
            throw new BufferOverflowException();
        } else {
            this.writtenBytes += tmp.readableBytes();
            this.compressFrame.write(tmp.array(), 0, tmp.readableBytes());
            this.empty = false;
        }
    }

    public int getLength() {
        return this.data.readableBytes();
    }

    public boolean isEmpty() {
        return this.empty;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void clientPacketData(INetworkInfo network, AppEngPacket packet, EntityPlayer player) {
        if (id != 0) {
            switch (id) {
                case 1 -> {
                    GuiScreen gui = Minecraft.getMinecraft().currentScreen;
                    if (gui instanceof GuiMEMonitorable) {
                        ((SpecialLogic) gui).r$setList(this.list.stream()
                                .map(itemStack -> SimpleItem.getInstance(itemStack.getDefinition()))
                                .collect(Collectors.toSet()));
                    }
                }
                case 2 -> MEHandler.craftableCacheS.addAll(this.list.stream()
                        .map(itemStack -> SimpleItem.getInstance(itemStack.getDefinition()))
                        .collect(Collectors.toSet()));
                case 3 -> ae2fcClientPacketData();
                case 4, 5 -> tcClientPacketData();
            }
        }
    }

    @Optional.Method(modid = "ae2fc")
    public void ae2fcClientPacketData(){
        switch (id) {
            case 3 -> {
                GuiScreen gui = Minecraft.getMinecraft().currentScreen;
                if (gui instanceof GuiUltimateEncoder) {
                    ((SpecialLogic) gui).r$addAllList(this.list.stream()
                            .map(itemStack -> SimpleItem.getInstance(itemStack.getDefinition()))
                            .collect(Collectors.toSet()));
                }
            }
        }
    }

    @Optional.Method(modid = "thermalfoundation")
    public void tcClientPacketData(){
        switch (id) {
            case 4 -> {
                GuiScreen guiS = Minecraft.getMinecraft().currentScreen;
                if (guiS instanceof SpecialLogic gui) {
                    gui.r$addAllList(this.list.stream()
                            .map(itemStack -> SimpleItem.getInstance(itemStack.getDefinition()))
                            .collect(Collectors.toSet()));
                }
            }
            case 5 -> MEHandler.craftableCacheS.addAll(this.list.stream()
                        .map(itemStack -> SimpleItem.getInstance(itemStack.getDefinition()))
                        .collect(Collectors.toSet()));
        }
    }

}
