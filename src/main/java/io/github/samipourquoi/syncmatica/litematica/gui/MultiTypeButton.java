package io.github.samipourquoi.syncmatica.litematica.gui;

import java.util.List;

import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;

// FIXME: Icons
// Not sure how to add Icon support to this without a problematic implementation
// Icon assumes final in its field - we cannot change the fact that the rendering probably
// executes based on this assumption
// for now I will leave the icons alone and pretend they dont exist
// actually upon reading the code there might not be a problem with this.
public class MultiTypeButton extends ButtonGeneric {

	IButtonType type;
	List<IButtonType> types;
	
	public MultiTypeButton(int x, int y, boolean rightAligned, List<IButtonType> types) {
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
			setHoverStrings((String[]) type.getHoverStrings().toArray());
		}
		setEnabled(type.getButtonListener() != null);
	}

	private void updateType() {
		for (IButtonType type : types) {
			if (type.isActive()) {
				this.type = type;
				return;
			}
		}
		// default type is 0
		this.type = types.get(0);
	}
	
	public IButtonType getActiveType() {
		return type;
	}
	
	private int calculateWidth() {
		int wMax = -1;
		for (IButtonType type : types) {
			int wType = calculateWidth(type);
			if (wType > wMax) {
				wMax = wType;
			}
		}
		return wMax;
	}
	
	private int calculateWidth(IButtonType type) {
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
	public MultiTypeButton setActionListener(IButtonActionListener listener) {
		return this;
	}
	
	public class MultiTypeListener implements IButtonActionListener {
		@Override
		public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
			update();
			if (type.getButtonListener() != null) {
				type.getButtonListener().actionPerformedWithButton(button, mouseButton);
			}
		}
		
	}
	
}
