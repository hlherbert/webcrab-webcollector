package webcrab.core.conf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 种子url
 */
public class InputSeedData {
    private final String FILE_NAME = "/input_seed.txt";
    protected static InputSeedData instance = new InputSeedData();
    private List<String> seeds;


    public static InputSeedData getInstance() {
        return instance;
    }

    protected InputSeedData() {
        load();
    }

    private void load() {
        InputStreamReader ir = null;
        BufferedReader br = null;
        try {
            InputStream in = SellerProperties.class.getResourceAsStream(FILE_NAME);
            ir = new InputStreamReader(in);
            br = new BufferedReader(ir);
            seeds = br.lines().filter(s -> !s.startsWith("#")).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<String> getSeeds() {
        return seeds;
    }

    public void setSeeds(List<String> seeds) {
        this.seeds = seeds;
    }
}
