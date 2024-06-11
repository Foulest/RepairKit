package net.foulest.repairkit.util;

import net.foulest.repairkit.RepairKit;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SwingUtilTest {

    @Test
    void createActionButton() {
        Runnable action = mock(Runnable.class);
        JButton button = SwingUtil.createActionButton("Test", new Rectangle(0, 0, 100, 50), Color.RED, action);

        assertEquals("Test", button.getText());
        assertEquals(new Rectangle(0, 0, 100, 50), button.getBounds());
        assertEquals(Color.RED, button.getBackground());

        for (ActionListener listener : button.getActionListeners()) {
            listener.actionPerformed(new ActionEvent(button, ActionEvent.ACTION_PERFORMED, null));
        }

        verify(action, times(1)).run();
    }

    @Test
    void testSetupAppIcon() {
        // Create a mock ImageIcon
        ImageIcon mockIcon = mock(ImageIcon.class);
        when(mockIcon.getImage()).thenReturn(new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB));

        // Create a mock JPanel
        JPanel mockPanel = mock(JPanel.class);

        // Call the method with the mock objects
        SwingUtil.setupAppIcon(10, 10, mockIcon, mockPanel);

        // Verify that methods were called on the mock objects
        verify(mockIcon, times(1)).getImage();
        verify(mockPanel, times(1)).add(any(JLabel.class));
    }

    @Test
    void createPanelButton() {
        JButton button = SwingUtil.createPanelButton("Test", new Rectangle(0, 0, 100, 50));

        assertEquals("Test", button.getText());
        assertEquals(new Rectangle(0, 0, 100, 50), button.getBounds());
        assertEquals(new Color(0, 120, 215), button.getBackground());
    }

    @Test
    void performPanelButtonAction() {
        // Setup
        JPanel mainPanel = new JPanel(new CardLayout());
        RepairKit.mainPanel = mainPanel; // Set the mainPanel field in RepairKit

        // Call the method
        SwingUtil.performPanelButtonAction("Test");

        // Verify that the correct card is shown
        CardLayout cardLayout = (CardLayout) mainPanel.getLayout();
        assertEquals(new CardLayout(0, 0).toString(), cardLayout.toString());
    }

    @Test
    void createLabel() {
        JLabel label = SwingUtil.createLabel("Test", new Rectangle(0, 0, 100, 50), new Font("Arial", Font.BOLD, 14));

        assertEquals("Test", label.getText());
        assertEquals(new Rectangle(0, 0, 100, 50), label.getBounds());
        assertEquals(new Font("Arial", Font.BOLD, 14), label.getFont());
    }
}
