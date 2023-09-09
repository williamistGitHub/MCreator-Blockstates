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
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class JBlockstateEntry extends JPanel implements ChangeListener {

	private final JComboBox<String> typeBox = new JComboBox<>(new String[]{
			"boolean",
			"int",
			"enum"
	});
	private final JTextField nameBox = new JTextField(10);

	private final JCheckBox boolDefaultValue = L10N.checkbox("elementgui.block.blockstate_default_bool");

	private final JSpinner intMin = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
	private final JSpinner intMax = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
	private final JSpinner intDefault = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));

	private final JTextField enumVals = new JTextField(20);
	private final JComboBox<String> enumDefault = new JComboBox<>();

	private final JButton remove = new JButton(UIRES.get("16px.clear"));

	public JBlockstateEntry(JPanel parent, List<JBlockstateEntry> entryList) {
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		setBackground(((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")).darker());

		final JComponent container = PanelUtils.expandHorizontally(this);

		parent.add(container);
		entryList.add(this);

		JPanel line = new JPanel(new FlowLayout(FlowLayout.LEFT));
		line.setOpaque(false);

		line.add(L10N.label("elementgui.block.blockstate_type"));
		line.add(typeBox);

		line.add(L10N.label("elementgui.block.blockstate_name"));
		line.add(nameBox);

		JPanel boolContainer = new JPanel();

		boolContainer.add(boolDefaultValue);
		boolDefaultValue.setOpaque(false);

		boolContainer.setOpaque(false);
		boolContainer.setVisible(true); // boolean is default selected, so its visible by default

		JPanel intContainer = new JPanel();

		intMin.addChangeListener(this);
		intContainer.add(L10N.label("elementgui.block.blockstate_int_min"));
		intContainer.add(intMin);

		intMax.addChangeListener(this);
		intContainer.add(L10N.label("elementgui.block.blockstate_int_max"));
		intContainer.add(intMax);

		intDefault.addChangeListener(this);
		intContainer.add(L10N.label("elementgui.block.blockstate_default"));
		intContainer.add(intDefault);

		intContainer.setOpaque(false);
		intContainer.setVisible(false);

		JPanel enumContainer = new JPanel();

		enumVals.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent e) {
				handle();
			}

			@Override public void removeUpdate(DocumentEvent e) {
				handle();
			}

			@Override public void changedUpdate(DocumentEvent e) {
				handle();
			}

			private void handle() {
				String[] splitted = enumVals.getText().split(",");
				enumDefault.setModel(new DefaultComboBoxModel<>(splitted));
			}
		});
		enumContainer.add(L10N.label("elementgui.block.blockstate_vals_enum"));
		enumContainer.add(enumVals);

		enumContainer.add(L10N.label("elementgui.block.blockstate_default"));
		enumContainer.add(enumDefault);

		enumContainer.setOpaque(false);
		enumContainer.setVisible(false);

		typeBox.addActionListener(e -> {
			enumContainer.setVisible(false);
			intContainer.setVisible(false);
			boolContainer.setVisible(false);

			// I don't think this can ever happen, but there's a warning for not checking, so I shall.
			if (typeBox.getSelectedItem() == null)
				return;

			if (typeBox.getSelectedItem().equals("boolean")) {
				boolContainer.setVisible(true);
			} else if (typeBox.getSelectedItem().equals("int")) {
				intContainer.setVisible(true);
			} else if (typeBox.getSelectedItem().equals("enum")) {
				enumContainer.setVisible(true);
			}
		});

		line.add(boolContainer);
		line.add(intContainer);
		line.add(enumContainer);

		remove.setText(L10N.t("elementgui.loot_table.remove_entry"));
		remove.addActionListener(e -> {
			entryList.remove(this);
			parent.remove(container);
			parent.revalidate();
			parent.repaint();
			parent.firePropertyChange("blockStatesChanged", false, true);
		});

		add(PanelUtils.centerAndEastElement(line, PanelUtils.join(remove)));

		parent.revalidate();
		parent.repaint();
	}

	public Block.Blockstate getState() {
		Block.Blockstate state = new Block.Blockstate();

		state.type = (String) typeBox.getSelectedItem();
		state.name = nameBox.getText();

		state.boolDefault = boolDefaultValue.isSelected();

		state.intMin = (Integer) intMin.getValue();
		state.intMax = (Integer) intMax.getValue();
		state.intDefault = (Integer) intDefault.getValue();

		state.enumValues = Arrays.asList(enumVals.getText().split(","));
		state.enumDefaultIndex = enumDefault.getSelectedIndex();

		return state;
	}

	public void setState(Block.Blockstate state) {
		typeBox.setSelectedItem(state.type);
		nameBox.setText(state.name);

		boolDefaultValue.setSelected(state.boolDefault);

		intMin.setValue(state.intMin);
		intMax.setValue(state.intMax);
		intDefault.setValue(state.intDefault);

		enumVals.setText(String.join(",", state.enumValues));
		enumDefault.setSelectedIndex(state.enumDefaultIndex);

		stateChanged(null);
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		typeBox.setEnabled(enabled);
		nameBox.setEnabled(enabled);

		boolDefaultValue.setEnabled(enabled);

		intMin.setEnabled(enabled);
		intMax.setEnabled(enabled);
		intDefault.setEnabled(enabled);

		enumVals.setEnabled(enabled);
		enumDefault.setEnabled(enabled);

		remove.setEnabled(enabled);
	}

	@Override public void stateChanged(ChangeEvent e) {
		((SpinnerNumberModel) intMin.getModel()).setMaximum((Integer) intMax.getValue());
		((SpinnerNumberModel) intMax.getModel()).setMinimum((Integer) intMin.getValue());

		((SpinnerNumberModel) intDefault.getModel()).setMinimum((Integer) intMin.getValue());
		((SpinnerNumberModel) intDefault.getModel()).setMaximum((Integer) intMax.getValue());

		if ((Integer) intDefault.getValue() < (Integer) intMin.getValue())
			intDefault.setValue(intMin.getValue());
		else if ((Integer) intDefault.getValue() > (Integer) intMax.getValue())
			intDefault.setValue(intMax.getValue());
	}
}
