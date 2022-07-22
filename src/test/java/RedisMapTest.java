import junit.framework.TestCase;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class RedisMapTest extends TestCase {
    private Jedis jedis;
    private final Map<String, String> hash = new HashMap<>();

    @Override
    public void setUp() {
        try {
            jedis = new Jedis(Const.URL);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        hash.put("Web-Разработчик", "1");
        hash.put("Data Science", "4");

        jedis.hmset(Const.KEY_MAP, hash);
    }

    public void testPrintAllCourses() throws IOException {
        List<String> listString = Files.readAllLines(Paths.get(Const.RESULT_LIST));

        StringJoiner expected = new StringJoiner(System.lineSeparator());
        listString.forEach(expected::add);

        StringJoiner actual = new StringJoiner(System.lineSeparator());
        Map<String, String> list = jedis.hgetAll(Const.KEY_MAP);
        for (Map.Entry<String, String> course : list.entrySet()) {
            String str = course.getKey() + " - " + course.getValue();
            actual.add(str);
        }

        assertEquals(expected.toString(), actual.toString());
    }

    public void testIncrement() {
        List<String> before = jedis.hmget(Const.KEY_MAP, "Data Science");
        int countBefore = Integer.parseInt(before.get(0));

        jedis.hincrBy(Const.KEY_MAP, "Data Science", 1);

        List<String> after = jedis.hmget(Const.KEY_MAP, "Data Science");
        int countAfter = Integer.parseInt(after.get(0));

        assertEquals(++countBefore, countAfter);
    }

    @Override
    public void tearDown() {
        jedis.del(Const.KEY_MAP);
        jedis.close();
    }
}