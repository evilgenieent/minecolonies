package com.minecolonies.network.messages;

import com.minecolonies.colony.ColonyManager;
import com.minecolonies.colony.Schematics;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

import java.util.*;

public class ColonyStylesMessage implements IMessage, IMessageHandler<ColonyStylesMessage, IMessage>
{
    private Map<String, List<String>> hutStyleMap;
    public ColonyStylesMessage(){}

    @Override
    public void toBytes(ByteBuf buf)
    {
        Set<String> huts = Schematics.getHuts();

        buf.writeInt(huts.size());
        for(String hut : huts)
        {
            List<String> styles = Schematics.getStylesForHut(hut);

            buf.writeInt(styles.size());
            for(String style : styles)
            {
                ByteBufUtils.writeUTF8String(buf, style);
            }

            ByteBufUtils.writeUTF8String(buf, hut);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        hutStyleMap = new HashMap<>();

        int numHuts = buf.readInt();
        for(int i = 0; i < numHuts; i++)
        {
            List<String> styles = new ArrayList<>();
            int numStyles = buf.readInt();
            for(int j = 0; j < numStyles; j++)
            {
                styles.add(ByteBufUtils.readUTF8String(buf));
            }

            hutStyleMap.put(ByteBufUtils.readUTF8String(buf), styles);
        }
    }

    @Override
    public IMessage onMessage(ColonyStylesMessage message, MessageContext ctx)
    {
        Schematics.setStyles(message.hutStyleMap);
        return null;
    }
}