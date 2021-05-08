package io.github.samipourquoi.syncmatica.litematica.gui;

import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;

import java.util.List;

// FIXME: Icons
// Not sure how to add Icon support to this without a problematic implementation
// Icon assumes final in its field - we cannot change the fact that the rendering probably
// executes based on this assumption
// for now I will leave the icons alone and pretend they dont exist
// actually upon reading the code there might not be a problem with this.
public class MultiTypeButton extends ButtonGeneric {

    IButtonType type;
    List<IButtonType> types;

    public MultiTypeButton(final int x, final int y, final boolean rightAligned, final List<IButtonType> types) {
        super(x, y, 1, 20, "");
        this.types = types;
        type = types.get(0);
        update();
        width = calculateWidth();
        if (rightAligned) {
            this.x -= width;
        }
        actionListener = new MultiTypeListener();
    }

    public void update() {
        updateType();
        displayString = type.getTranslatedKey();
        if (type.getHoverStrings() != null) {
            final List<String> hoverStrings = type.getHoverStrings();
            setHoverStrings(hoverStrings.toArray(new String[hoverStrings.size()]));
        }
        setEnabled(type.getButtonListener() != null);
    }

    private void updateType() {
        for (final IButtonType type : types) {
            if (type.isActive()) {
                this.type = type;
                return;
            }
        }
        // default type is 0
        type = types.get(0);
    }

    public IButtonType getActiveType() {
        return type;
    }

    private int calculateWidth() {
        int wMax = -1;
        for (final IButtonType type : types) {
            final int wType = calculateWidth(type);
            if (wType > wMax) {
                wMax = wType;
            }
        }
        return wMax;
    }

    private int calculateWidth(final IButtonType type) {
        int w = 0;
        if (type.getTranslatedKey() != null) {
            // is it really 30px?? need to check
            w += getStringWidth(type.getTranslatedKey()) + 10;
        }
        if (type.getIcon() != null) {
            w += type.getIcon().getWidth() + 8;
        }
        return w;
    }

    // gracefully ignored
    @Override
    public MultiTypeButton setActionListener(final IButtonActionListener listener) {
        return this;
    }

    public class MultiTypeListener implements IButtonActionListener {
        @Override
        public void actionPerformedWithButton(final ButtonBase button, final int mouseButton) {
            update();
            if (type.getButtonListener() != null) {
                type.getButtonListener().actionPerformedWithButton(button, mouseButton);
            }
        }

    }

}
