import java.util.*;

//for words like HELLO you will get HELXLO back and thats normal for playfair ,kindly ignore tha 'X's manually
class PlayfairCipher {

    private String KEY = "MONARCHY";
    private String PROCESSED_KEY = "monarchy";
    private int specialCharacter = 'j';
    private int[][] MATRIX = new int[5][5];
    private Map<Integer, Integer[]> CHAR_TO_INDEX = new HashMap<>();
    private boolean matrixFilled = false;

    public PlayfairCipher() {
        setKey(KEY);
    }
    public PlayfairCipher(String key) {
        setKey(key);
    }

    public static void main(String[] args) {
        // String plainText = "INSTRUMENT";

        Scanner sc = new Scanner(System.in);
        String plainText = sc.nextLine();

        PlayfairCipher cipher = new PlayfairCipher();

        String cipherText = cipher.encrypt(plainText);
        System.out.println("ciphered : " + cipherText);

        System.out.println("og : " + cipher.decrypt(cipherText));

        sc.close();
    }

//------------------------------------------------------------------------------------UTILITIES--------------------------------------------------------------------------------------

    // get key
    public String getKey() {
        return KEY;
    }

    // set key
    public String setKey(String key) {
        KEY = key;

        PROCESSED_KEY = removeDuplicatesAndLowerCase(key);
        fillMatrixAndMap();

        return KEY;
    }

    // get special character
    public  char getSpecialCharacter() {
        return (char) specialCharacter;
    }
    
    // set special character
    public char setSpecialCharacter(char ch) {
        specialCharacter = (int) ch;
        return (char) specialCharacter;
    }
    
    //get caps state of string
    public boolean[] capState(String st) {
        int n = st.length();
        boolean[] state = new boolean[n];
    
        // if capital -> true else false(lowecase)
        for (int i = 0; i < n; i++) {
            char ch = st.charAt(i);
            if ('A' <= ch && ch <= 'Z')
                state[i] = true;
        }
        return state;
    }
    
    //restore caps state of string
    public String restoreState(String st, boolean[] state) {
        int n = st.length();
        int stateLen = state.length;
    
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            char ch = st.charAt(i);
            if (i < stateLen && state[i])
                sb.append((char) ((int) ch - 32));
            else
                sb.append(ch);
        }
        return sb.toString();
    }
    
    public boolean validKey(String key) {
        int n = key.length();
    
        // at most 26 possible
        if (n > 26)
            return false;
    
        Set<Character> set = new HashSet<>();
        for (int i = 0; i < n; i++) {
            if (!set.add(key.charAt(i))) {
                return false;
            }
        }
    
        return true;
    }
    
    public String removeDuplicatesAndLowerCase(String key) {
        int n = key.length();
        key = key.toLowerCase();
    
        StringBuilder sb = new StringBuilder();
        boolean[] seen = new boolean[26];
    
        for (int i = 0; i < n; i++) {
            char ch = key.charAt(i);
            int idx = ch - 'a';
    
            if (!seen[idx])
                sb.append(ch);
            seen[idx] = true;
        }
    
        return sb.toString();
    }
    
    private void fillMatrixAndMap() {
        matrixFilled = true;
        fillMatrix(MATRIX, PROCESSED_KEY);
        fillMap(CHAR_TO_INDEX, MATRIX);
    }
    
    // custom key
    public int[][] fillMatrix(String key) {
        int[][] mat = new int[5][5];
        return fillMatrix(mat, key);
    }
    
    // custom key
    private int[][] fillMatrix(int[][] matrix, String key) {
        int n = key.length();
    
        boolean[] seen = new boolean[26];
    
        // filling key
        for (int idx = 0; idx < n; idx++) {
            int i = idx / 5;
            int j = idx % 5;
    
            int ch = (int) key.charAt(idx);
            if (ch == (int) specialCharacter)
                ch = specialCharacter - 1;
    
            seen[ch - 'a'] = true;
            matrix[i][j] = ch;
        }
    
        // filling remaining alphabets
        int k = n;
        for (int alphabet = 0; alphabet < 26; alphabet++) {
            if (alphabet == specialCharacter - 'a')
                continue;
    
            if (!seen[alphabet]) {
                int i = k / 5;
                int j = k % 5;
    
                int ch = alphabet + (int) 'a';
                matrix[i][j] = ch;
                k++;
            }
        }
        
        return matrix;
    }
    
    //for debuging
    private void printMatrix(int[][] matrix){
        System.out.print("\n\n\n\n");
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.print((char) matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
    
    public Map<Integer, Integer[]> fillMap(int[][] matrix) {
        return fillMap(new HashMap<>(), matrix);
    }
    
    //custom key
    public  Map<Integer, Integer[]> fillMap(Map<Integer, Integer[]> charToIndex, int[][] matrix) {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Integer ch = matrix[i][j];
                charToIndex.put(ch, new Integer[] { i, j });
            }
        }
        return charToIndex;
    }
