# Java Playfair Cipher

A robust, Object-Oriented implementation of the **Playfair Cipher** encryption algorithm in Java.

## Features
*  **Full Encryption & Decryption** logic.
*  **Preserves Case** (Input: `Hello` -> Output: `Xyzzy` -> Decrypt: `Hello`).
*  **Sentence Support** (Preserves spaces between words).
*  **Duplicate Handling** (Automatically inserts filler 'x' or 'q').
*  **Custom Keys** (defaults to "MONARCHY").

## Usage

```java
public class Main {
    public static void main(String[] args) {
        // 1. Create Cipher instance (default key is "MONARCHY")
        PlayfairCipher cipher = new PlayfairCipher("KEYWORD");

        String original = "INSTRUMENT";
        
        // 2. Encrypt
        String encrypted = cipher.encrypt(original);
        System.out.println("Encrypted: " + encrypted); 
        // Output: GATLMZCLRQ

        // 3. Decrypt
        String decrypted = cipher.decrypt(encrypted);
        System.out.println("Decrypted: " + decrypted);
        // Output: INSTRUMENT
    }
}