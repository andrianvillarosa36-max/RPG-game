package com.chuck.rpggame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.chuck.rpggame.core.Item;
import com.chuck.rpggame.core.Player;

import java.util.List;

/**
 * Simple tap-to-equip list, toggled by the BAG button in GameView. Touch
 * handling here only ever runs on the UI thread (called from
 * GameView.onTouchEvent), same as the rest of the input layer.
 */
public class InventoryPanel {
    private boolean open = false;
    private final RectF panelBounds;
    private final float rowHeight = 56f;

    private final Paint panelBg = new Paint();
    private final Paint rowBg = new Paint();
    private final Paint commonText = new Paint();
    private final Paint rareText = new Paint();
    private final Paint epicText = new Paint();
    private final Paint headerText = new Paint();

    public InventoryPanel(float screenWidth, float screenHeight) {
        float w = screenWidth * 0.55f;
        float h = screenHeight * 0.7f;
        float left = (screenWidth - w) / 2f;
        float top = (screenHeight - h) / 2f;
        panelBounds = new RectF(left, top, left + w, top + h);

        panelBg.setColor(Color.argb(230, 20, 20, 30));
        rowBg.setColor(Color.argb(120, 255, 255, 255));

        commonText.setColor(Color.WHITE);
        commonText.setTextSize(26f);
        commonText.setAntiAlias(true);

        rareText.setColor(Color.rgb(90, 170, 255));
        rareText.setTextSize(26f);
        rareText.setAntiAlias(true);

        epicText.setColor(Color.rgb(230, 170, 40));
        epicText.setTextSize(26f);
        epicText.setAntiAlias(true);

        headerText.setColor(Color.WHITE);
        headerText.setTextSize(28f);
        headerText.setAntiAlias(true);
        headerText.setFakeBoldText(true);
    }

    public void toggle() {
        open = !open;
    }

    public boolean isOpen() {
        return open;
    }

    public void handleTouch(float x, float y, Player player) {
        if (!panelBounds.contains(x, y)) return;
        List<Item> items = player.getInventory().getItems();
        float rowY = panelBounds.top + 50;
        for (Item item : items) {
            if (y >= rowY && y < rowY + rowHeight) {
                equip(player, item);
                return;
            }
            rowY += rowHeight;
        }
    }

    private void equip(Player player, Item item) {
        if (item.getType() == Item.Type.WEAPON) {
            player.equipWeapon(item);
        } else if (item.getType() == Item.Type.ARMOR) {
            player.equipArmor(item);
        } else if (item.getType() == Item.Type.ACCESSORY) {
            player.equipAccessory(item);
        }
    }

    public void draw(Canvas canvas, Player player) {
        if (!open) return;
        canvas.drawRoundRect(panelBounds, 20, 20, panelBg);
        canvas.drawText("Inventory \u2014 tap to equip", panelBounds.left + 20, panelBounds.top + 34, headerText);

        List<Item> items = player.getInventory().getItems();
        if (items.isEmpty()) {
            canvas.drawText("No items yet \u2014 defeat enemies for loot.", panelBounds.left + 20, panelBounds.top + 90, commonText);
            return;
        }

        float rowY = panelBounds.top + 50;
        for (Item item : items) {
            RectF row = new RectF(panelBounds.left + 10, rowY, panelBounds.right - 10, rowY + rowHeight - 6);
            canvas.drawRoundRect(row, 10, 10, rowBg);
            Paint textPaint = paintForRarity(item.getRarity());
            String tag = isEquipped(player, item) ? "  [equipped]" : "";
            canvas.drawText(item.getName() + tag, row.left + 16, row.top + rowHeight * 0.62f, textPaint);
            rowY += rowHeight;
        }
    }

    private boolean isEquipped(Player player, Item item) {
        return item == player.getEquippedWeapon()
                || item == player.getEquippedArmor()
                || item == player.getEquippedAccessory();
    }

    private Paint paintForRarity(Item.Rarity rarity) {
        if (rarity == Item.Rarity.EPIC) return epicText;
        if (rarity == Item.Rarity.RARE) return rareText;
        return commonText;
    }
}