//------------------------------------------------------------------------------------UTILITIES--------------------------------------------------------------------------------------


//------------------------------------------------------------------------------------ENCRYPTION--------------------------------------------------------------------------------------

    // use default key
    public String encrypt(String plainText) {
        return encrypt(plainText, PROCESSED_KEY);
    }

    // use custom key
    public String encrypt(String plainText, String key) {
        plainText = plainText.trim();

        //save cap state and process text to lowercase
        boolean[] state = capState(plainText);
        plainText = plainText.toLowerCase();

        //process key so that it only contains uique characters
        key = removeDuplicatesAndLowerCase(key);

        // if sentence
        if (plainText.contains(" ")) return encryptSentence(plainText, key);


        // first call
        if (!matrixFilled) fillMatrixAndMap();

        //if custom key is same as default key
        if (key.equals(PROCESSED_KEY)) {
            String str = generateEncryptedString(plainText, MATRIX, CHAR_TO_INDEX);
            str = restoreState(str, state);
            return str;
        }

        // key must contain unique chars
        if (!validKey(key))
            throw new Error("invalid key!!!!");

        int[][] customMatrix = fillMatrix(key);
        Map<Integer, Integer[]> customCharToIndex = fillMap(customMatrix);

        String str = generateEncryptedString(plainText, customMatrix, customCharToIndex);
        return restoreState(str, state);
    }

    public String encryptSentence(String plainText, String key) {
        StringTokenizer st = new StringTokenizer(plainText);

        StringBuilder sb = new StringBuilder();
        while (st.hasMoreTokens()) {
            sb.append(encrypt(st.nextToken(), key));
            sb.append(" ");
        }
        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }


    public String generateEncryptedString(String plainText, int[][] matrix, Map<Integer, Integer[]> charToIndex) {
        List<int[]> pairs = makePairs(plainText);
        StringBuilder sb = new StringBuilder();

        for (int[] pair : pairs) {
            String str = correspondingPair(pair, matrix, charToIndex);
            sb.append(str);
        }
        return sb.toString();
    }
    
    //returns pairs of string by rules
    public List<int[]> makePairs(String plainText) {
        plainText = prepare(plainText);
        int n = plainText.length();
        
        List<int[]> pairs = new ArrayList<>();
        for (int i = 0; i < n; i += 2) {
            int[] pair = new int[] { plainText.charAt(i), plainText.charAt(i + 1) };
            pairs.add(pair);
        }
        
        return pairs;
    }
    
    //process string for making pairs
    public String prepare(String plainText) {
        int n = plainText.length();
        
        //even though string is gonna come in lowercase we still lowercase it just to be safe
        StringBuilder sb = new StringBuilder(plainText.toLowerCase());
        for (int i = 0; i < n; i++) {
            if (sb.charAt(i) == 'j') sb.replace(i, i + 1, "i");
        }
        
        // if dup append x
        for (int i = 1; i < sb.length(); i += 2) {
            if (sb.charAt(i - 1) == sb.charAt(i)) {
                char ch = sb.charAt(i);
                char filler = ch == 'x' ? 'q' : 'x';
                
                sb.insert(i, filler);
                i++;
            }
        }
        
        // if odd append x
        if ((sb.length() & 1) == 1) {
            char filler = sb.charAt(sb.length() - 1) == 'x' ? 'q' : 'x';
            plainText = sb.append(filler).toString();
        }

        return sb.toString();
    }
    
    //maps pair's characters through matrix
    public String correspondingPair(int[] pair, int[][] matrix, Map<Integer, Integer[]> charToIndex) {
        String p = "";
        Integer[] c1 = charToIndex.get(pair[0]);
        Integer[] c2 = charToIndex.get(pair[1]);
        
        // same row
        if (c1[0] == c2[0]) {
            char ch1 = (char) matrix[c1[0]][(c1[1] + 1) % 5];
            char ch2 = (char) matrix[c2[0]][(c2[1] + 1) % 5];
            
            p = String.valueOf(ch1) + String.valueOf(ch2);
            return p;
        }
        
        // same column
        if (c1[1] == c2[1]) {
            char ch1 = (char) matrix[(c1[0] + 1) % 5][c1[1]];
            char ch2 = (char) matrix[(c2[0] + 1) % 5][c2[1]];
            
            p = String.valueOf(ch1) + String.valueOf(ch2);
            return p;
        }
        
        // rectangle
        char ch1 = (char) matrix[c1[0]][c2[1]];
        char ch2 = (char) matrix[c2[0]][c1[1]];
        
        p = String.valueOf(ch1) + String.valueOf(ch2);
        return p;
    }
    
    //------------------------------------------------------------------------------------ENCRYPTION--------------------------------------------------------------------------------------


    //------------------------------------------------------------------------------------DECRYPTION--------------------------------------------------------------------------------------
    
    // use default key
    public String decrypt(String cipherText) {
        return decrypt(cipherText, PROCESSED_KEY);
    }
    
    // use custom key
    public String decrypt(String cipherText, String key) {
        cipherText = cipherText.trim();
        
        //save cap state and process text to lowercase
        boolean[] state = capState(cipherText);
        cipherText = cipherText.toLowerCase();
        
        //process key so that it only contains uique characters
        key = removeDuplicatesAndLowerCase(key);
        
        // if sentence
        if (cipherText.contains(" ")) return decryptSentence(cipherText, key);
        
        
        // first call
        if (!matrixFilled) fillMatrixAndMap();
        
        //if custom key is same as default key
        if (key.equals(PROCESSED_KEY)) {
            String str = generateDecryptedString(cipherText, MATRIX, CHAR_TO_INDEX);
            str = restoreState(str, state);
            return str;
        }
        
        // key must contain unique chars
        if (!validKey(key))
            throw new Error("invalid key!!!!");
        
        int[][] customMatrix = fillMatrix(key);
        Map<Integer, Integer[]> customCharToIndex = fillMap(customMatrix);
        
        String str = generateDecryptedString(cipherText, customMatrix, customCharToIndex);
        return restoreState(str, state);
    }
    
    public String decryptSentence(String cipherText, String key) {
        StringTokenizer st = new StringTokenizer(cipherText);
        StringBuilder sb = new StringBuilder();
        while (st.hasMoreTokens()) {
            sb.append(decrypt(st.nextToken(), key));
            sb.append(" ");
        }
        sb.deleteCharAt(sb.length() - 1);
        
        return sb.toString();
    }
    
    
    public String generateDecryptedString(String cipherText, int[][] matrix, Map<Integer, Integer[]> charToIndex) {
        List<int[]> pairs = new ArrayList<>();
        for(int i = 0 ; i < cipherText.length() ; i += 2){
            if(i + 1 < cipherText.length()){
                pairs.add(new int[]{cipherText.charAt(i),cipherText.charAt(i+1)});
            }
        }
        
        StringBuilder sb = new StringBuilder();
        for (int[] pair : pairs) {
            String str = decryptPair(pair, matrix, charToIndex);
            sb.append(str);
        }
        return sb.toString();
    }
    
    //maps pair's characters through matrix
    public String decryptPair(int[] pair, int[][] matrix, Map<Integer, Integer[]> charToIndex) {
        String p = "";
        Integer[] c1 = charToIndex.get(pair[0]);
        Integer[] c2 = charToIndex.get(pair[1]);
        
        // same row
        if (c1[0] == c2[0]) {
            char ch1 = (char) matrix[c1[0]][(c1[1] - 1 + 5) % 5];
            char ch2 = (char) matrix[c2[0]][(c2[1] - 1 + 5) % 5];
            
            p = String.valueOf(ch1) + String.valueOf(ch2);
            return p;
        }
        
        // same column
        if (c1[1] == c2[1]) {
            char ch1 = (char) matrix[(c1[0] - 1 + 5) % 5][c1[1]];
            char ch2 = (char) matrix[(c2[0] - 1 + 5) % 5][c2[1]];
            
            p = String.valueOf(ch1) + String.valueOf(ch2);
            return p;
        }
        
        // rectangle
        char ch1 = (char) matrix[c1[0]][c2[1]];
        char ch2 = (char) matrix[c2[0]][c1[1]];
        
        p = String.valueOf(ch1) + String.valueOf(ch2);
        return p;
    }
}

//------------------------------------------------------------------------------------DECRYPTION--------------------------------------------------------------------------------------