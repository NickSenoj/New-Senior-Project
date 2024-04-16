// Name: Nicholas Jones
// Class: COMP 460
// Project: Senior Project
// File Name: FileViewer.java
// Professor: Dr. Theresa Wilson
// Latest Update Date: 04/16/2024

// What to change to use on your own computer:
// destinationFolder :: line 55

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import javax.swing.filechooser.FileSystemView;
import java.io.FileWriter;

public class FileViewer extends JFrame {
    private JPanel panel;
    private JScrollPane scrollPane;
    private File destinationFolder;
    private PassValidation passValidation;
    private boolean coverPhotoClicked = false;

    // Constructor for FileViewer class
    public FileViewer() {
        // Set up the JFrame
        setTitle("File Explorer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 250);
        setLocationRelativeTo(null);

        // Initialize panel and scrollPane
        panel = new JPanel(new GridLayout(0, 1, 0, 0));
        scrollPane = new JScrollPane(panel);
        add(scrollPane, BorderLayout.CENTER);

        // Create a panel for buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));

        // GridLayout with 1 row and 3 columns
        // Initialize the PasswordManager with the password from a text file
        passValidation = new PassValidation();

        // Initialize the destinationFolder
        // String homeDir = System.getProperty("user.home");
        // destinationFolder = new File(homeDir, "Senior Project");
        destinationFolder = new File("c:/Users/senoj/OneDrive/Desktop/New Senior Project");

        // Add File button functionality
        JButton addButton = new JButton("Add File");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (authenticateUser()) {
                    JFileChooser fileChooser = new JFileChooser();
                    int result = fileChooser.showOpenDialog(FileViewer.this);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        moveFile(selectedFile);
                    }
                }
            }
        });
        buttonPanel.add(addButton);

        // Remove File button functionality
        JButton removeButton = new JButton("Remove File");
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (authenticateUser()) {
                    showConfirmationDialog();
                }
            }
        });
        buttonPanel.add(removeButton);

        // Add user functionality
        JButton addUserButton = new JButton("Add User");
        addUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (authenticateUser()) {
                    addNewPassword();
                }
            }
        });
        buttonPanel.add(addUserButton);

        // Remove user functionality
        JButton removeUserButton = new JButton("Remove User");
        removeUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removePassword();
            }
        });
        buttonPanel.add(removeUserButton);

        // Add the buttonPanel to the main panel, at the bottom
        add(buttonPanel, BorderLayout.SOUTH);

        // Load files from the destinationFolder
        File[] files = destinationFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                addFile(file);
            }
        } else {
            System.out.println("Directory does not exist or is empty.");
        }
    }

    // add new user implementation
    private void addNewPassword() {
        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        contentPanel.add(new JLabel("New Password:"));
        contentPanel.add(passwordField);
        contentPanel.add(new JLabel("Confirm Password:"));
        contentPanel.add(confirmPasswordField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        // JButton okButton = new JButton("OK");
        // JButton cancelButton = new JButton("Cancel");
        // buttonPanel.add(okButton);
        // buttonPanel.add(cancelButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        int result = JOptionPane.showConfirmDialog(this, mainPanel, "Set New Password", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String newPassword = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            if (newPassword.equals(confirmPassword)) {
                try {
                    File passwordFile = new File(destinationFolder, "FaceRecPass.txt");
                    FileWriter writer = new FileWriter(passwordFile, true); // Append mode
                    writer.write(
                            PassEnc.toHexString(PassEnc.getSHA(newPassword + "abcdefghij")) + System.lineSeparator());
                    writer.close();
                    JOptionPane.showMessageDialog(this, "New password saved successfully!");
                } catch (NoSuchAlgorithmException | IOException e) {
                    JOptionPane.showMessageDialog(this, "Error saving new password: " + e.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Method to authenticate user with a password
    private boolean authenticateUser() {
        boolean authenticated = false;
        while (!authenticated) {
            JPasswordField passwordField = new JPasswordField();
            int option = JOptionPane.showConfirmDialog(FileViewer.this, passwordField, "Enter password:",
                    JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String inputPassword = new String(passwordField.getPassword());
                try {
                    File passwordFile = new File(destinationFolder, "FaceRecPass.txt");
                    if (passwordFile.exists()) {
                        BufferedReader reader = new BufferedReader(new FileReader(passwordFile));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            if (PassEnc.toHexString(PassEnc.getSHA(inputPassword + "abcdefghij")).equals(line.trim())) {
                                // Password authentication successful, now prompt for image selection
                                int imageIndex = showImageSelectionDialog();
                                if (imageIndex == JOptionPane.CLOSED_OPTION) {
                                    // Image selection successful, set authenticated to true
                                    authenticated = true;
                                    break;
                                } else {
                                    // Image selection failed, show error message
                                    JOptionPane.showMessageDialog(FileViewer.this,
                                            "Image selection failed.", "Authentication Failed",
                                            JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                        reader.close();
                        if (!authenticated) {
                            JOptionPane.showMessageDialog(FileViewer.this, "WRONG PASSWORD! Please try again.",
                                    "Authentication Failed", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(FileViewer.this, "Password file not found.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        break;
                    }
                } catch (IOException | NoSuchAlgorithmException e) {
                    JOptionPane.showMessageDialog(FileViewer.this, "Error reading password file: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    break;
                }
            } else {
                break;
            }
        }
        return authenticated;
    }

    // Method to allow picture authentication
    private int showImageSelectionDialog() {
        // Load the 4 images from a predefined location
        ImageIcon[] images = new ImageIcon[4];
        images[0] = new ImageIcon("CoverPhoto.png"); // 'a'
        images[1] = new ImageIcon("GolfPhoto.png"); // 'b'
        images[2] = new ImageIcon("KidPhoto.png"); // 'c'
        images[3] = new ImageIcon("PlanePhoto.png"); // 'd'

        // Create a panel to display the images
        JPanel imagePanel = new JPanel(new GridLayout(2, 2, 10, 10));
        for (int i = 0; i < images.length; i++) {
            JLabel label = new JLabel(images[i]);
            label.setPreferredSize(new Dimension(250, 250));
            imagePanel.add(label);
        }

        int result = JOptionPane.showOptionDialog(FileViewer.this, imagePanel, "Select an image",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);

        // Check if the user clicked the X button to close the dialog
        if (result == JOptionPane.CLOSED_OPTION) {
            // Exit the application
            System.exit(0);
        } else if (result == JOptionPane.OK_OPTION) { // OK button was clicked
            // Prompt the user to enter the letter corresponding to the correct image
            String input = JOptionPane.showInputDialog(FileViewer.this,
                    "Enter the letter to the correct image:");

            if (input != null && input.length() == 1) {
                char letter = Character.toLowerCase(input.charAt(0));
                if (letter == 'b') {
                    // Authentication successful, continue with the rest of the application
                    return JOptionPane.CLOSED_OPTION;
                } else {
                    JOptionPane.showMessageDialog(FileViewer.this, "Wrong letter entered.",
                            "Authentication Failed", JOptionPane.ERROR_MESSAGE);
                    // Exit the application
                    System.exit(0);
                }
            } else {
                JOptionPane.showMessageDialog(FileViewer.this, "Invalid input.",
                        "Authentication Failed", JOptionPane.ERROR_MESSAGE);
                // Exit the application
                System.exit(0);
            }
        }

        return result;
    }

    // Method to add a file to the panel
    private void addFile(File file) {
        Icon icon = FileSystemView.getFileSystemView().getSystemIcon(file);
        JLabel label = new JLabel(file.getName(), icon, JLabel.HORIZONTAL);
        label.addMouseListener(new FileClickListener(file));
        panel.add(label);
        panel.revalidate();
        panel.repaint();
    }

    // Method to remove a selected file
    private void removeSelectedFile(File file) {
        int confirm = JOptionPane.showConfirmDialog(FileViewer.this, "Are you sure you want to remove this file?",
                "Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String downloadsDir = System.getProperty("user.home") + "/Downloads";
            File destinationFile = new File(downloadsDir, file.getName());
            if (file.renameTo(destinationFile)) {
                for (Component component : panel.getComponents()) {
                    if (component instanceof JLabel) {
                        JLabel label = (JLabel) component;
                        if (label.getText().equals(file.getName())) {
                            panel.remove(label);
                            break;
                        }
                    }
                }
                panel.revalidate();
                panel.repaint();
                System.out.println("File removed successfully.");
            } else {
                System.out.println("Failed to remove file.");
            }
        }
    }

    private void removePassword() {
        if (authenticateUser()) {
            JPasswordField passwordField = new JPasswordField();
            int option = JOptionPane.showConfirmDialog(FileViewer.this, passwordField, "Enter password to remove:",
                    JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String inputPassword = new String(passwordField.getPassword());
                try {
                    File passwordFile = new File(destinationFolder, "FaceRecPass.txt");
                    if (passwordFile.exists()) {
                        File tempFile = new File(destinationFolder, "temp.txt");
                        BufferedReader reader = new BufferedReader(new FileReader(passwordFile));
                        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
                        String line;
                        boolean found = false;
                        while ((line = reader.readLine()) != null) {
                            if (!PassEnc.toHexString(PassEnc.getSHA(inputPassword + "abcdefghij"))
                                    .equals(line.trim())) {
                                writer.write(line);
                                writer.newLine();
                            } else {
                                found = true;
                            }
                        }
                        reader.close();
                        writer.close();
                        if (found) {
                            passwordFile.delete();
                            tempFile.renameTo(passwordFile);
                            JOptionPane.showMessageDialog(FileViewer.this, "Password removed successfully!");
                        } else {
                            tempFile.delete();
                            JOptionPane.showMessageDialog(FileViewer.this, "Password not found.", "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(FileViewer.this, "Password file not found.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IOException | NoSuchAlgorithmException e) {
                    JOptionPane.showMessageDialog(FileViewer.this, "Error removing password: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    // Method to move a file to a destination folder
    private void moveFile(File file) {
        File destinationFile = new File(destinationFolder.getAbsolutePath() + "/" + file.getName());
        if (file.renameTo(destinationFile)) {
            addFile(destinationFile);
            System.out.println("File moved successfully.");
        } else {
            System.out.println("Failed to move file.");
        }
    }

    // Inner class for handling mouse clicks on files in the panel
    private class FileClickListener extends MouseAdapter {
        private File file;

        public FileClickListener(File file) {
            this.file = file;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                if (authenticateUser()) {
                    openFile(file);
                }
            } else if (e.getClickCount() == 1) {
                toggleFileSelection(e);
            }
        }

        private void openFile(File file) {
            if (!Desktop.isDesktopSupported()) {
                System.out.println("Desktop is not supported");
                return;
            }
            Desktop desktop = Desktop.getDesktop();
            if (file.exists()) {
                try {
                    desktop.open(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void toggleFileSelection(MouseEvent e) {
            JLabel label = (JLabel) e.getSource();
            if (label.getForeground() == Color.BLACK) {
                label.setForeground(Color.RED);
            } else {
                label.setForeground(Color.BLACK);
            }
        }
    }

    // Method to show confirmation dialog for removing selected files
    private void showConfirmationDialog() {
        Component[] components = panel.getComponents();
        for (Component component : components) {
            if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                if (label.getForeground() == Color.RED) {
                    for (File file : destinationFolder.listFiles()) {
                        if (file.getName().equals(label.getText())) {
                            removeSelectedFile(file);
                            break;
                        }
                    }
                }
            }
        }
    }

    // Main method to start the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FileViewer fileExplorer = new FileViewer();
            fileExplorer.setVisible(true);
        });
    }
}
