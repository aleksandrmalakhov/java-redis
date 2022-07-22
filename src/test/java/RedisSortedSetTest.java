import junit.framework.TestCase;
import lombok.NonNull;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Tuple;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class RedisSortedSetTest extends TestCase {
    private Jedis jedis;
    private static final long COUNT_CITY = 10;

    @Override
    public void setUp() {
        try {
            jedis = new Jedis(Const.URL);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        jedis.zadd(Const.KEY_SORT, creatMap());
    }

    public void testCreatSortedSetRedis() {
        long actual = jedis.zcard(Const.KEY_SORT);

        assertEquals(COUNT_CITY, actual);
    }

    public void testSortedByPrice() throws IOException {
        String expected = getExpected(Const.SORTED_BY_PRICE);

        StringJoiner actual = new StringJoiner(System.lineSeparator());
        List<String> actualList = jedis.zrange(Const.KEY_SORT, 0, -1);
        actualList.forEach(actual::add);

        assertEquals(expected, actual.toString());
    }

    public void testTop3MaxPrice() throws IOException {
        String expected = getExpected(Const.TOP3_MAX_PRICE);

        List<Tuple> list = jedis.zrevrangeWithScores(Const.KEY_SORT, 0, 2);
        String actual = getActual(list);

        assertEquals(expected, actual);
    }

    public void testTop3MinPrice() throws IOException {
        String expected = getExpected(Const.TOP3_MIN_PRICE);

        List<Tuple> list = jedis.zrangeWithScores(Const.KEY_SORT, 0, 2);
        String actual = getActual(list);

        assertEquals(expected, actual);
    }

    @Override
    public void tearDown() {
        jedis.del(Const.KEY_SORT);
        jedis.close();
    }

    private String getExpected(String path) throws IOException {
        List<String> expectedList = Files.readAllLines(Paths.get(path));

        StringJoiner expected = new StringJoiner(System.lineSeparator());
        expectedList.forEach(expected::add);
        return expected.toString();
    }

    private String getActual(@NonNull List<Tuple> list) {
        StringJoiner actual = new StringJoiner(System.lineSeparator());
        list.forEach(city -> {
            String str = city.getElement() + " - " + city.getScore();
            actual.add(str);
        });

        return actual.toString();
    }

    @SuppressWarnings("unchecked")
    private @NonNull Map<String, Double> creatMap() {
        Map<String, Double> listCites = new HashMap<>();
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonData = (JSONObject) parser.parse(getJsonFile());
            JSONArray citesArray = (JSONArray) jsonData.get("cities");

            citesArray.forEach(object -> {
                JSONObject cityObject = (JSONObject) object;
                listCites.put((String) cityObject.get("name"), (Double) cityObject.get("ticketPrice"));
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return listCites;
    }

    private @NonNull String getJsonFile() {
        StringBuilder builder = new StringBuilder();
        try {
            List<String> lines = Files.readAllLines(Paths.get(Const.JSON_FILE));
            lines.forEach(builder::append);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return builder.toString();
    }
}