package application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.prefs.Preferences;

import javax.crypto.AEADBadTagException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class CryptoxFXMLController
{
    @FXML
    private Label selectedFileLabel;

    @FXML
    private Label warningLabel;

    @FXML
    private Button selectFileButton;

    @FXML
    private Button encryptButton;

    @FXML
    private Button decryptButton;

    @FXML
    private VBox root;

    @FXML
    private AnchorPane ap;

    @FXML
    private PasswordField pwField;

    @FXML
    private CheckBox deleteCb;

    private File selectedFile;

    private Preferences pref;

    @FXML
    public void initialize()
    {     
        pref = Preferences.userNodeForPackage(CryptoxFXMLController.class);
        String preference = pref.get("deleteCb", "false");

        if (preference.equals("true"))
        {
            deleteCb.setSelected(true);
        }
        else
        {
            deleteCb.setSelected(false);
        }
    }

    @FXML
    private void selectFileHandler(ActionEvent event)
    {
        FileChooser fileChooser = new FileChooser();
        selectedFile = fileChooser.showOpenDialog(ap.getScene().getWindow());
        warningLabel.setText("");
        if (selectedFile != null)
        {
            selectedFileLabel.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void encryptButtonHandler(ActionEvent event) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
    {
      
        if (selectedFile != null && pwField.getText() != null)
        {
            
            byte[] fileContent = Files.readAllBytes(selectedFile.toPath());

            SecureRandom rand = new SecureRandom();
            byte[] salt = new byte[16];
            rand.nextBytes(salt);

            byte[] pw = getPBKDF2(pwField.getText(), salt, 4096, 256);
            SecretKeySpec key = new SecretKeySpec(pw, "AES");
            Cipher c = Cipher.getInstance("AES/GCM/NoPadding", "BC");
            byte[] iv = new byte[16];
            rand = new SecureRandom();
            rand.nextBytes(iv);

            c.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
            byte[] enc = c.doFinal(fileContent);
            File fileEnc = new File(selectedFile.getAbsoluteFile() + ".enc");
            
            Files.write(fileEnc.toPath(), Arrays.concatenate(salt, iv, enc));
            
            

            if (deleteCb.isSelected())
            {
                selectedFile.delete();
            }

            warningLabel.setText("");
            pwField.setText("");
            pwField.setPromptText("password");
            selectedFileLabel.setText("");
            selectedFile = null;
        }
    }

    @FXML
    private void decryptButtonHandler(ActionEvent event) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
    {

        if (selectedFile != null && pwField.getText() != null)
        {
            byte[] fileEncBytes = Files.readAllBytes(selectedFile.toPath());

            byte[] saltF = Arrays.copyOfRange(fileEncBytes, 0, 16);
            byte[] ivF = Arrays.copyOfRange(fileEncBytes, 16, 32);
            byte[] contentF = Arrays.copyOfRange(fileEncBytes, 32, fileEncBytes.length);

            byte[] pw = getPBKDF2(pwField.getText(), saltF, 4096, 256);
            SecretKeySpec key = new SecretKeySpec(pw, "AES");

            Cipher c = Cipher.getInstance("AES/GCM/NoPadding", "BC");
            c.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(128, ivF));

            try
            {
                byte[] dec = c.doFinal(contentF);

                File file = new File(selectedFile.getAbsolutePath().substring(0, selectedFile.getAbsolutePath().length() - 4));
                Files.write(file.toPath(), dec);
                
                if (deleteCb.isSelected())
                {
                    selectedFile.delete();
                }
                warningLabel.setText("");
            }
            catch (AEADBadTagException e)
            {
                e.printStackTrace();
                warningLabel.setText("wrong password or integrity check failed.");
            }

            pwField.setText("");
            pwField.setPromptText("password");
            selectedFileLabel.setText("");
            selectedFile = null;
        }

    }

    @FXML
    private void dragFile(DragEvent event)
    {
        Dragboard db = event.getDragboard();
        selectedFile = db.getFiles().get(0);
        if (selectedFile != null)
        {
            selectedFileLabel.setText(selectedFile.getAbsolutePath());
        }

    }
    
    @FXML
    private void cbHandler(ActionEvent event) 
    {
        if (deleteCb.isSelected())
        {
            pref.put("deleteCb", "true");
        }
        else
        {
            pref.put("deleteCb", "false");
        }
    }

    private byte[] getPBKDF2(String password, byte[] salt, int iterationCount, int keyLength) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        char[] pwdChar = password.toCharArray();
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WITHHMACSHA1");
        PBEKeySpec spec = new PBEKeySpec(pwdChar, salt, iterationCount, keyLength);

        SecretKey key = factory.generateSecret(spec);
        return key.getEncoded();
    }

}
