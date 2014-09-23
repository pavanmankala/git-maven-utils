/**
 *
 */
package org.apache.git.maven;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.table.AbstractTableModel;

/**
 * @author p.mankala
 *
 */
public class TagValueTableModel extends AbstractTableModel {
    private final List<Entry<String, String>> tagValueModel;
    private final Map<String, String>         backing;

    public TagValueTableModel(Map<String, String> tabeModelMap) {
        tagValueModel = new ArrayList<Entry<String, String>>(tabeModelMap.entrySet());
        backing = tabeModelMap;
    }

    public void add(final String tag, final String value) {
        tagValueModel.add(new Entry<String, String>() {
            @Override
            public String setValue(String value) {
                return null;
            }

            @Override
            public String getValue() {
                return value;
            }

            @Override
            public String getKey() {
                return tag;
            }
        });
        backing.put(tag, value);
        fireTableDataChanged();
    }

    public void remove(int row) {
        Entry<String, String> e = tagValueModel.remove(row);
        backing.remove(e.getKey());
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return tagValueModel.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return tagValueModel.get(rowIndex).getKey();
            case 1:
                return tagValueModel.get(rowIndex).getValue();
            default:
                return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "Key";
            case 1:
                return "Value";
            default:
                return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        super.setValueAt(aValue, rowIndex, columnIndex);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
