import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.util.Date;

/**
 * @author fengxi
 * @date 2022年03月02日 15:08
 */
public class CaffeineTest {

    public static void main(String[] args) {
        LoadingCache<String, Integer> cache = Caffeine.newBuilder()
                .weakKeys()
                .weakValues()
                .build(key -> {
                    new Date();
                    return 12;
                });

    }
}
