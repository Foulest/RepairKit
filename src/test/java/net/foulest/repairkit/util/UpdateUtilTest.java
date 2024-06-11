package net.foulest.repairkit.util;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.mockito.Mockito.*;

class UpdateUtilTest {

    @Test
    void checkForUpdates() throws Exception {
        // Mock the static methods
        try (MockedStatic<FileUtil> mockedFileUtil = Mockito.mockStatic(FileUtil.class);
             MockedStatic<SoundUtil> mockedSoundUtil = Mockito.mockStatic(SoundUtil.class);
             MockedStatic<CommandUtil> mockedCommandUtil = Mockito.mockStatic(CommandUtil.class);
             MockedStatic<JOptionPane> mockedOptionPane = Mockito.mockStatic(JOptionPane.class)) {

            mockedFileUtil.when(FileUtil::getVersionFromProperties).thenReturn("1.0.0");
            mockedOptionPane.when(() -> JOptionPane.showConfirmDialog(any(), any(), any(), anyInt())).thenReturn(JOptionPane.YES_OPTION);
            mockedSoundUtil.when(() -> SoundUtil.playSound(anyString())).thenAnswer(invocation -> null);
            mockedCommandUtil.when(() -> CommandUtil.runCommand(anyString(), anyBoolean())).thenAnswer(invocation -> null);

            // Mock the network call to return a specific JSON response
            String jsonResponse = "{\"tag_name\":\"1.0.1\"}";
            InputStream is = new ByteArrayInputStream(jsonResponse.getBytes());
            HttpURLConnection mockConnection = mock(HttpURLConnection.class);
            when(mockConnection.getInputStream()).thenReturn(is);

            URL mockUrl = mock(URL.class);
            when(mockUrl.openConnection()).thenReturn(mockConnection);

            // Run the method and verify the interactions
            UpdateUtil.checkForUpdates();
            mockedSoundUtil.verify(() -> SoundUtil.playSound(anyString()), times(1));
            mockedCommandUtil.verify(() -> CommandUtil.runCommand(anyString(), anyBoolean()), times(1));
        }
    }
}
