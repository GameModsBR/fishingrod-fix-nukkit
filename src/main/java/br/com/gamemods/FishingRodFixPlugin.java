package br.com.gamemods;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEntityEvent;
import cn.nukkit.inventory.transaction.data.ReleaseItemData;
import cn.nukkit.inventory.transaction.data.UseItemData;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.network.protocol.AnimatePacket;
import cn.nukkit.network.protocol.InventoryTransactionPacket;
import cn.nukkit.network.protocol.LevelSoundEventPacketV2;
import cn.nukkit.network.protocol.types.NetworkInventoryAction;
import cn.nukkit.plugin.PluginBase;

public class FishingRodFixPlugin extends PluginBase implements Listener {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Item item = event.getItem();
        if (item.getId() == ItemID.FISHING_ROD) {
            Player player = event.getPlayer();
            int slot = player.getInventory().getHeldItemIndex();

            LevelSoundEventPacketV2 soundPacket = new LevelSoundEventPacketV2();
            soundPacket.sound = LevelSoundEventPacketV2.SOUND_THROW;
            soundPacket.x = (float) player.x;
            soundPacket.y = (float) player.y;
            soundPacket.z = (float) player.z;
            soundPacket.extraData = -1;
            soundPacket.entityIdentifier = "minecraft:player";
            soundPacket.isBabyMob = false;
            soundPacket.isGlobal = false;
            player.handleDataPacket(soundPacket);

            AnimatePacket animatePacket = new AnimatePacket();
            animatePacket.rowingTime = 0F;
            animatePacket.action = AnimatePacket.Action.SWING_ARM;
            animatePacket.eid = player.getId();
            player.handleDataPacket(animatePacket);

            InventoryTransactionPacket inventoryTransactionPacket = new InventoryTransactionPacket();
            inventoryTransactionPacket.actions = new NetworkInventoryAction[0];
            inventoryTransactionPacket.transactionType = InventoryTransactionPacket.TYPE_USE_ITEM;
            UseItemData useItemData = new UseItemData();
            useItemData.actionType = InventoryTransactionPacket.USE_ITEM_ACTION_CLICK_AIR;
            useItemData.blockPos = new BlockVector3(0, 0, 0);
            useItemData.face = BlockFace.SOUTH;
            useItemData.hotbarSlot = slot;
            useItemData.itemInHand = item.clone();
            useItemData.playerPos = player.getPosition();
            useItemData.clickPos = new Vector3f(0, 0, 0);
            useItemData.blockRuntimeId = 0;
            inventoryTransactionPacket.transactionData = useItemData;
            player.handleDataPacket(inventoryTransactionPacket);

            inventoryTransactionPacket = new InventoryTransactionPacket();
            inventoryTransactionPacket.actions = new NetworkInventoryAction[0];
            inventoryTransactionPacket.transactionType = InventoryTransactionPacket.TYPE_RELEASE_ITEM;
            ReleaseItemData releaseItemData = new ReleaseItemData();
            releaseItemData.actionType = InventoryTransactionPacket.RELEASE_ITEM_ACTION_RELEASE;
            releaseItemData.hotbarSlot = slot;
            releaseItemData.itemInHand = item.clone();
            releaseItemData.headRot = player.getPosition().up();
            inventoryTransactionPacket.transactionData = releaseItemData;
            player.handleDataPacket(inventoryTransactionPacket);
        }
    }
}
