import junit.framework.TestCase;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RedisSetTest extends TestCase {
    private Jedis jedis;
    private final List<String> list = new ArrayList<>();

    @Override
    public void setUp() {
        try {
            jedis = new Jedis(Const.URL);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        list.add("buy milk");
        list.add("refuel the car");
        list.add("feed the cat");

        for (String task : list) {
            jedis.sadd(Const.KEY_SET, task);
        }
    }

    public void testAddTasks() {
        assertEquals(list.size(), jedis.scard(Const.KEY_SET));
    }

    public void testAddNewTask() {
        long countTasks = jedis.scard(Const.KEY_SET);
        jedis.sadd(Const.KEY_SET, "New Task");

        assertEquals(countTasks + 1, jedis.scard(Const.KEY_SET));
    }

    public void testAddRepeatTask() {
        long countTasks = jedis.scard(Const.KEY_SET);
        Set<String> setTasks = jedis.smembers(Const.KEY_SET);
        setTasks.forEach(task -> jedis.sadd(Const.KEY_SET, task));

        assertEquals(countTasks, jedis.scard(Const.KEY_SET));
    }

    public void testDeleteAllTasks() {
        Set<String> setTasks = jedis.smembers(Const.KEY_SET);
        setTasks.forEach(task -> jedis.srem(Const.KEY_SET, task));

        assertEquals(0, jedis.scard(Const.KEY_SET));
    }

    @Override
    public void tearDown() {
        jedis.del(Const.KEY_SET);
        jedis.close();
    }
}