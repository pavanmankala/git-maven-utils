package org.apache.git.maven.task;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

class PasswordDialog extends JDialog {
    private JPasswordField field  = new JPasswordField(20);
    private boolean        accept = false;

    public PasswordDialog(String promptMessage) {
        setTitle("Prompt from Server");
        setModal(true);
        getContentPane().setLayout(new BorderLayout());
        JPanel c = new JPanel(new FlowLayout(FlowLayout.LEADING));
        c.add(new JLabel(promptMessage));
        c.add(field);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(new JButton(new AbstractAction() {
            {
                putValue(Action.NAME, "Ok");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                accept = true;
                setVisible(false);
                dispose();
            }
        }) {
            {
                PasswordDialog.this.getRootPane().setDefaultButton(this);
            }
        });
        buttonPanel.add(new JButton(new AbstractAction() {
            {
                putValue(Action.NAME, "Cancel");
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                accept = false;
                setVisible(false);
                dispose();
            }
        }));
        getContentPane().add(c, BorderLayout.PAGE_START);
        getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
        pack();
        setLocationRelativeTo(null);
    }

    public char[] getPassword() {
        return accept ? field.getPassword() : null;
    }

    public boolean isAccept() {
        return accept;
    }
}
