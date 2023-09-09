/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.mcreator.ui.minecraft.blockstates;

import net.mcreator.element.types.Block;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.JEntriesList;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JBlockstateList extends JEntriesList {

	private final List<JBlockstateEntry> blockstateList = new ArrayList<>();
	private final JPanel blockstateEntriesPanel = new JPanel(new GridLayout(0, 1, 5, 5));

	public JBlockstateList(MCreator mcreator, IHelpContext gui) {
		super(mcreator, new BorderLayout(), gui);

		JPanel topbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		topbar.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));

		add.setText(L10N.t("elementgui.block.add_blockstate"));
		topbar.add(add);
		add("North", topbar);

		blockstateEntriesPanel.setOpaque(false);
		add("Center", new JScrollPane(PanelUtils.pullElementUp(blockstateEntriesPanel)));

		add.addActionListener(e -> {
			JBlockstateEntry entry = new JBlockstateEntry(blockstateEntriesPanel, blockstateList);
			entry.setEnabled(this.isEnabled());
			registerEntryUI(entry);
			firePropertyChange("blockStatesChanged", false, true);
		});

		blockstateEntriesPanel.addPropertyChangeListener("blockStatesChanged", evt ->
				firePropertyChange("blockStatesChanged", false, true));

		setOpaque(false);
		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.block.blockstate_entries"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));
		setPreferredSize(new Dimension(getPreferredSize().width, (int) (mcreator.getSize().height * 0.6)));
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		add.setEnabled(enabled);
		blockstateList.forEach(e -> e.setEnabled(enabled));
	}

	public List<Block.Blockstate> getBlockstates() {
		return blockstateList.stream().map(JBlockstateEntry::getState).filter(Objects::nonNull).toList();
	}

	public void setBlockstates(List<Block.Blockstate> list) {
		blockstateList.clear();
		blockstateEntriesPanel.removeAll();
		list.forEach(blockstate -> {
			JBlockstateEntry entry = new JBlockstateEntry(blockstateEntriesPanel, blockstateList);
			entry.setEnabled(isEnabled());
			registerEntryUI(entry);
			entry.setState(blockstate);
		});
	}

}
