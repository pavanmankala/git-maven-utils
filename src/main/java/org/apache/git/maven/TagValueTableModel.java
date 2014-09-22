/**
 *
 */
package org.apache.git.maven;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.table.AbstractTableModel;

/**
 * @author p.mankala
 *
 */
public class TagValueTableModel extends AbstractTableModel {
    private final List<Entry<String, String>> tagValueModel = new ArrayList<>();

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
    }

    public void remove(int row) {
        tagValueModel.remove(row);
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
            case 1:
                return tagValueModel.get(rowIndex).getKey();
            case 2:
                return tagValueModel.get(rowIndex).getValue();
            default:
                return null;
        }
    }

}
